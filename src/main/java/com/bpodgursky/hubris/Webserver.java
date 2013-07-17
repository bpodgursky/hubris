package com.bpodgursky.hubris;


import com.bpodgursky.hubris.db.HubrisDb;
import com.bpodgursky.hubris.www.GamesServlet;
import com.bpodgursky.hubris.www.HubrisDefaultServlet;
import com.bpodgursky.hubris.www.LoginServlet;
import com.google.common.collect.Maps;
import java.net.URL;
import java.util.EnumSet;
import java.util.concurrent.Executors;
import javax.servlet.DispatcherType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.webapp.WebAppContext;

import static com.bpodgursky.hubris.account.LoginClient.LoginResponse;

public class Webserver {
  private static final Logger LOG = Logger.getLogger(Webserver.class);

  static{


    BasicConfigurator.resetConfiguration();
    final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n"), ConsoleAppender.SYSTEM_ERR);
    consoleAppender.setFollow(true);
    BasicConfigurator.configure(consoleAppender);

    Logger.getRootLogger().setLevel(Level.INFO);

    Logger.getLogger("org.eclipse.jetty").setLevel(Level.WARN);
  }

  public static void main(String[] args) throws Exception {
    Server uiServer = new Server(43767);
    final URL warUrl = uiServer.getClass().getClassLoader().getResource("com/bpodgursky/hubris");
    final String warUrlString = warUrl.toExternalForm();

    final GameStateSyncer syncer = new GameStateSyncer(new HubrisDb.Factory().getProduction());
    Runnable syncerTask = new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            LOG.info("Syncing games...");
            syncer.syncAllGames();
          }
          catch (Exception e) {
            LOG.error("Error syncing games", e);
          }

          try {
            Thread.sleep(600000L);
          } catch (InterruptedException e) {
            LOG.error("Syncer thread interrupted");
            return;
          }
        }
      }
    };
    Executors.newSingleThreadExecutor().execute(syncerTask);

    WebAppContext webAppContext = new WebAppContext(warUrlString, "/");
    webAppContext.setAttribute("login_clients", Maps.<String, LoginResponse>newHashMap());
    webAppContext.setAttribute("cookies", Maps.<String, String>newHashMap());
    webAppContext.addFilter(GzipFilter.class, "/games/*", EnumSet.of(DispatcherType.REQUEST));
    webAppContext.addServlet(LoginServlet.class, "/login");
    webAppContext.addServlet(GamesServlet.class, "/games/*");
    webAppContext.addServlet(HubrisDefaultServlet.class, "");

    uiServer.setHandler(webAppContext);

    uiServer.start();
    uiServer.join();
  }
}
