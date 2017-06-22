package com.bpodgursky.hubris.universe;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.bpodgursky.hubris.HubrisUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;


public class Fleet {

	public final String name;
	public final Integer id;
	public final Integer player;

  /**
   * The estimated time of arrival at the last destination. Note that this will ONLY  be set for fleets
   * you have complete knowledge of (e.g., your fleets) since you can only know the first destination of
   * a fleet.
   */
	public final Integer eta;

  /**
   * The time this fleet has before arriving at its next destination.
   */
	public final Integer nextEta;
	public final Integer ships;
	public final Integer victories;
	public final List<Integer> destinations;

  private final Coordinate coords;


  /**
   * "Jump Prep" -- number of minutes a fleet must remain at a star before launching.
   */
	public final Integer jumpPrepTime;

  /**
   * The star that this fleet is orbiting
   */
  private Integer starId;
	
	public Fleet(String name, Integer id, Integer player, Integer eta, Integer nextETA, Integer fleets, 
			Integer victories, List<Integer> destinations, Coordinate coords, Integer jumpPrepTime, Integer starId){
		this.name = name;
		this.id = id;
		this.player = player;
		this.eta = eta;
		this.nextEta = nextETA;
		this.ships = fleets;
		this.victories = victories;
		this.destinations = destinations;
    this.coords = coords;
		this.jumpPrepTime = jumpPrepTime;
    this.starId = starId;
	}

  public Fleet(Fleet fleet, Integer starId) {
    this(fleet.name, fleet.id, fleet.player, fleet.eta, fleet.nextEta, fleet.ships, fleet.victories, fleet.destinations,
      fleet.getCoords(), fleet.jumpPrepTime, starId);
  }


  public Coordinate getCoords(){
    return coords;
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
			json.put("coords", coords);
			json.put("jumpPrepTime", jumpPrepTime);

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

  public Integer getJumpPrepTime() {
    return jumpPrepTime;
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
    return HubrisUtil.getDistanceInLightYears(getCoords(), other.getCoords());
  }

  /**
   *
   * @param star
   * @return the distance to the provided star, measured in light-years.
   */
  public double distanceFrom(Star star) {
    return HubrisUtil.getDistanceInLightYears(getCoords(), star.getCoords());
  }

  public static class Deserializer implements JsonDeserializer<Fleet> {
    @Override
    public Fleet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = jsonElement.getAsJsonObject();

      return new Fleet(
          obj.get("n").getAsString(),
          obj.get("uid").getAsInt(),
          obj.get("puid").getAsInt(),
          0,
          0,
          obj.get("st").getAsInt(),
          obj.get("w").getAsInt(), //?
          StreamSupport
              .stream(obj.getAsJsonArray("o").spliterator(), false)
              .<Integer>map(
                  x -> x.getAsJsonArray().get(1).getAsInt()
              ).collect(Collectors.toList()),
          context.deserialize(obj, Coordinate.class),
          obj.get("l").getAsInt(), //?
          obj.get("ouid") == null ? null : obj.get("ouid").getAsInt()
      );
    }
  }
}
