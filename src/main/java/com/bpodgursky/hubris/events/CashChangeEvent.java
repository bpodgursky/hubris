package com.bpodgursky.hubris.events;

public class CashChangeEvent {

  private final int oldCash;
  private final int newCash;
  private final int player;

  public CashChangeEvent(int player, int oldCash, int newCash){
    this.oldCash = oldCash;
    this.newCash = newCash;
    this.player = player;
  }

  public int getOldCash() {
    return oldCash;
  }

  public int getNewCash() {
    return newCash;
  }

  public int getPlayer(){
    return player;
  }
}
