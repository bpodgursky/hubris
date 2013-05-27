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

import java.util.List;

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
  public GameState getState(GetState getState) throws Exception {
    return ResponseTransformer.parseUniverse(getState, post(getState));
  }

  @Override
  public void sendTech(SendTech tech) throws Exception {
    client.post(HubrisConstants.gameRequestUrl, tech.toRequestParams());
  }

  @Override
  public void sendMessage(SendMessage message) throws Exception {
    post(message);
  }

  @Override
  public void sendMessageComment(SendMessageComment comment) throws Exception {
    post(comment);
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
  public void sendCash(SendCash cash) throws Exception {
    post(cash);
  }

  @Override
  public void transferShips(TransferShips transfer) throws Exception {
    post(transfer);
  }

  @Override
  public void createCarrier(CreateCarrier createCarrier) throws Exception {
    post(createCarrier);
  }

  @Override
  public void clearAllPaths(ClearAllFleetPaths clear) throws Exception {
    post(clear);
  }

  @Override
  public void clearFleetLast(ClearFleetLastPath clear) throws Exception {
    post(clear);
  }

  @Override
  public void setWaypoint(SetWaypoint waypoint) throws Exception {
    post(waypoint);
  }

  @Override
  public void setGarrison(SetGarrison garrison) throws Exception {
    post(garrison);
  }

  @Override
  public void buyEconomy(UpgradeEconomy upgrade) throws Exception {
    post(upgrade);
  }

  @Override
  public void buyIndustry(UpgradeIndustry upgrade) throws Exception {
    post(upgrade);
  }

  @Override
  public void buyScience(UpgradeScience science) throws Exception {
    post(science);
  }

  @Override
  public void setNextResearch(SetNextResearch research) throws Exception {
    post(research);
  }

  @Override
  public void setResearch(SetResearch research) throws Exception {
    post(research);
  }
}
