package com.bpodgursky.hubris.connection;

import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.response.ResponseTransformer;
import com.bpodgursky.hubris.transfer.NpHttpClient;

public class RemoteConnection implements GameConnection {

	private final NpHttpClient client;

	
	public RemoteConnection(String userName, String password) throws Exception {
		LoginClient login = new LoginClient();
		LoginClient.LoginResponse response = login.login(userName, password);
		
		if (response.getResponseType() != LoginClient.LoginResponseType.SUCCESS) {
			System.err.println();
			throw new Exception("Couldn't login! Reason: " + response.getResponseType());
		}
		this.client = new NpHttpClient(response.getCookies());


	}

	@Override
	public <R> R sendRequest(GameRequest<R> request) throws Exception {
		return ResponseTransformer.parse(client.post(HubrisConstants.gameRequestUrl, request.toRequestParams()), request);
	}
}
