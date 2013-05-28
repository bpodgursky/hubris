package com.bpodgursky.hubris.listeners.test;

import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.StarUpgradedEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.List;

public class PrintUpgrade implements EventListener<StarUpgradedEvent> {

  @Override
  public void process(List<StarUpgradedEvent> events, GameState currentState) {

    for(StarUpgradedEvent event: events){
      System.out.println();
      System.out.println("star upgraded!");
      System.out.println("id: "+event.getStarId());
      System.out.println("ind change: "+event.getIndustryChange());
      System.out.println("econ change: "+event.getEconChange());
      System.out.println("sci change: "+event.getScienceChange());
    }

  }

  @Override
  public Class<StarUpgradedEvent> getEventType() {
    return StarUpgradedEvent.class;
  }
}
