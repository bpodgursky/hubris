package com.bpodgursky.hubris.common;

import java.util.prefs.Preferences;

public final class HubrisConstants {
	public static final String accountHomeUrl = "http://np.ironhelmet.com/account";
	public static final String gameRequestUrl = "http://np.ironhelmet.com/frequest";
	public static final Preferences preferences = Preferences.userRoot().node("hubris");
}
