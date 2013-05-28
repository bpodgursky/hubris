package com.bpodgursky.hubris.events;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.universe.GameState;

import java.util.Collection;
import java.util.List;

public interface EventListener<E> {
  public List<GameRequest> process(Collection<E> events, GameState currentState, CommandFactory commandFactory);
  public Class<E> getEventType();
}
