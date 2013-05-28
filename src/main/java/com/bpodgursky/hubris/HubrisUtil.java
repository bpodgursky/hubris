package com.bpodgursky.hubris;

import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.universe.GameState;

public class HubrisUtil {

  public static int getPlayerNumber(GameConnection connection, String npUsername, long gameId) throws Exception {
    GameState state1 = connection.getState(null, new GetState(0, npUsername, gameId));
    return Integer.parseInt(state1.gameData.getMid());
  }
}
