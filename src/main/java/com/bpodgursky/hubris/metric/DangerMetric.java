package com.bpodgursky.hubris.metric;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.universe.*;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DangerMetric {
  private static final Logger LOG = LoggerFactory.getLogger(DangerMetric.class);

  public static double getBestCombatValue(GameState state){
    double max = Double.NEGATIVE_INFINITY;

    for(Star star:  HubrisUtil.getFriendlyStars(state, state.getPlayerId())){
      max = Math.max(max, getCombatValue(state, star.getCoords()));
    }

    return max;
  }

  public static double getCombatValue(GameState state, Coordinate location){

    double sum = 0.0;

    for (Fleet fleet : HubrisUtil.getEnemyFleets(state, state.getPlayerId())) {
      Integer ships = fleet.getShips();
      if(ships != null){
        double value = getValue(location, fleet.getCoords(), ships);
        sum+= value;
      }
    }

    for (Star star : HubrisUtil.getEnemyStars(state, state.getPlayerId())) {
      if(state.starIsVisible(star.getId())){
        double value = getValue(location, star.getCoords(), star.getShips());
        sum+= value;
      }
    }

    return sum;
  }

  public static Vector getCombatVector(GameState state, Coordinate location){

    List<Vector> allVectors = Lists.newArrayList();

    for (Fleet fleet : HubrisUtil.getEnemyFleets(state, state.getPlayerId())) {
      Integer ships = fleet.getShips();
      if(ships != null){
        Vector vector = getVector(location, fleet.getCoords(), ships);
        allVectors.add(vector);

        LOG.info("Fleet: "+fleet.getName()+" vector: "+vector);
      }
    }

    for (Star star : HubrisUtil.getEnemyStars(state, state.getPlayerId())) {
      if(state.starIsVisible(star.getId())){
        Vector vector = getVector(location, star.getCoords(), star.getShips());
        allVectors.add(vector);

        LOG.info("Star: "+star.getName()+" vector: "+vector);
      }
    }

    return Vector.normalizedSum(allVectors);
  }

  private static Vector getVector(Coordinate location, Coordinate otherCoords, int ships){
    double weight = getValue(location, otherCoords, ships);

    int xDiff = location.getX() -otherCoords.getX();
    int yDiff = location.getY() - otherCoords.getY();

    return new Vector(xDiff * weight, yDiff * weight);
  }

  private static double getValue(Coordinate location, Coordinate otherCoords, int ships){
    return ships/(1.0+HubrisUtil.getDistanceInLightYears(location, otherCoords));
  }
}
