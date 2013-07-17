package com.bpodgursky.hubris.db;

import com.bpodgursky.hubris.db.models.hubris.tables.daos.GameStatesDao;
import com.bpodgursky.hubris.db.models.hubris.tables.daos.GameSyncsDao;
import com.bpodgursky.hubris.db.models.hubris.tables.daos.NpCookiesDao;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface HubrisDb {
  public static class Factory {
    public HubrisDb getProduction() {
      return new HubrisDb() {
        private final Connection connection;
        public final Configuration configuration;

        /* Constructor */ {
          try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/hubris", "hubris", "password");
            configuration = new DefaultConfiguration().set(new ConnectionProvider() {
              @Override
              public Connection acquire() throws DataAccessException {
                return connection;
              }

              @Override
              public void release(Connection connection) throws DataAccessException {
              }
            }).set(SQLDialect.MYSQL);
          }
          catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
          catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public NpCookiesDao npCookies() {
          return new NpCookiesDao(configuration);
        }

        @Override
        public GameStatesDao gameStates() {
          return new GameStatesDao(configuration);
        }

        @Override
        public GameSyncsDao gameSyncs() {
          return new GameSyncsDao(configuration);
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
          return configuration;
        }
      };
    }
  }

  public NpCookiesDao npCookies();
  public GameStatesDao gameStates();
  public GameSyncsDao gameSyncs();
  public DSLContext dslContext();
  public Connection connection();
  public Configuration configuration();
}
