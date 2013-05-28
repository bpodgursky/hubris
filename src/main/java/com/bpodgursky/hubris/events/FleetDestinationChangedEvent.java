package com.bpodgursky.hubris.events;

public class FleetDestinationChangedEvent {
  private final int fleetId;

  public FleetDestinationChangedEvent(int fleetId) {
    this.fleetId = fleetId;
  }

  public int getFleetId() {
    return fleetId;
  }
}
