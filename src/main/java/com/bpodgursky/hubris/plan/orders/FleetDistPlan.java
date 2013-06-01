package com.bpodgursky.hubris.plan.orders;

import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;

public abstract class FleetDistPlan {

  public static FleetDistPlan leaveOnStar(final int number){
    return new FleetDistPlan() {

      @Override
      public FleetBalance getBalance(int inFleet, int onStar) {
        int total = inFleet + onStar;
        int toTake = Math.max(1, total - number);

        return new FleetBalance(toTake, total - toTake);
      }

      public String toString(){
        return "[FleetDistPlan: Leaving on star: "+number+"]";
      }
    };

  }

  public static FleetDistPlan takeWithFleet(final int number){

    return new FleetDistPlan() {

      @Override
      public FleetBalance getBalance(int inFleet, int onStar) {
        int total = inFleet + onStar;
        int toTake = Math.min(number, total);

        return new FleetBalance(toTake, total - toTake);
      }

      public String toString(){
        return "[FleetDistPlan: Taking with fleet: "+number+"]";
      }
    };
  }


  public abstract FleetBalance getBalance(int inFleet, int onStar);

  public GameRequest makeTransfer(GameState state, String fleetName, int starId, CommandFactory factory) throws Exception {
    Fleet fleet = state.getFleet(fleetName);
    Star star = state.getStar(starId, false);

    int inFleet = fleet.getShips();
    int onStar = star.getShips();

    FleetBalance balance = getBalance(inFleet, onStar);

    return factory.transferShips(star.getId(), fleet.getId(), balance.onStar, balance.onShip);
  }

  public static class FleetBalance {

    private final int onShip;
    private final int onStar;

    public FleetBalance(int onShip, int onStar) {
      this.onShip = onShip;
      this.onStar = onStar;
    }
  }
}
