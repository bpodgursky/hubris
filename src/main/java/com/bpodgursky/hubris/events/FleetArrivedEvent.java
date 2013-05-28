package com.bpodgursky.hubris.events;

public class FleetArrivedEvent {
  private final int fleetId;
  private final int starId;

  public FleetArrivedEvent(int fleetId, int starId) {
    this.fleetId = fleetId;
    this.starId = starId;
  }

  public int getFleetId() {
    return fleetId;
  }

  public int getStarId() {
    return starId;
  }
}
