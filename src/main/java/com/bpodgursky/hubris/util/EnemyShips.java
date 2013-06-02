package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Fleet;

public class EnemyShips implements Filter<Fleet> {

  private final int player;
  public EnemyShips(int player){
    this.player = player;
  }

  @Override
  public boolean isAccept(Fleet item) {
    return item.getPlayer() != player;
  }
}
