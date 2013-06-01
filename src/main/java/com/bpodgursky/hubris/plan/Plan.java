package com.bpodgursky.hubris.plan;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Plan {
  private static final Logger LOG = LoggerFactory.getLogger(Plan.class);

  private final Set<Order> nextOrders = Sets.newHashSet();
  private final Map<Order, Order.ExecuteResult> results = Maps.newHashMap();

  private final CommandFactory factory;
  private final GameConnection connection;

  public Plan(CommandFactory factory, GameConnection connection){
    this.factory = factory;
    this.connection = connection;
  }

  public void tick(GameState current) throws Exception {

    Iterator<Order> orderIter = nextOrders.iterator();

    while(orderIter.hasNext()){
      Order currentOrder = orderIter.next();

      if(isCancel(currentOrder)){
        LOG.info("Cancelling order: "+currentOrder);

        orderIter.remove();
      }

      if(canExeute(currentOrder, current)){
        LOG.info("Executing order; "+currentOrder);

        orderIter.remove();

        Order.ExecuteResult result = currentOrder.execute(current, factory, connection);
        results.put(currentOrder, result);

        if(result == Order.ExecuteResult.EXECUTED){
          LOG.info("Order was submitted: "+currentOrder);

          nextOrders.addAll(currentOrder.getChildren());
        } else{
          LOG.info("Order is no longer valid: "+currentOrder);
        }
      }
    }
  }

  private boolean isCancel(Order order){
    for(Order prereq: order.getPrereqs()){
      if(results.get(prereq) == Order.ExecuteResult.INVALID){
        return true;
      }
    }
    return false;
  }

  private boolean canExeute(Order order, GameState state){
    for(Order prereq: order.getPrereqs()){
      if(results.get(prereq) != Order.ExecuteResult.EXECUTED || !prereq.isComplete(state)){
        return false;
      }
    }
    return true;
  }

  public void schedule(Collection<Order> tailOrders){

    Queue<Order> orderQueue = Lists.newLinkedList(tailOrders);
    Set<Order> newHeads = Sets.newHashSet();

    while(!orderQueue.isEmpty()){
      Order order = orderQueue.poll();
      Set<Order> prereqs = order.getPrereqs();

      orderQueue.addAll(prereqs);

      if(prereqs.isEmpty()){
        newHeads.add(order);
      }
    }

    nextOrders.addAll(newHeads);
  }
}
