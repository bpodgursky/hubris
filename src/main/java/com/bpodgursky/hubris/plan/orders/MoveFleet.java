package com.bpodgursky.hubris.plan.orders;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.command.SetWaypoint;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;

import java.util.Set;

public class MoveFleet extends Order {

  private final String fleetName;
  private final String fromStar;
  private final String toStar;

  public MoveFleet(String fleetName, String fromStar, String toStar, Set<Order> prereqs){
    super(prereqs);

    this.fleetName = fleetName;
    this.fromStar = fromStar;
    this.toStar = toStar;
  }

  @Override
  public boolean isComplete(GameState state) {

    Fleet fleet = state.getFleet(fleetName);
    Star starEnd = state.getStar(toStar, false);

    if(fleet != null && fleet.getDestinations().contains(starEnd.getId())){
      return false;
    }

    return true;
  }

  @Override
  public ExecuteResult execute(GameState state, CommandFactory factory, GameConnection connection) throws Exception {

    Player current = state.getPlayer(state.getPlayerId());
    Star from = state.getStar(fromStar, false);
    Star to = state.getStar(toStar, false);

    Fleet fleet = state.getFleet(fleetName);

    if(!from.fleets.contains(fleet.getId())){
      return ExecuteResult.INVALID;
    }

    if(!current.isWithinJumpRange(from, to)){
      return ExecuteResult.INVALID;
    }

    SetWaypoint waypoint = factory.setWaypoint(fleet.getId(), to.getId());

    connection.submit(waypoint);

    return ExecuteResult.EXECUTED;
  }

  @Override
  public String toString() {
    return "MoveFleet{" +
        "fleetName='" + fleetName + '\'' +
        ", fromStar='" + fromStar + '\'' +
        ", toStar='" + toStar + '\'' +
        '}';
  }
}
