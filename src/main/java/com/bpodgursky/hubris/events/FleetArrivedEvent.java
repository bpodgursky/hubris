package com.bpodgursky.hubris.events;

import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;

import java.util.List;

public class FleetArrivedEvent {
  private final Star star;
  private final Fleet fleet;
  private final Player previousPlayer;
  private final Player currentPlayer;
  private final List<Fleet> previousFleets;
  private final List<Fleet> currentFleets;
  private final int previousShips;
  private final int currentShips;

  public FleetArrivedEvent(Star star, Fleet fleet, Player previousPlayer, Player currentPlayer, List<Fleet> previousFleets, List<Fleet> currentFleets, int previousShips, int currentShips) {
    this.star = star;
    this.fleet = fleet;
    this.previousPlayer = previousPlayer;
    this.currentPlayer = currentPlayer;
    this.previousFleets = previousFleets;
    this.currentFleets = currentFleets;
    this.previousShips = previousShips;
    this.currentShips = currentShips;
  }

  public Fleet getFleet() {
    return fleet;
  }

  public Star getStar() {
    return star;
  }

  public Player getPreviousPlayer() {
    return previousPlayer;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public List<Fleet> getPreviousFleets() {
    return previousFleets;
  }

  public List<Fleet> getCurrentFleets() {
    return currentFleets;
  }

  public int getPreviousShips() {
    return previousShips;
  }

  public int getCurrentShips() {
    return currentShips;
  }

  public boolean wasBattle() {
    return previousPlayer != null && previousPlayer.getId() != currentPlayer.getId();
  }
}
