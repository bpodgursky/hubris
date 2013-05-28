package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.command.ClearAllFleetPaths;
import com.bpodgursky.hubris.command.ClearFleetLastPath;
import com.bpodgursky.hubris.command.CreateCarrier;
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

public class CommandFactory {
  private final String userName;
  private Integer playerNumber;
  private final Long gameNumber;

  public CommandFactory(String userName, Long gameNumber, int playerNumber) throws Exception {
    this.userName = userName;
    this.gameNumber = gameNumber;
    this.playerNumber = playerNumber;
  }

  public GetState getState(){
    return new GetState(playerNumber, userName, gameNumber);
  }

  public SendTech sendTech(TechType tech, Integer to) throws Exception {
    return new SendTech(playerNumber, userName, gameNumber, tech.getGameId(), to);
  }

  public SendMessage sendMessage(String subject, String body, List<Integer> to) throws Exception {
    return new SendMessage(playerNumber, userName, gameNumber, subject, body, to);
  }

  public SendMessageComment sendMessageComment(String key, String body) throws Exception {
    return new SendMessageComment(playerNumber, userName, gameNumber, key, body);
  }

  public GetMessageComments getMessageComments(String messageKey, Integer number, Integer offset) throws Exception {
    return new GetMessageComments(playerNumber, userName, gameNumber, messageKey, offset, number);
  }

  public SendCash sendCash(Integer destination, Integer amount) throws TransformerFactoryConfigurationError, Exception {
    return new SendCash(playerNumber, userName, gameNumber, destination, amount);
  }

  public TransferShips transferShips(Integer id1, Integer id2, Integer loc1Final, Integer loc2Final) throws Exception {
    return new TransferShips(playerNumber, userName, gameNumber, id1, id2, loc1Final, loc2Final);
  }

  public CreateCarrier createCarrier(Integer star, Integer strength) throws TransformerFactoryConfigurationError, Exception {
    return new CreateCarrier(playerNumber, userName, gameNumber, star, strength);
  }

  public ClearAllFleetPaths clearAllFleetPaths(Integer fleet) throws Exception {
    return new ClearAllFleetPaths(playerNumber, userName, gameNumber, fleet);
  }

  public ClearFleetLastPath clearFleetLastPath(Integer fleet) throws TransformerFactoryConfigurationError, Exception {
    return new ClearFleetLastPath(playerNumber, userName, gameNumber, fleet);
  }

  public SetWaypoint setWaypoint(Integer fleet, Integer star) throws TransformerFactoryConfigurationError, Exception {
    return new SetWaypoint(playerNumber, userName, gameNumber, fleet, star);
  }

  public SetGarrison setGarrison(Integer star, Integer size) throws TransformerFactoryConfigurationError, Exception {
    return new SetGarrison(playerNumber, userName, gameNumber, star, size);
  }

  public UpgradeEconomy buyEconomy(Integer star) throws Exception {
    return new UpgradeEconomy(playerNumber, userName, gameNumber, star);
  }

  public UpgradeIndustry buyIndustry(Integer star) throws Exception {
    return new UpgradeIndustry(playerNumber, userName, gameNumber, star);
  }

  public UpgradeScience buyScience(Integer star) throws Exception {
    return new UpgradeScience(playerNumber, userName, gameNumber, star);
  }

  public SetNextResearch setNextResearch(String researchName) throws TransformerFactoryConfigurationError, Exception {
    return new SetNextResearch(playerNumber, userName, gameNumber, researchName);
  }

  public SetResearch setResearch(TechType research) throws Exception {
    return new SetResearch(playerNumber, userName, gameNumber, research.getGameId());
  }
}
