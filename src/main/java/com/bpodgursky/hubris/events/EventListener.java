package com.bpodgursky.hubris.events;

import com.bpodgursky.hubris.universe.GameState;

import java.util.List;

public interface EventListener<E> {
  public void process(List<E> event, GameState currentState);
  public Class<E> getEventType();
}
