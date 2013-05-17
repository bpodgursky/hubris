package com.bpodgursky.hubris.universe;

import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;


public class GameStateDelta {

  private final Collection<Star> stars;
  private final Collection<Player> players;
  private final Collection<Fleet> fleets;
  private final Collection<Tech> techs;
  private final Alliance alliance;

  public GameStateDelta(Collection<Player> players, Collection<Star> stars, Collection<Fleet> fleets,
                        Collection<Tech> techs, Alliance alliance) {

    this.stars = stars;
    this.players = players;
    this.fleets = fleets;
    this.techs = techs;
    this.alliance = alliance;
  }

  public Collection<Star> getStars() {
    return this.stars;
  }

  public Collection<Player> getPlayers() {
    return this.players;
  }

  public Collection<Fleet> getFleets() {
    return this.fleets;
  }

  public Collection<Tech> getTechs() {
    return this.techs;
  }

  public Alliance getAlliance() {
    return this.alliance;
  }

  public String toString() {

    JSONObject obj = new JSONObject();
    try {
      obj.put("alliance", alliance);
      obj.put("players", players);
      obj.put("stars", stars);
      obj.put("fleets", fleets);
      obj.put("tech", techs);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    try {
      return obj.toString(1);
    } catch (JSONException e) {
      e.printStackTrace();

      return "";
    }
  }
}
