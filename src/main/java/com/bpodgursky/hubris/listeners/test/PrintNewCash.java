package com.bpodgursky.hubris.listeners.test;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.CashChangeEvent;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PrintNewCash implements EventListener<CashChangeEvent> {
  @Override
  public List<GameRequest> process(Collection<CashChangeEvent> events, GameState currentState, CommandFactory factory) {
    for(CashChangeEvent event: events){
      System.out.println();
      System.out.println("Hey, cash change!");
      System.out.println("player: "+event.getPlayer());
      System.out.println("difference: "+event.getDifference());
    }

    return Collections.emptyList();
  }

  @Override
  public Class<CashChangeEvent> getEventType() {
    return CashChangeEvent.class;
  }
}
