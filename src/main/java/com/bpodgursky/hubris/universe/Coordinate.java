package com.bpodgursky.hubris.universe;

public class Coordinate {
  private final int x;
  private final int y;

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
}
