package com.bpodgursky.hubris.db;

//import com.bpodgursky.hubris.hibernate.GameState;

import com.google.common.collect.Lists;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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

  public int stateCount(int gameId, int cookiesId) throws SQLException {
    PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM game_states WHERE game_id = ? AND cookies_id = ?");

    ps.setInt(1, gameId);
    ps.setInt(2, cookiesId);
    ResultSet results = ps.executeQuery();

    if (results.next()) {
      return results.getInt(1);
    }
    else {
      return 0;
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
    ResultSet resultSet = getStatesResults(gameId, cookiesId);

    List<GameStateResult> states = Lists.newArrayList();
    while(resultSet.next()){
      states.add(fromResultSet(resultSet));
    }
    resultSet.close();

    return states;
  }

  public void writeStates(int gameId, int cookiesId, OutputStream out, int skipSize) throws SQLException, IOException {
    PrintStream wrappedOut = new PrintStream(out);
    ResultSet results = getStatesResults(gameId, cookiesId);
    int index = 0;

    wrappedOut.print('[');
    while (results.next()) {
      if ((index % skipSize) == 0) {
        if (index > 0) {
          wrappedOut.print(',');
        }
        wrappedOut.print(fromResultSet(results).getState());
      }
      index++;
    }
    wrappedOut.write(']');
    wrappedOut.close();
    results.close();
  }

  private GameStateResult fromResultSet(ResultSet results) throws SQLException, IOException {
    StringWriter writer = new StringWriter();
    IOUtils.copy(results.getBinaryStream(1), writer);

    return new GameStateResult(results.getTimestamp(2).getTime(), writer.toString());
  }

  private ResultSet getStatesResults(int gameId, int cookiesId) throws SQLException {
    PreparedStatement s = conn.prepareStatement("SELECT state, time FROM game_states WHERE game_id = ? AND cookies_id = ? ORDER BY time");

    s.setInt(1, gameId);
    s.setInt(2, cookiesId);

    return s.executeQuery();
  }
}
