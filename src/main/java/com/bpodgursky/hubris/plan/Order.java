package com.bpodgursky.hubris.plan;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.universe.GameState;

public interface Order {
  public abstract boolean isComplete(GameState state);
  public abstract boolean isValid(GameState state);
  public abstract GameRequest makeRequest(GameState state, CommandFactory factory) throws Exception;
}
