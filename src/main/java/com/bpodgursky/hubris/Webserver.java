package com.bpodgursky.hubris;


import org.apache.log4j.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;

public class Webserver {

  static{
    Logger.getRootLogger().setLevel(Level.ALL);

    BasicConfigurator.resetConfiguration();
    final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n"), ConsoleAppender.SYSTEM_ERR);
    consoleAppender.setFollow(true);
    BasicConfigurator.configure(consoleAppender);
  }

  public static void main(String[] args) throws Exception {
    Server uiServer = new Server(43767);
    final URL warUrl = uiServer.getClass().getClassLoader().getResource("com/bpodgursky/hubris");
    final String warUrlString = warUrl.toExternalForm();

    WebAppContext webAppContext = new WebAppContext(warUrlString, "/");

    uiServer.setHandler(webAppContext);

    uiServer.start();

    Thread.sleep(1000000000l);

  }
}
