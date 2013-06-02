package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Star;

public class FriendlyStars implements Filter<Star> {

  private final int player;
  public FriendlyStars(int player){
    this.player = player;
  }

  @Override
  public boolean isAccept(Star item) {
    return item.getPlayerNumber() != null && item.getPlayerNumber() == player;
  }
}
