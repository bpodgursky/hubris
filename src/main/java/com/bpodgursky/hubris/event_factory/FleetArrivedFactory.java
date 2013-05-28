package com.bpodgursky.hubris.event_factory;

import com.bpodgursky.hubris.event.EventFactory;
import com.bpodgursky.hubris.events.FleetArrivedEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.List;

public class FleetArrivedFactory implements EventFactory<FleetArrivedEvent> {
  @Override
  public List<FleetArrivedEvent> getEvents(GameState newState) {
    return null;
  }

  @Override
  public Class<FleetArrivedEvent> getEventType() {
    return FleetArrivedEvent.class;
  }
}
