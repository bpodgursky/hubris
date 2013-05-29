package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.account.GameMeta;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.helpers.SpendHelper;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;
import com.bpodgursky.hubris.universe.TechType;
import jline.console.ConsoleReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManager {
  private final NpHttpClient client;

  private static final Pattern GAME_PATTERN
      = Pattern.compile("href='/game[?]game=([^']+)'>([^<]+)<");

  public GameManager(NpHttpClient client) {
    this.client = client;
  }

  /**
   * Requests a list of games this user is currently a member of.
   *
   * @return
   */
  public List<GameMeta> getActiveGames() {
    String source = client.get(HubrisConstants.accountHomeUrl);
    Matcher matcher = GAME_PATTERN.matcher(source);

    // TODO: make sure we're getting back the page we expect.
    if (!matcher.find()) {
      return Collections.emptyList();
    }

    List<GameMeta> games = new ArrayList<GameMeta>();

    do {
      String name = matcher.group(2);
      Integer id;
      try {
        id = Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        throw new RuntimeException("Expected game ID (" + matcher.group(1) + ") to be an integer!");
      }

      games.add(new GameMeta(name, id));
    } while (matcher.find());

    return games;
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Syntax is: GameManager <settings_file.yml>");
      System.exit(1);
    }

    ClientSettings settings = ClientSettings.loadFromYaml(args[0]);
    String cookies = settings.getCookies();
    NpHttpClient client = new NpHttpClient(cookies);
    GameManager manager = new GameManager(client);
    ConsoleReader reader = new ConsoleReader();

    while (true) {
      System.out.println(" ---- ACTIVE GAMES ---- ");
      List<GameMeta> games = manager.getActiveGames();
      for (int i = 0; i < games.size(); i++) {
        GameMeta game = games.get(i);
        System.out.printf("%2d. %s\n", i, game.getName());
      }
      GameMeta game = games.get(Integer.parseInt(reader.readLine("Enter game: ")));

      GameConnection connection = new RemoteConnection(cookies);
      String npUsername = settings.getNpUsername();
      long id = game.getId();
      int player = HubrisUtil.getPlayerNumber(connection, npUsername, id);

      CommandFactory factory = new CommandFactory(npUsername, id, player);
      GameState state = connection.getState(null, factory.getState());

      System.out.println(state);

      for (Star star : state.getAllStars(false)) {
        if(star.getPlayerNumber() == player){
          System.out.println(star.getResources()+"\t"+star.science+"\t"+star.scienceUpgrade);
        }
      }

//      System.out.println(state.gameData.toString());
//
//      Collection<GameRequest> spendRequests = SpendHelper.planSpend(state, 1.0, 1.0, .5, 100, factory);
//
//      System.out.println(spendRequests);


      System.out.println(state.getPlayer(state.getPlayerId()).getTech(TechType.RANGE));
    }
  }
}
