package com.bpodgursky.hubris.universe;

import com.google.common.collect.Maps;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class Tech {
  private final Integer requiredUpgradePoints;
  private final Integer currentResearchPoints;
  private final Integer currentLevel;
  private final TechType type;

  private final Double v;
  private final Double bv;
  private final Double sv;

  public Tech(TechType type, Integer currentLevel, Integer requiredUpgradePoints, Integer currentResearchPoints,
              Double v, Double bv, Double sv) {

    this.type = type;
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
      json.put("type", type);
      json.put("currentResearchPoints", currentResearchPoints);
      json.put("requiredUpgradePoints", requiredUpgradePoints);
      json.put("currentLevel", currentLevel);
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

  public TechType getType() {
    return type;
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

  public static Map<TechType, Tech> asMap(List<Tech> techs) {
    Map<TechType, Tech> map = Maps.newHashMap();
    for (Tech tech : techs) {
      map.put(tech.getType(), tech);
    }
    return map;
  }
}
