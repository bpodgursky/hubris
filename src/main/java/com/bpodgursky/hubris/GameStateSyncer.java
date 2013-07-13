package com.bpodgursky.hubris;

import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.db.HubrisDb;
import com.bpodgursky.hubris.universe.GameState;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bpodgursky.hubris.db.CookiesPersistence.CookiesResult;
import static com.bpodgursky.hubris.db.GameSyncsPersistence.GameSync;

public class GameStateSyncer {
  private static final Logger LOG = LoggerFactory.getLogger(GameStateSyncer.class);

  private final HubrisDb conn;

  public GameStateSyncer(HubrisDb conn) {
    this.conn = conn;
  }

  public void syncAllGames() throws SQLException {
    Set<GameSync> syncRequests = conn.gameSyncsPersistence().findAll();

    for (GameSync syncRequest : syncRequests) {
      CookiesResult cookies = conn.cookiesPersistence().find(syncRequest.getCookiesId());
      GameConnection connection = new RemoteConnection(cookies.getCookies());
      Long gameId = Long.valueOf(syncRequest.getGameId());
      LOG.info("Syncing game {} for cookies {}, uuid = {}", gameId, cookies.getId(), cookies.getUuid());

      try {
        GameState state = connection.getState(null, new GetState(0, cookies.getUsername(), gameId));
        conn.gameStatePersistence().saveState(gameId, cookies.getId(), new Date(System.currentTimeMillis()), state.toString());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void main(String[] args) throws SQLException {
    new GameStateSyncer(HubrisDb.get()).syncAllGames();
  }
}
