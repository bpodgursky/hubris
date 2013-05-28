package com.bpodgursky.hubris.listeners.test;

import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.ResearchChangeEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.List;

public class PrintResearchChange implements EventListener<ResearchChangeEvent> {

  @Override
  public void process(List<ResearchChangeEvent> events, GameState currentState) {

    for(ResearchChangeEvent event: events){
      System.out.println();
      System.out.println(event.getPlayerId());
      System.out.println(event.getOldResearch());
      System.out.println(event.getNewResearch());

    }
  }

  @Override
  public Class<ResearchChangeEvent> getEventType() {
    return ResearchChangeEvent.class;
  }
}
