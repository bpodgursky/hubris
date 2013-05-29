package com.bpodgursky.hubris.util;

public class BattleOutcome {
  private final boolean defenderWon;
  private final int shipsRemaining;

  public BattleOutcome(boolean defenderWon, int shipsRemaining) {
    this.defenderWon = defenderWon;
    this.shipsRemaining = shipsRemaining;
  }

  public boolean defenderWon() {
    return defenderWon;
  }

  public int getShipsRemaining() {
    return shipsRemaining;
  }
}
