package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.command.ClearAllFleetPaths;
import com.bpodgursky.hubris.command.ClearFleetLastPath;
import com.bpodgursky.hubris.command.CreateCarrier;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.command.GetEvents;
import com.bpodgursky.hubris.command.GetMessageComments;
import com.bpodgursky.hubris.command.GetMessages;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.command.SendCash;
import com.bpodgursky.hubris.command.SendMessage;
import com.bpodgursky.hubris.command.SendMessageComment;
import com.bpodgursky.hubris.command.SendTech;
import com.bpodgursky.hubris.command.SetGarrison;
import com.bpodgursky.hubris.command.SetNextResearch;
import com.bpodgursky.hubris.command.SetResearch;
import com.bpodgursky.hubris.command.SetWaypoint;
import com.bpodgursky.hubris.command.TransferShips;
import com.bpodgursky.hubris.command.UpgradeEconomy;
import com.bpodgursky.hubris.command.UpgradeIndustry;
import com.bpodgursky.hubris.command.UpgradeScience;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.notification.GameNotification;
import com.bpodgursky.hubris.notification.Message;
import com.bpodgursky.hubris.universe.Comment;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.TechType;

import javax.xml.transform.TransformerFactoryConfigurationError;
import java.util.List;

public class SingleGameClient {
  private final String userName;
  private Integer playerNumber;
  private final Long gameNumber;
  private final GameConnection connection;

  public SingleGameClient(String userName, Long gameNumber, GameConnection connection) throws Exception {
    this.userName = userName;
    this.gameNumber = gameNumber;
    this.connection = connection;
    this.playerNumber = 0;

    // Fetch player number
    playerNumber = Integer.parseInt(getState().gameData.getMid());
  }

  public void sendTech(TechType tech, Integer to) throws Exception {
    send(new SendTech(playerNumber, userName, gameNumber, tech.getGameId(), to));
  }

  public void sendMessage(String subject, String body, List<Integer> to) throws Exception {
    send(new SendMessage(playerNumber, userName, gameNumber, subject, body, to));
  }

  public void sendMessageComment(String key, String body) throws Exception {
    send(new SendMessageComment(playerNumber, userName, gameNumber, key, body));
  }

  public List<GameNotification> getEvents(Integer offset, Integer number) throws Exception {
    return send(new GetEvents(playerNumber, userName, gameNumber, offset, number));
  }

  public List<Comment> getMessageComments(String messageKey, Integer number, Integer offset) throws Exception {
    return send(new GetMessageComments(playerNumber, userName, gameNumber, messageKey, offset, number));
  }

  public List<Message> getMessages(Integer offset, Integer number) throws Exception {
    return send(new GetMessages(playerNumber, userName, gameNumber, number, offset));
  }

  public void sendCash(Integer destination, Integer amount) throws TransformerFactoryConfigurationError, Exception {
    send(new SendCash(playerNumber, userName, gameNumber, destination, amount));
  }

  public void transferShips(Integer id1, Integer id2, Integer loc1Final, Integer loc2Final) throws Exception {
    send(new TransferShips(playerNumber, userName, gameNumber, id1, id2, loc1Final, loc2Final));
  }

  public void createCarrier(Integer star, Integer strength) throws TransformerFactoryConfigurationError, Exception {
    send(new CreateCarrier(playerNumber, userName, gameNumber, star, strength));
  }

  public void clearAllFleetPaths(Integer fleet) throws Exception {
    send(new ClearAllFleetPaths(playerNumber, userName, gameNumber, fleet));
  }

  public void clearFleetLastPath(Integer fleet) throws TransformerFactoryConfigurationError, Exception {
    send(new ClearFleetLastPath(playerNumber, userName, gameNumber, fleet));
  }

  public void setWaypoint(Integer fleet, Integer star) throws TransformerFactoryConfigurationError, Exception {
    send(new SetWaypoint(playerNumber, userName, gameNumber, fleet, star));
  }

  public void setGarrison(Integer star, Integer size) throws TransformerFactoryConfigurationError, Exception {
    send(new SetGarrison(playerNumber, userName, gameNumber, star, size));
  }

  public void buyEconomy(Integer star) throws Exception {
    send(new UpgradeEconomy(playerNumber, userName, gameNumber, star));
  }

  public void buyIndustry(Integer star) throws Exception {
    send(new UpgradeIndustry(playerNumber, userName, gameNumber, star));
  }

  public void buyScience(Integer star) throws Exception {
    send(new UpgradeScience(playerNumber, userName, gameNumber, star));
  }

  public void setNextResearch(String researchName) throws TransformerFactoryConfigurationError, Exception {
    send(new SetNextResearch(playerNumber, userName, gameNumber, researchName));
  }

  public void setResearch(TechType research) throws Exception {
    send(new SetResearch(playerNumber, userName, gameNumber, research.getGameId()));
  }

  public <R> R send(GameRequest request) throws Exception {
    return connection.sendRequest(request);
  }

  public GameState getState() throws TransformerFactoryConfigurationError, Exception {
    return send(new GetState(playerNumber, userName, gameNumber));
  }
}
