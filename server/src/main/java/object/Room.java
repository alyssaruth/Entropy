package object;

import auth.UserConnection;
import org.w3c.dom.Document;
import server.EntropyServer;
import util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side version of a Room
 */
public final class Room extends RoomWrapper
{
	private boolean isCopy = false;
	
	private ExtendedConcurrentHashMap<Integer, String> hmPlayerByPlayerNumber = new ExtendedConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, String> hmFormerPlayerByPlayerNumber = new ConcurrentHashMap<>();
	private ArrayList<OnlineMessage> chatHistory = new ArrayList<>();
	private GameWrapper previousGame = null;
	private GameWrapper currentGame = null;
	
	private EntropyServer server = null;
	
	public Room(String roomName, int mode, int players, EntropyServer server)
	{
		super(roomName, mode, players);
		
		this.server = server;
	}
	
	public boolean isFull()
	{
		return currentPlayers.size() == players;
	}
	public boolean isEmpty()
	{
		return currentPlayers.size() == 0
		  && observers.size() == 0;
	}
	
	public void clearChatIfEmpty()
	{
		synchronized (this)
		{
			if (!isEmpty())
			{
				return;
			}
			
			chatHistory.clear();
		}
	}
	
	public int addToCurrentPlayers(String username, int playerNumber)
	{
		synchronized (this)
		{
			String existingUsername = hmPlayerByPlayerNumber.get(playerNumber);
			if (existingUsername != null)
			{
				Debug.append(username + " tried to join room " + roomName + " as player " + playerNumber
							+ " but the space was taken by " + existingUsername);
				return -1;
			}
			
			if (hmPlayerByPlayerNumber.containsValue(username))
			{
				Debug.append(username + " tried to join room " + roomName + " twice!");
				return -1;
			}
			
			currentPlayers.add(username);
			hmPlayerByPlayerNumber.put(playerNumber, username);
			observers.remove(username);
			
			notifyAllPlayersOfPlayerChange(username, false);
			server.lobbyChanged();
			return playerNumber;
		}
	}
	
	public void removePlayer(String username, boolean fireLobbyChanged)
	{
		for (int playerNumber=0; playerNumber<players; playerNumber++)
		{
			String user = hmPlayerByPlayerNumber.get(playerNumber);
			if (user != null 
			  && username.equals(user))
			{
				hmPlayerByPlayerNumber.remove(playerNumber);
				hmFormerPlayerByPlayerNumber.put(playerNumber, user);
				
				//Notify everyone in the room that this player has left. Block on this.
				notifyAllPlayersOfPlayerChange(username, true);
				
				//The game has not started
				if (currentGame.getGameStartMillis() == -1)
				{
					//Unset the countdown if it's going, reset current players and get out of this madness
					currentGame.setCountdownStartMillis(-1);
					resetCurrentPlayers(fireLobbyChanged);
					return;
				}
				
				//There is a game in progress
				if (currentGame.getGameEndMillis() == -1)
				{
					LeftBid bid = new LeftBid();
					Player player = new Player(playerNumber, EntropyUtil.getColourForPlayerNumber(playerNumber));
					player.setName(username);
					bid.setPlayer(player);
					
					BidHistory history = currentGame.getCurrentBidHistory();
					history.addBidForPlayer(playerNumber, bid);
					
					int roundNumber = currentGame.getRoundNumber();
					if (roundNumber > 1)
					{
						StatisticsUtil.recordGamePlayed(username, players, mode);
					}
					
					//Moved this into here as otherwise we set it to 0 incorrectly and a person ends up with no cards!
					HandDetails details = currentGame.getCurrentRoundDetails();
					ConcurrentHashMap<Integer, Integer> hmHandSizeByPlayerNumber = details.getHandSizes();
					hmHandSizeByPlayerNumber.put(playerNumber, 0);
				}
				
				int playerSize = hmPlayerByPlayerNumber.size();
				if (playerSize == 1)
				{
					int remainingPlayerNumber = hmPlayerByPlayerNumber.getOnlyKey();
					finishCurrentGame(remainingPlayerNumber);
				}
				else if (playerSize == 0)
				{
					resetCurrentPlayers(fireLobbyChanged);
					clearChatIfEmpty();
				}
			}
		}
	}
	
	private void notifyAllPlayersOfPlayerChange(String userToExclude, boolean blocking)
	{
		Document notification = XmlBuilderServer.getPlayerNotification(this);
		notifyAllUsersViaGameSocket(notification, userToExclude, blocking);
	}
	
