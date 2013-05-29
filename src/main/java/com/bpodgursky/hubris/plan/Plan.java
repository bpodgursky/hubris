package com.bpodgursky.hubris.plan;

import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Sets;
import org.jgrapht.DirectedGraph;

import java.util.Set;

public class Plan {

  private final Set<Order> ordersInProgress = Sets.newHashSet();

  public Plan(){
  }

  public void tick(GameState current){

    for(Order currentOrder: ordersInProgress){

    }

  }

}
