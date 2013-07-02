package com.bpodgursky.hubris.script;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.util.BattleOutcome;

public class BattleSim {

  public static void main(String[] args) {

    BattleOutcome battleOutcome = HubrisUtil.getBattleOutcome(6, 4, 19, 45);


    System.out.println(battleOutcome);


    BattleOutcome battleOutcome2 = HubrisUtil.getBattleOutcome(6, 5, 10, 19);

    System.out.println(battleOutcome2);
  }

}
