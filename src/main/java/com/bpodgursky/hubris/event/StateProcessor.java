package com.bpodgursky.hubris.event;

import com.bpodgursky.hubris.event_factory.CashChangeFactory;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;

public class StateProcessor {

  public static final List<EventFactory> DEFAULT_FACTORIES = Lists.<EventFactory>newArrayList(
      new CashChangeFactory()
  );

  private final List<EventFactory> eventFactories = Lists.newArrayList(DEFAULT_FACTORIES);
  private final Multimap<Class, EventListener> listeners = HashMultimap.create();

  private GameState currentState;

  public StateProcessor(GameState initialState) {
    this.currentState = initialState;
  }

  public void addEventFactory(EventFactory factory) {
    this.eventFactories.add(factory);
  }

  public void addEventListener(EventListener listener) {
    listeners.put(listener.getEventType(), listener);
  }

  public void update(GameState newState) {

    Multimap<Class, Object> events = HashMultimap.create();
    for (EventFactory eventFactory : eventFactories) {
      events.putAll(eventFactory.getEventType(), eventFactory.getEvents(currentState, newState));
    }

    for (Class aClass : events.keySet()) {
      Collection<EventListener> eventListener = listeners.get(aClass);

      for (EventListener listener : eventListener) {
        for (Object o : events.get(aClass)) {
          listener.process(o, newState);
        }
      }
    }

    currentState = newState;
  }


}