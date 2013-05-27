package com.bpodgursky.hubris.event;

import com.bpodgursky.hubris.universe.GameState;

public interface EventListener<E> {
  public void process(E event, GameState currentState);
  public Class<E> getEventType();
}
