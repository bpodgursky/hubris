package com.bpodgursky.hubris.connection;


import com.bpodgursky.hubris.command.*;
import com.bpodgursky.hubris.notification.GameNotification;
import com.bpodgursky.hubris.notification.Message;
import com.bpodgursky.hubris.universe.Comment;
import com.bpodgursky.hubris.universe.GameState;

import java.util.List;

public interface GameConnection {

  public GameState getState(GameState currentState, GetState getState) throws Exception;

  public void sendTech(SendTech tech) throws Exception;

  public void sendMessage(SendMessage message) throws Exception;

  public void sendMessageComment(SendMessageComment comment) throws Exception;

  public List<GameNotification> getNotifications(GetEvents events) throws Exception;

  public List<Comment> getComments(GetMessageComments messageComments) throws Exception;

  public List<Message> getMessages(GetMessages messages) throws Exception;

  public void sendCash(SendCash cash) throws Exception;

  public void transferShips(TransferShips transfer) throws Exception;

  public void createCarrier(CreateCarrier createCarrier) throws Exception;

  public void clearAllPaths(ClearAllFleetPaths clear) throws Exception;

  public void clearFleetLast(ClearFleetLastPath clear) throws Exception;

  public void setWaypoint(SetWaypoint waypoint) throws Exception;

  public void setGarrison(SetGarrison garrison) throws Exception;

  public void buyEconomy(UpgradeEconomy upgrade) throws Exception;

  public void buyIndustry(UpgradeIndustry upgrade) throws Exception;

  public void buyScience(UpgradeScience science) throws Exception;

  public void setNextResearch(SetNextResearch research) throws Exception;

  public void setResearch(SetResearch research) throws Exception;


}
