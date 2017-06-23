package com.bpodgursky.hubris.universe;


import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Tech {
  private final Integer requiredUpgradePoints;
  private final Integer research;
  private final Integer level;

  private final Double brr;
  private final Double bv;
  private final Double sv;
  private final Double value;

  public Tech(Integer research,
              Integer level,
              Double brr,
              Double bv,
              Double sv,
              Double value) {
    if (research != null && brr != null) {
      this.requiredUpgradePoints = (int)Math.round(brr * level);
    } else {
      this.requiredUpgradePoints = null;
    }
    this.research = research;
    this.level = level;
    this.brr = brr;
    this.bv = bv;
    this.sv = sv;
    this.value = value;
  }

  public Integer getRequiredUpgradePoints() {
    return requiredUpgradePoints;
  }

  public Integer getResearch() {
    return research;
  }

  public Integer getLevel() {
    return level;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static class Deserializer implements JsonDeserializer<Tech> {
    @Override
    public Tech deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = jsonElement.getAsJsonObject();

      return new Tech(
          context.deserialize(obj.get("research"), Integer.class),
          context.deserialize(obj.get("level"), Integer.class),
          context.deserialize(obj.get("brr"), Double.class),
          context.deserialize(obj.get("bv"), Double.class),
          context.deserialize(obj.get("sv"), Double.class),
          context.deserialize(obj.get("value"), Double.class)
      );
    }
  }
}
