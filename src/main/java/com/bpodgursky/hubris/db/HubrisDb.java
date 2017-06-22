package com.bpodgursky.hubris.db;

import com.bpodgursky.hubris.db.models.hubris.tables.daos.GameStatesDao;
import com.bpodgursky.hubris.db.models.hubris.tables.daos.GameSyncsDao;
import com.bpodgursky.hubris.db.models.hubris.tables.daos.NpCookiesDao;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

public interface HubrisDb {
  public static class Factory {
    public HubrisDb getProduction() {
      return getProduction(new File("config/database.yml"));
    }

    public HubrisDb getProduction(File configFile) {
      try {
        HubrisDbImpl.Config config = new Yaml().loadAs(new FileReader(configFile), HubrisDbImpl.Config.class);
        return new HubrisDbImpl(config);
      }
      catch (FileNotFoundException e) {
        throw new RuntimeException("Couldn't find database configuration file", e);
      }
      catch (SQLException e) {
        throw new RuntimeException("Error connecting to database", e);
      }
    }
  }

  public NpCookiesDao npCookies();
  public GameStatesDao gameStates();
  public GameSyncsDao gameSyncs();
  public DSLContext dslContext();
  public Connection connection();
  public Configuration configuration();
}
