package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.FleetCreatedEvent;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Lists;

import java.util.List;

public class FleetCreatedFactory implements EventFactory<FleetCreatedEvent> {
  @Override
  public List<FleetCreatedEvent> getEvents(GameState newState) {
    GameState oldState = newState.previousState();
    List<FleetCreatedEvent> events = Lists.newArrayList();

    for (Fleet fleet : newState.getAllFleets()) {
      if (oldState.getFleet(fleet.getId()) == null
        && fleet.getStarId() != null
        && oldState.starIsVisible(fleet.getStarId())) {
        events.add(new FleetCreatedEvent(fleet.getId()));
      }
    }

    return events;
  }

  @Override
  public Class<FleetCreatedEvent> getEventType() {
    return FleetCreatedEvent.class;
  }
}
