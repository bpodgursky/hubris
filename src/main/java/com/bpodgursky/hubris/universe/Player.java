package com.bpodgursky.hubris.universe;
import com.bpodgursky.hubris.HubrisUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;


public class Player {
	
	private final boolean ai;
	private final String alliances;
	private final Integer cash;
	private final TechType currentResearch;
	private final String nextResearch;
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

  public Player(String name,
                int id,
                Integer economy,
                Integer industry,
                Integer science,
                Integer stars,
                Integer carriers,
                Integer homeFleets,
                Integer shipFleets,
                boolean ai,
                String alliances,
                Integer cash,
                TechType currentResearch,
                String nextResearch,
                Map<TechType, Tech> techState) {
    this.id = id;
		this.ai = ai;
		this.allFleets = homeFleets + shipFleets;
		this.alliances = alliances;
		this.cash = cash;
		this.currentResearch = currentResearch;
		this.nextResearch = nextResearch;
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
    return new Gson().toJson(this);
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

  public Map<TechType, Tech> getTechState() {
    return techState;
  }

  public int getTechLevel(TechType type) {
    if (techState.containsKey(type)) {
      return techState.get(type).getLevel();
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

  public int getWeapons() {
    return getTechLevel(TechType.WEAPONS);
  }

  public int getRange() {
    return getTechLevel(TechType.RANGE) + 3;
  }

  public int getScanning() {
    return getTechLevel(TechType.SCANNING) + 2;
  }

  public Integer getSpeed() {
    // TODO: warp gates?
    return 1;
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

  public static class Deserializer implements JsonDeserializer<Player> {
    @Override
    public Player deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = jsonElement.getAsJsonObject();

      Map<TechType, Tech> tech = Maps.newHashMap();
      JsonObject rawTech = obj.getAsJsonObject("tech");
      for (Map.Entry<String, JsonElement> entry : rawTech.entrySet()) {
        tech.put(
            TechType.fromStringValue(entry.getKey()),
            context.deserialize(entry.getValue(), Tech.class)
        );
      }

      return new Player(
          obj.get("alias").getAsString(),
          obj.get("uid").getAsInt(),
          obj.get("total_economy").getAsInt(),
          obj.get("total_industry").getAsInt(),
          obj.get("total_science").getAsInt(),
          obj.get("total_stars").getAsInt(),
          obj.get("total_fleets").getAsInt(),
          0, //unused?
          0, //unused?
          obj.get("ai").getAsInt() != 0,
          "",
          context.deserialize(obj.get("cash"), Integer.class),
          TechType.fromStringValue(Optional.ofNullable(obj.get("researching")).<String>map(JsonElement::getAsString).orElse(null)),
          Optional.ofNullable(obj.get("researching_next")).<String>map(JsonElement::getAsString).orElse(null),
          tech
      );
    }
  }
}
