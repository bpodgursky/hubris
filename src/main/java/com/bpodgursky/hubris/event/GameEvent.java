package com.bpodgursky.hubris.event;

public class GameEvent {
	
	public final String key;
	public final Long at;

	public GameEvent(String key, Long at){
		this.key = key;
		this.at = at;
	}
}
