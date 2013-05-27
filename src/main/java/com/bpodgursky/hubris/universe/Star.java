package com.bpodgursky.hubris.universe;
import org.json.JSONException;
import org.json.JSONObject;


public class Star {

	public final String name;
	public final Integer economy;
	public final Integer econUpgrade;
	public final Integer fleets;
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
	
	public Star(String name, Integer playerNumber, Integer economy, Integer econUpgrade, Integer fleets, Integer industry,
			Integer industryUpgrade, Integer science, Integer scienceUpgrade, Integer id, Integer x, Integer y, Integer g, Integer resources){
		
		this.name = name;
		this.playerNumber = playerNumber;
		this.economy = economy;
		this.econUpgrade = econUpgrade;
		this.fleets = fleets;
		this.industry = industry;
		this.industryUpgrade = industryUpgrade;
		this.science = science;
		this.scienceUpgrade = scienceUpgrade;
		this.id = id;
		this.x = x;
		this.y = y;
		this.garrisonSize = g;
		this.resources = resources;
		
	}
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("name", name);
			json.put("playerNumber", playerNumber);
			json.put("economy", economy);
			json.put("econUpgrade", econUpgrade);
			json.put("fleets", fleets);
			json.put("industry", industry);
			json.put("industryUpgrade", industryUpgrade);
			json.put("science", science);
			json.put("scienceUpgrade", scienceUpgrade);
			json.put("id", id);
			json.put("x", x);
			json.put("y", y);
			json.put("g", garrisonSize);
			
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

  public Integer getFleets() {
    return fleets;
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
}
