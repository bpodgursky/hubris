package com.bpodgursky.hubris.plan;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.plan.orders.BalanceFleets;
import com.bpodgursky.hubris.plan.orders.MoveFleet;
import com.bpodgursky.hubris.universe.GameState;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Plan {
  private static final Logger LOG = LoggerFactory.getLogger(Plan.class);

  private final Set<Order> nextOrders = Sets.newHashSet();
  private final Map<Order, Order.ExecuteResult> results = Maps.newHashMap();

  private final Multimap<String, Order> orderForFleet = HashMultimap.create();

  private final CommandFactory factory;
  private final GameConnection connection;

  public Plan(CommandFactory factory, GameConnection connection){
    this.factory = factory;
    this.connection = connection;
  }

  private void removeOrder(Order order, Iterator<Order> iter){
    iter.remove();

    String fleet = getFleet(order);
    if(fleet != null){
      orderForFleet.remove(fleet, order);
    }
  }

  public boolean isFleetIdle(String fleet){
    return orderForFleet.get(fleet).isEmpty();
  }

  public void tick(GameState current) throws Exception {

    Iterator<Order> orderIter = nextOrders.iterator();
    Set<Order> toAdd = Sets.newHashSet();

    while(orderIter.hasNext()){
      Order currentOrder = orderIter.next();

      if(isCancel(currentOrder)){
        LOG.info("Cancelling order: "+currentOrder);

        removeOrder(currentOrder, orderIter);
      }

      if(canExeute(currentOrder, current)){
        LOG.info("Executing order; "+currentOrder);

        removeOrder(currentOrder, orderIter);

        Order.ExecuteResult result = currentOrder.execute(current, factory, connection);
        results.put(currentOrder, result);

        if(result == Order.ExecuteResult.EXECUTED){
          LOG.info("Order was submitted: "+currentOrder);
          toAdd.addAll(currentOrder.getChildren());
        } else{
          LOG.info("Order is no longer valid: "+currentOrder);
        }
      }
    }

    nextOrders.addAll(toAdd);
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


      String fleet = getFleet(order);

      if(fleet != null){
        orderForFleet.put(fleet, order);
      }
    }

    nextOrders.addAll(newHeads);
  }

  private static String getFleet(Order order){
    if(order instanceof MoveFleet){
      return ((MoveFleet)order).getFleetName();
    }else if(order instanceof BalanceFleets){
      return ((BalanceFleets)order).getFleetName();
    }
    return null;
  }
}
