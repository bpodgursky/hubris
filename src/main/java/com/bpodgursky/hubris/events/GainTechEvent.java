package com.bpodgursky.hubris.events;

public class GainTechEvent {
  private final int playerId;
  private final int gainedWeapons;
  private final double gainedRange;
  private final double gainedSpeed;
  private final double gainedScanning;

  public GainTechEvent(int playerId, int gainedWeapons, double gainedRange, double gainedSpeed, double gainedScanning) {
    this.playerId = playerId;
    this.gainedWeapons = gainedWeapons;
    this.gainedRange = gainedRange;
    this.gainedSpeed = gainedSpeed;
    this.gainedScanning = gainedScanning;
  }

  public int getPlayerId() {
    return playerId;
  }

  public int getGainedWeapons() {
    return gainedWeapons;
  }

  public double getGainedRange() {
    return gainedRange;
  }

  public double getGainedSpeed() {
    return gainedSpeed;
  }

  public double getGainedScanning() {
    return gainedScanning;
  }
}
