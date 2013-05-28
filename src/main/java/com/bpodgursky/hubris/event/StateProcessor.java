package com.bpodgursky.hubris.event;

import com.bpodgursky.hubris.event_factory.StarUpgradeFactory;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.factories.CashChangeFactory;
import com.bpodgursky.hubris.events.factories.EventFactory;
import com.bpodgursky.hubris.events.factories.FleetArrivedFactory;
import com.bpodgursky.hubris.events.factories.FleetCreatedFactory;
import com.bpodgursky.hubris.events.factories.FleetDestinationChangedFactory;
import com.bpodgursky.hubris.events.factories.FleetSpottedFactory;
import com.bpodgursky.hubris.events.factories.GainTechFactory;
import com.bpodgursky.hubris.events.factories.StarRevealedFactory;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;

public class StateProcessor {

  public static final List<EventFactory> DEFAULT_FACTORIES = Lists.<EventFactory>newArrayList(
      new CashChangeFactory(),
      new FleetArrivedFactory(),
      new FleetSpottedFactory(),
      new FleetCreatedFactory(),
      new StarUpgradeFactory(),
      new FleetDestinationChangedFactory(),
      new StarRevealedFactory(),
      new GainTechFactory()
  );

  private final List<EventFactory> eventFactories = Lists.newArrayList(DEFAULT_FACTORIES);
  private final Multimap<Class, EventListener> listeners = HashMultimap.create();

  public void addEventFactory(EventFactory factory) {
    this.eventFactories.add(factory);
  }

  public void addEventListener(EventListener listener) {
    listeners.put(listener.getEventType(), listener);
  }

  public void update(GameState newState) {

    //  skip the first time
    if (newState.previousState() == null) {
      return;
    }

    Multimap<Class, Object> events = HashMultimap.create();
    for (EventFactory eventFactory : eventFactories) {
      events.putAll(eventFactory.getEventType(), eventFactory.getEvents(newState));
    }

    for (Class aClass : events.keySet()) {
      Collection<EventListener> eventListener = listeners.get(aClass);

      for (EventListener listener : eventListener) {
        listener.process(Lists.newArrayList(events.get(aClass)), newState);
      }
    }
  }


}
