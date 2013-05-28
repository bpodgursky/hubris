package com.bpodgursky.hubris.event_factory;

import com.bpodgursky.hubris.event.EventFactory;
import com.bpodgursky.hubris.events.CashChangeEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class CashChangeFactory implements EventFactory<CashChangeEvent> {

  @Override
  public List<CashChangeEvent> getEvents(GameState newState) {
    List<CashChangeEvent> events = Lists.newArrayList();
    for (Map.Entry<Integer, Player> entry : newState.previousState().getPlayers().entrySet()) {
      int oldCash = entry.getValue().getCash();
      int newCash = newState.getPlayers().get(entry.getKey()).getCash();

      if(oldCash != newCash){
        events.add(new CashChangeEvent(entry.getKey(), newCash - oldCash));
      }
    }

    return events;
  }

  @Override
  public Class<CashChangeEvent> getEventType() {
    return CashChangeEvent.class;
  }
}