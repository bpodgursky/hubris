package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.account.GameMeta;
import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.bpodgursky.hubris.universe.GameState;
import jline.console.ConsoleReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
      SingleGameClient connection = new SingleGameClient(settings.getUsername(), game.getId(), new RemoteConnection(cookies));
      System.out.println(connection.getState());
    }

//    SingleGameClient connection = new SingleGameClient(settings.getUsername(), 6, 28326395l, new RemoteConnection(cookies));
//
//    GameState state = connection.getState();
//    System.out.println(state);
//
//    state.writeGnuPlot("current_state");
//
//    System.out.println(manager.getActiveGames());
  }
}
