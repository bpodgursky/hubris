package com.bpodgursky.hubris.universe;
import org.json.JSONException;
import org.json.JSONObject;


public class Alliance {

	//TODO does this make more sense as a map?
	public final String alliances;
		
	public Alliance(String alliances){
		this.alliances = alliances;
	}
	
	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("alliances", alliances);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}

  public String getAlliances() {
    return alliances;
  }
}
