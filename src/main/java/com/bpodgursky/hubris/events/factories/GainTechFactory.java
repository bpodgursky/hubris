package com.bpodgursky.hubris.events.factories;

import com.bpodgursky.hubris.events.GainTechEvent;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.google.common.collect.Lists;

import java.util.List;

public class GainTechFactory implements EventFactory<GainTechEvent> {
  @Override
  public List<GainTechEvent> getEvents(GameState newState) {
    GameState oldState = newState.previousState();
    List<GainTechEvent> events = Lists.newArrayList();

    for (Player player : newState.getPlayers().values()) {
      Player oldPlayer = oldState.getPlayer(player.getId());
      int weapons = player.getWeapons() - oldPlayer.getWeapons();
      double range = player.getRange() - oldPlayer.getRange();
      double speed = player.getSpeed() - oldPlayer.getSpeed();
      double scanning = player.getScanning() - oldPlayer.getScanning();

      if (weapons > 0 || range > 0 || speed > 0 || scanning > 0) {
        events.add(new GainTechEvent(player.getId(), weapons, range, speed, scanning));
      }
    }

    return events;
  }

  @Override
  public Class<GainTechEvent> getEventType() {
    return GainTechEvent.class;
  }
}
