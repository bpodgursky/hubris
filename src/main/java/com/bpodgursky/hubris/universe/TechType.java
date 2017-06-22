package com.bpodgursky.hubris.universe;

/**
 * 
 */
public enum TechType {
	WEAPONS("fleet_combat", 1d),
	RANGE("propulsion", 1d),
	SCANNING("scanning_range", 1d),
  TERRAFORMING("terraforming", 1d),
  EXPERIMENTATION("experimentation", 1d),
  BANKING("banking", 1d),
  MANUFACTURING("manufacturing", 1d),;

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
    if (id == null) {
      return null;
    }

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
