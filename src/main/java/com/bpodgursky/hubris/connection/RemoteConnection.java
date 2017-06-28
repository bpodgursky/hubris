package com.bpodgursky.hubris.connection;

import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.command.*;
import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.notification.GameNotification;
import com.bpodgursky.hubris.notification.Message;
import com.bpodgursky.hubris.response.ResponseTransformer;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.bpodgursky.hubris.universe.Comment;
import com.bpodgursky.hubris.universe.GameState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Optional;

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

  private String post(GameRequest request) throws Exception {
    return client.post(HubrisConstants.gameRequestUrl, request.toRequestParams());
  }

  @Override
  public GameState getState(GameState currentState, GetState getState) throws Exception {
    return ResponseTransformer.parseUniverse(currentState, getState, post(getState));
  }

  @Override
  public List<GameNotification> getNotifications(GetEvents events) throws Exception {
    return ResponseTransformer.parseEventList(post(events));
  }

  @Override
  public List<Comment> getComments(GetMessageComments messageComments) throws Exception {
    return ResponseTransformer.parseMessageComments(post(messageComments));
  }

  @Override
  public List<Message> getMessages(GetMessages messages) throws Exception {
    return ResponseTransformer.parseMessages(post(messages));
  }

  @Override
  public void submit(GameRequest request) throws Exception {
    post(request);
  }

  @Override
  public Optional<String> refreshCookies() throws Exception {
    return client.getAuthCookie(HubrisConstants.homepageUrl);
  }

  @Override
  public boolean isLoggedIn() throws Exception {
    JsonArray response = new JsonParser().parse(client.post(HubrisConstants.gamesListUrl, "type=init_player")).getAsJsonArray();
    return "meta:init_player".equals(response.get(0).getAsString());
  }
}
