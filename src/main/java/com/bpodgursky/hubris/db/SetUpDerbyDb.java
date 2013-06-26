package com.bpodgursky.hubris.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SetUpDerbyDb {

  public static void main(String[] args) throws SQLException, ClassNotFoundException {

    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    Connection conn = DriverManager.getConnection("jdbc:derby:derbyDB;create=true");

    Statement s = conn.createStatement();
    s.execute("CREATE TABLE game_states (id INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
        "game_id INT NOT NULL, " +
        "cookies_id INT NOT NULL,  " +
        "time TIMESTAMP NOT NULL, " +
        "state BLOB)");
    s.execute("CREATE INDEX game_states_game_id ON game_states (game_id)");
    s.execute("CREATE TABLE cookies (id INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
        "uuid VARCHAR(255), " +
        "user_name VARCHAR(255), " +
        "cookies VARCHAR(2000))");
    s.execute("CREATE UNIQUE INDEX cookies_uuid ON cookies (uuid)");
    s.execute("CREATE TABLE game_syncs (id INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
        "cookies_id INT NOT NULL, " +
        "game_id INT NOT NULL)");
    s.execute("CREATE UNIQUE INDEX game_syncs_unique ON game_syncs (cookies_id, game_id)");
  }
}
