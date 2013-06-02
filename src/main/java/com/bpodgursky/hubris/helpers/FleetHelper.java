package com.bpodgursky.hubris.helpers;

import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Lists;

import java.util.List;

public class FleetHelper {

  public static List<Fleet> getIdleFleets(GameState state){

    List<Fleet> idle = Lists.newArrayList();
    for(Fleet f: state.getAllFleets()){
      if(f.getPlayer() != state.getPlayerId()){
        continue;
      }

      if(!f.getDestinations().isEmpty()){
        continue;
      }

      idle.add(f);
    }

    return idle;
  }
}
