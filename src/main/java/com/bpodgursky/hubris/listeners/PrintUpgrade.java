package com.bpodgursky.hubris.listeners;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.StarUpgradedEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;
import java.util.Collections;

public class PrintUpgrade implements EventListener<StarUpgradedEvent> {

  @Override
  public Collection<GameRequest> process(Collection<StarUpgradedEvent> events, GameState currentState, CommandFactory factory) {

    for(StarUpgradedEvent event: events){
      System.out.println();
      System.out.println("Star upgraded:");
      System.out.println("id: "+event.getStarId());
      System.out.println("Name: "+currentState.getStar(event.getStarId(), false).getName());
      System.out.println("Industry change: "+event.getIndustryChange());
      System.out.println("Economy change: "+event.getEconChange());
      System.out.println("Science change: "+event.getScienceChange());
    }

    return Collections.emptyList();
  }

  @Override
  public Class<StarUpgradedEvent> getEventType() {
    return StarUpgradedEvent.class;
  }
}
