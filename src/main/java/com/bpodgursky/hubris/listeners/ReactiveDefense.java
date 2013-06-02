package com.bpodgursky.hubris.listeners;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.command.SetWaypoint;
import com.bpodgursky.hubris.events.EventListener;
import com.bpodgursky.hubris.events.FleetDestinationChangedEvent;
import com.bpodgursky.hubris.helpers.FleetHelper;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.bpodgursky.hubris.universe.TechType;
import com.bpodgursky.hubris.util.BattleOutcome;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Detects when an enemy points a fleet at you and does what it can to defend the target star.
 */
public class ReactiveDefense implements EventListener<FleetDestinationChangedEvent> {
  private static final Logger LOG = LoggerFactory.getLogger(ReactiveDefense.class);

  @Override
  public Collection<GameRequest> process(Collection<FleetDestinationChangedEvent> events, GameState currentState, CommandFactory commandFactory) throws Exception {
    Set<Integer> claimedFleets = Sets.newHashSet();
    List<GameRequest> commands = Lists.newArrayList();

    for (FleetDestinationChangedEvent event : events) {
      if (isActOfWar(event, currentState)) {
        Fleet attackingFleet = currentState.getFleet(event.getFleetId());
        Star starUnderThreat = currentState.getStar(attackingFleet.getDestinations().get(0), false);
        Player me = currentState.getPlayer(currentState.getPlayerId());
        Player enemy = currentState.getPlayer(attackingFleet.getPlayer());
        int fleetEta = attackingFleet.getNextEta();

        LOG.info("Act of war: {}'s fleet {} is now pointing at {}", enemy.getName(), attackingFleet.getName(), starUnderThreat.getName());

        int myWeapons = (int) me.getFutureTechValue(TechType.WEAPONS, fleetEta);
        int enemyWeapons = (int) enemy.getTechLevel(TechType.WEAPONS);
        Integer newShipsProduced = starUnderThreat.getNumShipsProduced(fleetEta);
        Integer shipsWhenAttackerArrives = starUnderThreat.getShipsIncludingFleets(currentState) + newShipsProduced;
        Integer availableShipsForTransfer = 0;

        // Find any currently dispatched fleets that will arrive before the attacker does
        List<Fleet> fleetsInRange = HubrisUtil.getFleetsInRange(currentState, starUnderThreat, me.getSpeed() * (fleetEta / 24.0));
        List<Fleet> fleetsToSend = Lists.newArrayList();
        for (Fleet fleet : fleetsInRange) {
          // Skip fleets that are already at this star
          if (starUnderThreat.getId().equals(fleet.getStarId())) {
            continue;
          }

          // TODO: stop the fleet at starUnderThreat
          if (fleet.getDestinations().isEmpty() || fleet.getDestinations().get(0).equals(starUnderThreat.getId())) {
            availableShipsForTransfer += fleet.getShips();

            if (fleet.getStarId() != null) {
              // TODO: don't double-count star's ships for stars with multiple fleets
              availableShipsForTransfer += currentState.getStar(fleet.getStarId(), false).getShips();
            }
          }
          if (fleet.getDestinations().isEmpty()) {
            fleetsToSend.add(fleet);
          }
        }

        // TODO: find stars with available ships but no fleets

        // If the attacker is going to win anyway, flee.
        BattleOutcome bestCase = HubrisUtil.getBattleOutcome(myWeapons, enemyWeapons, shipsWhenAttackerArrives+availableShipsForTransfer, attackingFleet.getShips());
        if (!bestCase.defenderWon()) {
          // TODO: transfer all but one ship to the fleet, find a star to flee to.
        }

        BattleOutcome outcomeIfDoNothing = HubrisUtil.getBattleOutcome(myWeapons, myWeapons, shipsWhenAttackerArrives, attackingFleet.getShips());
        while (!fleetsToSend.isEmpty() && !outcomeIfDoNothing.defenderWon()) {
          Fleet toSend = fleetsToSend.remove(0);

          // TODO: transfer ships from star to fleet
          commands.add(commandFactory.clearAllFleetPaths(toSend.getId()));
          commands.add(commandFactory.setWaypoint(toSend.getId(), starUnderThreat.getId()));
        }
      }
    }

    return commands;
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

    Star destination = state.getStar(fleet.getDestinations().get(0), false);

    // Ignore stars we can't see
    if (destination.getResources() == null) {
      return false;
    }
    // Ignore useless stars (for now -- may want to revisit this in the future)
    if (destination.getEconomy() < 0 && destination.getIndustry() < 0 && destination.getScience() < 0) {
      return false;
    }

    // Only consider fleets belonging to other players being sent at stars belonging to us
    return fleet.getPlayer() != state.getPlayerId() && destination.getPlayerNumber() == state.getPlayerId();
  }
}
