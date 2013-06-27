package com.bpodgursky.hubris.db;

//import com.bpodgursky.hubris.hibernate.GameState;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;
import java.util.List;

public class GameStatePersistence {

  private final Connection conn;
  public GameStatePersistence() throws SQLException {
     conn = DriverManager.getConnection("jdbc:derby:derbyDB;create=true");
  }

  public static class GameStateResult {

    private final long time;
    private final String state;

    public GameStateResult(long time, String state) {
      this.time = time;
      this.state = state;
    }

    public long getTime() {
      return time;
    }

    public String getState() {
      return state;
    }

    @Override
    public String toString() {
      return "GameStateResult{" +
          "time=" + time +
          ", state='" + state + '\'' +
          '}';
    }
  }

  public void saveState(long gameId, int cookiesId, Date time, String gameState) throws SQLException {

    PreparedStatement ps = conn.prepareStatement("INSERT INTO game_states (game_id, cookies_id, time, state) VALUES (?, ?, ?, ?)");

    ps.setLong(1, gameId);
    ps.setInt(2, cookiesId);
    ps.setTimestamp(3, new Timestamp(time.getTime()));
    ps.setBinaryStream(4, new ByteArrayInputStream( gameState.getBytes()));

    ps.executeUpdate();
  }


  public List<GameStateResult> getStates(int gameId, int cookiesId) throws SQLException, IOException {
    PreparedStatement s = conn.prepareStatement("SELECT state, time FROM game_states WHERE game_id = ? AND cookies_id = ? ORDER BY time");

    s.setInt(1, gameId);
    s.setInt(2, cookiesId);

    ResultSet resultSet = s.executeQuery();

    List<GameStateResult> states = Lists.newArrayList();
    while(resultSet.next()){

      StringWriter writer = new StringWriter();
      IOUtils.copy(resultSet.getBinaryStream(1), writer);

      states.add(new GameStateResult(resultSet.getTimestamp(2).getTime(), writer.toString()));
    }

    return states;
  }
}
