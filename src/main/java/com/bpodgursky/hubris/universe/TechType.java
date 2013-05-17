package com.bpodgursky.hubris.universe;

/**
 * 
 */
public enum TechType {
	WEAPONS,
	SPEED,
	RANGE,
	SCANNING;
	
	private static String WEAPONS_STR = "fleet_combat";
	private static String SPEED_STR = "fleet_speed";
	private static String RANGE_STR = "fleet_range";
	private static String SCANNING_STR = "scanning_range";
	
	/**
	 * Gets the string NP uses to identify this type
	 * 
	 * @return
	 */
	public String getGameId() {
		switch (this) {
		case WEAPONS:
			return WEAPONS_STR;
		case SPEED:
			return SPEED_STR;
		case RANGE:
			return RANGE_STR;
		case SCANNING:
			return SCANNING_STR;
		default:
			return null; // this shouldn't happen
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static TechType fromGameId(String id) {
		if (id.equals(WEAPONS_STR)) {
			return WEAPONS;
		}
		else if (id.equals(SPEED_STR)) {
			return SPEED;
		}
		else if (id.equals(RANGE_STR)) {
			return RANGE;
		}
		else if (id.equals(SCANNING_STR)) {
			return SCANNING;
		}
		else {
			throw new IllegalArgumentException("Invalid game id `" + id + "'");
		}
	}
}
