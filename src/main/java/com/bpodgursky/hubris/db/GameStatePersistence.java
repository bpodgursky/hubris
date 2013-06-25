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

  public void saveState(String gameName, String playerName, Date time, String gameState) throws SQLException {

    PreparedStatement ps = conn.prepareStatement("insert into game_states values (?, ?, ?, ?)");

    ps.setString(1, gameName);
    ps.setString(2, playerName);
    ps.setTimestamp(3, new Timestamp(time.getTime()));
    ps.setBinaryStream(4, new ByteArrayInputStream( gameState.getBytes()));

    ps.executeUpdate();
  }


  public List<GameStateResult> getStates(String gameName, String playerName) throws SQLException, IOException {
    PreparedStatement s = conn.prepareStatement("SELECT state,time FROM game_states WHERE game_name=? AND user_name=? ORDER BY time");

    s.setString(1, gameName);
    s.setString(2, playerName);

    ResultSet resultSet = s.executeQuery();

    List<GameStateResult> states = Lists.newArrayList();
    while(resultSet.next()){

      StringWriter writer = new StringWriter();
      IOUtils.copy(resultSet.getBinaryStream(1), writer);

      states.add(new GameStateResult(resultSet.getTimestamp(2).getTime(), writer.toString()));
    }

    return states;
  }

  public static void main(String[] args) throws SQLException, IOException, InterruptedException {

    GameStatePersistence pers = new GameStatePersistence();

    pers.saveState("game1", "player1", new Date(System.currentTimeMillis()), "state1");
    Thread.sleep(10);
    pers.saveState("game1", "player1", new Date(System.currentTimeMillis()), "state2");

    pers.saveState("game1", "player2", new Date(System.currentTimeMillis()), "state3");
    pers.saveState("game1", "player2", new Date(System.currentTimeMillis()), "state4");

    System.out.println(pers.getStates("game1", "player1"));
    System.out.println(pers.getStates("game1", "player2"));

  }
}
