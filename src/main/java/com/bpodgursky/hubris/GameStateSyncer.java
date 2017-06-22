package com.bpodgursky.hubris;

import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.db.HubrisDb;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.GameStates;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.GameSyncs;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.NpCookies;
import com.bpodgursky.hubris.universe.GameState;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
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
      NpCookies cookies = conn.npCookies().findById(syncRequest.getCookiesId());
      GameConnection connection = new RemoteConnection(cookies.getCookies());
      Long gameId = syncRequest.getGameId();
      LOG.info("Syncing game {} for cookies {}, uuid = {}", gameId, cookies.getId(), cookies.getUuid());

      try {
        GameState state = connection.getState(null, new GetState(0, cookies.getUsername(), gameId));
        GameStates row = new GameStates();
        row.setCookiesId(cookies.getId());
        row.setGameId(syncRequest.getGameId());
        row.setState(state.toString());

        conn.gameStates().insert(row);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
