package com.bpodgursky.hubris.events;

import com.bpodgursky.hubris.universe.TechType;

public class ResearchChangeEvent {

  private final int playerId;
  private final TechType newResearch;
  private final TechType oldResearch;

  public ResearchChangeEvent(int playerId, TechType oldResearch, TechType newResearch) {
    this.playerId = playerId;
    this.newResearch = newResearch;
    this.oldResearch = oldResearch;
  }

  public int getPlayerId() {
    return playerId;
  }

  public TechType getNewResearch() {
    return newResearch;
  }

  public TechType getOldResearch() {
    return oldResearch;
  }
}
