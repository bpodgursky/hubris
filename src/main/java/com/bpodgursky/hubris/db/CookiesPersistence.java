package com.bpodgursky.hubris.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CookiesPersistence {
  private static final String GET_BY_UUID_QUERY = "SELECT * FROM cookies WHERE uuid = ?";
  private static final String GET_BY_ID_QUERY = "SELECT * FROM cookies WHERE id = ?";
  private static final String INSERT_QUERY = "INSERT INTO cookies (uuid, user_name, cookies) VALUES (?, ?, ?)";

  private final Connection conn;

  public CookiesPersistence() throws SQLException {
    conn = DriverManager.getConnection("jdbc:derby:derbyDB;create=true");
  }

  public static class CookiesResult {
    private final int id;
    private final String uuid;
    private final String username;
    private final String cookies;

    public CookiesResult(int id, String uuid, String username, String cookies) {
      this.id = id;
      this.uuid = uuid;
      this.username = username;
      this.cookies = cookies;
    }

    public int getId() {
      return id;
    }

    public String getUuid() {
      return uuid;
    }

    public String getUsername() {
      return username;
    }

    public String getCookies() {
      return cookies;
    }
  }

  public CookiesResult findByUuid(String uuid) throws SQLException {
    PreparedStatement statement = conn.prepareStatement(GET_BY_UUID_QUERY);

    statement.setString(1, uuid);
    ResultSet result = statement.executeQuery();

    if (result.next()) {
      return new CookiesResult(result.getInt("id"),
          result.getString("uuid"),
          result.getString("user_name"),
          result.getString("cookies"));
    }
    else {
      return null;
    }
  }

  public CookiesResult find(int id) throws SQLException {
    PreparedStatement statement = conn.prepareStatement(GET_BY_ID_QUERY);

    statement.setInt(1, id);
    ResultSet result = statement.executeQuery();

    if (result.next()) {
      return new CookiesResult(result.getInt("id"),
          result.getString("uuid"),
          result.getString("user_name"),
          result.getString("cookies"));
    }
    else {
      return null;
    }
  }

  public void create(String uuid, String username, String cookies) throws SQLException {
    PreparedStatement statement = conn.prepareStatement(INSERT_QUERY);

    statement.setString(1, uuid);
    statement.setString(2, username);
    statement.setString(3, cookies);
    statement.execute();
  }
}
