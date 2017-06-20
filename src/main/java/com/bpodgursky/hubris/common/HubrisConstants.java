package com.bpodgursky.hubris.common;

import java.util.prefs.Preferences;

public final class HubrisConstants {
	public static final String loginUrl = "https://np.ironhelmet.com/arequest/login";
	public static final String accountHomeUrl = "http://np.ironhelmet.com/account";
	public static final String gamesListUrl = "https://np.ironhelmet.com/mrequest/init_player";
	public static final String gameRequestUrl = "https://np.ironhelmet.com/trequest/order";
  public static final String homepageUrl = "http://np.ironhelmet.com/";
  public static final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";
	public static final Preferences preferences = Preferences.userRoot().node("hubris");
}
