package com.bpodgursky.hubris.helpers;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

public class SpendHelper {
  private static final Logger LOG = LoggerFactory.getLogger(SpendHelper.class);

  private static final double ECON_NORM = 1.0;
  private static final double IND_NORM = 1.0;
  private static final double SCI_NORM = .1;

  public static final double DEFAULT_STAR_CARRIER_RATIO = 4.0;

  private static final int FLEET_COST = 25;

  public static Collection<GameRequest> planSpend(GameState state,
                                                  double econWeight,
                                                  double industryWeight,
                                                  double scienceWeight,
                                                  double maxStarCarrierRatio,
                                                  int escrow,
                                                  CommandFactory factory) throws Exception {

    econWeight = adjustInput(econWeight);
    industryWeight = adjustInput(industryWeight);
    scienceWeight = adjustInput(scienceWeight);

    int totalCash = state.getPlayer(state.getPlayerId()).getCash();
    int sumCost = 0;

    List<GameRequest> requests = Lists.newArrayList();


    Player p = state.getPlayer(state.getPlayerId());
    int carriers = p.getCarriers();
    int stars = p.getStars();

    int carriersToBuy = (int) ((stars - carriers * maxStarCarrierRatio) / maxStarCarrierRatio);

    LOG.info("Number of carriers to buy: "+carriersToBuy);

    List<Star> starsForFleets = HubrisUtil.getStars(state, new HubrisUtil.SortByShips(),
        Lists.newArrayList(new HubrisUtil.FriendlyStars(state.getPlayerId()), new HubrisUtil.StarsWithoutCarriers()));

    int boughtCarriers = 0;
    for(Star toPurchase: Lists.reverse(starsForFleets)){

      if(sumCost + FLEET_COST > totalCash - escrow){
        break;
      }

      if(toPurchase.getShips() == 0){
        break;
      }

      if(boughtCarriers >= carriersToBuy){
        break;
      }

      sumCost+=FLEET_COST;
      boughtCarriers++;

      requests.add(factory.createCarrier(toPurchase.getId(), toPurchase.getShips()));

      LOG.info("Buying a carrier on star " + toPurchase.getName());

    }

    PriorityQueue<StarUpgrade> queue = new PriorityQueue<StarUpgrade>();
    for (Star star : state.getAllStars(false)) {

      if (!state.starIsVisible(star.getId())) {
        continue;
      }

      if (star.getPlayerNumber() != state.getPlayerId()) {
        continue;
      }

      int nextEconLevel = star.getEconomy() + 1;
      int nextIndLevel = star.getIndustry() + 1;
      int nextSciLevel = star.getScience() + 1;

      int econCost = upgradeCost(UpgradeType.ECON, star.getResources(), nextEconLevel);
      int indCost = upgradeCost(UpgradeType.INDUSTRY, star.getResources(), nextIndLevel);
      int sciCost = upgradeCost(UpgradeType.SCIENCE, star.getResources(), nextSciLevel);

      if (econWeight < Double.MAX_VALUE) {
        queue.add(new StarUpgrade(star,
            UpgradeType.ECON,
            econCost,
            ECON_NORM * econCost * econWeight,
            nextEconLevel));
      }

      if (industryWeight < Double.MAX_VALUE) {
        queue.add(new StarUpgrade(star,
            UpgradeType.INDUSTRY,
            indCost,
            IND_NORM * indCost * industryWeight,
            nextIndLevel));
      }

      if (scienceWeight < Double.MAX_VALUE) {
        queue.add(new StarUpgrade(star,
            UpgradeType.SCIENCE,
            sciCost,
            SCI_NORM * sciCost * scienceWeight,
            nextSciLevel));
      }
    }

    while (!queue.isEmpty()) {
      StarUpgrade upgrade = queue.poll();

      if (sumCost + upgrade.realCost > totalCash - escrow) {
        continue;
      }

      LOG.info("Buying "+upgrade.type+" on star " + upgrade.star.getName());
      LOG.info("\tReal cost " + upgrade.realCost);
      LOG.info("\tAdusted cost " + upgrade.adjustedCost);
      LOG.info("\tBuying level " + upgrade.level);
      LOG.info("\tTotal cost so far of " + sumCost);

      sumCost += upgrade.realCost;
      int realNextCost = upgradeCost(upgrade.type, upgrade.star.getResources(), upgrade.level + 1);
      switch (upgrade.type) {
        case ECON:
          requests.add(factory.buyEconomy(upgrade.star.getId()));
          queue.add(new StarUpgrade(upgrade.star, upgrade.type,
              realNextCost,
              realNextCost * ECON_NORM * econWeight,
              upgrade.level + 1));
          break;
        case INDUSTRY:
          requests.add(factory.buyIndustry(upgrade.star.getId()));
          queue.add(new StarUpgrade(upgrade.star, upgrade.type,
              realNextCost,
              realNextCost * IND_NORM * industryWeight,
              upgrade.level + 1));
          break;
        case SCIENCE:
          requests.add(factory.buyScience(upgrade.star.getId()));
          queue.add(new StarUpgrade(upgrade.star,
              upgrade.type,
              realNextCost,
              realNextCost * SCI_NORM * scienceWeight,
              upgrade.level + 1));
          break;
        default:
          throw new RuntimeException();
      }
    }

    return requests;
  }

  private static double adjustInput(double priority) {
    if (priority == 0.0) {
      return Double.MAX_VALUE;
    }

    return 1.0 / priority;
  }

  private static int upgradeCost(UpgradeType type, int resources, int toLevel) {
    switch (type) {
      case ECON:
        return 500 * (toLevel) / (resources + 5);
      case INDUSTRY:
        return 500 * (toLevel) / (resources + 5);
      case SCIENCE:
        return 5000 * (toLevel) / (resources + 5);
      default:
        throw new RuntimeException();
    }
  }

  private enum UpgradeType {
    ECON, INDUSTRY, SCIENCE
  }

  private static class StarUpgrade implements Comparable<StarUpgrade> {

    private final Star star;
    private final UpgradeType type;
    private final int level;
    private final double adjustedCost;
    private final int realCost;

    private StarUpgrade(Star star, UpgradeType type, int realCost, double adjustedCost, int level) {
      this.realCost = realCost;
      this.type = type;
      this.adjustedCost = adjustedCost;
      this.level = level;
      this.star = star;
    }

    @Override
    public int compareTo(StarUpgrade o) {
      return Double.compare(adjustedCost, o.adjustedCost);
    }
  }
}
