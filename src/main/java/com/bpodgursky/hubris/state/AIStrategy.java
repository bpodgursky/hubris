package com.bpodgursky.hubris.state;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.plan.orders.FleetDistStrat;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AIStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(AIStrategy.class);

  //  TODO parameterize or something?

  public static final double REINFORCE_FRACTION = 0.5;
  public static final double PICKUP_FRACTION = 0.5;

  public static enum FleetState {
    PICKUP, REINFORCE
  }

  public final Map<String, FleetState> fleetToState;

  public AIStrategy(){
    this(Maps.<String, FleetState>newHashMap());
  }

  protected AIStrategy(Map<String, FleetState> fleetToState){
    this.fleetToState = fleetToState;
  }

  public AIStrategy update(GameState state){

    List<Fleet> friendlyFleets = HubrisUtil.getFriendlyFleets(state, state.getPlayerId());
    Iterator<Fleet> iter = friendlyFleets.iterator();

    Map<String, FleetState> newFleetToState = Maps.newHashMap();

    int pickup = 0;
    while(iter.hasNext() && pickup++ < PICKUP_FRACTION * friendlyFleets.size()){
      newFleetToState.put(iter.next().getName(), FleetState.PICKUP);
    }

    while(iter.hasNext()){
      newFleetToState.put(iter.next().getName(), FleetState.REINFORCE);
    }

    LOG.info("New fleet to states: "+newFleetToState);

    return new AIStrategy(newFleetToState);
  }

  public FleetDistStrat getFleetDistStrat(String fleetName){
    return FleetDistStrat.defensiveDist();
  }

  public double getExpandWeight(String fleetName){
    FleetState state = fleetToState.get(fleetName);
    if(state == null){
      return 1.0;
    }

    switch(state){
      case PICKUP:
        return 1.0;
      case REINFORCE:
        return 1.0;
      default:
        throw new RuntimeException();

    }
  }

  public double getReinforceWeight(String fleetName){
    FleetState state = fleetToState.get(fleetName);
    if(state == null){
      return 1.0;
    }

    switch(state){
      case PICKUP:
        return 0.1;
      case REINFORCE:
        return 1.0;
      default:
        throw new RuntimeException();

    }
  }

  public double getPickupWeight(String fleetName){
    FleetState state = fleetToState.get(fleetName);
    if(state == null){
      return 1.0;
    }

    switch(state){
      case PICKUP:
        return 1.0;
      case REINFORCE:
        return 0.1;
      default:
        throw new RuntimeException();
    }
  }

}
