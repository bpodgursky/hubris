package com.bpodgursky.hubris.helpers;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.functions.NameFromStar;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.plan.orders.BalanceFleets;
import com.bpodgursky.hubris.plan.orders.FleetDistStrat;
import com.bpodgursky.hubris.plan.orders.MoveFleet;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExploreHelper {
  private static final Logger LOG = LoggerFactory.getLogger(ExploreHelper.class);

  public static Collection<Order> planExplore(List<Fleet> fleets, GameState state, double maxDistance, FleetDistStrat distStrat){
    LOG.info("Planning exploration for fleets: "+fleets);

    Player player = state.getPlayer(state.getPlayerId());

    Set<Integer> queuedStars = Sets.newHashSet();

    Map<String, TailOrder> fleetToLastOrder = Maps.newHashMap();
    for(Fleet fleet: fleets){
      Star currentStar = state.getStar(fleet.getStarId(), false);
      List<Star> stars = HubrisUtil.getConquerableStarsInRange(state,
          currentStar.getCoords(),
          currentStar.getShipsIncludingFleets(state),
          player.getRange()
      );

      LOG.info("Fleet "+fleet.getName()+" has conquerable stars in range: "+ Collections2.transform(stars, new NameFromStar()));

      Star target = getHighestValue(stars, queuedStars, player.getId());

      //  TODO diff strat depending on whether it's moving towards or away from the enemy threat vector
      //  TODO also, if you have more ships, vector should count for more
      if(target != null){
        queuedStars.add(target.getId());
        fleetToLastOrder.put(fleet.getName(), new TailOrder(take(fleet.getName(), currentStar, target, distStrat), currentStar.distanceFrom(target)));
      }
    }

    Map<String, TailOrder> finalTails = Maps.newHashMap();

    while(!fleetToLastOrder.isEmpty()){

      Map<String, TailOrder> newTails = Maps.newHashMap();
      for(Map.Entry<String, TailOrder> tail: fleetToLastOrder.entrySet()){

        String starName = tail.getValue().order.getToStar();
        String fleetName = tail.getValue().order.getFleetName();
        Star star = state.getStar(starName, false);

        List<Star> stars = HubrisUtil.getStarsInRange(state, star, player.getRange());
        Star target = getHighestValue(stars, queuedStars, player.getId());

        if(target != null){
          double cumCost = tail.getValue().cost + star.distanceFrom(target);
          if(cumCost <= maxDistance){
            queuedStars.add(target.getId());
            newTails.put(fleetName, new TailOrder(take(fleetName, star, target, distStrat, tail.getValue().order), cumCost));
          }else{
            finalTails.put(fleetName, tail.getValue());
          }
        }else{
          finalTails.put(fleetName, tail.getValue());
        }
      }
      fleetToLastOrder = newTails;
    }

    Set<Order> lastOrders = Sets.newHashSet();
    for(TailOrder order: finalTails.values()){
      lastOrders.add(order.order);
    }

    return lastOrders;
  }

  private static MoveFleet take(String fleetName, Star from, Star to, FleetDistStrat distPlan, Order ... previous){
    Order balanceCurrent = new BalanceFleets(from.getName(), fleetName, distPlan, Sets.newHashSet(previous));
    return new MoveFleet(fleetName, from.getName(), to.getName(), Sets.newHashSet(balanceCurrent));
  }

  public static class TailOrder {

    private final double cost;
    private final MoveFleet order;

    public TailOrder(MoveFleet order, double cost) {
      this.order = order;
      this.cost = cost;
    }

  }



  public static Star getHighestValue(List<Star> stars, Set<Integer> skip, Integer currentPlayer){

    double highestValue = Double.NEGATIVE_INFINITY;
    Star selected = null;

    for(Star s: stars){

      if(skip.contains(s.getId())){
        continue;
      }

      double value = getValue(s, currentPlayer);

      if(value > highestValue){
        highestValue = value;
        selected = s;
      }
    }

    return selected;
  }

  private static double getValue(Star star, int currentPlayer){
    if(star.getResources() == null){
      return 20;
    }

    if(star.getPlayerNumber() == currentPlayer){
      return -1000 + star.getShips();
    }

    return star.getResources();
  }
}
