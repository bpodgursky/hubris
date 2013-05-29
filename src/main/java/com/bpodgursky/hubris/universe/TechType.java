package com.bpodgursky.hubris.universe;

/**
 * 
 */
public enum TechType {
	WEAPONS("fleet_combat"),
	SPEED("fleet_speed"),
	RANGE("fleet_range"),
	SCANNING("scanning_range");

  private final String stringValue;
  TechType(String stringValue) {
    this.stringValue = stringValue;
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
