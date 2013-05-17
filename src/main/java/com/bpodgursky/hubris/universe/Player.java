package com.bpodgursky.hubris.universe;
import org.json.JSONException;
import org.json.JSONObject;


public class Player {
	
	public final boolean ai;
	public final String alliances;
	public final Integer cash;
	public final String currentResearch;
	public final String nextResearch;
	public final Integer weapons;
	public final Double range;
	public final Double speed;
	public final Double scanning;
	public final Integer id;
	public final String name;
	public final Integer economy;
	public final Integer industry;
	public final Integer carriers;
	public final Integer homeFleets;
	public final Integer science;
	public final Integer shipFleets;
	public final Integer allFleets;
	
	public final Integer stars;
	
	public Player(String name, Integer id, Integer economy, Integer industry, Integer science, Integer stars, Integer carriers, Integer homeFleets, Integer shipFleets,
			boolean ai, String alliances, Integer cash, String currentResearch, String nextResearch, Integer weapons, Double range,
			Double speed, Double scanning){
		
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
}
