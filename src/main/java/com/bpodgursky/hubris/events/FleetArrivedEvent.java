package com.bpodgursky.hubris.events;

public class FleetArrivedEvent {
  private final int fleetId;

  public FleetArrivedEvent(int fleetId) {
    this.fleetId = fleetId;
  }

  public int getFleetId() {
    return fleetId;
  }
}
