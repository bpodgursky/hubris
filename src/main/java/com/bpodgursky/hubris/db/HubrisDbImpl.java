package com.bpodgursky.hubris.db;

import com.bpodgursky.hubris.db.models.hubris.tables.daos.GameStatesDao;
import com.bpodgursky.hubris.db.models.hubris.tables.daos.GameSyncsDao;
import com.bpodgursky.hubris.db.models.hubris.tables.daos.NpCookiesDao;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HubrisDbImpl implements HubrisDb {
  public static class Config {
    private String database;
    private String username;
    private String password;

    @Override
    public String toString() {
      return "Config{" +
          "database='" + database + '\'' +
          ", username='" + username + '\'' +
          ", password='" + password + '\'' +
          '}';
    }

    public String getDatabase() {
      return database;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }

    public void setDatabase(String database) {
      this.database = database;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  private final Connection connection;
  private final Configuration jooqConfig;

  HubrisDbImpl(Config config) throws SQLException {
    // Load MySQL JDBC driver
    try {
      Class.forName("com.mysql.jdbc.Driver");
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    String connectionUrl = "jdbc:mysql://localhost/" + config.getDatabase()
      + "?autoReconnect=true&failOverReadOnly=false&maxReconnects=100";

    connection = DriverManager.getConnection(connectionUrl,
      config.getUsername(),
      config.getPassword());
    jooqConfig = new DefaultConfiguration()
      .set(new DefaultConnectionProvider(connection))
      .set(SQLDialect.MYSQL);
  }

  @Override
  public NpCookiesDao npCookies() {
    return new NpCookiesDao(jooqConfig);
  }

  @Override
  public GameStatesDao gameStates() {
    return new GameStatesDao(jooqConfig);
  }

  @Override
  public GameSyncsDao gameSyncs() {
    return new GameSyncsDao(jooqConfig);
  }

  @Override
  public DSLContext dslContext() {
    return DSL.using(connection, SQLDialect.MYSQL);
  }

  @Override
  public Connection connection() {
    return connection;
  }

  @Override
  public Configuration configuration() {
    return jooqConfig;
  }
}
