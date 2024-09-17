package util;

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
}
