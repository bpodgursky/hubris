package com.bpodgursky.hubris.metric;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.universe.Coordinate;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.collect.Lists;

import java.util.List;

public class SimpleShipProximityMetric {

  public static double evaluate(GameState state){

    double value = 0;

    List<Coordinate> enemyCoordinates = Lists.newArrayList();

    for(Fleet fleet: HubrisUtil.getEnemyFleets(state, state.getPlayerId())){
      enemyCoordinates.add(fleet.getCoords());
    }

    for(Star star: HubrisUtil.getEnemyStars(state, state.getPlayerId())){
      enemyCoordinates.add(star.getCoords());
    }

    for(Fleet fleet: HubrisUtil.getFriendlyFleets(state, state.getPlayerId())){
      double v = fleet.getShips() / (1.0 + getMinDistanceLightYears(fleet.getCoords(), enemyCoordinates));
      value += v;
    }

    for(Star star:  HubrisUtil.getFriendlyStars(state, state.getPlayerId())){
      double v = star.getShips() / (1.0 + getMinDistanceLightYears(star.getCoords(), enemyCoordinates));
      value += v;
    }

    return value;

  }

  private static double getMinDistanceLightYears(Coordinate coord, List<Coordinate> targets){

    double min = Double.MAX_VALUE;

    for(Coordinate target: targets){
      min = Math.min( HubrisUtil.getDistanceInLightYears(coord, target), min);
    }

    return min;
  }

}
