package com.bpodgursky.hubris.connection;

import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.response.ResponseTransformer;
import com.bpodgursky.hubris.transfer.NpHttpClient;

public class RemoteConnection implements GameConnection {
	private final NpHttpClient client;

	
	public RemoteConnection(String userName, String password) throws Exception {
    this(new LoginClient().login(userName, password));
	}

  public RemoteConnection(LoginClient.LoginResponse loginResponse) throws Exception {
    if (loginResponse.getResponseType() != LoginClient.LoginResponseType.SUCCESS) {
      throw new RuntimeException("Couldn't login! Reason: " + loginResponse.getResponseType());
    }
    this.client = new NpHttpClient(loginResponse.getCookies());
  }

  public RemoteConnection(String cookies) {
    this.client = new NpHttpClient(cookies);
  }

	@Override
	public <R> R sendRequest(GameRequest request) throws Exception {
		return ResponseTransformer.parse(client.post(HubrisConstants.gameRequestUrl, request.toRequestParams()), request);
	}
}
