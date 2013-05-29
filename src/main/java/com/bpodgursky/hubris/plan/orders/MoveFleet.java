package com.bpodgursky.hubris.plan.orders;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.universe.GameState;

public class MoveFleet implements Order {

  private final int fleet;
  private final int fromStar;
  private final int toStar;

  public MoveFleet(int fleet, int fromStar, int toStar){
    this.fleet = fleet;
    this.fromStar = fromStar;
    this.toStar = toStar;
  }

  @Override
  public boolean isComplete(GameState state) {
    // TODO implement

    return false;
  }

  @Override
  public boolean isValid(GameState state) {

    if(!state.getStar(fromStar, false).fleets.contains(fleet)){
      return false;
    }

    if(!star.is)

  }

  @Override
  public GameRequest makeRequest(GameState state, CommandFactory factory) throws Exception {
    return factory.setWaypoint(fleet, toStar);
  }
}
