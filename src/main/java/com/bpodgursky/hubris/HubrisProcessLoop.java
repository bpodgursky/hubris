package com.bpodgursky.hubris;

import com.bpodgursky.hubris.client.GenericManager;
import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.event.StateProcessor;
import com.bpodgursky.hubris.listeners.*;
import com.bpodgursky.hubris.metric.SimpleShipProximityMetric;
import com.bpodgursky.hubris.universe.GameState;

public class HubrisProcessLoop {

  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.err.println("Syntax is: GameManager <settings_file.yml>");
      System.exit(1);
    }

    GenericManager.GameInfo info = GenericManager.login(args[0]);
    GameConnection connection = info.getConnection();
    CommandFactory factory = info.getFactory();

    StateProcessor processsor = new StateProcessor(connection, factory);

    processsor.addEventListener(new SpendOnIncomeListener(400, 1.0, 1.0, .5));
    processsor.addEventListener(new ReactiveDefense());
    processsor.addEventListener(new PrintNewCash());
    processsor.addEventListener(new PrintUpgrade());
    processsor.addEventListener(new PrintResearchChange());

    GameState currentState = null;
    currentState = connection.getState(currentState, factory.getState());

    double evaluate = SimpleShipProximityMetric.evaluate(currentState);

    System.out.println("total:");
    System.out.println(evaluate);

    while(true){
      currentState = connection.getState(currentState, factory.getState());
      processsor.update(currentState);

      Thread.sleep(20000);
    }
  }
}
