package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.FleetArrivedEvent;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Lists;

import java.util.List;

public class FleetArrivedFactory implements EventFactory<FleetArrivedEvent> {
  @Override
  public List<FleetArrivedEvent> getEvents(GameState newState) {
    GameState oldState = newState.previousState();
    List<FleetArrivedEvent> events = Lists.newArrayList();

    for (Fleet fleet : newState.getAllFleets()) {
      // don't fire fleet created events
      if (oldState.getFleet(fleet.getId()) != null
        && fleet.getStarId() != null
        && oldState.starIsVisible(fleet.getStarId())) {
        events.add(new FleetArrivedEvent(fleet.getId(), fleet.getStarId()));
      }
    }

    // Handle fleets that were destroyed
    for (Fleet fleet : oldState.getAllFleets()) {
      if (newState.getFleet(fleet.getId()) == null && !fleet.getDestinations().isEmpty()) {
        int destinationStar = fleet.getDestinations().get(0);

        if (oldState.starIsVisible(destinationStar) && newState.starIsVisible(destinationStar)) {
          events.add(new FleetArrivedEvent(fleet.getId(), destinationStar));
        }
      }
    }

    return events;
  }

  @Override
  public Class<FleetArrivedEvent> getEventType() {
    return FleetArrivedEvent.class;
  }
}
