package com.bpodgursky.hubris.listeners;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.FleetDestinationChangedEvent;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.bpodgursky.hubris.universe.TechType;
import com.bpodgursky.hubris.util.BattleOutcome;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Detects when an enemy points a fleet at you and does what it can to defend the target star.
 */
public class ReactiveDefense implements EventListener<FleetDestinationChangedEvent> {
  @Override
  public Collection<GameRequest> process(Collection<FleetDestinationChangedEvent> events, GameState currentState, CommandFactory commandFactory) throws Exception {
    Set<Integer> claimedFleets = Sets.newHashSet();

    for (FleetDestinationChangedEvent event : events) {
      if (isActOfWar(event, currentState)) {
        Fleet attackingFleet = currentState.getFleet(event.getFleetId());
        Star starUnderThreat = currentState.getStar(attackingFleet.getDestinations().get(0), false);
        Player me = currentState.getPlayer(currentState.getPlayerId());
        Player enemy = currentState.getPlayer(attackingFleet.getPlayer());

        Integer myWeapons = me.getFutureTechValue(TechType.WEAPONS, attackingFleet.getEta());
        Integer enemyWeapons = enemy.getWeapons();
        Integer newShipsProduced = starUnderThreat.getNumShipsProduced(attackingFleet.getEta());
        Integer shipsWhenAttackerArrives = starUnderThreat.getShips() ;

//        BattleOutcome outcomeIfDoNothing = HubrisUtil.getBattleOutcome(myWeapons, enemyWeapons,  )
      }
    }

    return Collections.emptyList();
  }

  @Override
  public Class<FleetDestinationChangedEvent> getEventType() {
    return FleetDestinationChangedEvent.class;
  }

  protected static boolean isActOfWar(FleetDestinationChangedEvent event, GameState state) {
    Fleet fleet = state.getFleet(event.getFleetId());

    // Don't consider fleets that had their waypoints cleared
    if (fleet.getDestinations().isEmpty()) {
      return false;
    }

    Player owner = state.getPlayer(fleet.getPlayer());
    Star destination = state.getStar(fleet.getDestinations().get(0), false);

    // Ignore useless stars (for now -- may want to revisit this in the future)
    if (destination.getEconomy() < 0 && destination.getIndustry() < 0 && destination.getScience() < 0) {
      return false;
    }

    // Only consider fleets belonging to other players being sent at stars belonging to us
    return fleet.getPlayer() != state.getPlayerId() && destination.getPlayerNumber() == state.getPlayerId();
  }
}
