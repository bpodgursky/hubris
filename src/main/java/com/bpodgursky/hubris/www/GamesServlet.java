package com.bpodgursky.hubris.www;

import com.bpodgursky.hubris.account.GameMeta;
import com.bpodgursky.hubris.client.GenericManager;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.db.models.hubris.Tables;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.NpCookies;
import com.bpodgursky.hubris.db.models.hubris.tables.records.GameStatesRecord;
import com.bpodgursky.hubris.db.models.hubris.tables.records.GameSyncsRecord;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.bpodgursky.hubris.universe.GameState;
import org.apache.commons.io.IOUtils;
import org.jooq.Cursor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.bpodgursky.hubris.db.GameStatePersistence.GameStateResult;

public class GamesServlet extends HubrisServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    NpCookies result = getCookies(req);
    NpHttpClient client = new NpHttpClient(result.getCookies());

    if ("/index".equals(req.getPathInfo())) {
      List<GameMeta> activeGames = GenericManager.getActiveGames(client);
      req.setAttribute("active_games", activeGames);
      req.getRequestDispatcher("/_games/_game_list.jsp").forward(req, resp);
    }
    else if ("/states_batch/".equals(req.getPathInfo())) {
      int gameId = Integer.parseInt(req.getParameter("gameId"));
      try {
        writeStates(gameId, result.getId(), resp.getOutputStream(), 10);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    else if (req.getParameter("syncs") != null) {
      int gameId = Integer.parseInt(req.getPathInfo().substring(1));
      if ("1".equals(req.getParameter("syncs"))) {
        db.dslContext()
          .insertInto(Tables.GAME_SYNCS, Tables.GAME_SYNCS.COOKIES_ID, Tables.GAME_SYNCS.GAME_ID)
          .values(result.getId(), gameId)
          .execute();
      }
      else {
        db.dslContext()
          .delete(Tables.GAME_SYNCS)
          .where(
            Tables.GAME_SYNCS.COOKIES_ID.eq(result.getId()),
            Tables.GAME_SYNCS.GAME_ID.eq(gameId))
          .execute();
      }
    }
    else {
      int gameId = Integer.parseInt(req.getPathInfo().substring(1));
      GameConnection gameConnection = new RemoteConnection(result.getCookies());
      try {
        GameSyncsRecord sync = db.dslContext()
          .selectFrom(Tables.GAME_SYNCS)
          .where(
            Tables.GAME_SYNCS.COOKIES_ID.eq(result.getId()),
            Tables.GAME_SYNCS.GAME_ID.eq(gameId)
          )
          .fetchOne();

        GameState state = gameConnection.getState(null, new GetState(0, result.getUsername(), (long)gameId));
        req.setAttribute("game_state", state);
        req.setAttribute("has_sync", sync != null);
        req.getRequestDispatcher("/_games/_game.jsp").forward(req, resp);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void writeStates(int gameId, int cookiesId, OutputStream out, int skipSize) throws SQLException, IOException {
    PrintStream wrappedOut = new PrintStream(out);
    Cursor<GameStatesRecord> results = getStatesResults(gameId, cookiesId);
    int index = 0;

    wrappedOut.print('[');
    while (results.hasNext()) {
      GameStatesRecord record = results.fetchOne();

      if ((index % skipSize) == 0) {
        if (index > 0) {
          wrappedOut.print(',');
        }
        wrappedOut.print(record.getState());
      }
      index++;
    }
    wrappedOut.write(']');
    wrappedOut.close();
    results.close();
  }

  private GameStateResult fromResultSet(ResultSet results) throws SQLException, IOException {
    StringWriter writer = new StringWriter();
    IOUtils.copy(results.getBinaryStream(1), writer);

    return new GameStateResult(results.getTimestamp(2).getTime(), writer.toString());
  }

  private Cursor<GameStatesRecord> getStatesResults(int gameId, int cookiesId) throws SQLException {
    return db.dslContext()
      .selectFrom(Tables.GAME_STATES)
      .where(Tables.GAME_STATES.GAME_ID.eq(gameId), Tables.GAME_STATES.COOKIES_ID.eq(cookiesId))
      .orderBy(Tables.GAME_STATES.TIME)
      .fetchLazy();
  }
}
