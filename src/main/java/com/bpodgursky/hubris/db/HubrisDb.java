package com.bpodgursky.hubris.db;

import java.sql.SQLException;

public class HubrisDb {
  private static HubrisDb instance;

  private CookiesPersistence cookiesPersistence;
  private GameStatePersistence gameStatePersistence;
  private GameSyncsPersistence gameSyncsPersistence;

  private HubrisDb() throws SQLException {
    this.cookiesPersistence = new CookiesPersistence();
    this.gameStatePersistence = new GameStatePersistence();
    this.gameSyncsPersistence = new GameSyncsPersistence();
  }

  public CookiesPersistence cookiesPersistence() {
    return cookiesPersistence;
  }

  public GameStatePersistence gameStatePersistence() {
    return gameStatePersistence;
  }

  public GameSyncsPersistence gameSyncsPersistence() {
    return gameSyncsPersistence;
  }

  public static HubrisDb get() {
    if (instance == null) {
      try {
        instance = new HubrisDb();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return instance;
  }
}
