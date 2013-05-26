package com.bpodgursky.hubris.universe;

import org.json.JSONException;
import org.json.JSONObject;


public class Tech {

  public final Integer requiredUpgradePoints;
  public final Integer currentResearchPoints;
  public final Integer currentLevel;
  public final String researchName;

  public final Double v;
  public final Double bv;
  public final Double sv;

  public Tech(String researchName, Integer currentLevel, Integer requiredUpgradePoints, Integer currentResearchPoints,
              Double v, Double bv, Double sv) {

    this.researchName = researchName;
    this.v = v;
    this.bv = bv;
    this.sv = sv;
    this.currentLevel = currentLevel;
    this.requiredUpgradePoints = requiredUpgradePoints;
    this.currentResearchPoints = currentResearchPoints;

  }

  public String toString() {

    JSONObject json = new JSONObject();
    try {
      json.put("researchName", researchName);
      json.put("currentResearchPoints", currentResearchPoints);
      json.put("requiredUpgradePoints", requiredUpgradePoints);
      json.put("currentLevel", currentLevel);
      json.put("researchName", researchName);
      json.put("v", v);
      json.put("bv", bv);
      json.put("sv", sv);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json.toString();
  }

  public Double getSv() {
    return sv;
  }

  public Double getBv() {
    return bv;
  }

  public Double getV() {
    return v;
  }

  public String getResearchName() {
    return researchName;
  }

  public Integer getCurrentLevel() {
    return currentLevel;
  }

  public Integer getCurrentResearchPoints() {
    return currentResearchPoints;
  }

  public Integer getRequiredUpgradePoints() {
    return requiredUpgradePoints;
  }
}
