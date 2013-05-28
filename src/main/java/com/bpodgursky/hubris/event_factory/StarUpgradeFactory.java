package com.bpodgursky.hubris.event_factory;

import com.bpodgursky.hubris.events.StarUpgradedEvent;
import com.bpodgursky.hubris.events.factories.EventFactory;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.collect.Lists;

import java.util.List;

public class StarUpgradeFactory implements EventFactory<StarUpgradedEvent> {

  @Override
  public List<StarUpgradedEvent> getEvents(GameState newState) {
    GameState prevState = newState.previousState();

    List<StarUpgradedEvent> upgrades = Lists.newArrayList();
    for (Star star : newState.getAllStars(false)) {
      if(prevState.starIsVisible(star.getId()) && newState.starIsVisible(star.getId())){
        Star prevStar = prevState.getStar(star.getId(), false);

        int econChange = star.economy - prevStar.economy;
        int indChange = star.industry - prevStar.industry;
        int sciChange = star.science - prevStar.science;

        if(econChange != 0 || indChange != 0 || sciChange != 0){
          upgrades.add(new StarUpgradedEvent(star.getId(), econChange, indChange, sciChange));
        }
      }
    }

    return upgrades;
  }

  @Override
  public Class<StarUpgradedEvent> getEventType() {
    return StarUpgradedEvent.class;
  }
}
