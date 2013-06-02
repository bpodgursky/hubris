package com.bpodgursky.hubris.plan.orders;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;

import java.util.Collections;
import java.util.Set;

public class BalanceFleets extends Order {

  public final FleetDistPlan plan;
  private final String starName;
  private final String fleetName;

  public BalanceFleets(String starName, String fleetName, FleetDistPlan plan) {
    this(starName, fleetName, plan, Collections.<Order>emptySet());
  }

  public BalanceFleets(String starName, String fleetName, FleetDistPlan plan, Set<Order> prereqs) {
    super(prereqs);

    this.starName = starName;
    this.fleetName = fleetName;
    this.plan = plan;
  }

  public String getStarName() {
    return starName;
  }

  public String getFleetName() {
    return fleetName;
  }

  @Override
  public ExecuteResult execute(GameState state, CommandFactory factory, GameConnection connection) throws Exception {

    Fleet fleet = state.getFleet(fleetName);
    Star star = state.getStar(starName, false);

    if(fleet == null || !star.getFleets().contains(fleet.getId())){
      return ExecuteResult.INVALID;
    }

    connection.submit(plan.makeTransfer(state, fleetName, star.getId(), factory));

    return ExecuteResult.EXECUTED;
  }

  @Override
  public boolean isComplete(GameState state) {
    return true;
  }

  @Override
  public String toString() {
    return "BalanceFleets{" +
        "plan=" + plan +
        ", starName='" + starName + '\'' +
        ", fleetName='" + fleetName + '\'' +
        '}';
  }
}
