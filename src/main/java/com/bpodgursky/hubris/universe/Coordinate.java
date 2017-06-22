package com.bpodgursky.hubris.universe;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Coordinate {
  private final int x;
  private final int y;

  public static final int FLOAT_TO_INT_FACTOR = 1000;

  public Coordinate(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public static Coordinate from(int x, int y){
    return new Coordinate(x, y);
  }

  public double distance(Coordinate other){
    return Math.sqrt(Math.pow(x-other.x, 2) + Math.pow(y-other.y, 2));
  }

  @Override
  public String toString() {
    return "Coordinate{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }

  public static class Deserializer implements JsonDeserializer<Coordinate> {
    @Override
    public Coordinate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject obj = jsonElement.getAsJsonObject();

      return new Coordinate(
          (int)Math.round(Double.parseDouble(obj.get("x").getAsString())*FLOAT_TO_INT_FACTOR),
          (int)Math.round(Double.parseDouble(obj.get("y").getAsString())*FLOAT_TO_INT_FACTOR)
      );
    }
  }
}
