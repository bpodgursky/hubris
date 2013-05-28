package com.bpodgursky.hubris.events;

public class FleetSpottedEvent {
  private final int fleetId;

  public FleetSpottedEvent(int fleetId) {
    this.fleetId = fleetId;
  }

  public int getFleetId() {
    return fleetId;
  }
}
