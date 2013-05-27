package com.bpodgursky.hubris.command;

import com.bpodgursky.hubris.universe.Comment;

import java.util.List;
import java.util.Map;

public class GetMessageComments extends GameRequest {

	public final Integer offset;
	public final Integer number;
	public final String messageKey;
	
	public GetMessageComments(Integer player, String userName, Long game, String messageKey, Integer offset, Integer number) {
		super(RequestType.GetMessageComments, player, userName, game);
		
		this.offset = offset;
		this.number = number;
		this.messageKey = messageKey;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) {
		 params.put("offset", Integer.toString(offset));
		 params.put("number", Integer.toString(number));
		 params.put("message_key", messageKey);
	}
}
