package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import object.Bid;
import object.BidHistory;
import object.GameWrapper;
import object.HandDetails;
import object.OnlineMessage;
import object.Room;
import object.UserConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import server.DownloadHandler;
import server.EntropyServer;

public class XmlBuilderServer implements XmlConstants,
										 ServerRegistry
{
	//Cached acknowledgements
	//AJH 28 Feb 2015 - Made these final - they don't change
	private static final Document ACKNOWLEDGEMENT = XmlUtil.factorySimpleMessage(RESPONSE_TAG_ACKNOWLEDGEMENT);
	private static final Document symmetricKeyAck = XmlUtil.factorySimpleMessage(RESPONSE_TAG_SYMMETRIC_KEY);
	
	private static final int ACHIEVMENTS_TOTAL = 80;
	
	public static Document getKickOffResponse(String username, String reason)
	{
		Document response = XmlUtil.factoryNewDocument();

		Element rootElement = response.createElement(RESPONSE_TAG_KICK_OFF);
		rootElement.setAttribute("RemovalReason", reason);
		
		if (username != null
		  && !username.isEmpty())
		{
			rootElement.setAttribute("Username", username);
			Debug.appendBanner("Kicking off " + username + ". Reason: " + reason);
		}
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getUpdateAvailableResponse(long fileSize, String versionNumber)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_UPDATE_AVAILABLE);
		rootElement.setAttribute("FileSize", "" + fileSize);
		rootElement.setAttribute("VersionNumber", versionNumber);
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getNewAccountResponse(String username, String passwordHash, String email)
	{
		String error = AccountUtil.createAccount(username, passwordHash, email);
		Document response = XmlUtil.factoryNewDocument();

		Element rootElement = response.createElement(RESPONSE_TAG_NEW_ACCOUNT);
		rootElement.setAttribute("Username", username);
		if (!error.isEmpty())
		{
			rootElement.setAttribute("Error", error);
		}
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getChangePasswordResponse(String username, String oldPass, String newPass)
	{
		String error = AccountUtil.changePassword(username, oldPass, newPass);
		Document response = XmlUtil.factoryNewDocument();

		Element rootElement = response.createElement(RESPONSE_TAG_CHANGE_PASSWORD);
		rootElement.setAttribute("Username", username);
		if (!error.isEmpty())
		{
			rootElement.setAttribute("Error", error);
		}
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getChangeEmailResponse(String username, String oldEmail, String newEmail, boolean sendTest)
	{
		String email = AccountUtil.getEmailForUser(username);
		if (!email.equals(oldEmail))
		{
			return getKickOffResponse(username, "An error occurred validating your credentials.");
		}
		
		String error = AccountUtil.changeEmail(username, newEmail, sendTest);
		
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_CHANGE_EMAIL);
		rootElement.setAttribute("Username", username);
		if (!error.isEmpty())
		{
			rootElement.setAttribute("Error", error);
		}
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getConnectResponse(String username, String hashedPassword, String version, 
	  UserConnection usc, EntropyServer server, boolean mobile)
	{
		Document response = XmlUtil.factoryNewDocument();
		String currentVersion = OnlineConstants.SERVER_VERSION;
		
		if (!version.equals(currentVersion))
		{
			Debug.append("Rejecting connection for " + username + " due to out-of-date version (" + version + ")");
			Element rootElement = response.createElement(RESPONSE_TAG_CONNECT_FAILURE);
			rootElement.setAttribute("FailureReason", "Your version of Entropy is out of date.\nDownload the newest version and try again.");
			rootElement.setAttribute("VersionNumber", OnlineConstants.ENTROPY_VERSION_NUMBER);
			rootElement.setAttribute("FileSize", "" + DownloadHandler.getLatestVersionFileSize(OnlineConstants.FILE_NAME_ENTROPY_JAR));
			response.appendChild(rootElement);
		}
		else if (!AccountUtil.usernameExists(username))
		{
			Element rootElement = response.createElement(RESPONSE_TAG_CONNECT_FAILURE);
			rootElement.setAttribute("FailureReason", "The username you entered does not exist.");
			response.appendChild(rootElement);
		}
		else if (!AccountUtil.passwordIsCorrect(username, hashedPassword))
		{
			Element rootElement = response.createElement(RESPONSE_TAG_CONNECT_FAILURE);
			rootElement.setAttribute("FailureReason", "Incorrect password.");
			response.appendChild(rootElement);
		}
		else if (server.isAlreadyOnline(username))
		{
			Element rootElement = response.createElement(RESPONSE_TAG_CONNECT_FAILURE);
			rootElement.setAttribute("FailureReason", "Another user is already logged on with that name.");
			response.appendChild(rootElement);
		}
		else
		{
			usc.update(username, mobile);
			server.updateMostConcurrentUsers();
			
			//We won't have a notification socket for them yet, so don't bother trying to notify them
			server.lobbyChanged(usc);
			
			Element rootElement = response.createElement(RESPONSE_TAG_CONNECT_SUCCESS);
			rootElement.setAttribute("Username", username);
			String email = AccountUtil.getEmailForUser(username);
			rootElement.setAttribute("Email", email);
			boolean changePassword = AccountUtil.passwordNeedsToBeChanged(username);
			XmlUtil.setAttributeBoolean(rootElement, "ChangePassword", changePassword);
			
			response.appendChild(rootElement);
			
			//Append the lobby response
			appendLobbyResponse(response, server);
			
			//Append the current chat history for the lobby
			appendCurrentChatElement(response, OnlineConstants.LOBBY_ID, server);
			
			//Append the current stats for this user
			appendStatisticsResponse(response, username);
		}
		
		return response;
	}
	
	public static Document getResetPasswordResponse(String username, String email)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_RESET_PASSWORD);
		String actualEmail = AccountUtil.getEmailForUser(username);
		
		if (!AccountUtil.usernameExists(username))
		{
			rootElement.setAttribute("FailureReason", "The username you entered does not exist.");
		}
		else if (actualEmail.isEmpty())
		{
			String failureReason = "The account specified does not have an email address associated with it."
								 + "\n\nYou will not be able to reset your password for this account.";
			rootElement.setAttribute("FailureReason", failureReason);
		}
		else if (!email.equals(actualEmail))
		{
			rootElement.setAttribute("FailureReason", "The email you entered was incorrect.");
		}
		else
		{
			boolean success = AccountUtil.resetPassword(username, email);
			if (!success)
			{
				rootElement.setAttribute("FailureReason", "There was an error resetting your password.");
			}
		}
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getAcknowledgement()
	{
		return ACKNOWLEDGEMENT;
	}
	
	public static Document getAchievementUpdateAck(EntropyServer server, String username, String achievementName, int newCount)
	{
		if (newCount < 0)
		{
			return getKickOffResponse(username, "Achievement count could not be parsed.");
		}
		
		if (newCount > ACHIEVMENTS_TOTAL)
		{
			return getKickOffResponse(username, "Nice try.");
		}
		
		playerStats.putInt(username + "Achievements", newCount);
		
		//Now loop through rooms where the player is actively playing and add a chat message
		if (!achievementName.isEmpty())
		{
			String message = username + " just earned " + achievementName + "!";
			
			ArrayList<Room> rooms = server.getRooms();
			int size = rooms.size();
			for (int i=0; i<size; i++)
			{
				Room room = rooms.get(i);
				List<String> players = room.getCurrentPlayers();
				if (players.contains(username))
				{
					OnlineMessage om = new OnlineMessage("black", message, "Game");
					room.addToChatHistoryAndNotifyUsers(om);
				}
			}
		}
		
		return ACKNOWLEDGEMENT;
	}
	
	public static Document getLeaderboardResponse(List<Room> rooms) throws Throwable
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_LEADERBOARD);
		
		addGlobalStats(response, rootElement, rooms);
		
		String[] usernames = accountData.keys();
		
		int length = usernames.length;
		for (int i=0; i<length; i++)
		{
			String username = usernames[i];
			if (username.contains("ForceReset"))
			{
				continue;
			}
			
			Element userElement = response.createElement("User");
			addWinsAndLossesToElement(userElement, username);
			
			int achievementCount = StatisticsUtil.getAchievementCount(username);
			if (achievementCount > 0)
			{
				userElement.setAttribute("Achievements", "" + achievementCount);
			}
			
			rootElement.appendChild(userElement);
		}
		
		response.appendChild(rootElement);
		return response;
	}
	
	private static void addWinsAndLossesToElement(Element userElement, String username)
	{
		int twoPlayerEntropyLost = StatisticsUtil.getEntropyLost(username, 2);
		int threePlayerEntropyLost = StatisticsUtil.getEntropyLost(username, 3);
		int fourPlayerEntropyLost = StatisticsUtil.getEntropyLost(username, 4);
		int twoPlayerEntropyWon = StatisticsUtil.getEntropyWon(username, 2);
		int threePlayerEntropyWon = StatisticsUtil.getEntropyWon(username, 3);
		int fourPlayerEntropyWon = StatisticsUtil.getEntropyWon(username, 4);
		
		int twoPlayerVectropyLost = StatisticsUtil.getVectropyLost(username, 2);
		int threePlayerVectropyLost = StatisticsUtil.getVectropyLost(username, 3);
		int fourPlayerVectropyLost = StatisticsUtil.getVectropyLost(username, 4);
		int twoPlayerVectropyWon = StatisticsUtil.getVectropyWon(username, 2);
		int threePlayerVectropyWon = StatisticsUtil.getVectropyWon(username, 3);
		int fourPlayerVectropyWon = StatisticsUtil.getVectropyWon(username, 4);
		
		userElement.setAttribute("Username", username);
		userElement.setAttribute("Entropy2Lost", "" + twoPlayerEntropyLost);
		userElement.setAttribute("Entropy3Lost", "" + threePlayerEntropyLost);
		userElement.setAttribute("Entropy4Lost", "" + fourPlayerEntropyLost);
		userElement.setAttribute("Entropy2Won", "" + twoPlayerEntropyWon);
		userElement.setAttribute("Entropy3Won", "" + threePlayerEntropyWon);
		userElement.setAttribute("Entropy4Won", "" + fourPlayerEntropyWon);
		
		userElement.setAttribute("Vectropy2Lost", "" + twoPlayerVectropyLost);
		userElement.setAttribute("Vectropy3Lost", "" + threePlayerVectropyLost);
		userElement.setAttribute("Vectropy4Lost", "" + fourPlayerVectropyLost);
		userElement.setAttribute("Vectropy2Won", "" + twoPlayerVectropyWon);
		userElement.setAttribute("Vectropy3Won", "" + threePlayerVectropyWon);
		userElement.setAttribute("Vectropy4Won", "" + fourPlayerVectropyWon);
	}
	
	private static void addGlobalStats(Document response, Element rootElement, List<Room> rooms)
	{
		long gamesPlayed = StatisticsUtil.getTotalGamesPlayed();
		long duration = StatisticsUtil.getTotalDuration();
		int usersOnline = StatisticsUtil.getMostConcurrentUsers();
		
		int roomSize = rooms.size();
		for (int i=0; i<roomSize; i++)
		{
			Room room = rooms.get(i);
			duration += room.getCurrentGameDuration();
		}
		
		rootElement.setAttribute("TotalGames", "" + gamesPlayed);
		rootElement.setAttribute("TotalDuration", "" + duration);
		rootElement.setAttribute("UsersOnline", "" + usersOnline);
		
		addRoomStats(response, rootElement);
	}
	
	private static void addRoomStats(Document response, Element root)
	{
		HashMap<String, RoomStatsWrapper> hmStatsByRoomName = null;
		try
		{
			hmStatsByRoomName = StatisticsUtil.initialiseRoomStatsHm();
		}
		catch (BackingStoreException bse)
		{
			Debug.append("Failed to build up room stats for leaderboard response.");
			Debug.stackTrace(bse);
			return;
		}
		
		Iterator<Map.Entry<String, RoomStatsWrapper>> it = hmStatsByRoomName.entrySet().iterator();
		for (; it.hasNext(); )
		{
			Map.Entry<String, RoomStatsWrapper> entry = it.next();
			String roomName = entry.getKey();
			RoomStatsWrapper wrapper = entry.getValue();
			Element roomStatsElement = response.createElement("RoomStats");
			int gamesPlayed = wrapper.getGamesPlayed();
			long totalDuration = wrapper.getTotalDuration();
			
			roomStatsElement.setAttribute("RoomName", roomName);
			roomStatsElement.setAttribute("RoomGamesPlayed", "" + gamesPlayed);
			roomStatsElement.setAttribute("RoomDuration", "" + totalDuration);
			
			root.appendChild(roomStatsElement);
		}
	}
	
	public static Document factoryStatisticsNotification(String username)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element statsElement = createStatisticsElement(response, username);
		response.appendChild(statsElement);
		return response;
	}
	
	private static Document appendStatisticsResponse(Document response, String username)
	{
		Element rootElement = response.getDocumentElement();
		Element statsElement = createStatisticsElement(response, username);
		
		rootElement.appendChild(statsElement);
		return response;
	}
	
	private static Element createStatisticsElement(Document response, String username)
	{
		Element statsElement = response.createElement(RESPONSE_TAG_STATISTICS_NOTIFICATION);
		addWinsAndLossesToElement(statsElement, username);
		
		return statsElement;
	}
	
	public static Document getChatNotification(String roomName, OnlineMessage chatMessage)
	{
		Document notification = XmlUtil.factoryNewDocument();
		Element root = notification.createElement(RESPONSE_TAG_CHAT_NOTIFICATION);
		root.setAttribute("RoomName", roomName);
		
		appendChatMessage(notification, root, chatMessage, -1);
		
		notification.appendChild(root);
		return notification;
	}
	
	private static void appendCurrentChatElement(Document response, String roomName, EntropyServer server)
	{
		Element root = response.getDocumentElement();
		Element rootChatElement = response.createElement(RESPONSE_TAG_CHAT_NOTIFICATION);
		rootChatElement.setAttribute("RoomName", roomName);

		List<OnlineMessage> onlineMessages = server.getChatHistory(roomName);
		int size = onlineMessages.size();
		int startIndex = 0;
		if (size > 10)
		{
			startIndex = size - 10;
		}
		
		appendChatMessages(response, rootChatElement, onlineMessages, startIndex);
		root.appendChild(rootChatElement);
	}
	
	private static void appendChatMessages(Document response, Element root, List<OnlineMessage> chatHistory, 
	  int startIndex)
	{
		for (int i=startIndex; i<chatHistory.size(); i++)
		{
			OnlineMessage message = chatHistory.get(i);
			int index = i - startIndex;
			appendChatMessage(response, root, message, index);
		}
	}
	
	private static void appendChatMessage(Document response, Element root, OnlineMessage message, int index)
	{
		Element messageElement = response.createElement("Message");
		
		if (index > -1)
		{
			messageElement.setAttribute("Index", "" + index);
		}
		
		messageElement.setAttribute("MessageText", message.getText());
		messageElement.setAttribute("Colour", message.getColour());
		messageElement.setAttribute("Username", message.getUsername());
		
		root.appendChild(messageElement);
	}
	
	public static Document getSymmetricKeyAcknowledgement()
	{
		return symmetricKeyAck;
	}
	
	public static Document getChangePortResponse(EntropyServer server, String ipAndPort)
	{
		int index = ipAndPort.indexOf("_");
		String ip = ipAndPort.substring(0, index);
		
		ArrayList<String> ports = server.getAllPortsCurrentlyUsedByIp(ip);
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_CHANGE_PORT);
		
		for (int i=0; i<ports.size(); i++)
		{
			Element portElement = response.createElement("UsedPort");
			portElement.setAttribute("PortNumber", ports.get(i));
			rootElement.appendChild(portElement);
		}
		
		response.appendChild(rootElement);
		return response;	
	}
	
	public static Document appendLobbyResponse(Document message, EntropyServer server)
	{
		List<Room> rooms = server.getRooms();
		List<UserConnection> userConnections = server.getUserConnections(true);
		
		Element root = message.getDocumentElement();
		Element rootElement = message.createElement(RESPONSE_TAG_LOBBY_NOTIFICATION);
		
		for (int i=0; i<rooms.size(); i++)
		{
			Room room = rooms.get(i);
			String name = room.getRoomName();
			List<String> currentPlayers = room.getCurrentPlayers();
			String players = "" + room.getPlayers();
			List<String> observers = room.getObservers();
			String mode = "" + room.getMode();
			String jokerQuantity = "" + room.getJokerQuantity();
			String jokerValue = "" + room.getJokerValue();
			boolean includeMoons = room.getIncludeMoons();
			boolean includeStars = room.getIncludeStars();
			boolean illegalAllowed = room.getIllegalAllowed();
			boolean negativeJacks = room.getNegativeJacks();
			boolean cardReveal = room.getCardReveal();
			
			Element roomElement = message.createElement("Room");
			roomElement.setAttribute("RoomName", name);
			roomElement.setAttribute("GameMode", mode);
			roomElement.setAttribute("Players", players);
			roomElement.setAttribute("JokerQuantity", jokerQuantity);
			roomElement.setAttribute("JokerValue", jokerValue);
			XmlUtil.setAttributeBoolean(roomElement, "IncludeMoons", includeMoons);
			XmlUtil.setAttributeBoolean(roomElement, "IncludeStars", includeStars);
			XmlUtil.setAttributeBoolean(roomElement, "NegativeJacks", negativeJacks);
			XmlUtil.setAttributeBoolean(roomElement, "IllegalAllowed", illegalAllowed);
			XmlUtil.setAttributeBoolean(roomElement, "CardReveal", cardReveal);
			
			for (int j=0; j<currentPlayers.size(); j++)
			{
				Element playerElement = message.createElement("Player");
				playerElement.setAttribute("Username", currentPlayers.get(j));
				roomElement.appendChild(playerElement);
			}

			for (int j=0; j<observers.size(); j++)
			{
				Element observerElement = message.createElement("Observer");
				observerElement.setAttribute("Username", observers.get(j));
				roomElement.appendChild(observerElement);
			}
			
			rootElement.appendChild(roomElement);
		}
		
		for (int i=0; i<userConnections.size(); i++)
		{
			UserConnection usc = userConnections.get(i);
			String username = usc.getUsername();
			String colour = usc.getColour();
			boolean mobile = usc.getMobile();
			int achievements = playerStats.getInt(username + "Achievements", 0);
			
			Element onlineUserElement = message.createElement("OnlineUser");
			onlineUserElement.setAttribute("Username", username);
			onlineUserElement.setAttribute("Achievements", "" + achievements);
			onlineUserElement.setAttribute("Colour", colour);
			XmlUtil.setAttributeBoolean(onlineUserElement, "Mobile", mobile);
			
			rootElement.appendChild(onlineUserElement);
		}
		
		if (root != null)
		{
			root.appendChild(rootElement);
		}
		else
		{
			message.appendChild(rootElement);
		}
		
		return message;
	}
	
	public static Document getRoomJoinResponse(Room room, String username, String observerStr, int playerNumber, EntropyServer server)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_JOIN_ROOM_RESPONSE);
		rootElement.setAttribute("RoomId", room.getRoomName());
		
		boolean observer = !observerStr.isEmpty();
		if (observer)
		{
			room.addToObservers(username);
			rootElement.setAttribute("Observer", "true");
		}
		else
		{
			synchronized (room)
			{
				if (!room.isFull())
				{
					playerNumber = room.addToCurrentPlayers(username, playerNumber);
					rootElement.setAttribute("PlayerNumber", "" + playerNumber);
					
					if (room.isFull())
					{
						server.registerCopy(room);
					}
				}
				else
				{
					rootElement.setAttribute("RoomFull", "true");
				}
			}
		}
		
		response.appendChild(rootElement);
		
		//Append the current chat history and players for this room
		appendCurrentChatElement(response, room.getRoomName(), server);
		addPlayers(room, rootElement);
		
		if (observer)
		{
			addFormerPlayers(room, rootElement);
		}
		
		return response;
	}
	
	public static Document getCloseRoomResponse(Room room, String username) 
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_CLOSE_ROOM_RESPONSE);
		rootElement.setAttribute("RoomId", room.getRoomName());

		room.removeFromObservers(username);
		room.removePlayer(username, true);
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getPlayerNotification(Room room)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_PLAYER_NOTIFICATION);
		rootElement.setAttribute("RoomName", room.getRoomName());
		
		addPlayers(room, rootElement);
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getObserverResponse(Room room)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_OBSERVER_RESPONSE);
		rootElement.setAttribute("RoomId", room.getRoomName());
		
		GameWrapper game = room.getNextGameForId("");
		long countdownTimeRemaining = game.getCountdownTimeRemaining();
		
		if (countdownTimeRemaining > 0)
		{
			rootElement.setAttribute("CountdownTimeRemaining", "" + countdownTimeRemaining);
		}
		else if (room.isGameInProgress())
		{
			XmlUtil.setAttributeBoolean(rootElement, "GameInProgress", true);
			
			//Set the game start millis as it's been requested
			game.setGameStartMillisIfUnset(System.currentTimeMillis());
			
			int roundNumber = game.getRoundNumber();
			BidHistory history = game.getBidHistoryForRound(roundNumber);
			
			rootElement.setAttribute("GameId", game.getGameId());
			rootElement.setAttribute("RoundNumber", "" + roundNumber);
			rootElement.setAttribute("PersonToStart", "" + history.getPersonToStart());
			
			addPlayerHands(room, response, rootElement, game, roundNumber);
			
			int lastPlayerToAct = history.getLastPlayerToAct();
			rootElement.setAttribute("LastPlayerToAct", "" + lastPlayerToAct);
			
			addLastBids(room, roundNumber, rootElement);
		}
		
		response.appendChild(rootElement);
		return response;
	}
	private static void addPlayers(Room room, Element rootElement)
	{
		int numberOfPlayers = room.getPlayers();
		for (int i=0; i<numberOfPlayers; i++)
		{
			String username = room.getPlayer(i);
			if (username != null)
			{
				rootElement.setAttribute("Player-" + i, username);
			}
		}
	}
	private static void addFormerPlayers(Room room, Element rootElement)
	{
		int numberOfPlayers = room.getPlayers();
		for (int i=0; i<numberOfPlayers; i++)
		{
			String username = room.getFormerPlayer(i);
			if (username != null)
			{
				rootElement.setAttribute("FormerPlayer-" + i, username);
			}
		}
	}
	private static void addPlayerHands(Room room, Document response, Element rootElement, GameWrapper game, int roundNumber)
	{
		HandDetails details = game.getDetailsForRound(roundNumber);
		
		int numberOfPlayers = room.getPlayers();
		for (int i=0; i<numberOfPlayers; i++)
		{
			Element handElement = response.createElement("Hand");
			handElement.setAttribute("PlayerNumber", "" + i);
			
			String[] hand = details.getHand(i);
			for (int j=0; j<hand.length; j++)
			{
				handElement.setAttribute("Card-" + j, hand[j]);
			}
			rootElement.appendChild(handElement);
		}
	}
	private static void addLastBids(Room room, int roundNumber, Element rootElement)
	{
		int numberOfPlayers = room.getPlayers();
		for (int i=0; i<numberOfPlayers; i++)
		{
			Bid bid = room.getLastBidForPlayer(i, roundNumber);
			if (bid != null)
			{
				rootElement.setAttribute("LastBid-" + i, bid.toXmlString());
			}
		}
	}
	
	public static Document getNewGameResponse(Room room, String previousGameId)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_NEW_GAME);
		
		rootElement.setAttribute("RoomId", room.getRoomName());
		
		if (!room.isFull())
		{
			rootElement.setAttribute("WaitingForPlayers", "true");
			response.appendChild(rootElement);
			return response;
		}
		
		GameWrapper nextGame = room.getNextGameForId(previousGameId);
		nextGame.setCountdownStartMillisIfUnset();
		
		long countdownTimeRemaining = nextGame.getCountdownTimeRemaining();
		if (countdownTimeRemaining > 0)
		{
			rootElement.setAttribute("CountdownTimeRemaining", "" + countdownTimeRemaining);
			response.appendChild(rootElement);
			return response;
		}
		
		//Set the game start millis as it's been requested
		nextGame.setGameStartMillisIfUnset(System.currentTimeMillis());
		
		rootElement.setAttribute("GameId", nextGame.getGameId());
		addPlayerHands(room, response, rootElement, nextGame, 1);
		rootElement.setAttribute("PersonToStart", "" + nextGame.getPersonToStart(1));
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getBidAck(Room room, String gameId, int roundNumber, String bidStr, int previousBidder)
	{
		boolean includeMoons = room.getIncludeMoons();
		boolean includeStars = room.getIncludeStars();
		
		Bid bid = Bid.factoryFromXmlString(bidStr, includeMoons, includeStars);
		int playerNumber = bid.getPlayer().getPlayerNumber();
		boolean added = room.addBidForPlayer(gameId, playerNumber, roundNumber, bid);
		
		Bid previousBid = room.getLastBidForPlayer(previousBidder, roundNumber);
		if (bid.isChallenge()
		  && added)
		{
			room.handleChallenge(gameId, roundNumber, playerNumber, previousBidder, previousBid);
		}
		else if (bid.isIllegal()
		  && added)
		{
			room.handleIllegal(gameId, roundNumber, playerNumber, previousBidder, previousBid);
		}

		return ACKNOWLEDGEMENT;
	}
	
	public static Document getBidNotification(String roomName, int playerNumber, Bid bid)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_BID_NOTIFICATION);
		
		rootElement.setAttribute("RoomName", roomName);
		rootElement.setAttribute("PlayerNumber", "" + playerNumber);
		rootElement.setAttribute("Bid", bid.toXmlString());
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document factoryGameOverNotification(Room room, int winningPlayer)
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_GAME_OVER_NOTIFICATION);
		
		rootElement.setAttribute("WinningPlayer", "" + winningPlayer);
		rootElement.setAttribute("RoomName", room.getRoomName());
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document factoryNewRoundNotification(Room room, HandDetails details, int personToStart) 
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_NEW_ROUND_NOTIFICATION);
		rootElement.setAttribute("RoomName", room.getRoomName());
		
		int numberOfPlayers = room.getPlayers();
		for (int i=0; i<numberOfPlayers; i++)
		{
			String[] hand = details.getHand(i);
			if (hand.length == 0)
			{
				continue;
			}
			
			Element handElement = response.createElement("Hand");
			handElement.setAttribute("PlayerNumber", "" + i);
			for (int j=0; j<hand.length; j++)
			{
				handElement.setAttribute("Card-" + j, hand[j]);
			}
			rootElement.appendChild(handElement);
		}
		
		rootElement.setAttribute("PersonToStart", "" + personToStart);
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getStackTraceResponse()
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_STACK_TRACE);
		
		response.appendChild(rootElement);
		return response;
	}
	
	public static Document getSocketTimeOutResponse()
	{
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement(RESPONSE_TAG_SOCKET_TIME_OUT);
		
		response.appendChild(rootElement);
		return response;
	}

	public static boolean isSessionMessage(String name) 
	{
		return !name.equals(ROOT_TAG_CONNECTION_REQUEST)
		  && !name.equals(ROOT_TAG_NEW_ACCOUNT_REQUEST)
		  && !name.equals(ROOT_TAG_RESET_PASSWORD_REQUEST)
		  && !name.equals(ROOT_TAG_NEW_SYMMETRIC_KEY)
		  && !DownloadHandler.isDownloadMessage(name);
	}
}
