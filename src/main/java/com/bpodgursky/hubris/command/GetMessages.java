package com.bpodgursky.hubris.command;

import com.bpodgursky.hubris.notification.Message;

import java.util.List;
import java.util.Map;

public class GetMessages extends GameRequest {

	public final Integer number;
	public final Integer offset;
	
	public GetMessages(Integer player, String userName, Long game, Integer number, Integer offset) {
		super(RequestType.GetMessages, player, userName, game);
	
		this.number = number;
		this.offset = offset;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) {
		 params.put("offset", Integer.toString(offset));
		 params.put("number", Integer.toString(number));
	}

}
