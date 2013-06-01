package com.bpodgursky.hubris;

import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.bpodgursky.hubris.universe.TechType;
import com.bpodgursky.hubris.util.BattleOutcome;
import com.google.common.collect.Lists;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

import java.util.List;

public class HubrisUtil {
  /**
   * To convert from Euclidean distance to lightyears, it looks like we divide by 10000.
   */
  public static final int LY_TO_DISTANCE_CONVERSION_FACTOR = 10000;

  /**
   * Number of research points gained each hour per level of science a player has.
   */
  public static final int SCIENCE_TO_RESEARCH_PER_HOUR = 6;

  public static final int DEFENDER_WEAPONS_BONUS = 1;

  public static final int INDUSTRY_PRODUCTION_PERIOD = 12;

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
    return state1.gameData.getMid();
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

  public static BattleOutcome getBattleOutcome(int defenderWeapons, int attackerWeapons, int defenderShips, int attackerShips) {
    defenderWeapons += DEFENDER_WEAPONS_BONUS;

    while (attackerShips > 0 && defenderShips > 0) {
      attackerShips -= defenderWeapons;
      if (attackerShips <= 0) {
        break;
      }
      defenderShips -= attackerWeapons;
    }

    if (defenderShips > 0) {
      return new BattleOutcome(true, defenderShips);
    }
    else {
      return new BattleOutcome(false, attackerShips);
    }
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

  public static void startLogging(){
    BasicConfigurator.resetConfiguration();
    final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n"), ConsoleAppender.SYSTEM_ERR);
    consoleAppender.setFollow(true);
    BasicConfigurator.configure(consoleAppender);
  }
}
