package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Fleet;

public class FriendlyShips implements Filter<Fleet> {

  private final int player;
  public FriendlyShips(int player){
    this.player = player;
  }


  @Override
  public boolean isAccept(Fleet item) {
    return item.getPlayer() == player;
  }
}
