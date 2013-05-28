package com.bpodgursky.hubris.events;

public class StarRevealedEvent {
  private final int starId;

  public StarRevealedEvent(int starId) {
    this.starId = starId;
  }

  public int getStarId() {
    return starId;
  }
}
