package com.bpodgursky.hubris.notification;

import com.bpodgursky.hubris.universe.TechType;
import org.json.JSONException;
import org.json.JSONObject;

public class TechResearch extends GameNotification {

	public final TechType tech;
	public final Integer newLevel;
	
	public TechResearch(String key, Long at, TechType tech, Integer newLevel){
		super(key, at);
		
		this.tech = tech;
		this.newLevel = newLevel;
	}
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("tech", tech);
			json.put("newLevel", newLevel);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
