package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.ResearchChangeEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;

import java.util.List;

public class ChangeResearchFactory implements EventFactory<ResearchChangeEvent> {

  @Override
  public List<ResearchChangeEvent> getEvents(GameState newState) {

    for (Player player : newState.getAllPlayers()) {
      player.getCurrentResearch();

    }

    return null;
  }

  @Override
  public Class<ResearchChangeEvent> getEventType() {
    return ResearchChangeEvent.class;
  }
}
