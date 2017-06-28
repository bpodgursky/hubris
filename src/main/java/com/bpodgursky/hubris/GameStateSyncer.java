package com.bpodgursky.hubris;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.db.HubrisDb;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.GameStates;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.GameSyncs;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.NpCookies;
import com.bpodgursky.hubris.universe.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStateSyncer {
  private static final Logger LOG = LoggerFactory.getLogger(GameStateSyncer.class);

  private final HubrisDb conn;

  public GameStateSyncer(HubrisDb conn) {
    this.conn = conn;
  }

  public void syncAllGames() throws SQLException {
    List<GameSyncs> syncRequests = conn.gameSyncs().findAll();

    for (GameSyncs syncRequest : syncRequests) {
      try {
        NpCookies cookies = conn.npCookies().findById(syncRequest.getCookiesId());
        GameConnection connection = new RemoteConnection(cookies.getCookies());

        if (!connection.isLoggedIn()) {
          LOG.warn("auth cookie for user id {} expired... attempting to refresh with username/password...", cookies.getId());
          
          LoginClient loginClient = new LoginClient();
          LoginClient.LoginResponse loginResponse = loginClient.login(cookies.getUsername(), cookies.getPassword());

          if (loginResponse.getResponseType() == LoginClient.LoginResponseType.INVALID_LOGIN) {
            throw new RuntimeException("Error renewing cookies for cookie ID: " + cookies.getId());
          }

          cookies.setCookies(loginResponse.getCookies());
          conn.npCookies().update(cookies);
          connection = new RemoteConnection(cookies.getCookies());
        }

        Long gameId = syncRequest.getGameId();
        LOG.info("Syncing game {} for cookies {}", gameId, cookies.getId());

        GameState state = connection.getState(null, new GetState(0, cookies.getUsername(), gameId));
        GameStates row = new GameStates();
        row.setCookiesId(cookies.getId());
        row.setGameId(syncRequest.getGameId());
        row.setState(state.toString());

        conn.gameStates().insert(row);

        LOG.info("Trying to refresh auth cookie {}", cookies.getId());

        Optional<String> authCookies = connection.refreshCookies();

        if (!authCookies.isPresent()) {
          LOG.error("Couldn't extract auth cookies!");
        } else {
          cookies.setCookies(authCookies.get());
          conn.npCookies().update(cookies);
        }
      } catch (Exception e) {
        LOG.error("Encountered an error", e);
      }
    }
  }
}
