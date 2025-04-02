package object;

import java.util.concurrent.ConcurrentHashMap;

import static utils.CoreGlobals.logger;

/**
 * Wrap up a single game
 */
public class GameWrapper 
{
	private static final int COUNTDOWN_TIME_MILLIS = 5000;
	
	private String gameId = "";
	private int winningPlayer = -1;
	private int roundNumber = 1;
	private long gameStartMillis = -1;
	private long gameEndMillis = -1;
	private ConcurrentHashMap<Integer, HandDetails> hmRoundDetailsByRoundNumber = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, BidHistory> hmBidHistoryByRoundNumber = new ConcurrentHashMap<>();
	private long countdownStartMillis = -1;
	
	public GameWrapper(String gameId)
	{
		this.gameId = gameId;
	}
	
	public GameWrapper factoryCopy()
	{
		GameWrapper wrapper = new GameWrapper(gameId);
		wrapper.setWinningPlayer(winningPlayer);
		wrapper.setRoundDetails(hmRoundDetailsByRoundNumber);
		wrapper.setBidHistory(hmBidHistoryByRoundNumber);
		wrapper.setCountdownStartMillis(countdownStartMillis);
		wrapper.setGameStartMillisIfUnset(gameStartMillis);
		wrapper.setGameEndMillis(System.currentTimeMillis());
		wrapper.setRoundNumber(roundNumber);
		
		return wrapper;
	}
	
	public String getGameId()
	{
		return gameId;
	}
	public int getWinningPlayer()
	{
		return winningPlayer;
	}
	public void setWinningPlayer(int winningPlayer)
	{
		this.winningPlayer = winningPlayer;
	}
	
	public HandDetails getCurrentRoundDetails()
	{
		return hmRoundDetailsByRoundNumber.get(roundNumber);
	}
	public HandDetails getDetailsForRound(int roundNumber)
	{
		return hmRoundDetailsByRoundNumber.get(roundNumber);
	}
	public void setDetailsForRound(int roundNumber, HandDetails details)
	{
		hmRoundDetailsByRoundNumber.put(roundNumber, details);
	}
	public BidHistory getCurrentBidHistory()
	{
		return hmBidHistoryByRoundNumber.get(roundNumber);
	}
	public BidHistory getBidHistoryForRound(int roundNumber)
	{
		return hmBidHistoryByRoundNumber.get(roundNumber);
	}
	public void setBidHistoryForRound( int roundNumber, BidHistory bidHistory)
	{
		hmBidHistoryByRoundNumber.put(roundNumber, bidHistory);
	}
	public void setRoundDetails(ConcurrentHashMap<Integer, HandDetails> hmRoundDetailsByRoundNumber)
	{
		this.hmRoundDetailsByRoundNumber = hmRoundDetailsByRoundNumber;
	}
	public void setBidHistory(ConcurrentHashMap<Integer, BidHistory> hmBidHistoryByRoundNumber)
	{
		this.hmBidHistoryByRoundNumber = hmBidHistoryByRoundNumber;
	}
	
	/**
	 * Helpers
	 */
	public int getPersonToStart(int roundNumber)
	{
		BidHistory history = hmBidHistoryByRoundNumber.get(roundNumber);
		return history.getPersonToStart();
	}
	public long getCountdownTimeRemaining()
	{
		long timeElapsed = System.currentTimeMillis() - countdownStartMillis;
		if (timeElapsed > COUNTDOWN_TIME_MILLIS)
		{
			return 0;
		}
		
		return COUNTDOWN_TIME_MILLIS - timeElapsed;
	}
	public void setCountdownStartMillisIfUnset()
	{
		if (countdownStartMillis == -1)
		{
			countdownStartMillis = System.currentTimeMillis();
		}
	}
	public void setCountdownStartMillis(long countdownStartMillis)
	{
		this.countdownStartMillis = countdownStartMillis;
	}
	public long getGameStartMillis()
	{
		return gameStartMillis;
	}
	public void setGameStartMillisIfUnset(long millis)
	{
		if (gameStartMillis == -1)
		{
			gameStartMillis = millis;
		}
	}
	public long getGameEndMillis()
	{
		return gameEndMillis;
	}
	public void setGameEndMillis(long gameEndMillis)
	{
		this.gameEndMillis = gameEndMillis;
	}
	public long getGameDurationMillis()
	{
		return gameEndMillis - gameStartMillis;
	}
	public int getRoundNumber()
	{
		return roundNumber;
	}
	public void setRoundNumber(int roundNumber)
	{
		this.roundNumber = roundNumber;
	}
	
	/**
	 * Debug
	 */
	public void debugDump(String description)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(description);
		sb.append(" ID: ");
		sb.append(gameId);
		sb.append("\n--------------\n\n");

		sb.append("Countdown Start Time: " + countdownStartMillis + "\n");
		sb.append("Start Time: " + gameStartMillis + "\n");
		sb.append("End Time: " + gameEndMillis + "\n");
		sb.append("Winning Player: " + winningPlayer + "\n");
		
		int totalRounds = hmBidHistoryByRoundNumber.size();
		for (int i=1; i<=totalRounds; i++)
		{
			dumpRoundInformation(sb, i);
		}

		logger.info("debugDump", sb.toString());
	}
	
	private void dumpRoundInformation(StringBuilder sb, int roundNumber)
	{
		BidHistory history = hmBidHistoryByRoundNumber.get(roundNumber);
		HandDetails details = hmRoundDetailsByRoundNumber.get(roundNumber);

		sb.append("\nRound" + roundNumber);
		sb.append("\n-----------------------\n");

		sb.append("\n	Last player to act: " + history.getLastPlayerToAct());
		sb.append("\n	BidHistory: " + history);
		sb.append("\n	" + details.getHandsForLogging());
	}
}
