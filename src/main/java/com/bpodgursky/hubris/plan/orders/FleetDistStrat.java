package com.bpodgursky.hubris.plan.orders;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;

import java.util.List;

public abstract class FleetDistStrat {


  public static FleetDistStrat defensiveDist(){
    return new FleetDistStrat() {
      @Override
      public FleetBalance getBalance(int inFleet, int onStar, String fleetName, String starName, GameState state) {

        double maxRange = HubrisUtil.getLongestJumpRange(state);
        Star star = state.getStar(starName, false);

        List<Star> stars = HubrisUtil.getEnemyStarsInRange(state, state.getPlayerId(), star.getCoords(), maxRange);

        int defense = 0;
        for(Star otherStar: stars){
          if(otherStar.getPlayerNumber() != null){
            if(HubrisUtil.canReach(otherStar.getId(), star.getId(), state)){
              Integer extra = otherStar.getShipsIncludingFleets(state);
              if(extra != null){
                defense += extra;
              }
            }
          }
        }

        defense = Math.max(defense, 1);

        return leaveOnStar(defense, inFleet, onStar);
      }

      public String toString(){
        return "[FleetDistStrat: defensive strategy]";
      }

    };
  }

  protected static FleetBalance leaveOnStar(final int number, int inFleet, int onStar){
    int total = inFleet + onStar;
    int toTake = Math.max(1, total - number);

    return new FleetBalance(toTake, total - toTake);
  }

  public static FleetDistStrat leaveOnStar(final int number){
    return new FleetDistStrat() {

      @Override
      public FleetBalance getBalance(int inFleet, int onStar, String fleetName, String starName, GameState state) {
        return leaveOnStar(number, inFleet, onStar);
      }

      public String toString(){
        return "[FleetDistStrat: Leaving on star: "+number+"]";
      }
    };
  }

  protected static FleetBalance takeWithFleet(final int number, int inFleet, int onStar){
    int total = inFleet + onStar;
    int toTake = Math.min(number, total);

    return new FleetBalance(toTake, total - toTake);
  }

  public static FleetDistStrat takeWithFleet(final int number){

    return new FleetDistStrat() {

      @Override
      public FleetBalance getBalance(int inFleet, int onStar, String fleetName, String starName, GameState state) {
        return takeWithFleet(number, inFleet, onStar);
      }

      public String toString(){
        return "[FleetDistStrat: Taking with fleet: "+number+"]";
      }
    };
  }


  public abstract FleetBalance getBalance(int inFleet, int onStar, String fleet, String star, GameState state);

  public GameRequest makeTransfer(GameState state, String fleetName, int starId, CommandFactory factory) throws Exception {
    Fleet fleet = state.getFleet(fleetName);
    Star star = state.getStar(starId, false);

    int inFleet = fleet.getShips();
    int onStar = star.getShips();

    FleetBalance balance = getBalance(inFleet, onStar, fleet.getName(), star.getName(), state);

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
