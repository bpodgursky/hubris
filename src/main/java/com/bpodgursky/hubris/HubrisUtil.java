package com.bpodgursky.hubris;

import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.collect.Lists;

import java.util.List;

public class HubrisUtil {
  /**
   * To convert from Euclidean distance to lightyears, it looks like we divide by 1000.
   */
  public static int LY_TO_DISTANCE_CONVERSION_FACTOR = 1000;

  /**
   * For some really brilliant reason, <i>you</i> have to tell the game what your player ID is, even though the
   * game is already aware of what your player ID is. This mimics the actual client to get the player ID given
   * your username.
   *
   * @param connection
   * @param npUsername
   * @param gameId
   * @return
   * @throws Exception
   */
  public static int getPlayerNumber(GameConnection connection, String npUsername, long gameId) throws Exception {
    GameState state1 = connection.getState(null, new GetState(0, npUsername, gameId));
    return Integer.parseInt(state1.gameData.getMid());
  }

  /**
   *
   * @param state
   * @param star
   * @param lightYears
   */
  public static List<Star> getStarsInRange(GameState state, Star star, double lightYears) {
    return getStarsInRange(state, star.getX(), star.getY(), lightYears);
  }

  /**
   *
   * @param state
   * @param fleet
   * @param lightYears
   * @return
   */
  public static List<Star> getStarsInRange(GameState state, Fleet fleet, double lightYears) {
    return getStarsInRange(state, fleet.getX(), fleet.getY(), lightYears);
  }

  /**
   *
   * @param state
   * @param star
   * @param lightYears
   * @return
   */
  private static List<Fleet> getFleetsInRange(GameState state, Star star, double lightYears) {
    List<Fleet> fleetsInRange = Lists.newArrayList();

    for (Fleet candidate : state.getAllFleets()) {
      if (getDistanceInLightYears(star.getX(), star.getY(), candidate.getX(), candidate.getY()) <= lightYears) {
        fleetsInRange.add(candidate);
      }
    }

    return fleetsInRange;
  }

  private static List<Star> getStarsInRange(GameState state, int x, int y, double lightYears) {
    List<Star> starsInRange = Lists.newArrayList();

    for (Star candidate : state.getAllStars(false)) {
      if (getDistanceInLightYears(x, y, candidate.getX(), candidate.getY()) <= lightYears) {
        starsInRange.add(candidate);
      }
    }

    return starsInRange;
  }

  /**
   *
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @return
   */
  public static double getDistanceInLightYears(int x1, int y1, int x2, int y2) {
    return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2)) / LY_TO_DISTANCE_CONVERSION_FACTOR;
  }
}
