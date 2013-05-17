package com.bpodgursky.hubris.event;

import org.json.JSONException;
import org.json.JSONObject;

public class CashSent extends GameEvent {

	public final Integer recipient;
	public final Integer amount;
	
	public CashSent(String key, Long at, Integer recipient, Integer cash) {
		super(key, at);
		
		this.recipient = recipient;
		this.amount = cash;
	}
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("recipient", recipient);
			json.put("amount", amount);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
