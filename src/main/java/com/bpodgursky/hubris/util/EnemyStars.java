package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Star;

public class EnemyStars implements Filter<Star> {

  private final int player;
  public EnemyStars(int player){
    this.player = player;
  }

  @Override
  public boolean isAccept(Star item) {
    return item.getPlayerNumber() != -1 && item.getPlayerNumber() != player;
  }
}
