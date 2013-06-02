package com.bpodgursky.hubris.event;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.event_factory.StarUpgradeFactory;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.factories.*;
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
import com.sun.jdi.event.EventSet;

import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class StateProcessor {

  public static final List<EventFactory> DEFAULT_FACTORIES = Lists.<EventFactory>newArrayList(
      new CashChangeFactory(),
      new FleetArrivedFactory(),
      new FleetSpottedFactory(),
      new FleetCreatedFactory(),
      new StarUpgradeFactory(),
      new FleetDestinationChangedFactory(),
      new StarRevealedFactory(),
      new ChangeResearchFactory(),
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

  private final GameConnection connection;
  private final CommandFactory commandFactory;

  public StateProcessor(GameConnection connection, CommandFactory factory){
    this.connection = connection;
    this.commandFactory = factory;
  }

  public GameState update(GameState newState) throws Exception {

    //  skip the first time
    if (newState.previousState() == null) {
      return newState;
    }

    Queue<EventSet> toProcess = Lists.newLinkedList();

    //  produce events for the initial new state
    toProcess.addAll(getNewEvents(newState));

    //  process until something submits a command
    while(!toProcess.isEmpty()){
      EventSet eventSet = toProcess.poll();
      Collection<EventListener> listenersForType = listeners.get(eventSet.getEventClass());

      for(EventListener listener: listenersForType){
        if (!eventSet.getEvents().isEmpty()) {
          Collection< GameRequest > requests = listener.process(eventSet.getEvents(), newState, commandFactory);

          //  if something submits orders, we keep update the state, and continue processing events
          if(!requests.isEmpty()){

            //  execute each of the relevant events
            for(GameRequest request: requests){
              connection.submit(request);
            }

            newState = connection.getState(newState, commandFactory.getState());
            toProcess.addAll(getNewEvents(newState));
          }
        }
      }
    }

    return newState;
  }

  private Collection<EventSet> getNewEvents(GameState state){
    List<EventSet> toProcess = Lists.newArrayList();
    for (EventFactory<?> eventFactory : eventFactories) {
      toProcess.add(new EventSet(eventFactory.getEvents(state), eventFactory.getEventType()));
    }
    return toProcess;
  }

  private static class EventSet {

    private final Class eventClass;
    private final Collection events;

    private EventSet(Collection events, Class eventClass) {
      this.events = events;
      this.eventClass = eventClass;
    }

    private Class getEventClass() {
      return eventClass;
    }

    private Collection getEvents() {
      return events;
    }
  }
}
