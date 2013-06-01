package com.bpodgursky.hubris.plan.orders;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.universe.GameState;

import java.util.List;
import java.util.Set;

public class AddToList extends Order {

  private final String name;
  private final List<String> target;

  public AddToList(Set<Order> prereqs, String name, List<String> target) {
    super(prereqs);

    this.name = name;
    this.target = target;
  }

  @Override
  public ExecuteResult execute(GameState state, CommandFactory factory, GameConnection connection) throws Exception {
    target.add(name);

    return ExecuteResult.EXECUTED;
  }

  @Override
  public boolean isComplete(GameState state) {
    return true;
  }

  public String toString(){
    return "Add to list: "+name;
  }
}
