package com.bpodgursky.hubris.command;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendMessage extends GameRequest {

	public final String subject;
	public final String body;
	public final List<Integer> to;
	
	public SendMessage(Integer player, String userName, Long game, String subject, String body, List<Integer> to) {
		super(RequestType.SendMessage, player, userName, game);

		this.subject = subject;
		this.body = body;
		
		List<Integer> recip = new ArrayList<Integer>(to);
		recip.add(this.playerNumber);
		Collections.sort(recip);
		
		this.to = recip;
	}
	
	@Override
	protected void addRequestParams(Map<String, String> params) {
		params.put("subject", this.subject);
		params.put("body", body);
		params.put("cash", "0");
		params.put("star", "0");
		params.put("tech", "");
		params.put("fleet", "0");
		params.put("fr", Integer.toString(playerNumber));
		params.put("att", "0");
		params.put("to",  StringUtils.join(to, ".")+".");
	}
}
