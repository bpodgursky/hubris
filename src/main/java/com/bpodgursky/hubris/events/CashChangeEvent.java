package com.bpodgursky.hubris.events;

public class CashChangeEvent {

  private final int player;
  private final int change;

  public CashChangeEvent(int player, int change){
    this.change = change;
    this.player = player;
  }

  public int getDifference(){
    return change;
  }

  public int getPlayer(){
    return player;
  }
}
