package util;

import java.util.prefs.Preferences;

public interface ServerRegistry extends Registry
{
	//Nodes
	public static Preferences accountData = Preferences.userRoot().node("entropyUsernames");
	public static Preferences emailData = Preferences.userRoot().node("entropyEmails");
	public static Preferences adminData = Preferences.userRoot().node("entropyAdmins");
	public static Preferences playerStats = Preferences.userRoot().node("entropyServer");
	public static Preferences globalStats = Preferences.userRoot().node("globalStats");
	
	//globalStats
	public static final String STATISTICS_INT_FUNCTIONS_HANDLED = "functionsHandled";
	public static final String STATISTICS_INT_NOTIFICATIONS_SENT = "notificationsSent";
	public static final String STATISTICS_INT_MOST_FUNCTIONS_HANDLED = "mostFunctionsHandled";
	public static final String STATISTICS_INT_MOST_FUNCTIONS_RECEIVED = "mostFunctionsReceived";
	public static final String STATISTICS_INT_MOST_CONCURRENT_USERS = "mostConcurrentUsers";
	
	public static final String STATISTICS_ROOM_SUFFIX_PLAYED = "Played";
	public static final String STATISTICS_ROOM_SUFFIX_DURATION = "Duration";
}
