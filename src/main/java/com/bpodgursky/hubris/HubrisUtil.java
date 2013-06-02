package com.bpodgursky.hubris;

import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.universe.*;
import com.bpodgursky.hubris.util.*;
import com.google.common.collect.Lists;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

import java.util.Collections;
import java.util.Comparator;
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

  public static final int MINUTES_IN_TICK = 10;

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


  public static double getLongestJumpRange(GameState state){

    double range = Double.MIN_VALUE;
    for(Player p: state.getAllPlayers()){
      double jumpRange = p.getRange();
      range = Math.max(jumpRange, range);
    }

    return range;
  }


  public static boolean canReach(Integer fromId, Integer toId, GameState state){

    Star from = state.getStar(fromId, false);
    Star to = state.getStar(toId, false);

    Integer playerId = from.getPlayerNumber();
    if(playerId == -1){
      return false;
    }

    Player player = state.getPlayer(playerId);
    double range = player.getRange();

    return range >= from.distanceFrom(to);
  }

  public static double getDistanceInLightYears(Coordinate coord1, Coordinate coord2) {
    return coord1.distance(coord2) / LY_TO_DISTANCE_CONVERSION_FACTOR;
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


  public static List<Star> getStarsInRange(GameState state, Star star, double lightYears) {
    return getStars(state, new SortByShips(), Lists.<Filter<Star>>newArrayList(
        new StarInRange(star.getCoords(), lightYears)
    ));
  }

  public static List<Star> getFriendlyStars(GameState state, int player){
    return getStars(state, new SortByShips(), Lists.<Filter<Star>>newArrayList(
        new FriendlyStars(player)
    ));
  }

  public static List<Star> getEnemyStars(GameState state, int player){
    return getStars(state, new SortByShips(), Lists.<Filter<Star>>newArrayList(
        new EnemyStars(player)
    ));
  }

  public static List<Star> getEnemyStarsInRange(GameState state, int player, Coordinate coords, double lightYears){
    return getStars(state, new SortByShips(), Lists.<Filter<Star>>newArrayList(
        new EnemyStars(player),
        new StarInRange(coords, lightYears)
    ));
  }

  public static List<Fleet> getFleetsInRange(GameState state, Star star, double lightYears) {
    return getFleets(state, Lists.<Filter<Fleet>>newArrayList(
        new FleetInRange(star.getCoords(), lightYears)
    ));
  }

  public static List<Fleet> getEnemyFleets(GameState state, int player){
    return getFleets(state, Lists.<Filter<Fleet>>newArrayList(
        new EnemyShips(player)
    ));
  }

  public static List<Fleet> getFriendlyFleets(GameState state, int player){
    return getFleets(state, Lists.<Filter<Fleet>>newArrayList(
        new FriendlyFleets(player)
    ));
  }

  public static List<Star> getStarsInRange(GameState state, Fleet fleet, double lightYears) {
    return getStars(state, new SortByShips(), Lists.<Filter<Star>>newArrayList(
        new StarInRange(fleet.getCoords(), lightYears)
    ));
  }


  private static List<Fleet> getFleets(GameState state, List<Filter<Fleet>> filters){
    List<Fleet> fleets = Lists.newArrayList();

    for (Fleet candidate : state.getAllFleets()) {

      boolean isAccept = true;
      for(Filter<Fleet> filter: filters){
        if(!filter.isAccept(candidate)){
          isAccept = false;
        }
      }

      if(isAccept){
        fleets.add(candidate);
      }
    }

    return fleets;
  }

  private static List<Star> getStars(GameState state, Comparator<Star> sortOrder, List<Filter<Star>> filters){

    List<Star> stars = Lists.newArrayList();
    for (Star candidate : state.getAllStars(false)) {

      boolean isAccept = true;
      for(Filter<Star> filter: filters){
        if(!filter.isAccept(candidate)){
          isAccept = false;
        }
      }

      if(isAccept){
        stars.add(candidate);
      }
    }

    Collections.sort(stars, sortOrder);

    return stars;
  }

//  public static boolean canTake(int ships, Star target){
//
//  }

  public static void startLogging(){
    BasicConfigurator.resetConfiguration();
    final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n"), ConsoleAppender.SYSTEM_ERR);
    consoleAppender.setFollow(true);
    BasicConfigurator.configure(consoleAppender);
  }
}
