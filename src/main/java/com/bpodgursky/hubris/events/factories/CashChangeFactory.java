package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.CashChangeEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class CashChangeFactory implements EventFactory<CashChangeEvent> {

  @Override
  public List<CashChangeEvent> getEvents(GameState newState, GameState previousState) {
    List<CashChangeEvent> events = Lists.newArrayList();
    for (Player player : previousState.getAllPlayers()) {
      int oldCash = player.getCash();
      int newCash = newState.getPlayers().get(player.getId()).getCash();

      if(oldCash != newCash){
        events.add(new CashChangeEvent(player.getId(), newCash - oldCash));
      }
    }

    return events;
  }

  @Override
  public Class<CashChangeEvent> getEventType() {
    return CashChangeEvent.class;
  }
}
