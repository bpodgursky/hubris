package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.FleetSpottedEvent;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Lists;

import java.util.List;

public class FleetSpottedFactory implements EventFactory<FleetSpottedEvent> {
  @Override
  public List<FleetSpottedEvent> getEvents(GameState newState, GameState oldState) {
    List<FleetSpottedEvent> events = Lists.newArrayList();

    for (Fleet fleet : newState.getAllFleets()) {
      // don't fire fleet created events
      if (oldState.getFleet(fleet.getId()) == null
        && (fleet.getStarId() == null || !oldState.starIsVisible(fleet.getStarId()))) {
        events.add(new FleetSpottedEvent(fleet.getId()));
      }
    }

    return events;
  }

  @Override
  public Class<FleetSpottedEvent> getEventType() {
    return FleetSpottedEvent.class;
  }
}
