package com.bpodgursky.hubris.util;

import com.bpodgursky.hubris.universe.Star;

import java.util.Comparator;

public class SortStarByShips implements Comparator<Star> {

  @Override
  public int compare(Star o1, Star o2) {
    return new Integer(getShips(o1)).compareTo(getShips(o2));
  }

  private int getShips(Star star){
    if(star.getShips() == null){
      return -1;
    }

    return star.getShips();
  }
}
