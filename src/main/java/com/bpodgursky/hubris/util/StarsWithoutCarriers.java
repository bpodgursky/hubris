package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Star;

public class StarsWithoutCarriers implements Filter<Star> {

  @Override
  public boolean isAccept(Star item) {
    return item.getFleets() != null && item.getFleets().isEmpty();
  }
}
