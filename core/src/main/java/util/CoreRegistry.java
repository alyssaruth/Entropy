package util;

import java.util.prefs.Preferences;

public interface CoreRegistry 
{
	public static final Preferences instance = Preferences.userRoot().node("entropyInstance");

	public static final String INSTANCE_INT_REPLAY_CONVERSION = "replayConversion";
	public static final String INSTANCE_STRING_DEVICE_ID = "deviceId";
}
