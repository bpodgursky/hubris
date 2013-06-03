package com.bpodgursky.hubris.helpers;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.functions.NameFromStar;
import com.bpodgursky.hubris.metric.DangerMetric;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.plan.orders.BalanceFleets;
import com.bpodgursky.hubris.plan.orders.MoveFleet;
import com.bpodgursky.hubris.state.AIStrategy;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExploreHelper {
  private static final Logger LOG = LoggerFactory.getLogger(ExploreHelper.class);

  //  TODO should be params
  //  TODO keep these parameters per fleet -- some will focus on reinforcing, some on conquering, etc
//  private static final double EXPAND_WEIGHT = 1.0;
//  private static final double REINFORCE_WEIGHT = 1.0;
//  private static final double PICKUP_WEIGHT = 1.0;

  public static Collection<Order> planExplore(List<Fleet> fleets, GameState state, AIStrategy aiState, double maxDistance) {
    LOG.info("Planning exploration for fleets: " + fleets);

    Player player = state.getPlayer(state.getPlayerId());

    Set<Integer> queuedStars = Sets.newHashSet();

    Map<String, TailOrder> fleetToLastOrder = Maps.newHashMap();
    for (Fleet fleet : fleets) {
      Star currentStar = state.getStar(fleet.getStarId(), false);
      List<Star> stars = HubrisUtil.getConquerableStarsInRange(state,
          currentStar.getCoords(),
          currentStar.getShipsIncludingFleets(state),
          player.getRange()
      );

      LOG.info("Fleet " + fleet.getName() + " has conquerable stars in range: " + Collections2.transform(stars, new NameFromStar()));

      Star target = getHighestValue(state, aiState, fleet, stars, queuedStars, player);

      if (target != null) {
        queuedStars.add(target.getId());
        fleetToLastOrder.put(fleet.getName(), new TailOrder(take(fleet.getName(), currentStar, target, aiState), currentStar.distanceFrom(target)));
      }
    }

    Map<String, TailOrder> finalTails = Maps.newHashMap();

    while (!fleetToLastOrder.isEmpty()) {

      Map<String, TailOrder> newTails = Maps.newHashMap();
      for (Map.Entry<String, TailOrder> tail : fleetToLastOrder.entrySet()) {

        String starName = tail.getValue().order.getStarName();
        String fleetName = tail.getValue().order.getFleetName();
        Star star = state.getStar(starName, false);

        Fleet fleet = state.getFleet(fleetName);

        List<Star> stars = HubrisUtil.getStarsInRange(state, star, player.getRange());
        Star target = getHighestValue(state, aiState, fleet, stars, queuedStars, player);

        if (target != null) {
          double cumCost = tail.getValue().cost + star.distanceFrom(target);
          if (cumCost <= maxDistance) {
            queuedStars.add(target.getId());
            newTails.put(fleetName, new TailOrder(take(fleetName, star, target, aiState, tail.getValue().order), cumCost));
          } else {
            finalTails.put(fleetName, tail.getValue());
          }
        } else {
          finalTails.put(fleetName, tail.getValue());
        }
      }
      fleetToLastOrder = newTails;
    }

    Set<Order> lastOrders = Sets.newHashSet();
    for (TailOrder order : finalTails.values()) {
      lastOrders.add(order.order);
    }

    return lastOrders;
  }

  private static BalanceFleets take(String fleetName, Star from, Star to, AIStrategy ai, Order... previous) {
    Order balanceCurrent = new BalanceFleets(from.getName(), fleetName, ai.getFleetDistStrat(fleetName), Sets.newHashSet(previous));
    MoveFleet move = new MoveFleet(fleetName, from.getName(), to.getName(), Sets.newHashSet(balanceCurrent));
    return new BalanceFleets(to.getName(), fleetName, ai.getFleetDistStrat(fleetName), Sets.<Order>newHashSet(move));
  }

  public static class TailOrder {

    private final double cost;
    private final BalanceFleets order;

    public TailOrder(BalanceFleets order, double cost) {
      this.order = order;
      this.cost = cost;
    }

  }


  public static Star getHighestValue(GameState state, AIStrategy ai, Fleet fleet, List<Star> stars, Set<Integer> skip, Player currentPlayer) {

    double highestValue = Double.NEGATIVE_INFINITY;
    Star selected = null;

    for (Star s : stars) {

      if (skip.contains(s.getId())) {
        continue;
      }

      double value = getTargetValue(state, ai, fleet, s, currentPlayer);

      if (value > highestValue) {
        highestValue = value;
        selected = s;
      }
    }

    return selected;
  }

  private static int estimateResources(Star star) {
    if (star.getResources() == null) {
      return 20;
    }

    return star.getResources();
  }

  private static double getTargetValue(GameState state, AIStrategy ai, Fleet fleet, Star star, Player currentPlayer) {
    int resources = estimateResources(star);
    String fleetName = fleet.getName();

    //  valuable if it isn't yours and there are resources on it
    if (star.getPlayerNumber() != currentPlayer.getId()) {
      return ai.getExpandWeight(fleetName) * ( (resources*1.0) / HubrisUtil.totalControlledResources(state));
    }

    double currentCombatValue = DangerMetric.getCombatValue(state, fleet.getCoords());
    double targetCombatValue = DangerMetric.getCombatValue(state, star.getCoords());

    double fleetPercentOfShips = fleet.getShips() / (currentPlayer.getAllFleets() * 1.0);
    double targetPercentOfShips = star.getShips() / (currentPlayer.getAllFleets() * 1.0);

    double bestCombatValue = DangerMetric.getBestCombatValue(state);

    double result =
        //  valuable if this fleet has a bunch of ships on it and target star is closer to the enemy
        ai.getReinforceWeight(fleetName) * (fleetPercentOfShips * ((targetCombatValue - currentCombatValue)/bestCombatValue)) +
        //  valuable if you can pick up ships from the star  (more valuable the farther from danger it is)
        ai.getPickupWeight(fleetName) * (targetPercentOfShips * ((bestCombatValue - targetCombatValue)/bestCombatValue));

    return result;

  }
}