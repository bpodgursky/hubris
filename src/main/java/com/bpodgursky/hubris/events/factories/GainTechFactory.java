package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.GainTechEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.google.common.collect.Lists;

import java.util.List;

public class GainTechFactory implements EventFactory<GainTechEvent> {
  @Override
  public List<GainTechEvent> getEvents(GameState newState) {
    GameState oldState = newState.previousState();
    List<GainTechEvent> events = Lists.newArrayList();

    for (Player player : newState.getPlayers().values()) {
    }

    return events;
  }

  @Override
  public Class<GainTechEvent> getEventType() {
    return GainTechEvent.class;
  }
}
