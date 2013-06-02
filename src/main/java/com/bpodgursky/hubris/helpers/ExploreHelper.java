package com.bpodgursky.hubris.helpers;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.plan.orders.BalanceFleets;
import com.bpodgursky.hubris.plan.orders.FleetDistPlan;
import com.bpodgursky.hubris.plan.orders.MoveFleet;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExploreHelper {
  private static final Logger LOG = LoggerFactory.getLogger(ExploreHelper.class);

  public static Collection<Order> planExplore(List<Fleet> fleets, GameState state, double maxDistance){
    LOG.info("Planning exploration for fleets: "+fleets);

    Player player = state.getPlayer(state.getPlayerId());

    Set<Integer> queuedStars = Sets.newHashSet();

    Map<String, TailOrder> fleetToLastOrder = Maps.newHashMap();
    for(Fleet fleet: fleets){
      Star currentStar = state.getStar(fleet.getStarId(), false);
      List<Star> stars = HubrisUtil.getStarsInRange(state, currentStar, player.getRange());

      Star target = getHighestValue(stars, queuedStars, player.getId());
      if(target != null){
        queuedStars.add(target.getId());
        fleetToLastOrder.put(fleet.getName(), new TailOrder(take(fleet.getName(), currentStar, target), currentStar.distanceFrom(target)));
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
            newTails.put(fleetName, new TailOrder(take(fleetName, star, target, tail.getValue().order), cumCost));
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

  private static MoveFleet take(String fleetName, Star from, Star to, Order ... previous){
    Order balanceCurrent = new BalanceFleets(from.getName(), fleetName, FleetDistPlan.leaveOnStar(1), Sets.newHashSet(previous));
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

  public static Star getHighestValue(List<Star> stars, Set<Integer> skip, Integer skipPlayer){

    int highestResource = Integer.MIN_VALUE;
    Star selected = null;

    for(Star s: stars){

      if(skip.contains(s.getId())){
        continue;
      }

      if(s.getPlayerNumber() == null || s.getPlayerNumber().intValue() == skipPlayer){
        continue;
      }

      int resources = getResources(s);
      if(resources > highestResource){
        highestResource = resources;
        selected = s;
      }
    }

    return selected;
  }

  private static int getResources(Star star){
    if(star.getResources() == null){
      return 20;
    }

    return star.getResources();
  }
}
