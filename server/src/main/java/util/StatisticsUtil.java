package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.BackingStoreException;

import object.GameWrapper;

import static utils.InjectedThings.logger;

public class StatisticsUtil implements ServerRegistry
{
	public static int getAchievementCount(String username)
	{
		return playerStats.getInt(username + "Achievements", 0);
	}
	
	public static int getEntropyLost(String username, int players)
	{
		return playerStats.getInt(username + STATISTICS_INT_ENTROPY_ONLINE_GAMES_PLAYED + players, 0)
			 - playerStats.getInt(username + STATISTICS_INT_ENTROPY_ONLINE_GAMES_WON + players, 0);
	}
	public static int getVectropyLost(String username, int players)
	{
		return playerStats.getInt(username + STATISTICS_INT_VECTROPY_ONLINE_GAMES_PLAYED + players, 0)
			 - playerStats.getInt(username + STATISTICS_INT_VECTROPY_ONLINE_GAMES_WON + players, 0);
	}
	public static int getEntropyWon(String username, int players)
	{
		return playerStats.getInt(username + STATISTICS_INT_ENTROPY_ONLINE_GAMES_WON + players, 0);
	}
	
	public static int getVectropyWon(String username, int players)
	{
		return playerStats.getInt(username + STATISTICS_INT_VECTROPY_ONLINE_GAMES_WON + players, 0);
	}
	
	public static void recordWin(String username, int players, int mode)
	{
		if (mode == GameConstants.GAME_MODE_ENTROPY)
		{
			recordEntropyWin(username, players);
		}
		else
		{
			recordVectropyWin(username, players);
		}
	}
	private static void recordEntropyWin(String username, int players)
	{
		String node = username + STATISTICS_INT_ENTROPY_ONLINE_GAMES_WON + players;
		int wins = playerStats.getInt(node, 0) + 1;
		playerStats.putInt(node, wins);
	}
	private static void recordVectropyWin(String username, int players)
	{
		String node = username + STATISTICS_INT_VECTROPY_ONLINE_GAMES_WON + players;
		int wins = playerStats.getInt(node, 0) + 1;
		playerStats.putInt(node, wins);
	}
	
	public static void recordGamePlayed(String username, int players, int mode)
	{
		if (mode == GameConstants.GAME_MODE_ENTROPY)
		{
			recordEntropyPlayed(username, players);
		}
		else
		{
			recordVectropyPlayed(username, players);
		}
	}
	private static void recordEntropyPlayed(String username, int players)
	{
		String node = username + STATISTICS_INT_ENTROPY_ONLINE_GAMES_PLAYED + players;
		int played = playerStats.getInt(node, 0) + 1;
		playerStats.putInt(node, played);
	}
	private static void recordVectropyPlayed(String username, int players)
	{
		String node = username + STATISTICS_INT_VECTROPY_ONLINE_GAMES_PLAYED + players;
		int played = playerStats.getInt(node, 0) + 1;
		playerStats.putInt(node, played);
	}
	
	public static void saveGlobalStatistics(String roomName, GameWrapper game)
	{
		int index = roomName.indexOf(' ');
		roomName = roomName.substring(0, index);
		
		long gameDurationMillis = game.getGameDurationMillis();
		int gamesPlayed = globalStats.getInt(roomName + "Played", 0);
		globalStats.putInt(roomName + STATISTICS_ROOM_SUFFIX_PLAYED, gamesPlayed + 1);
		
		long duration = globalStats.getLong(roomName + "Duration", 0);
		globalStats.putLong(roomName + STATISTICS_ROOM_SUFFIX_DURATION, duration + gameDurationMillis);
	}
	