	private void notifyAllUsersViaGameSocket(Document notification, String userToExclude, boolean blocking)
	{
		HashSet<String> usersToNotify = getAllUsersInRoom();
		if (userToExclude != null)
		{
			usersToNotify.remove(userToExclude);
		}
		
		ArrayList<UserConnection> uscs = server.getUserConnectionsForUsernames(usersToNotify);
		server.sendViaNotificationSocket(uscs, notification, XmlConstants.SOCKET_NAME_GAME, blocking);
	}
	
	private void finishCurrentGame(int winningPlayer)
	{
		int roundNumber = currentGame.getRoundNumber();
		String winningUsername = hmPlayerByPlayerNumber.get(winningPlayer);
		saveStatistics(winningUsername, roundNumber);
		resetCurrentPlayers();
		currentGame.setWinningPlayer(winningPlayer);
		
		if (roundNumber > 1)
		{
			OnlineMessage om = new OnlineMessage("black", winningUsername + " won!", "Game");
			addToChatHistoryAndNotifyUsers(om);
		}
		
		initialiseGame();
		
		//Notify everyone that the game is over now we've finished setting up
		Document notification = XmlBuilderServer.factoryGameOverNotification(this, winningPlayer);
		notifyAllUsersViaGameSocket(notification, null, false);
	}
	
	public void resetCurrentPlayers()
	{
		resetCurrentPlayers(true);
	}
	public void resetCurrentPlayers(boolean fireLobbyChanged)
	{
		currentPlayers.clear();
		for (int i=0; i<players; i++)
		{
			String username = hmPlayerByPlayerNumber.get(i);
			if (username != null)
			{
				currentPlayers.add(username);
			}
		}
		
		hmFormerPlayerByPlayerNumber.clear();
		if (fireLobbyChanged)
		{
			server.lobbyChanged();
		}
	}
	
	
	public void addToObservers(String username)
	{
		synchronized (this)
		{
			if (!observers.contains(username))
			{
				observers.add(username);
			}
			
			removePlayer(username, false);
			server.lobbyChanged();
		}
	}
	public void removeFromObservers(String username)
	{
		boolean removed = observers.remove(username);
		if (removed)
		{
			server.lobbyChanged();
		}
		
		clearChatIfEmpty();
	}
	
