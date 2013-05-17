package com.bpodgursky.hubris.event;

import com.bpodgursky.hubris.universe.TechType;
import org.json.JSONException;
import org.json.JSONObject;

public class TechSent extends GameEvent {

	public final Integer recipient;
	public final TechType tech;
	
	public TechSent(String key, Long at, TechType type, Integer recipient) {
		super(key, at);
		
		this.recipient = recipient;
		this.tech = type;
	}

	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("recipient", recipient);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