	public static int getTotalNotificationsSent()
	{
		return globalStats.getInt(STATISTICS_INT_NOTIFICATIONS_SENT, 0);
	}
	public static void saveTotalNotificationsSent(AtomicInteger totalNotificationsSent)
	{
		int notificationsSent = totalNotificationsSent.intValue();
		globalStats.putInt(STATISTICS_INT_NOTIFICATIONS_SENT, notificationsSent);
	}
	public static int getTotalFunctionsHandled()
	{
		return globalStats.getInt(STATISTICS_INT_FUNCTIONS_HANDLED, 0);
	}
	public static void saveTotalFunctionsHandled(AtomicInteger totalFunctionsHandled)
	{
		int functionsHandled = totalFunctionsHandled.get();
		globalStats.putInt(STATISTICS_INT_FUNCTIONS_HANDLED, functionsHandled);
	}
	public static int getMostFunctionsReceived()
	{
		return globalStats.getInt(STATISTICS_INT_MOST_FUNCTIONS_RECEIVED, 0);
	}
	public static void saveMostFunctionsReceived(AtomicInteger mostFunctionsReceived)
	{
		globalStats.putInt(STATISTICS_INT_MOST_FUNCTIONS_RECEIVED, mostFunctionsReceived.intValue());
	}
	public static int getMostFunctionsHandled()
	{
		return globalStats.getInt(STATISTICS_INT_MOST_FUNCTIONS_HANDLED, 0);
	}
	public static void saveMostFunctionsHandled(AtomicInteger mostFunctionsHandled)
	{
		globalStats.putInt(STATISTICS_INT_MOST_FUNCTIONS_HANDLED, mostFunctionsHandled.intValue());
	}
	
	public static long getTotalDuration()
	{
		return getTotalForStat(STATISTICS_ROOM_SUFFIX_DURATION);
	}
	public static long getTotalGamesPlayed()
	{
		return getTotalForStat(STATISTICS_ROOM_SUFFIX_PLAYED);
	}
	private static long getTotalForStat(String stat)
	{
		long total = 0;
		
		try
		{
			String[] keys = globalStats.keys();
			int size = keys.length;
			for (int i=0; i<size; i++)
			{
				String key = keys[i];
				if (key.contains(stat))
				{
					total += globalStats.getLong(key, 0);
				}
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
		
		return total;
	}
	
	public static void doGlobalStatsDump()
	{
		try
		{
			Debug.appendWithoutDate("");
			
			HashMap<String, RoomStatsWrapper> hm = initialiseRoomStatsHm();
			Iterator<Map.Entry<String, RoomStatsWrapper>> it = hm.entrySet().iterator();
			for (; it.hasNext(); )
			{
				Map.Entry<String, RoomStatsWrapper> entry = it.next();
				String roomName = entry.getKey();
				RoomStatsWrapper statsWrapper = entry.getValue();
				
				String loggingStr = " - ";
				loggingStr += roomName;
				loggingStr += ": ";
				loggingStr += statsWrapper.getGamesPlayed();
				loggingStr += " games in ";
				loggingStr += DateUtil.formatHHMMSS(statsWrapper.getTotalDuration());
				
				Debug.appendWithoutDate(loggingStr);
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	public static HashMap<String, RoomStatsWrapper> initialiseRoomStatsHm() throws BackingStoreException
	{
		HashMap<String, RoomStatsWrapper> hm = new HashMap<>();
		
		String[] keys = globalStats.keys();
		for (int i=0; i<keys.length; i++)
		{
			String key = keys[i];
			String roomName = getRoomNameIfApplicable(key);
			if (roomName != null)
			{
				RoomStatsWrapper wrapper = hm.get(roomName);
				if (wrapper == null)
				{
					wrapper = new RoomStatsWrapper();
				}
				
				wrapper.setAppropriateStatistic(key, globalStats);
				hm.put(roomName, wrapper);
			}
		}
		
		return hm;
	}
	
	private static String getRoomNameIfApplicable(String key)
	{
		if (key.contains(STATISTICS_ROOM_SUFFIX_DURATION))
		{
			return key.replace(STATISTICS_ROOM_SUFFIX_DURATION, "");
		}
		else if (key.contains(STATISTICS_ROOM_SUFFIX_PLAYED))
		{
			return key.replace(STATISTICS_ROOM_SUFFIX_PLAYED, "");
		}
		
		return null;
	}
	
	public static int getMostConcurrentUsers()
	{
		return globalStats.getInt(STATISTICS_INT_MOST_CONCURRENT_USERS, 0);
	}
	public static void updateMostConcurrentUsers(int players)
	{
		int previousMax = globalStats.getInt(STATISTICS_INT_MOST_CONCURRENT_USERS, 0);
		if (players > previousMax)
		{
			globalStats.putInt(STATISTICS_INT_MOST_CONCURRENT_USERS, players);
		}
	}
	
	public static void clearServerData()
	{
		try
		{
			globalStats.clear();
			Debug.append("Global stats cleared successfully.");
		}
		catch (Throwable t)
		{
			logger.error("statsError", "Failed to clear global stats", t);
		}
	}
}
