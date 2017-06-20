package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.account.GameMeta;
import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jline.console.ConsoleReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericManager {

  private static final Pattern GAME_PATTERN
      = Pattern.compile("href='/game[?]game=([^']+)'>([^<]+)<");

  public static class GameInfo {
    private final CommandFactory factory;
    private final GameConnection connection;

    public GameInfo(GameConnection connection, CommandFactory factory) {
      this.connection = connection;
      this.factory = factory;
    }

    public CommandFactory getFactory() {
      return factory;
    }

    public GameConnection getConnection() {
      return connection;
    }
  }

  public static List<GameMeta> getActiveGames(NpHttpClient client) {
    String source = client.post(HubrisConstants.gamesListUrl, "type=init_player");
    JsonElement response = new JsonParser().parse(source);
    JsonObject playerData = response.getAsJsonArray().get(1).getAsJsonObject();
    JsonElement games = playerData.getAsJsonArray("open_games");
    List<GameMeta> gameList = new ArrayList<>();

    for (JsonElement game : games.getAsJsonArray()) {
      gameList.add(new GameMeta(
          game.getAsJsonObject().get("name").getAsString(),
          Long.parseLong(game.getAsJsonObject().get("number").getAsString())
      ));
    }
    return gameList;
  }


  public static GameInfo login(String yamlFile) throws Exception {

    ClientSettings settings = ClientSettings.loadFromYaml(yamlFile);
    String cookies = settings.getCookies();
    NpHttpClient client = new NpHttpClient(cookies);
    ConsoleReader reader = new ConsoleReader();

    System.out.println(" ---- ACTIVE GAMES ---- ");
    List<GameMeta> games = getActiveGames(client);
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

    return new GameInfo(connection, factory);
  }
}
