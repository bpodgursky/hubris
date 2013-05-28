package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.StarRevealedEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.collect.Lists;

import java.util.List;

public class StarRevealedFactory implements EventFactory<StarRevealedEvent> {
  @Override
  public List<StarRevealedEvent> getEvents(GameState newState) {
    GameState oldState = newState.previousState();
    List<StarRevealedEvent> events = Lists.newArrayList();

    for (Star star : newState.getAllStars(false)) {
      boolean isVisible = newState.starIsVisible(star.getId());

      if (isVisible && !oldState.starIsVisible(star.getId())) {
        events.add(new StarRevealedEvent(star.getId()));
      }
    }

    return events;
  }

  @Override
  public Class<StarRevealedEvent> getEventType() {
    return StarRevealedEvent.class;
  }
}
