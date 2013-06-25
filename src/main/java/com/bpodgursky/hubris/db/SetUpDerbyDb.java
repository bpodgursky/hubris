package com.bpodgursky.hubris.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SetUpDerbyDb {

  public static void main(String[] args) throws SQLException {

    Connection conn = DriverManager.getConnection("jdbc:derby:derbyDB;create=true");

    Statement s = conn.createStatement();
    s.execute("create table game_states(game_name varchar(255), user_name varchar(255), time timestamp, state blob)");

  }
}
