package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.event.StateProcessor;
import com.bpodgursky.hubris.helpers.ExploreHelper;
import com.bpodgursky.hubris.helpers.FleetHelper;
import com.bpodgursky.hubris.helpers.SpendHelper;
import com.bpodgursky.hubris.listeners.SpendOnIncomeListener;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.plan.Plan;
import com.bpodgursky.hubris.plan.orders.FleetDistStrat;
import com.bpodgursky.hubris.state.AIStrategy;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameManager {
  private static final Logger LOG = LoggerFactory.getLogger(GameManager.class);

  static {
    HubrisUtil.startLogging();
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Syntax is: GameManager <settings_file.yml>");
      System.exit(1);
    }

    GenericManager.GameInfo info = GenericManager.login(args[0]);
    process(info.getConnection(), info.getFactory());
  }

  private static void process(GameConnection connection, CommandFactory factory) throws Exception {

    Plan plan = new Plan(factory, connection);

    GameState currentState = null;
    StateProcessor processsor = new StateProcessor(connection, factory);
    AIStrategy strategy = new AIStrategy();

    processsor.addEventListener(new SpendOnIncomeListener(0, 1.0, 1.0, 0.5));

    currentState = connection.getState(currentState, factory.getState());
    for(GameRequest request: SpendHelper.planSpend(currentState, 1.0, 1.0, 0.5, SpendHelper.DEFAULT_STAR_CARRIER_RATIO, 0, factory)){
      connection.submit(request);
    }

    while (true) {
      try {

        GameState newState = connection.getState(currentState, factory.getState());
        strategy = strategy.update(currentState);

        plan.tick(currentState);
        processsor.update(currentState, newState);

        List<Fleet> idleFleets = FleetHelper.getIdleFleets(currentState, plan);
        LOG.info("Found idle fleets:" +idleFleets);

        Collection<Order> orders = ExploreHelper.planExplore(idleFleets, currentState, strategy, 1.0);

        LOG.info("New orders: "+orders);

        plan.schedule(orders);

        currentState = newState;

      } catch (Exception e) {
        e.printStackTrace();
      }

      Thread.sleep(20000);
    }

  }
}
