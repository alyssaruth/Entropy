package online.util;

import javax.crypto.SecretKey;

import object.Bid;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.AbstractClient;
import util.Debug;
import util.EncryptionUtil;
import util.KeyGeneratorUtil;
import util.MessageUtil;
import util.OnlineConstants;
import util.XmlConstants;
import util.XmlUtil;

public class XmlBuilderClient implements XmlConstants
{
	/**
	 * Used for symmetricKey requests
	 */
	public static boolean sendSymmetricKeyRequest()
	{
		//1. Clear the old key and cache a new one in the 'temporary' space
		MessageUtil.symmetricKey = null;
		SecretKey symmetricKey = KeyGeneratorUtil.generateSymmetricKey();
		MessageUtil.tempSymmetricKey = symmetricKey;
		
		//2. Factory the request and fire it off in the same thread
		Document symmetricKeyRequest = factoryNewSymmetricKeyRequest(symmetricKey);
		String responseStr = AbstractClient.getInstance().sendSync(symmetricKeyRequest, false);
		
		//3. If the response is valid, swap the temporary key into the permanent space
		if (validSymmetricKeyResponse(responseStr))
		{
			if (MessageUtil.tempSymmetricKey != null)
			{
				MessageUtil.symmetricKey = MessageUtil.tempSymmetricKey;
				MessageUtil.tempSymmetricKey = null;
			}
			
			return true;
		}
		
		return false;
	}
	
	private static boolean validSymmetricKeyResponse(String responseStr)
	{
		//1. If the response is null (e.g. we had some kind of timeout), return false
		if (responseStr == null)
		{
			return false;
		}
		
		//2. We must be able to decrypt the response using the temporary symmetric key
		responseStr = EncryptionUtil.decrypt(responseStr, MessageUtil.tempSymmetricKey);
		if (responseStr == null)
		{
			Debug.stackTrace("Failed to decrypt symmetric key response: " + responseStr + " - Server may not be genuine.");
			return false;
		}
		
		//3. Check the response decrypts to valid XML and that it is a valid symmetric key response
		Document response = XmlUtil.getDocumentFromXmlString(responseStr);
		Element root = response.getDocumentElement();
		String responseName = root.getNodeName();
		if (responseName.equals(RESPONSE_TAG_CHANGE_PORT))
		{
			//We've been told to change port because someone else with the same IP is connected
			boolean success = MessageUtil.changeCachedPort(root);
			if (!success)
			{
				return false;
			}
			
			return XmlBuilderClient.sendSymmetricKeyRequest();
		}
		
		if (!responseName.equals(RESPONSE_TAG_SYMMETRIC_KEY))
		{
			Debug.stackTrace("Unexpected responseName in response to symmetric key request: " + responseName);
			return false;
		}
		
		return true;
	}
	
	private static Document factoryNewSymmetricKeyRequest(SecretKey symmetricKey)
	{
		String symmetricKeyString = EncryptionUtil.convertSecretKeyToString(symmetricKey);
		String encryptedKey = EncryptionUtil.encrypt(symmetricKeyString, MessageUtil.publicKey, true);
		
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_NEW_SYMMETRIC_KEY);
		rootElement.setAttribute("EncryptedKey", encryptedKey);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryHeartbeat(String username)
	{
		return XmlUtil.factorySimpleMessage(username, ROOT_TAG_HEARTBEAT);
	}
	
	public static Document factoryNewAccountRequest(String username, String passwordHash, String email)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_NEW_ACCOUNT_REQUEST);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("Password", passwordHash);
		rootElement.setAttribute("Email", email);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryChangePasswordRequest(String username, String oldPasswordHash, String newPasswordHash)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_CHANGE_PASSWORD_REQUEST);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("PasswordOld", oldPasswordHash);
		rootElement.setAttribute("PasswordNew", newPasswordHash);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryChangeEmailRequest(String username, String oldEmail, String newEmail, boolean sendTest)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_CHANGE_EMAIL_REQUEST);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("EmailOld", oldEmail);
		rootElement.setAttribute("EmailNew", newEmail);
		XmlUtil.setAttributeBoolean(rootElement, "SendTest", sendTest);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryConnectionRequest(String username, String passwordHash, boolean mobile)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_CONNECTION_REQUEST);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("Password", passwordHash);
		rootElement.setAttribute("Version", OnlineConstants.SERVER_VERSION);
		XmlUtil.setAttributeBoolean(rootElement, "Mobile", mobile);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryResetPasswordRequest(String username, String email)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_RESET_PASSWORD_REQUEST);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("Email", email);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryDisconnectRequest(String username)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_DISCONNECT_REQUEST);
		rootElement.setAttribute("Username", username);
		
		document.appendChild(rootElement);
		return document;
	}
	
	public static Document factoryNewChatXml(String roomId, String username, String colour, String message)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_NEW_CHAT);
		
		if (message.startsWith("/admin "))
		{
			message = message.substring("/admin ".length());
			rootElement.setAttribute("Admin", "true");
		}
		
		rootElement.setAttribute("RoomId", roomId);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("MessageText", message);
		rootElement.setAttribute("Colour", colour);
		
		document.appendChild(rootElement);
		
		return document;
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