	public void initialiseGame()
	{
		try
		{
			String gameId = "G" + System.currentTimeMillis();
			if (currentGame != null)
			{
				previousGame = currentGame.factoryCopy();
			}
			
			currentGame = new GameWrapper(gameId);
			
			HandDetails details = new HandDetails();
			ExtendedConcurrentHashMap<Integer, Integer> hmHandSizeByPlayerNumber = new ExtendedConcurrentHashMap<>();
			for (int i=0; i<players; i++)
			{
				hmHandSizeByPlayerNumber.put(i, Integer.valueOf(5));
			}
			
			ConcurrentHashMap<Integer, String[]> hmHandByPlayerNumber = dealHandsHashMap(hmHandSizeByPlayerNumber);
			
			details.setHands(hmHandByPlayerNumber);
			details.setHandSizes(hmHandSizeByPlayerNumber);
			currentGame.setDetailsForRound(1, details);
	
			Random rand = new Random();
			int personToStart = rand.nextInt(players);
			
			BidHistory history = new BidHistory();
			history.setPersonToStart(personToStart);
			currentGame.setBidHistoryForRound(1, history);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	public void handleChallenge(String gameId, int roundNumber, int playerNumber, int challengedNumber, Bid bid) 
	{
		GameWrapper game = getGameForId(gameId);
		HandDetails details = game.getDetailsForRound(roundNumber);
		ConcurrentHashMap<Integer, String[]> hmHandByPlayerNumber = details.getHands();
		if (bid.isOverbid(hmHandByPlayerNumber, jokerValue))
		{
			//bidder loses
			setUpNextRound(challengedNumber);
		}
		else
		{
			//challenger loses
			setUpNextRound(playerNumber);
		}
	}
	
	public void handleIllegal(String gameId, int roundNumber, int playerNumber, int bidderNumber, Bid bid) 
	{
		GameWrapper game = getGameForId(gameId);
		HandDetails details = game.getDetailsForRound(roundNumber);
		ConcurrentHashMap<Integer, String[]> hmHandByPlayerNumber = details.getHands();
		if (bid.isPerfect(hmHandByPlayerNumber, jokerValue, includeMoons, includeStars))
		{
			setUpNextRound(bidderNumber);
		}
		else
		{
			setUpNextRound(playerNumber);
		}
	}
	
	public void setUpNextRound(int losingPlayerNumber)
	{
		int currentRoundNumber = currentGame.getRoundNumber();
		
		HandDetails nextRoundDetails = currentGame.getDetailsForRound(currentRoundNumber + 1);
		if (nextRoundDetails != null)
		{
			Debug.stackTrace("Trying to set up next round but it's not null. Room " + roomName);
			return;
		}
		
		HandDetails currentRoundDetails = currentGame.getDetailsForRound(currentRoundNumber);
		ExtendedConcurrentHashMap<Integer, Integer> hmHandSizeByPlayerNumber = currentRoundDetails.getHandSizes();
		Integer handSize = hmHandSizeByPlayerNumber.get(losingPlayerNumber);
		int newHandSize = Math.max(0, handSize.intValue() - 1);
		
		ExtendedConcurrentHashMap<Integer, Integer> hmHandSizeByPlayerNumberForNextRound = hmHandSizeByPlayerNumber.factoryCopy();
		hmHandSizeByPlayerNumberForNextRound.put(losingPlayerNumber, Integer.valueOf(newHandSize));
		
		int potentialWinner = getWinningPlayer(hmHandSizeByPlayerNumberForNextRound);
		if (potentialWinner > -1)
		{
			finishCurrentGame(potentialWinner);
		}
		else
		{
			nextRoundDetails = new HandDetails();
			nextRoundDetails.setHandSizes(hmHandSizeByPlayerNumberForNextRound);
			
			ConcurrentHashMap<Integer, String[]> hmHandByPlayerNumber = dealHandsHashMap(hmHandSizeByPlayerNumberForNextRound);
			
			nextRoundDetails.setHands(hmHandByPlayerNumber);
			currentGame.setDetailsForRound(currentRoundNumber + 1, nextRoundDetails);
			
			BidHistory history = new BidHistory();
			history.setPersonToStart(losingPlayerNumber);
			currentGame.setBidHistoryForRound(currentRoundNumber + 1, history);
			
			currentGame.setRoundNumber(currentRoundNumber + 1);
			
			Document newRoundNotification = XmlBuilderServer.factoryNewRoundNotification(this, nextRoundDetails, losingPlayerNumber);
			notifyAllUsersViaGameSocket(newRoundNotification, null, false);
		}
	}
	
	private ConcurrentHashMap<Integer, String[]> dealHandsHashMap(ExtendedConcurrentHashMap<Integer, Integer> hmHandSizeByPlayerNumber)
	{
		ConcurrentHashMap<Integer, String[]> hmHandByPlayerNumber = new ConcurrentHashMap<>();
		
		long seed = server.generateSeed();
		List<String> deck = CardsUtil.createAndShuffleDeck(true, jokerQuantity, includeMoons, includeStars, negativeJacks, seed);
		for (int i=0; i<players; i++)
		{
			int size = hmHandSizeByPlayerNumber.get(i);
			String hand[] = new String[size];
			for (int j=0; j<size; j++)
			{
				hand[j] = deck.remove(j);
			}
			
			hmHandByPlayerNumber.put(i, hand);
		}
		
		return hmHandByPlayerNumber;
	}
	
	private int getWinningPlayer(ConcurrentHashMap<Integer, Integer> hmHandSizeByPlayerNumber)
	{
		int activePlayers = 0;
		int potentialWinner = 0;
		
		for (int i=0; i<players; i++)
		{
			int handSize = hmHandSizeByPlayerNumber.get(i);
			if (handSize > 0)
			{
				activePlayers++;
				potentialWinner = i;
			}
		}
		
		if (activePlayers > 1)
		{
			return -1;
		}
		else
		{
			return potentialWinner;
		}
	}
	
	private void saveStatistics(String winningPlayer, int roundNumber)
	{
		//don't save any statistics if the game didn't go beyond the first round
		if (roundNumber == 1)
		{
			return;
		}
		
		StatisticsUtil.recordWin(winningPlayer, players, mode);
		
		int size = currentPlayers.size();
		for (int i=0; i<size; i++)
		{
			String player = currentPlayers.get(i);
			if (hmPlayerByPlayerNumber.containsValue(player))
			{
				//They didn't leave, so record a game played
				StatisticsUtil.recordGamePlayed(player, players, mode);
				
				//Push out a stats notification
				Document statsNotification = XmlBuilderServer.factoryStatisticsNotification(player);
				UserConnection usc = server.getUserConnectionForUsername(player);
				usc.sendNotificationInWorkerPool(statsNotification, server, XmlConstants.SOCKET_NAME_LOBBY, null);
			}
		}
	}
	
	public long getCurrentGameDuration()
	{
		long gameStart = currentGame.getGameStartMillis();
		if (gameStart == -1
		  || currentGame.getRoundNumber() == 1)
		{
			return 0;
		}
		
		long currentTime = System.currentTimeMillis();
		return (currentTime - gameStart);
	}
	
	/**
	 * Get/sets
	 */
	public boolean isGameInProgress()
	{
		//This no longer works
		//return currentGame != null;
		//return currentPlayers.size() == players && (waitingForPlayerToSeeResult == null);
		return currentPlayers.size() == players;
	}
	public boolean getIsCopy()
	{
		return isCopy;
	}
	public void setIsCopy(boolean isCopy)
	{
		this.isCopy = isCopy;
	}

	/**
	 * HashMap gets/sets
	 */
	public String getPlayer(int playerNumber)
	{
		return hmPlayerByPlayerNumber.get(playerNumber);
	}
	public String getFormerPlayer(int playerNumber)
	{
		return hmFormerPlayerByPlayerNumber.get(playerNumber);
	}
	public GameWrapper getGameForId(String gameId)
	{
		String currentId = currentGame.getGameId();
		if (gameId.equals(currentId))
		{
			return currentGame;
		}
		
		String previousId = previousGame.getGameId();
		if (gameId.equals(previousId))
		{
			return previousGame;
		}

		throw new RuntimeException("Got a null game for room " + roomName + " and gameId " + gameId);
	}
	public GameWrapper getNextGameForId(String previousGameIdFromClient)
	{
		if (previousGameIdFromClient.isEmpty())
		{
			return currentGame;
		}
		
		if (previousGame == null)
		{
			Debug.append("Tried to get next game for gameId " + previousGameIdFromClient + " but previous game was null.");
			Debug.appendWithoutDate("Current: " + currentGame.getGameId());
			return currentGame;
		}
		
		String previousGameId = previousGame.getGameId();
		if (previousGameId.equals(previousGameIdFromClient))
		{
			return currentGame;
		}
		
		Debug.append("Tried to get next game for gameId " + previousGameIdFromClient + " but this didn't match my previous game.");
		Debug.appendWithoutDate("Previous: " + previousGame.getGameId());
		Debug.appendWithoutDate("Current: " + currentGame.getGameId());
		return currentGame;
	}
	
	
	public Bid getLastBidForPlayer(int playerNumber, int roundNumber)
	{
		if (currentGame == null
		  || playerNumber == -1)
		{
			return null;
		}
		
		BidHistory history = currentGame.getBidHistoryForRound(roundNumber);
		return history.getLastBidForPlayer(playerNumber);
	}
	public boolean addBidForPlayer(String gameId, int playerNumber, int roundNumber, Bid newBid)
	{
		GameWrapper game = getGameForId(gameId);
		
		BidHistory history = game.getBidHistoryForRound(roundNumber);
		boolean added = history.addBidForPlayer(playerNumber, newBid);
		
		if (added)
		{
			//Notify all other players
			Document bidNotification = XmlBuilderServer.getBidNotification(roomName, playerNumber, newBid);
			notifyAllUsersViaGameSocket(bidNotification, null, false);
		}
		
		return added;
	}
	
	public void addToChatHistoryAndNotifyUsers(OnlineMessage message)
	{
		if (isEmpty())
		{
			return;
		}
		
		chatHistory.add(message);
		
		Document chatMessage = XmlBuilderServer.getChatNotification(roomName, message);
		HashSet<String> users = getAllUsersInRoom();
		ArrayList<UserConnection> uscs = server.getUserConnectionsForUsernames(users);
		server.sendViaNotificationSocket(uscs, chatMessage, XmlConstants.SOCKET_NAME_CHAT, false);
	}
	public ArrayList<OnlineMessage> getChatHistory()
	{
		return chatHistory;
	}
	
	@Override
	public String toString() 
	{
		return roomName;
	}
}
