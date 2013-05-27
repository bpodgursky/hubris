package com.bpodgursky.hubris.command;

import com.bpodgursky.hubris.command.GameRequest;

import java.util.Map;

public class SendMessageComment extends GameRequest {

	public final String key;
	public final String body;
	
	public SendMessageComment(Integer player, String userName, Long game, String key, String body) {
		super(RequestType.SendMessageComment, player, userName, game);

		this.key = key;
		this.body = body;
	}
	
	@Override
	protected void addRequestParams(Map<String, String> params) {
		params.put("message_key", key);
		params.put("body", body);
	}

}
