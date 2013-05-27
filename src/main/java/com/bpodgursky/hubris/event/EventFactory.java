package com.bpodgursky.hubris.event;

import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Lists;

import java.util.List;

public interface EventFactory<E> {
  public List<E> getEvents(GameState oldState, GameState newState);
  public Class<E> getEventType();
}
