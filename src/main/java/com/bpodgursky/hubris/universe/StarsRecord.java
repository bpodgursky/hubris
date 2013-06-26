package com.bpodgursky.hubris.universe;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public class StarsRecord {
  private final Map<Integer, Star> starsByID;
  private final Map<String, Star> starsByName;

  StarsRecord(Map<Integer, Star> starsByID) {
    this.starsByID = starsByID;
    this.starsByName = Maps.newHashMap();

    for (Map.Entry<Integer, Star> entry : starsByID.entrySet()) {
      starsByName.put(entry.getValue().getName(), entry.getValue());
    }
  }

  public Star get(String name){
    return starsByName.get(name);
  }

  public Star get(int id){
    return starsByID.get(id);
  }

  public Collection<Star> getAllStars(){
    return starsByID.values();
  }
}
