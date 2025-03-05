package online.util;

import object.Bid;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.XmlConstants;
import util.XmlUtil;

public class XmlBuilderClient implements XmlConstants
{
	public static Document factoryHeartbeat(String username)
	{
		return XmlUtil.factorySimpleMessage(username, ROOT_TAG_HEARTBEAT);
	}
	
	public static Document factoryRoomJoinRequestXml(String id, String username, boolean observer, int playerNumber)
	{
		Document document = XmlUtil.factoryNewDocument();
		
		Element rootElement = document.createElement(ROOT_TAG_ROOM_JOIN_REQUEST);
		String optionStr = observer? "Observer":"Player";
		rootElement.setAttribute(optionStr, "true");
		rootElement.setAttribute("RoomId", id);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("PlayerNumber", "" + playerNumber);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryCloseRoomRequestXml(String id, String username)
	{
		Document document = XmlUtil.factoryNewDocument();
		
		Element rootElement = document.createElement(ROOT_TAG_CLOSE_ROOM_REQUEST);
		
		rootElement.setAttribute("RoomId", id);
		rootElement.setAttribute("Username", username);
		
		document.appendChild(rootElement);
		return document;
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
	
	public static Document factoryBidXml(String roomId, String username, String gameId, int roundNumber, Bid bid, int previousBidder)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_BID);
		
		rootElement.setAttribute("RoomId", roomId);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("GameId", gameId);
		rootElement.setAttribute("RoundNumber", "" + roundNumber);
		rootElement.setAttribute("Bid", bid.toXmlString());
		
		if (previousBidder > -1)
		{
			rootElement.setAttribute("PreviousBidder", "" + previousBidder);
		}
		
		document.appendChild(rootElement);
		return document;
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