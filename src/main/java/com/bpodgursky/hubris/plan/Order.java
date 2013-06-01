package com.bpodgursky.hubris.plan;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Sets;

import java.util.Set;

public abstract class Order {

  private final Set<Order> prereqs;
  private final Set<Order> children = Sets.newHashSet();

  public static enum ExecuteResult {
    WAITING, EXECUTED, INVALID
  }

  protected Order(Set<Order> prereqs){
    this.prereqs = prereqs;
    for(Order pre: prereqs){
      pre.addChild(this);
    }
  }

  public abstract ExecuteResult execute(GameState state, CommandFactory factory, GameConnection connection) throws Exception;
  public abstract boolean isComplete(GameState state);

  public Set<Order> getPrereqs(){
    return prereqs;
  }

  //  TODO need to cleanup, split out dependency graph from impl or something.  Separate interface for execute, dunno
  private void addChild(Order parent){
    this.children.add(parent);
  }

  public Set<Order> getChildren(){
    return children;
  }
}
