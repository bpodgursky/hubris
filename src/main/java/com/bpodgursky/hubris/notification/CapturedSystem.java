package com.bpodgursky.hubris.notification;

import org.json.JSONException;
import org.json.JSONObject;

public class CapturedSystem extends GameNotification {

	public final Integer star;
	public final Integer fleet;
	
	public CapturedSystem(String key, Long at, Integer star, Integer fleet) {
		super(key, at);
		
		this.star = star;
		this.fleet = fleet;
	}
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("star", star);
			json.put("fleet", fleet);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
