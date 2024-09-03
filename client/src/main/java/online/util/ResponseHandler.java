
package online.util;

import object.Bid;
import object.OnlineMessage;
import object.OnlineUsername;
import object.RoomWrapper;
import online.screen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import screen.ScreenCache;
import util.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static utils.InjectedThings.logger;

public class ResponseHandler implements XmlConstants
{
	public static void handleResponse(String messageStr, String responseStr) throws Throwable
	{
		EntropyLobby lobby = ScreenCache.getEntropyLobby();
		
		SecretKey symmetricKeyForDecryption = MessageUtil.symmetricKey;
		if (symmetricKeyForDecryption == null)
		{
			Debug.stackTrace("Handling server response with no symmetricKey set");
		}
		
		responseStr = EncryptionUtil.decrypt(responseStr, symmetricKeyForDecryption);
		if (responseStr == null)
		{
			throw new Throwable("Failed to decrypt response. Server may not be genuine.");
		}
		
		Document response = XmlUtil.getDocumentFromXmlString(responseStr);
		
		Element root = response.getDocumentElement();
		String responseName = root.getNodeName();
		
		if (responseName.equals(RESPONSE_TAG_KICK_OFF))
		{
			handleKickOff(root);
			return;
		}
		
		MessageUtil.setShuttingDown(false);
		if (responseName.equals(RESPONSE_TAG_NEW_ACCOUNT))
		{
			handleNewAccountResponse(root);
		}
		else if (responseName.equals(RESPONSE_TAG_CHANGE_PASSWORD))
		{
			handleChangePasswordResponse(root);
		}
		else if (responseName.equals(RESPONSE_TAG_CONNECT_SUCCESS))
		{
			handleConnectSuccess(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_CONNECT_FAILURE))
		{
			handleConnectFailure(root);
			return;
		}
		else if (responseName.equals(RESPONSE_TAG_ACKNOWLEDGEMENT))
		{
			//do nothing
		}
		else if (responseName.equals(RESPONSE_TAG_CHAT_NOTIFICATION))
		{
			handleChatResponse(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_LOBBY_NOTIFICATION))
		{
			handleLobbyResponse(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_JOIN_ROOM_RESPONSE))
		{
			handleJoinRoomAck(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_CLOSE_ROOM_RESPONSE))
		{
			handleCloseRoomAck(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_PLAYER_NOTIFICATION))
		{
			handlePlayerNotification(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_OBSERVER_RESPONSE))
		{
			handleObserverResponse(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_NEW_GAME))
		{
			handleNewGameResponse(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_BID_NOTIFICATION))
		{
			handleNewBid(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_NEW_ROUND_NOTIFICATION))
		{
			handleNewRoundResponse(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_GAME_OVER_NOTIFICATION))
		{
			handleGameOverResponse(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_LEADERBOARD))
		{
			handleLeaderboardResponse(root);
		}
		else if (responseName.equals(RESPONSE_TAG_STATISTICS_NOTIFICATION))
		{
			handleStatisticsResponse(root, lobby);
		}
		else if (responseName.equals(RESPONSE_TAG_STACK_TRACE))
		{
			ScreenCache.dismissConnectingDialog();
			
			throw new Throwable("Server asked to stack trace.");
		}
		else if (responseName.equals(RESPONSE_TAG_SOCKET_TIME_OUT))
		{
			logger.info("resendingMessage", "Resending " + messageStr + " because Server had a SocketTimeout");
			MessageUtil.sendMessage(messageStr, 0);
		}
		else
		{
			throw new Throwable("Unexpected response.");
		}
	}
	
	private static void handleNewAccountResponse(Element root)
	{
		NewAccountDialog newAccountDialog = ScreenCache.getNewAccountDialog();
		ScreenCache.dismissConnectingDialog();
		
		String error = root.getAttribute("Error");
		if (!error.isEmpty())
		{
			DialogUtil.showErrorLater(error);
		}
		else
		{
			DialogUtil.showInfoLater("Account created successfully.");
			newAccountDialog.disposeLater();
		}
	}
	
