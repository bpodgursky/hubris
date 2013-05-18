package com.bpodgursky.hubris.account;

import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.transfer.NpHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountManager {
  private final NpHttpClient client;

  private static final Pattern GAME_PATTERN
      = Pattern.compile("href='/game[?]game=([^']+)'>([^<]+)<");

  public AccountManager(NpHttpClient client) {
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

  public static void main(String[] args) {
    System.out.println(System.getProperty("java.ext.dirs"));
    System.out.println("Logging in...");
    LoginClient loginClient = new LoginClient();
    LoginClient.LoginResponse login = loginClient.login("rapleaf.np.test@gmail.com", "rapleaf_np");

    if (login.getResponseType() != LoginClient.LoginResponseType.SUCCESS) {
      System.out.println("ERROR: " + login);
      System.exit(1);
    }

    NpHttpClient client = new NpHttpClient(login.getCookies());
    AccountManager manager = new AccountManager(client);

    System.out.println(manager.getActiveGames());
  }
}
