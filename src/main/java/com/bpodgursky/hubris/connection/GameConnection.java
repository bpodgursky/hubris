package com.bpodgursky.hubris.connection;


import com.bpodgursky.hubris.command.*;
import com.bpodgursky.hubris.notification.GameNotification;
import com.bpodgursky.hubris.notification.Message;
import com.bpodgursky.hubris.universe.Comment;
import com.bpodgursky.hubris.universe.GameState;

import java.util.List;

public interface GameConnection {

  public GameState getState(GameState currentState, GetState getState) throws Exception;

  public List<GameNotification> getNotifications(GetEvents events) throws Exception;

  public List<Comment> getComments(GetMessageComments messageComments) throws Exception;

  public List<Message> getMessages(GetMessages messages) throws Exception;


  public void submit(GameRequest request) throws Exception;
}
