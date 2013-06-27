package com.bpodgursky.hubris.www;

import com.bpodgursky.hubris.account.GameMeta;
import com.bpodgursky.hubris.client.GenericManager;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.db.CookiesPersistence;
import com.bpodgursky.hubris.db.GameSyncsPersistence;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.bpodgursky.hubris.universe.GameState;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.bpodgursky.hubris.db.GameStatePersistence.GameStateResult;

public class GamesServlet extends HubrisServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    CookiesPersistence.CookiesResult result = getCookies(req);
    NpHttpClient client = new NpHttpClient(result.getCookies());

    if ("/index".equals(req.getPathInfo())) {
      List<GameMeta> activeGames = GenericManager.getActiveGames(client);
      req.setAttribute("active_games", activeGames);
      req.getRequestDispatcher("/_games/_game_list.jsp").forward(req, resp);
    }
    else if ("/states_batch/".equals(req.getPathInfo())) {
      int gameId = Integer.parseInt(req.getParameter("gameId"));
      try {
        List<GameStateResult> states = db.gameStatePersistence().getStates(gameId, result.getId());
        writeResultsBatch(states, resp);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    else if (req.getParameter("syncs") != null) {
      int gameId = Integer.parseInt(req.getPathInfo().substring(1));
      try {
        if ("1".equals(req.getParameter("syncs"))) {
          db.gameSyncsPersistence().create(result.getId(), gameId);
        }
        else {
          db.gameSyncsPersistence().delete(result.getId(), gameId);
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      int gameId = Integer.parseInt(req.getPathInfo().substring(1));
      GameConnection gameConnection = new RemoteConnection(result.getCookies());
      try {
        GameSyncsPersistence.GameSync sync = db.gameSyncsPersistence().find(result.getId(), gameId);
        GameState state = gameConnection.getState(null, new GetState(0, result.getUsername(), (long)gameId));
        req.setAttribute("game_state", state);
        req.setAttribute("has_sync", sync != null);
        req.getRequestDispatcher("/_games/_game.jsp").forward(req, resp);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected void writeResultsBatch(List<GameStateResult> results, HttpServletResponse resp) throws IOException {
    ServletOutputStream out = resp.getOutputStream();
    out.print("[");

    if (! results.isEmpty()) {
      out.print(results.get(0).getState());

      for (int i = 1; i < results.size(); i++) {
        if ((i % 10) == 0) {
          out.print(',');
          out.print(results.get(i).getState());
        }
      }
    }

    out.print("]");
    out.close();
  }
}
