package com.bpodgursky.hubris.universe;
import java.util.List;

import com.bpodgursky.hubris.HubrisUtil;
import org.json.JSONException;
import org.json.JSONObject;


public class Fleet {

	public final String name;
	public final Integer id;
	public final Integer player;

  /**
   * The estimated time of arrival at the last destination.
   */
	public final Integer eta;

  /**
   * The time this fleet has before arriving at its next destination. Note that this can indicate "jump
   * prep" time when its at a star.
   */
	public final Integer nextEta;
	public final Integer ships;
	public final Integer victories;
	public final List<Integer> destinations;
	public final Integer x;
	public final Integer y;
	
	//	dunno what this is
	public final Integer rt;

  private Integer starId;
	
	public Fleet(String name, Integer id, Integer player, Integer eta, Integer nextETA, Integer fleets, 
			Integer victories, List<Integer> destinations, Integer x, Integer y, Integer rt, Integer starId){
		this.name = name;
		this.id = id;
		this.player = player;
		this.eta = eta;
		this.nextEta = nextETA;
		this.ships = fleets;
		this.victories = victories;
		this.destinations = destinations;
		this.x = x;
		this.y = y;
		this.rt = rt;
    this.starId = starId;
	}

  public Fleet(Fleet fleet, Integer starId) {
    this(fleet.name, fleet.id, fleet.player, fleet.eta, fleet.nextEta, fleet.ships, fleet.victories, fleet.destinations,
      fleet.x, fleet.y, fleet.rt, starId);
  }

	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("name", name);
			json.put("id", id);
			json.put("player", player);
			json.put("eta", eta);
			json.put("nextEta", nextEta);
			json.put("ships", ships);
			json.put("victories", victories);
			json.put("destinations", destinations);
			json.put("x", x);
			json.put("y", y);
			json.put("rt", rt);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}

  public String getName() {
    return name;
  }

  public Integer getId() {
    return id;
  }

  public Integer getPlayer() {
    return player;
  }

  public Integer getEta() {
    return eta;
  }

  public Integer getNextEta() {
    return nextEta;
  }

  public Integer getShips() {
    return ships;
  }

  public Integer getVictories() {
    return victories;
  }

  public List<Integer> getDestinations() {
    return destinations;
  }

  public Integer getX() {
    return x;
  }

  public Integer getY() {
    return y;
  }

  public Integer getRt() {
    return rt;
  }

  public boolean isAtStar() {
    return starId != null;
  }

  public Integer getStarId() {
    return starId;
  }

  /**
   *
   * @param other
   * @return the distance from this fleet to the other fleet, measured in light-years.
   */
  public double distanceFrom(Fleet other) {
    return HubrisUtil.getDistanceInLightYears(getX(), getY(), other.getX(), other.getY());
  }

  /**
   *
   * @param star
   * @return the distance to the provided star, measured in light-years.
   */
  public double distanceFrom(Star star) {
    return HubrisUtil.getDistanceInLightYears(getX(), getY(), star.getX(), star.getY());
  }
}
