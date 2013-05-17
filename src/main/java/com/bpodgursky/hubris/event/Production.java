package com.bpodgursky.hubris.event;

import org.json.JSONException;
import org.json.JSONObject;

public class Production extends GameEvent{

	public final Integer cash;
	
	public Production(String key, Long at, Integer cash) {
		super(key, at);
		
		this.cash = cash;
	}

	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("cash", cash);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
