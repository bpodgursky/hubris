package com.bpodgursky.hubris.events;

import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.Star;

public class FleetArrivedEvent {
  private final Star star;
  private final Fleet fleet;

  public FleetArrivedEvent(Star star, Fleet fleet) {
    this.star = star;
    this.fleet = fleet;
  }

  public Fleet getFleet() {
    return fleet;
  }

  public Star getStar() {
    return star;
  }
}
