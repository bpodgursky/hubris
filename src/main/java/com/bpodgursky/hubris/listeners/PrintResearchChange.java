package com.bpodgursky.hubris.listeners;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.ResearchChangeEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;
import java.util.Collections;

public class PrintResearchChange implements EventListener<ResearchChangeEvent> {

  @Override
  public Collection<GameRequest> process(Collection<ResearchChangeEvent> events, GameState currentState, CommandFactory commandFactory) {

    for(ResearchChangeEvent event: events){
      System.out.println();
      System.out.println("Research change event:");
      System.out.println("Player: "+event.getPlayerId());
      System.out.println("Old research: "+event.getOldResearch());
      System.out.println("New research: "+event.getNewResearch());
    }

    return Collections.emptyList();
  }

  @Override
  public Class<ResearchChangeEvent> getEventType() {
    return ResearchChangeEvent.class;
  }
}
