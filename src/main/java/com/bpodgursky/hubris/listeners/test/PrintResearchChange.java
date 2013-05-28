package com.bpodgursky.hubris.listeners.test;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.ResearchChangeEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PrintResearchChange implements EventListener<ResearchChangeEvent> {

  @Override
  public List<GameRequest> process(Collection<ResearchChangeEvent> events, GameState currentState, CommandFactory commandFactory) {

    for(ResearchChangeEvent event: events){
      System.out.println();
      System.out.println(event.getPlayerId());
      System.out.println(event.getOldResearch());
      System.out.println(event.getNewResearch());

    }

    return Collections.emptyList();
  }

  @Override
  public Class<ResearchChangeEvent> getEventType() {
    return ResearchChangeEvent.class;
  }
}
