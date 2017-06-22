package com.bpodgursky.hubris;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.db.HubrisDb;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.GameStates;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.NpCookies;
import com.bpodgursky.hubris.response.ResponseTransformer;
import com.bpodgursky.hubris.universe.GameState;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

public class ImportGameState {
  public static void main(String[] args) throws IOException, TransformerException, SAXException {
    HubrisDb conn = new HubrisDb.Factory().getProduction();

    String cookie = args[0];
    long gameId = Long.parseLong(args[1]);
    File[] dir  = new File(args[2]).listFiles();

    NpCookies cookies = conn.npCookies().fetchByUuid(cookie).get(0);

    for (File file : dir) {
      try {
        System.out.println("Processing: " + file.getAbsolutePath());

        GameState state = ResponseTransformer.parseUniverse(
            null,
            new GetState(0, "", gameId),
            FileUtils.readFileToString(file, "UTF-8")
        );
        GameStates row = new GameStates();
        row.setCookiesId(cookies.getId());
        row.setGameId(gameId);
        row.setState(state.toString());

        conn.gameStates().insert(row);
      } catch (Exception e) {
        System.out.println("Skipping due to error: " + e.getMessage());
      }
    }
  }
}
