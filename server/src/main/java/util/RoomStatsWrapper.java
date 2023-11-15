package util;

import java.util.prefs.Preferences;

class RoomStatsWrapper
{
	private long totalDuration;
	private int gamesPlayed;
	
	public RoomStatsWrapper()
	{
	}
	
	public void setAppropriateStatistic(String key, Preferences globalStats)
	{
		if (key.contains(ServerRegistry.STATISTICS_ROOM_SUFFIX_DURATION))
		{
			totalDuration = globalStats.getLong(key, 0);
		}
		else if (key.contains(ServerRegistry.STATISTICS_ROOM_SUFFIX_PLAYED))
		{
			gamesPlayed = globalStats.getInt(key, 0);
		}
	}
	
	public long getTotalDuration()
	{
		return totalDuration;
	}
	public int getGamesPlayed()
	{
		return gamesPlayed;
	}
}