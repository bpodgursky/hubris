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

    for(Fleet fleet: HubrisUtil.getEnemyShips(state, state.getPlayerId())){
      enemyCoordinates.add(new Coordinate(fleet.getX(), fleet.getY()));
    }

    for(Star star: HubrisUtil.getEnemyStars(state, state.getPlayerId())){
      enemyCoordinates.add(new Coordinate(star.getX(), star.getY()));
    }

    for(Fleet fleet: HubrisUtil.getFriendlyShips(state, state.getPlayerId())){
      double v = fleet.getShips() / (1.0 + getMinDistanceLightYears(new Coordinate(fleet.getX(), fleet.getY()), enemyCoordinates));
      value += v;
    }

    for(Star star:  HubrisUtil.getFriendlyStars(state, state.getPlayerId())){
      double v = star.getShips() / (1.0 + getMinDistanceLightYears(new Coordinate(star.getX(), star.getY()), enemyCoordinates));
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
