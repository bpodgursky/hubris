package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.ResearchChangeEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.TechType;
import com.google.common.collect.Lists;

import java.util.List;

public class ChangeResearchFactory implements EventFactory<ResearchChangeEvent> {

  @Override
  public List<ResearchChangeEvent> getEvents(GameState newState) {
    GameState prevState = newState.previousState();

    List<ResearchChangeEvent> researchChanges = Lists.newArrayList();
    for (Player player : newState.getAllPlayers()) {
      TechType currentResearch = player.getCurrentResearch();
      TechType oldResearch = prevState.getPlayer(player.getId()).getCurrentResearch();

      if(currentResearch != oldResearch){
        researchChanges.add(new ResearchChangeEvent(player.getId(), oldResearch, currentResearch));
      }
    }

    return researchChanges;
  }

  @Override
  public Class<ResearchChangeEvent> getEventType() {
    return ResearchChangeEvent.class;
  }
}
