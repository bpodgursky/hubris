package com.bpodgursky.hubris.db;

import com.google.common.collect.Sets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class GameSyncsPersistence {
  private static final String FIND_ALL_QUERY = "SELECT * FROM game_syncs";
  private static final String FIND_BY_COOKIES_AND_GAME = "SELECT * FROM game_syncs WHERE cookies_id = ? AND game_id = ?";
  private static final String INSERT_QUERY = "INSERT INTO game_syncs (cookies_id, game_id) VALUES (?, ?)";
  private static final String DELETE_QUERY = "DELETE FROM game_syncs WHERE cookies_id = ? AND game_id = ?";

  private final Connection conn;

  public GameSyncsPersistence() throws SQLException {
    conn = DriverManager.getConnection("jdbc:derby:derbyDB;create=true");
  }

  public static class GameSync {
    private final int id;
    private final int cookiesId;
    private final int gameId;

    public GameSync(int id, int cookiesId, int gameId) {
      this.id = id;
      this.cookiesId = cookiesId;
      this.gameId = gameId;
    }

    public int getId() {
      return id;
    }

    public int getCookiesId() {
      return cookiesId;
    }

    public int getGameId() {
      return gameId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      GameSync gameSync = (GameSync) o;

      if (cookiesId != gameSync.cookiesId) return false;
      if (gameId != gameSync.gameId) return false;
      if (id != gameSync.id) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = id;
      result = 31 * result + cookiesId;
      result = 31 * result + gameId;
      return result;
    }
  }

  public Set<GameSync> findAll() throws SQLException {
    PreparedStatement statement = conn.prepareStatement(FIND_ALL_QUERY);
    ResultSet results = statement.executeQuery();
    Set<GameSync> allSyncs = Sets.newHashSet();

    while (results.next()) {
      allSyncs.add(fromResult(results));
    }

    return allSyncs;
  }

  public GameSync find(int cookiesId, int gameId) throws SQLException {
    PreparedStatement statement = conn.prepareStatement(FIND_BY_COOKIES_AND_GAME);

    statement.setInt(1, cookiesId);
    statement.setInt(2, gameId);
    ResultSet resultSet = statement.executeQuery();

    if (resultSet.next()) {
      return fromResult(resultSet);
    }
    else {
      return null;
    }
  }

  public void create(int cookiesId, int gameId) throws SQLException {
    PreparedStatement statement = conn.prepareStatement(INSERT_QUERY);

    statement.setInt(1, cookiesId);
    statement.setInt(2, gameId);
    statement.execute();
  }

  public void delete(int cookieId, int gameId) throws SQLException {
    PreparedStatement statement = conn.prepareStatement(DELETE_QUERY);

    statement.setInt(1, cookieId);
    statement.setInt(2, gameId);
    statement.execute();
  }

  protected GameSync fromResult(ResultSet results) throws SQLException {
    return new GameSync(results.getInt("id"),
      results.getInt("cookies_id"),
      results.getInt("game_id"));
  }
}
