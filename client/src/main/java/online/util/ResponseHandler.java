
package online.util;

import object.Bid;
import online.screen.EntropyLobby;
import online.screen.GameRoom;
import online.screen.Leaderboard;
import online.screen.OnlineStatsPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import screen.ScreenCache;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static utils.CoreGlobals.logger;

public class ResponseHandler implements XmlConstants
{
	public static void handleResponse(String messageStr, String responseStr) throws Throwable {
		var decryptedResponseStr = EncryptionUtil.decrypt(responseStr, MessageUtil.symmetricKey);
		if (decryptedResponseStr == null) {
			throw new Throwable("Failed to decrypt response. Server may not be genuine.");
		}

		handleDecryptedResponse(messageStr, decryptedResponseStr);
	}
	public static void handleDecryptedResponse(String messageStr, String responseStr) throws Throwable {
		Document response = XmlUtil.getDocumentFromXmlString(responseStr);
		
		Element root = response.getDocumentElement();
		String responseName = root.getNodeName();
		EntropyLobby lobby = ScreenCache.get(EntropyLobby.class);

		if (responseName.equals(RESPONSE_TAG_ACKNOWLEDGEMENT))
		{
			//do nothing
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
			
			List<String> hand = getHandFromElement(child);
			gameRoom.setHand(playerNumber, hand);
		}
	}
	
	public static List<String> getHandFromElement(Element handElement)
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
		
		return cards;
	}
	
	private static void handleLeaderboardResponse(Element root)
	{
		Leaderboard leaderboard = ScreenCache.get(Leaderboard.class);
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
	}
}
