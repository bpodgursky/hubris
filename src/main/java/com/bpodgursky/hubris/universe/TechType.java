package com.bpodgursky.hubris.universe;

/**
 * 
 */
public enum TechType {
	WEAPONS("fleet_combat", 1d),
	SPEED("fleet_speed", 0.24),
	RANGE("fleet_range", 0.25),
	SCANNING("scanning_range", 0.10);

  private final String stringValue;
  private final double upgradePoints;
  TechType(String stringValue, double upgradePoints) {
    this.stringValue = stringValue;
    this.upgradePoints = upgradePoints;
  }

  /**
	 * Gets the string NP uses to identify this type
	 * 
	 * @return
	 */
	public String getStringValue() {
    return stringValue;
	}

  /**
   * Gets the number of points per level you gain when upgrading this tech
   *
   * @return
   */
  public double getUpgradePoints() {
    return upgradePoints;
  }

  /**
	 * 
	 * @param id
	 * @return
	 */
	public static TechType fromStringValue(String id) {
    for (TechType techType : values()) {
      if (techType.getStringValue().equals(id)) {
        return techType;
      }
    }
    throw new IllegalArgumentException("Invalid tech type: " + id);
	}

  @Override
  public String toString() {
    return getStringValue();
  }
}
