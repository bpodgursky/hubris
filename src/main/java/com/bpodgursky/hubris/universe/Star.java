package com.bpodgursky.hubris.universe;
import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.util.BattleOutcome;
import com.google.common.collect.Lists;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;


public class Star {
	public final String name;
	public final Integer economy;
	public final Integer econUpgrade;
	public final Integer ships;
	public final Integer industry;
	public final Integer industryUpgrade;
	public final Integer science;
	public final Integer scienceUpgrade;
	public final Integer id;
	public final Integer x;
	public final Integer y;
	public final Integer playerNumber;
	public final Integer garrisonSize;
	public final Integer resources;
  public final Set<Integer> fleets;

	public Star(String name, Integer playerNumber, Integer economy, Integer econUpgrade, Integer ships, Integer industry,
			Integer industryUpgrade, Integer science, Integer scienceUpgrade, Integer id, Integer x, Integer y, Integer g, Integer resources, Set<Integer> fleets){
		this.name = name;
		this.playerNumber = playerNumber;
		this.economy = economy;
		this.econUpgrade = econUpgrade;
		this.ships = ships;
		this.industry = industry;
		this.industryUpgrade = industryUpgrade;
		this.science = science;
		this.scienceUpgrade = scienceUpgrade;
		this.id = id;
		this.x = x;
		this.y = y;
		this.garrisonSize = g;
		this.resources = resources;
		this.fleets = fleets;
	}

  public Star(Star old, int x, int y) {
    this(old.name, old.playerNumber, old.economy, old.econUpgrade, old.ships, old.industry, old.industryUpgrade,
      old.science, old.scienceUpgrade, old.id, x, y, old.garrisonSize, old.resources, old.fleets);
  }

  public Star(Star old, Set<Integer> fleets) {
    this(old.name, old.playerNumber, old.economy, old.econUpgrade, old.ships, old.industry, old.industryUpgrade,
      old.science, old.scienceUpgrade, old.id, old.x, old.y, old.garrisonSize, old.resources, fleets);
  }
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("name", name);
			json.put("playerNumber", playerNumber);
			json.put("economy", economy);
			json.put("econUpgrade", econUpgrade);
			json.put("ships", ships);
			json.put("industry", industry);
			json.put("industryUpgrade", industryUpgrade);
			json.put("science", science);
			json.put("scienceUpgrade", scienceUpgrade);
			json.put("id", id);
			json.put("x", x);
			json.put("y", y);
			json.put("g", garrisonSize);
      json.put("fleets", fleets);
			
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}

  public String getName() {
    return name;
  }

  public Integer getEconomy() {
    return economy;
  }

  public Integer getEconUpgrade() {
    return econUpgrade;
  }

  public Integer getShips() {
    return ships;
  }

  public Integer getIndustry() {
    return industry;
  }

  public Integer getIndustryUpgrade() {
    return industryUpgrade;
  }

  public Integer getScience() {
    return science;
  }

  public Integer getScienceUpgrade() {
    return scienceUpgrade;
  }

  public Integer getId() {
    return id;
  }

  public Integer getX() {
    return x;
  }

  public Integer getY() {
    return y;
  }

  public Integer getPlayerNumber() {
    return playerNumber;
  }

  public Integer getGarrisonSize() {
    return garrisonSize;
  }

  public Integer getResources() {
    return resources;
  }

  public Set<Integer> getFleets() {
    return fleets;
  }

  /**
   * Approximates the ship production of this star after N minutes. It doesn't consider when the timer will tick,
   * so this could be an underestimate. Assumes that 1 ship per 12 hours per point of industry are produced.
   *
   * @param minutes
   * @return the number of ships that will be produced at this star after waiting N minutes
   */
  public int getNumShipsProduced(int minutes) {
    int industryPeriods = (int)Math.floor(minutes / (60.0 * 12));
    return getIndustry()*industryPeriods;
  }

  /**
   *
   * @param other
   * @return the distance from this star to other, measured in light-years.
   */
  public double distanceFrom(Star other) {
    return HubrisUtil.getDistanceInLightYears(getX(), getY(), other.getX(), other.getY());
  }

  /**
   *
   * @param fleet
   * @return the distance from this star to the provided fleet, measured in light-years.
   */
  public double distanceFrom(Fleet fleet) {
    return HubrisUtil.getDistanceInLightYears(getX(), getY(), fleet.getX(), fleet.getY());
  }

  /**
   *
   * @return the number of ships at this star including any ships in fleets
   */
  public int getShipsIncludingFleets(GameState state) {
    int fleetShips = 0;
    for (Integer fleet : fleets) {
      fleetShips += state.getFleet(fleet).getShips();
    }
    return getShips() + fleetShips;
  }
}
