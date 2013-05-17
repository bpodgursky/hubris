package com.bpodgursky.hubris.event;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fleet combat event.  Create more useful events to respond to
 *
 * @author bpodgursky
 */
public class FleetCombat extends GameEvent {

  public final Integer defender;
  public final Map<Integer, Integer> playerIDToShipsBefore;
  public final Map<Integer, Integer> playerIDToShipsAfter;
  public final Map<Integer, Integer> fleetCombatSkills;
  public final Integer starID;

  public FleetCombat(String key, Long at, Integer defender, Map<Integer, Integer> shipsBefore, Map<Integer, Integer> shipsAfter,
                     Map<Integer, Integer> combatSkills, Integer starID) {
    super(key, at);

    this.defender = defender;
    this.starID = starID;
    this.playerIDToShipsAfter = shipsAfter;
    this.playerIDToShipsBefore = shipsBefore;
    this.fleetCombatSkills = combatSkills;
  }

  public String toString() {

    JSONObject json = new JSONObject();
    try {
      json.put("defender", defender);
      json.put("starID", starID);
      json.put("playerIDToShipsBefore", playerIDToShipsBefore);
      json.put("playerIDToShipsAfter", playerIDToShipsAfter);
      json.put("fleetCombatSkills", fleetCombatSkills);
      json.put("key", key);
      json.put("timestamp", at);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json.toString();
  }
}