	private static void handleChangePasswordResponse(Element root)
	{
		ChangePasswordDialog changePasswordDialog = ScreenCache.getChangePasswordDialog();
		
		String error = root.getAttribute("Error");
		if (!error.isEmpty())
		{
			DialogUtil.showErrorLater(error);
		}
		else
		{
			DialogUtil.showInfoLater("Password changed successfully.");
			changePasswordDialog.disposeLater();
		}
	}
	
	private static void handleConnectSuccess(Element root, final EntropyLobby lobby)
	{
		ScreenCache.dismissConnectingDialog();
		
		LoginDialog loginDialog = ScreenCache.getLoginDialog();
		loginDialog.dispose();
		
		final String username = root.getAttribute("Username");
		String email = root.getAttribute("Email");
		lobby.setUsername(username);
		lobby.setEmail(email);
		lobby.setLocationRelativeTo(null);
		lobby.setVisible(true);
		lobby.init();
		
		boolean changePassword = XmlUtil.getAttributeBoolean(root, "ChangePassword");
		if (changePassword)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(username, false);
					ScreenCache.setChangePasswordDialog(changePasswordDialog);
					changePasswordDialog.setModal(true);
					changePasswordDialog.setLocationRelativeTo(lobby);
					changePasswordDialog.setVisible(true);
				}
			});
		}
		
		//Handle appended lobby response
		Element lobbyResponseElement = XmlUtil.getElementIfExists(root, RESPONSE_TAG_LOBBY_NOTIFICATION);
		handleLobbyResponse(lobbyResponseElement, lobby);
		
		//Handle appended chat response
		Element chatChild = XmlUtil.getElementIfExists(root, RESPONSE_TAG_CHAT_NOTIFICATION);
		handleChatResponse(chatChild, lobby);
		
		//Handle appended stats response
		Element statsChild = XmlUtil.getElementIfExists(root, RESPONSE_TAG_STATISTICS_NOTIFICATION);
		handleStatisticsResponse(statsChild, lobby);
		
		ScreenCache.getMainScreen().minimise();
	}
	
	private static void handleConnectFailure(Element root)
	{
		String failureReason = root.getAttribute("FailureReason");
		
		ScreenCache.dismissConnectingDialog();
		
		if (failureReason.contains("out of date"))
		{
			promptForUpdate();
		}
		else
		{
			DialogUtil.showErrorLater(failureReason);
		}
	}
	
	private static void promptForUpdate()
	{
		SwingUtilities.invokeLater(() -> {
            int answer = DialogUtil.showQuestion("Entropy needs to update in order to connect. \n\nUpdate now?", false);
            if (answer == JOptionPane.YES_OPTION)
            {
                UpdateManager.INSTANCE.checkForUpdates(OnlineConstants.ENTROPY_VERSION_NUMBER);
            }
        });
	}
	
	private static void handleKickOff(Element root)
	{
		ConnectingDialog dialog = ScreenCache.getConnectingDialog();
		if (dialog.isVisible())
		{
			dialog.dismissDialog();
			DialogUtil.showErrorLater("Unable to connect to the Entropy server.");
			return;
		}
		
		EntropyLobby lobby = ScreenCache.getEntropyLobby();
		if (lobby.isVisible())
		{
			String removalReason = root.getAttribute("RemovalReason");
			String message = removalReason + "\nEntropyOnline will now exit.";
			DialogUtil.showErrorLater(message);
			
			lobby.exit(true);
		}
	}
	
	private static void handleChatResponse(Element root, EntropyLobby lobby)
	{
		String name = root.getAttribute("RoomName");
		OnlineChatPanel panel = lobby.getChatPanelForRoomName(name);
		
		NodeList children = root.getElementsByTagName("Message");
		int length = children.getLength();
		if (length == 1)
		{
			Element child = (Element)children.item(0);
			OnlineMessage message = getOnlineMessageFromChild(child);
			panel.updateChatBox(message);
		}
		else
		{
			//If we have more than one tag, we could parse them in any order. Build up a list of the messages,
			//paying attention to the extra 'Index' attribute
			List<OnlineMessage> messages = new ArrayList<>();
			for (int i=0; i<length; i++)
			{
				Element child = (Element)children.item(i);
				OnlineMessage message = getOnlineMessageFromChild(child);
				int index = XmlUtil.getAttributeInt(child, "Index", -1);
				
				messages.add(index, message);
			}
			
			panel.updateChatBox(messages);
		}
	}
	
	private static OnlineMessage getOnlineMessageFromChild(Element child)
	{
		String username = child.getAttribute("Username");
		String messageText = child.getAttribute("MessageText");
		String colour = child.getAttribute("Colour");
		
		return new OnlineMessage(colour, messageText, username);
	}
	
	private static void handleLobbyResponse(Element root, EntropyLobby lobby)
	{
		List<RoomWrapper> rooms = new ArrayList<>();
		
		NodeList children = root.getElementsByTagName("Room");
		int length = children.getLength();
		
		for (int i=0; i<length; i++)
		{
			Element child = (Element)children.item(i);
			
			String name = child.getAttribute("RoomName");
			int players = XmlUtil.getAttributeInt(child, "Players");
			int gameMode = XmlUtil.getAttributeInt(child, "GameMode");
			int jokerQuantity = XmlUtil.getAttributeInt(child, "JokerQuantity");
			int jokerValue = XmlUtil.getAttributeInt(child, "JokerValue");
			boolean includeMoons = XmlUtil.getAttributeBoolean(child, "IncludeMoons");
			boolean includeStars = XmlUtil.getAttributeBoolean(child, "IncludeStars");
			boolean negativeJacks = XmlUtil.getAttributeBoolean(child, "NegativeJacks");
			boolean illegalAllowed = XmlUtil.getAttributeBoolean(child, "IllegalAllowed");
			boolean cardReveal = XmlUtil.getAttributeBoolean(child, "CardReveal");
			
			NodeList currentPlayers = child.getElementsByTagName("Player");
			List<String> currentPlayerList = factoryListForNodeList(currentPlayers);
			
			NodeList observers = child.getElementsByTagName("Observer");
			List<String> observerList = factoryListForNodeList(observers);
			
			RoomWrapper room = new RoomWrapper(name, gameMode, players);
			room.setCurrentPlayers(currentPlayerList);
			room.setObservers(observerList);
			room.setJokerQuantity(jokerQuantity);
			room.setJokerValue(jokerValue);
			room.setIncludeMoons(includeMoons);
			room.setIncludeStars(includeStars);
			room.setNegativeJacks(negativeJacks);
			room.setIllegalAllowed(illegalAllowed);
			room.setCardReveal(cardReveal);
			rooms.add(room);
		}
		
		lobby.synchroniseRooms(rooms);
		
		NodeList usernameChildren = root.getElementsByTagName("OnlineUser");
		int usernameLength = usernameChildren.getLength();
		
		List<OnlineUsername> serverUsernames = new ArrayList<>();
		for (int i=0; i<usernameLength; i++)
		{
			Element child = (Element)usernameChildren.item(i);
			String username = child.getAttribute("Username");
			int achievements = XmlUtil.getAttributeInt(child, "Achievements");
			String colour = child.getAttribute("Colour");
			boolean mobile = XmlUtil.getAttributeBoolean(child, "Mobile");
			
			OnlineUsername onlineUser = new OnlineUsername(username, colour, achievements, mobile);
			serverUsernames.add(onlineUser);
		}
		
		lobby.synchUsernamesInAwtThread(serverUsernames);
	}
	
	private static List<String> factoryListForNodeList(NodeList list)
	{
		List<String> returnList = new ArrayList<>();
		
		int playersLength = list.getLength();
		for (int j=0; j<playersLength; j++)
		{
			Element player = (Element)list.item(j);
			String playerName = player.getAttribute("Username");
			returnList.add(playerName);
		}
		
		return returnList;
	}
	
	private static void handleJoinRoomAck(final Element root, final EntropyLobby lobby)
	{
		String username = lobby.getUsername();
		
		String id = root.getAttribute("RoomId");
		String roomFull = root.getAttribute("RoomFull");
		
		if (!roomFull.isEmpty())
		{
			DialogUtil.showErrorLater("The room you are trying to join is now full.");
			return;
		}
		
		String observer = root.getAttribute("Observer");
		int playerNumber = XmlUtil.getAttributeInt(root, "PlayerNumber");
		if (observer.isEmpty() && playerNumber == -1)
		{
			DialogUtil.showErrorLater("The seat you tried to take is now occupied.");
			return;
		}
		
		final GameRoom gameRoom = lobby.getGameRoomForName(id);
		final boolean hotswap = gameRoom.isVisible();
		gameRoom.setUsername(username);
		
		if (observer.isEmpty())
		{
			gameRoom.adjustSize();
			gameRoom.setObserver(false);
			
			gameRoom.setPlayerNumber(playerNumber);
		}
		else
		{
			gameRoom.setObserver(true);
		}
		
		final Element chatChild = XmlUtil.getElementIfExists(root, RESPONSE_TAG_CHAT_NOTIFICATION);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				gameRoom.setVisible(true);
				gameRoom.init(hotswap);
				
				synchronisePlayers(gameRoom, root);
				
				if (!hotswap)
				{
					gameRoom.setLocationRelativeTo(ScreenCache.getEntropyLobby());
					handleChatResponse(chatChild, lobby);
				}
			}
		});
	}
	
	private static void handleCloseRoomAck(Element root, EntropyLobby lobby)
	{
		String id = root.getAttribute("RoomId");
		GameRoom gameRoom = lobby.getGameRoomForName(id);
		gameRoom.closeWindow();
	}
	private static void handlePlayerNotification(Element root, EntropyLobby lobby)
	{
		String name = root.getAttribute("RoomName");
		GameRoom gameRoom = lobby.getGameRoomForName(name);
		
		synchronisePlayers(gameRoom, root);
	}
	
	private static void handleObserverResponse(Element root, EntropyLobby lobby)
	{
		String id = root.getAttribute("RoomId");
		GameRoom gameRoom = lobby.getGameRoomForName(id);
		
		boolean gameInProgress = XmlUtil.getAttributeBoolean(root, "GameInProgress");
		if (gameInProgress)
		{
			int personToStart = XmlUtil.getAttributeInt(root, "PersonToStart");
			int roundNumber = XmlUtil.getAttributeInt(root, "RoundNumber");
			int lastPlayerToAct = XmlUtil.getAttributeInt(root, "LastPlayerToAct");
			String gameId = root.getAttribute("GameId");
			
			setHandsFromXml(gameRoom, root);
			gameRoom.setRoundNumber(roundNumber);
			gameRoom.setGameId(gameId);
			
			setBidsFromXml(gameRoom, root);
			
			gameRoom.startObserving(personToStart, lastPlayerToAct);
		}
	}
	private static void synchronisePlayers(GameRoom gameRoom, Element root)
	{
		HashMap<Integer, String> hmPlayerByPlayerNumber = new HashMap<>();
		HashMap<Integer, String> hmFormerPlayerByPlayerNumber = new HashMap<>();
		for (int i=0; i<GameRoom.MAX_NUMBER_OF_PLAYERS; i++)
		{
			String username = root.getAttribute("Player-" + i);
			if (!username.isEmpty())
			{
				hmPlayerByPlayerNumber.put(i, username);
			}
			
			String formerUsername = root.getAttribute("FormerPlayer-" + i);
			if (!formerUsername.isEmpty())
			{
				hmFormerPlayerByPlayerNumber.put(i, formerUsername);
			}
		}
		
		gameRoom.synchronisePlayers(hmPlayerByPlayerNumber, hmFormerPlayerByPlayerNumber);
	}
	private static void setBidsFromXml(GameRoom room, Element root)
	{
		for (int i=0; i<4; i++)
		{
			String bidStr = root.getAttribute("LastBid-" + i);
			if (bidStr.isEmpty())
			{
				continue;
			}
			
			boolean includeMoons = room.getIncludeMoons();
			boolean includeStars = room.getIncludeStars();
			Bid bid = Bid.factoryFromXmlString(bidStr, includeMoons, includeStars);
			room.hmBidByPlayerNumber.put(i, bid);
		}
	}
	
	private static void handleNewGameResponse(Element root, EntropyLobby lobby)
	{
		String id = root.getAttribute("RoomId");
		String gameId = root.getAttribute("GameId");
		GameRoom gameRoom = lobby.getGameRoomForName(id);
		
		boolean waitingForPlayers = XmlUtil.getAttributeBoolean(root, "WaitingForPlayers");
		if (waitingForPlayers)
		{
			gameRoom.waitingForPlayers();
			return;
		}
		
		long countdownTimeRemaining = XmlUtil.getAttributeLong(root, "CountdownTimeRemaining");
		if (countdownTimeRemaining > 0)
		{
			gameRoom.waitingForCountdown(countdownTimeRemaining);
			return;
		}
		
		gameRoom.setGameId(gameId);
		setHandsFromXml(gameRoom, root);
		
		int personToStart = XmlUtil.getAttributeInt(root, "PersonToStart");
		gameRoom.startGame(personToStart);
	}
	
	private static void handleNewBid(Element root, EntropyLobby lobby)
	{
		String id = root.getAttribute("RoomName");
		GameRoom gameRoom = lobby.getGameRoomForName(id);
		int playerNumber = XmlUtil.getAttributeInt(root, "PlayerNumber");
		String bidStr = root.getAttribute("Bid");
		
		boolean includeMoons = gameRoom.getIncludeMoons();
		boolean includeStars = gameRoom.getIncludeStars();
		Bid bid = Bid.factoryFromXmlString(bidStr, includeMoons, includeStars);
		
		gameRoom.handleBid(playerNumber, bid);
	}
	
	private static void handleGameOverResponse(Element root, EntropyLobby lobby)
	{
		pauseDuringChallenge();
		
		String name = root.getAttribute("RoomName");
		GameRoom gameRoom = lobby.getGameRoomForName(name);
		
		int winningPlayer = XmlUtil.getAttributeInt(root, "WinningPlayer");
		gameRoom.processEndOfGameFromServer(winningPlayer);
	}
	
	private static void handleNewRoundResponse(Element root, EntropyLobby lobby)
	{
		pauseDuringChallenge();
		
		String name = root.getAttribute("RoomName");
		GameRoom gameRoom = lobby.getGameRoomForName(name);
		gameRoom.saveRoundForReplay();
		
		setHandsFromXml(gameRoom, root);
		
		int personToStart = XmlUtil.getAttributeInt(root, "PersonToStart");
		gameRoom.startRound(personToStart);
	}
	
	/**
	 * When we receive a new round notification or a game over notification, the client will currently be displaying
	 * the result of the last round. Pause before moving on so the user can take in what's happened.
	 */
	private static void pauseDuringChallenge()
	{
		int sleepMillis = 1000 * Registry.prefs.getInt(Registry.PREFERENCES_INT_AUTO_START_SECONDS, 2);
		try { Thread.sleep(sleepMillis); } catch (Throwable t) {}
	}
	
	private static void setHandsFromXml(GameRoom gameRoom, Element root)
	{
		gameRoom.clearHands();
		
		NodeList children = root.getElementsByTagName("Hand");
		int length = children.getLength();
		
		for (int i=0; i<length; i++)
		{
			Element child = (Element)children.item(i);
			int playerNumber = XmlUtil.getAttributeInt(child, "PlayerNumber");
			
			String[] hand = getHandFromElement(child);
			gameRoom.setHand(playerNumber, hand);
		}
	}
	
	public static String[] getHandFromElement(Element handElement)
	{
		List<String> cards = new ArrayList<>();
		
		for (int j=0; j<5; j++)
		{
			String card = handElement.getAttribute("Card-" + j);
			if (!card.isEmpty())
			{
				cards.add(card);
			}
		}
		
		int size = cards.size();
		String[] hand = new String[size];
		for (int j=0; j<size; j++)
		{
			hand[j] = cards.get(j);
		}
		
		return hand;
	}
	
	private static void handleLeaderboardResponse(Element root)
	{
		Leaderboard leaderboard = ScreenCache.getLeaderboard();
		if (!leaderboard.isVisible())
		{
			//don't bother doing anything
			return;
		}

		leaderboard.buildTablesFromResponseLater(root);
	}
	
	private static void handleStatisticsResponse(Element root, EntropyLobby lobby)
	{
		OnlineStatsPanel statsPanel = lobby.getOnlineStatsPanel();
		statsPanel.updateVariablesFromResponse(root);
		
		//Also update our local stats so we can unlock achievements
		AchievementsUtil.updateOnlineStats(root);
	}
}
