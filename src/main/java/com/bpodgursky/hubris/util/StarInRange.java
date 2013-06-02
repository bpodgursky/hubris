package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.universe.Coordinate;
import com.bpodgursky.hubris.universe.Star;

public class StarInRange implements Filter<Star> {

  private final Coordinate coords;
  private final double lightYears;

  public StarInRange(Coordinate coords, double lightYears){
    this.coords = coords;
    this.lightYears = lightYears;
  }

  @Override
  public boolean isAccept(Star item) {
    return HubrisUtil.getDistanceInLightYears(coords, item.getCoords()) <= lightYears;
  }
}
