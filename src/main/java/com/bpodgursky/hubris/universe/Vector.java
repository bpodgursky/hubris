package com.bpodgursky.hubris.universe;

import java.util.List;

public class Vector {

  private final double xdir;
  private final double ydir;

  public Vector(double yVector, double xVector) {
    this.ydir = yVector;
    this.xdir = xVector;
  }

  public double getXdir() {
    return xdir;
  }

  public double getYdir() {
    return ydir;
  }

  public static Vector normalizedSum(List<Vector> vectors){
    double xSum = 0;
    double ySum = 0;

    for (Vector vector : vectors) {
      xSum += vector.getXdir();
      ySum += vector.getYdir();
    }

    double mag = Math.sqrt(Math.pow(xSum, 2)+Math.pow(ySum, 2));

    return new Vector(xSum/mag, ySum/mag);
  }
}
