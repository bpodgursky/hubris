package com.bpodgursky.hubris.command;

import com.bpodgursky.hubris.notification.GameNotification;

import java.util.List;
import java.util.Map;

public class GetEvents extends GameRequest {

	public final Integer offset;
	public final Integer number;
	
	public GetEvents(Integer player, String userName, Long game, Integer offset, Integer number) {
		super(RequestType.GetEvents, player, userName, game);
		
		this.offset = offset;
		this.number = number;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) {
		 params.put("number", Integer.toString(number));
		 params.put("offset", Integer.toString(offset));
	}

}
