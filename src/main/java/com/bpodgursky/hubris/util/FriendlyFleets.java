package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Fleet;

public class FriendlyFleets implements Filter<Fleet> {

  private final int player;
  public FriendlyFleets(int player){
    this.player = player;
  }


  @Override
  public boolean isAccept(Fleet item) {
    return item.getPlayer() == player;
  }
}
