package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.FleetDestinationChangedEvent;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Lists;

import java.util.List;

public class FleetDestinationChangedFactory implements EventFactory<FleetDestinationChangedEvent> {

  @Override
  public List<FleetDestinationChangedEvent> getEvents(GameState newState) {
    GameState oldState = newState.previousState();
    List<FleetDestinationChangedEvent> events = Lists.newArrayList();

    for (Fleet fleet : newState.getAllFleets()) {
      List<Integer> destinations = fleet.getDestinations();

      if (oldState.getFleet(fleet.getId()) != null && !oldState.getFleet(fleet.getId()).getDestinations().equals(destinations)) {
        events.add(new FleetDestinationChangedEvent(fleet.getId()));
      }
    }

    return events;
  }

  @Override
  public Class<FleetDestinationChangedEvent> getEventType() {
    return FleetDestinationChangedEvent.class;
  }
}
