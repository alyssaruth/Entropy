package online.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import game.PlayerAction;
import object.Bid;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.XmlConstants;
import util.XmlUtil;
import utils.CoreGlobals;

public class XmlBuilderClient implements XmlConstants
{
	public static Document factoryHeartbeat(String username)
	{
		return XmlUtil.factorySimpleMessage(username, ROOT_TAG_HEARTBEAT);
	}
	
	public static Document factoryObserverRequest(String roomId, String username)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_OBSERVER_REQUEST);
		
		rootElement.setAttribute("RoomId", roomId);
		rootElement.setAttribute("Username", username);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryNewGameRequest(String roomId, String gameId, String username)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_NEW_GAME_REQUEST);
		
		rootElement.setAttribute("RoomId", roomId);
		rootElement.setAttribute("CurrentGameId", gameId);
		rootElement.setAttribute("Username", username);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryBidXml(String roomId, String username, String gameId, int roundNumber, PlayerAction action, int previousBidder)
	{
		try {
			Document document = XmlUtil.factoryNewDocument();
			Element rootElement = document.createElement(ROOT_TAG_BID);

			rootElement.setAttribute("RoomId", roomId);
			rootElement.setAttribute("Username", username);
			rootElement.setAttribute("GameId", gameId);
			rootElement.setAttribute("RoundNumber", "" + roundNumber);
			rootElement.setAttribute("Bid", CoreGlobals.jsonMapper.writeValueAsString(action));

			if (previousBidder > -1)
			{
				rootElement.setAttribute("PreviousBidder", "" + previousBidder);
			}

			document.appendChild(rootElement);
			return document;
		} catch (JsonProcessingException jpe) {
			throw new RuntimeException("Couldn't write bid as JSON: " + action, jpe);
		}
	}
	
	public static Document factoryLeaderboardRequest(String username)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_LEADERBOARD_REQUEST);
		
		rootElement.setAttribute("Username", username);
		
		document.appendChild(rootElement);
		return document;
	}
}