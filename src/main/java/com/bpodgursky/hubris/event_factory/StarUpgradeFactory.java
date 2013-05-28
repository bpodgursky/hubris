package com.bpodgursky.hubris.event_factory;

import com.bpodgursky.hubris.event.EventFactory;
import com.bpodgursky.hubris.events.StarUpgradedEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;

import java.util.List;

public class StarUpgradeFactory implements EventFactory<StarUpgradedEvent> {

  @Override
  public List<StarUpgradedEvent> getEvents(GameState newState) {
    GameState prevState = newState.previousState();

    for (Star star : newState.getStars(false)) {
      Star prevStar = prevState.getStar(star.getId(), false);

      if(star.isVisible())
    }
  }

  @Override
  public Class<StarUpgradedEvent> getEventType() {

  }
}
