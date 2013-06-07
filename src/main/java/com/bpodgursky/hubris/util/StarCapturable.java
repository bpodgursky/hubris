package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;

public class StarCapturable implements Filter<Star> {


  private final GameState state;
  private final int assaultWeapons;
  private final int assaultShips;

  public StarCapturable(GameState state, int assaultShips, int assaultWeapons){
    this.state = state;
    this.assaultShips = assaultShips;
    this.assaultWeapons = assaultWeapons;
  }

  @Override
  public boolean isAccept(Star item) {
    Integer playerNumber = item.getPlayerNumber();

    if(playerNumber == -1){
      return true;
    }

    if(playerNumber == state.getPlayerId()){
      return true;
    }

    Integer ships = item.getShipsIncludingFleets(state);
    if(ships == null){
      ships = 1;
    }

    int weapons = state.getPlayer(playerNumber).getWeapons();

    BattleOutcome outcome = HubrisUtil.getBattleOutcome(weapons, assaultWeapons, ships, assaultShips);

    return !outcome.defenderWon();
  }
}
