package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Fleet;

import java.util.Comparator;

public class SortFleetByShips implements Comparator<Fleet> {

  @Override
  public int compare(Fleet o1, Fleet o2) {
    return new Integer(getShips(o1)).compareTo(getShips(o2));
  }

  private int getShips(Fleet fleet){
    if(fleet.getShips() == null){
      return -1;
    }

    return fleet.getShips();
  }
}
