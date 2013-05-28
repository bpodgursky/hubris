package com.bpodgursky.hubris.events;

public class FleetCreatedEvent {
  private int fleetId;

  public FleetCreatedEvent(int fleetId) {
    this.fleetId = fleetId;
  }

  public int getFleetId() {
    return fleetId;
  }
}
