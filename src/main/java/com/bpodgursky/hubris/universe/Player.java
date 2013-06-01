package com.bpodgursky.hubris.universe;
import com.bpodgursky.hubris.HubrisUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class Player {
	
	private final boolean ai;
	private final String alliances;
	private final Integer cash;
	private final TechType currentResearch;
	private final String nextResearch;
	private final Integer weapons;
	private final Double range;
	private final Double speed;
	private final Double scanning;
	private final int id;
	private final String name;
	private final Integer economy;
	private final Integer industry;
	private final Integer carriers;
	private final Integer homeFleets;
	private final Integer science;
	private final Integer shipFleets;
	private final Integer allFleets;
	private final Integer stars;
  private final Map<TechType, Tech> techState;
	
	public Player(String name, int id, Integer economy, Integer industry, Integer science, Integer stars, Integer carriers, Integer homeFleets, Integer shipFleets,
			boolean ai, String alliances, Integer cash, TechType currentResearch, String nextResearch, Integer weapons, Double range,
			Double speed, Double scanning, Map<TechType, Tech> techState){
		
		this.id = id;
		this.ai = ai;
		this.allFleets = homeFleets + shipFleets;
		this.alliances = alliances;
		this.cash = cash;
		this.currentResearch = currentResearch;
		this.nextResearch = nextResearch;
		this.weapons = weapons;
		this.range = range;
		this.speed = speed;
		this.scanning = scanning;
		this.name = name;
		this.economy = economy;
		this.industry = industry;
		this.carriers = carriers;
		this.homeFleets = homeFleets;
		this.shipFleets = shipFleets;
		this.science = science;
		this.stars = stars;
		this.techState = techState;
	}
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("id", id);
			json.put("ai", ai);
			json.put("allFleets",allFleets);
			json.put("alliances", alliances);
			json.put("cash", cash);
			json.put("currentResearch", currentResearch);
			json.put("nextResearch", nextResearch);
			json.put("weapons", weapons);
			json.put("range", range);
			json.put("speed", speed);
			json.put("scanning", scanning);
			json.put("name", name);
			json.put("economy", economy);
			json.put("industry", industry);
			json.put("carriers", carriers);
			json.put("homeFleets", homeFleets);
			json.put("shipFleets", shipFleets);
			json.put("science", science);
			json.put("stars", stars);
			
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}

  public boolean isAi() {
    return ai;
  }

  public String getAlliances() {
    return alliances;
  }

  public Integer getCash() {
    return cash;
  }

  public TechType getCurrentResearch() {
    return currentResearch;
  }

  public String getNextResearch() {
    return nextResearch;
  }

  public Integer getWeapons() {
    return weapons;
  }

  public Double getRange() {
    return range;
  }

  public Double getSpeed() {
    return speed;
  }

  public Double getScanning() {
    return scanning;
  }

  public double getTechLevel(TechType type) {
    switch (type) {
      case RANGE: return getRange();
      case SCANNING: return getScanning();
      case SPEED: return getSpeed();
      case WEAPONS: return getWeapons();
    }
    throw new IllegalArgumentException("Unknown tech type: " + type);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getEconomy() {
    return economy;
  }

  public Integer getIndustry() {
    return industry;
  }

  public Integer getCarriers() {
    return carriers;
  }

  public Integer getHomeFleets() {
    return homeFleets;
  }

  public Integer getScience() {
    return science;
  }

  public Integer getShipFleets() {
    return shipFleets;
  }

  public Integer getAllFleets() {
    return allFleets;
  }

  public Integer getStars() {
    return stars;
  }

  public int getUpgradePointsPerHour() {
    return getScience() * HubrisUtil.SCIENCE_TO_RESEARCH_PER_HOUR;
  }

  public Tech getTech(TechType type) {
    if (techState == null) {
      throw new IllegalStateException("Tried to access tech for player with unset tech state");
    }
    return techState.get(type);
  }

  public boolean isWithinJumpRange(Star star1, Star star2) {
    double distance = star1.distanceFrom(star2);

    return getRange() >= distance;
  }

  /**
   * Returns the value of the provided tech type N minutes in the future. If tech for this player isn't
   * known, it just returns the current value. Conditionally adds a +1 bonus if research will compute
   * within N minutes for the provided tech type.
   *
   * @param tech
   * @param minutes
   * @return
   */
  public double getFutureTechValue(TechType tech, int minutes) {
    if (this.getCurrentResearch() != tech || techState == null) {
      return this.getTechLevel(tech);
    }

    int requiredUpgradePoints = this.getTech(tech).getRequiredUpgradePoints();
    int upgradePointsAvailable = (int)Math.floor((minutes / 60.0) * this.getUpgradePointsPerHour());
    double bonus = upgradePointsAvailable >= requiredUpgradePoints ? tech.getUpgradePoints() : 0;

    // TODO: consider next research too
    return this.getTechLevel(tech) + bonus;
  }
}
