package com.bpodgursky.hubris.notification;

import org.json.JSONException;
import org.json.JSONObject;

public class CashReceived extends GameNotification {

	public final Integer sender;
	public final Integer amount;
	
	public CashReceived(String key, Long at, Integer sender, Integer amount){
		super(key, at);
		
		this.sender = sender;
		this.amount = amount;
	}
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("sender", sender);
			json.put("amount", amount);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
