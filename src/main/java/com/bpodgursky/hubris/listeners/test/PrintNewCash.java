package com.bpodgursky.hubris.listeners.test;

import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.CashChangeEvent;
import com.bpodgursky.hubris.universe.GameState;

public class PrintNewCash implements EventListener<CashChangeEvent> {
  @Override
  public void process(CashChangeEvent event, GameState currentState) {
    System.out.println();
    System.out.println("Hey, cash change!");
    System.out.println("player: "+event.getPlayer());
    System.out.println("difference: "+event.getDifference());
  }

  @Override
  public Class<CashChangeEvent> getEventType() {
    return CashChangeEvent.class;
  }
}
