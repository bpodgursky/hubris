package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.FleetArrivedEvent;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collections;
import java.util.List;

public class FleetArrivedFactory implements EventFactory<FleetArrivedEvent> {
  @Override
  public List<FleetArrivedEvent> getEvents(GameState newState) {
    GameState oldState = newState.previousState();

    for (Fleet fleet : newState.getAllFleets()) {
      // don't fire fleet created events
      if (oldState.getFleet(fleet.getId()) != null
        && fleet.getStarId() != null
        && oldState.starIsVisible(fleet.getStarId())) {
        return Collections.singletonList(new FleetArrivedEvent(fleet.getId()));
      }
    }

    return Collections.emptyList();
  }

  @Override
  public Class<FleetArrivedEvent> getEventType() {
    return FleetArrivedEvent.class;
  }
}
