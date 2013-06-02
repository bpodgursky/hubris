package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.universe.Coordinate;
import com.bpodgursky.hubris.universe.Fleet;

public class FleetInRange implements Filter<Fleet> {

  private final Coordinate coords;
  private final double lightYears;

  public FleetInRange(Coordinate coords, double lightYears){
    this.coords = coords;
    this.lightYears = lightYears;
  }

  @Override
  public boolean isAccept(Fleet item) {
    double distance = HubrisUtil.getDistanceInLightYears(coords, item.getCoords());

    return distance != 0.0 && distance <= lightYears;
  }
}
