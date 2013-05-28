package com.bpodgursky.hubris.listeners;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.CashChangeEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;
import java.util.Collections;

public class PrintNewCash implements EventListener<CashChangeEvent> {
  @Override
  public Collection<GameRequest> process(Collection<CashChangeEvent> events, GameState currentState, CommandFactory factory) {
    for(CashChangeEvent event: events){
      System.out.println();
      System.out.println("Cache change event:");
      System.out.println("Player: "+event.getPlayer());
      System.out.println("Change: "+event.getDifference());
    }

    return Collections.emptyList();
  }

  @Override
  public Class<CashChangeEvent> getEventType() {
    return CashChangeEvent.class;
  }
}
