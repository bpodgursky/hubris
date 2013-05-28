package com.bpodgursky.hubris.events;

public class StarSpottedEvent {
  private final int starId;

  public StarSpottedEvent(int starId) {
    this.starId = starId;
  }

  public int getStarId() {
    return starId;
  }
}
