package com.bpodgursky.hubris.listeners;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.FleetDestinationChangedEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;

/**
 * Detects when an enemy points a fleet at you and does what it can to defend the target star
 */
public class ReactiveDefense implements EventListener<FleetDestinationChangedEvent> {
  @Override
  public Collection<GameRequest> process(Collection<FleetDestinationChangedEvent> events, GameState currentState, CommandFactory commandFactory) throws Exception {
    return null;
    //currentState.getFleet(0).get
  }

  @Override
  public Class<FleetDestinationChangedEvent> getEventType() {
    return FleetDestinationChangedEvent.class;
  }
}
