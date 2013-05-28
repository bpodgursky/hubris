package com.bpodgursky.hubris.listeners;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.CashChangeEvent;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.helpers.SpendHelper;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;
import java.util.Collections;

public class SpendOnIncomeListener implements EventListener<CashChangeEvent> {

  private final int escrow;
  private final double econWeight;
  private final double indWeight;
  private final double sciWeight;

  public SpendOnIncomeListener(int escrow, double econWeight, double indWeight, double sciWeight){
    this.escrow = escrow;
    this.econWeight = econWeight;
    this.indWeight = indWeight;
    this.sciWeight = sciWeight;
  }

  @Override
  public Collection<GameRequest> process(Collection<CashChangeEvent> events, GameState currentState, CommandFactory commandFactory) throws Exception {
    for(CashChangeEvent event: events){
      if(event.getPlayer() == currentState.getPlayerId() && event.getDifference() > 0){
        return SpendHelper.planSpend(currentState, econWeight, indWeight, sciWeight, escrow, commandFactory);
      }
    }

    return Collections.emptyList();
  }

  @Override
  public Class<CashChangeEvent> getEventType() {
    return CashChangeEvent.class;
  }
}
