package com.bpodgursky.hubris.events;

public class StarUpgradedEvent {

  private final int econChange;
  private final int industryChange;
  private final int scienceChange;

  public StarUpgradedEvent(int econChange, int industryChange, int scienceChange) {
    this.econChange = econChange;
    this.industryChange = industryChange;
    this.scienceChange = scienceChange;
  }

  public int getEconChange(){
    return econChange;
  }

  public int getIndustryChange(){
    return industryChange;
  }

  public int getScienceChange(){
    return scienceChange;
  }
}


