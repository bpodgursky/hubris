package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.helpers.ExploreHelper;
import com.bpodgursky.hubris.helpers.FleetHelper;
import com.bpodgursky.hubris.metric.DangerMetric;
import com.bpodgursky.hubris.plan.Plan;
import com.bpodgursky.hubris.plan.orders.FleetDistStrat;
import com.bpodgursky.hubris.state.AIStrategy;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScratchManager {
  private static final Logger LOG = LoggerFactory.getLogger(ScratchManager.class);

  static {
    HubrisUtil.startLogging();
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Syntax is: GameManager <settings_file.yml>");
      System.exit(1);
    }

    GenericManager.GameInfo info = GenericManager.login(args[0]);
    GameConnection connection = info.getConnection();
    CommandFactory factory = info.getFactory();

    GameState state = connection.getState(null, factory.getState());


    Plan plan = new Plan(factory, connection);
    AIStrategy strategy = new AIStrategy();
    strategy = strategy.update(state);


    ExploreHelper.planExplore(FleetHelper.getIdleFleets(state, plan), state, strategy, 1.0);

  }

}
