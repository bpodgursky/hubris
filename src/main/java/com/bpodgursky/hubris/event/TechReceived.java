package com.bpodgursky.hubris.event;

import com.bpodgursky.hubris.universe.TechType;
import org.json.JSONException;
import org.json.JSONObject;

public class TechReceived extends GameEvent {

	public final Integer sender;
	public final TechType type;
	
	public TechReceived(String key, Long at, TechType type, Integer sender) {
		super(key, at);
		
		this.sender = sender;
		this.type = type;
	}

	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("sender", sender);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}