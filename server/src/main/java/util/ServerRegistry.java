package util;

import java.util.prefs.Preferences;

public interface ServerRegistry extends Registry
{
	//Nodes
	public static Preferences accountData = Preferences.userRoot().node("entropyUsernames");
	public static Preferences playerStats = Preferences.userRoot().node("entropyServer");

	
	public static final String STATISTICS_ROOM_SUFFIX_PLAYED = "Played";
	public static final String STATISTICS_ROOM_SUFFIX_DURATION = "Duration";
}
