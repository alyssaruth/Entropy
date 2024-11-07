package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.crypto.SecretKey;

import auth.UserConnection;
import http.LegacyConstants;
import object.NotificationSocket;
import object.Room;
import object.ServerRunnable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.*;
import utils.CoreGlobals;

public class MessageHandlerRunnable implements ServerRunnable,
											   XmlConstants
{
	private EntropyServer server = null;
	private Socket clientSocket = null;
	private String messageStr = null;
	private UserConnection usc = null;
	private SecretKey symmetricKey = null;
	private String ipAddress = null;
	private boolean notificationSocket = false;
	
	public MessageHandlerRunnable(EntropyServer server, Socket clientSocket)
	{
		this.server = server;
		this.clientSocket = clientSocket;
	}
	
	@SuppressWarnings("resource")
	@Override
	public void run() 
	{
		String encryptedMessage = null;
		String name = "";
		BufferedOutputStream os = null;
		OutputStreamWriter osw = null;
		BufferedReader in = null;
		
		try
		{
			os = new BufferedOutputStream(clientSocket.getOutputStream());
			osw = new OutputStreamWriter(os, "US-ASCII");
			
			clientSocket.setSoTimeout(120 * 1000); //2 minutes
			initVariablesFromSocket();
			
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			encryptedMessage = in.readLine();

			//Now get the response
			Document response = getResponse(encryptedMessage);
			
			//If messageStr is null here, just return out
			if (messageStr == null)
			{
				return;
			}
			
			Document message = XmlUtil.getDocumentFromXmlString(messageStr);
			Element root = message.getDocumentElement();
			name = root.getNodeName();
			String username = root.getAttribute("Username");
			
			if (notificationSocket)
			{
				usc.replaceNotificationSocket(name, new NotificationSocket(clientSocket, os, osw, in));
			}
			
			//If the response is null, do nothing further
			if (response == null)
			{
				return;
			}

			String responseStr = XmlUtil.getStringFromDocument(response);
			if (symmetricKey != null)
			{
				responseStr = EncryptionUtil.encrypt(responseStr, symmetricKey);
			}
			
			osw.write(responseStr + "\n");
			osw.flush();
		}
		catch (SocketTimeoutException | SocketException ste)
		{
			Debug.append("Caught " + ste + " reading message. This probably means the user disconnected.");
			Debug.append("Socket: " + clientSocket);
			
			Document response = XmlBuilderServer.getSocketTimeOutResponse();
			sendResponseWithCatch(response, "SocketTimeOut", osw);
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " for message: " + messageStr);
			Debug.stackTrace(t);
			Document response = XmlBuilderServer.getStackTraceResponse();
			sendResponseWithCatch(response, "StackTrace", osw);
		}
		finally
		{
			if (!notificationSocket)
			{
				if (osw != null)
				{
					try {osw.close();} catch (Throwable t){}
				}
				
				if (os != null)
				{
					try {os.close();} catch (Throwable t){}
				}
				
				if (in != null)
				{
					try {in.close();} catch (Throwable t){}
				}
				
				if (clientSocket != null)
				{
					try {clientSocket.close();} catch (Throwable t){}
				}
			}
		}
	}
	
	private void initVariablesFromSocket()
	{
		InetAddress address = clientSocket.getInetAddress();
		ipAddress = address.getHostAddress();
		usc = ServerGlobals.INSTANCE.getUscStore().find(ipAddress);
		
		if (usc != null)
		{
			symmetricKey = LegacyConstants.INSTANCE.getSYMMETRIC_KEY();
		}
	}
	
	/**
	 * Gets the response to be sent to the client. Returns null if there is an error.
	 */
	private Document getResponse(String encryptedMessage) throws Throwable
	{
		Document unencryptedDocument = XmlUtil.getDocumentFromXmlString(encryptedMessage, true);
		if (unencryptedDocument != null)
		{
			//We've been sent an unencrypted XML message. Either this is the client agreeing a new
			//symmetric key, or else it's someone using an old version of Entropy/sending crap.
			messageStr = encryptedMessage;
			return handleUnencryptedMessage(unencryptedDocument);
		}

		if (symmetricKey == null)
		{
			//This client hasn't agreed a symmetric key with us. Stack trace and return null.
			//Don't blacklist for this. Whilst it shouldn't happen, I can replicate this just by connecting to the
			//server then hibernating the PC until I've been kicked off. Still stack trace for now.
			//server.incrementFunctionsReceivedAndHandledForMessage("NO_SYMMETRIC_KEY");
			//server.addToBlacklist(ipAddress, "NO_SYMMETRIC_KEY");
			Debug.stackTrace("Received non-XML message from an IP with no symmetric key: " + ipAddress + ". Message: " + encryptedMessage);
			return null;
		}

		//Decrypt the message with the symmetric key
		messageStr = EncryptionUtil.decrypt(encryptedMessage, symmetricKey);
		if (messageStr == null)
		{
			Debug.append("Failed to decrypt message " + encryptedMessage + " from IP " + ipAddress);
			return null;
		}
		
		return getResponseForMessage();
	}
	
	private Document handleUnencryptedMessage(Document message)
	{
		Element root = message.getDocumentElement();
		String name = root.getNodeName();

		CoreGlobals.logger.info("unencrypted.message", "Received unencrypted " + name + " message. Probably using out of date version? "
				  + "Message: " + messageStr);
		return null;
	}
	
	private Document getResponseForMessage() throws Throwable
	{
		Document document = XmlUtil.getDocumentFromXmlString(messageStr);
		if (document == null)
		{
			Debug.append("Received unexpected message: " + messageStr);
			return null;
		}
		
		Element root = document.getDocumentElement();
		String id = root.getAttribute("RoomId");
		String username = root.getAttribute("Username");
		String name = root.getNodeName();

		usc.setLastActiveNow();
		
		if (name.equals(ROOT_TAG_NEW_CHAT))
		{
			String newMessage = root.getAttribute("MessageText");
			String colour = root.getAttribute("Colour");
			String admin = root.getAttribute("Admin");
			
			if (!admin.isEmpty())
			{
				server.addAdminMessage(newMessage);
			}
			else
			{
				server.addToChatHistory(id, newMessage, colour, username);
			}
			
			return XmlBuilderServer.getAcknowledgement();
		}
		else if (name.equals(ROOT_TAG_ROOM_JOIN_REQUEST))
		{
			Room room = server.getRoomForName(id);
			String observerStr = root.getAttribute("Observer");
			int playerNumber = XmlUtil.getAttributeInt(root, "PlayerNumber");
			
			return XmlBuilderServer.getRoomJoinResponse(room, username, observerStr, playerNumber, server);
		}
		else if (name.equals(ROOT_TAG_CLOSE_ROOM_REQUEST))
		{
			Room room = server.getRoomForName(id);
			
			return XmlBuilderServer.getCloseRoomResponse(room, username);
		}
		else if (name.equals(ROOT_TAG_OBSERVER_REQUEST))
		{
			Room room = server.getRoomForName(id);
			
			return XmlBuilderServer.getObserverResponse(room);
		}
		else if (name.equals(ROOT_TAG_NEW_GAME_REQUEST))
		{
			Room room = server.getRoomForName(id);
			String currentGameId = root.getAttribute("CurrentGameId");
			
			return XmlBuilderServer.getNewGameResponse(room, currentGameId);
		}
		else if (name.equals(ROOT_TAG_BID))
		{
			Room room = server.getRoomForName(id);
			String gameId = root.getAttribute("GameId");
			int roundNumber = XmlUtil.getAttributeInt(root, "RoundNumber");
			String bidStr = root.getAttribute("Bid");
			int previousBidder = XmlUtil.getAttributeInt(root, "PreviousBidder", -1);
			
			return XmlBuilderServer.getBidAck(room, gameId, roundNumber, bidStr, previousBidder);
		}
		else if (name.equals(ROOT_TAG_LEADERBOARD_REQUEST))
		{
			List<Room> rooms = server.getRooms();
			return XmlBuilderServer.getLeaderboardResponse(rooms);
		}
		else if (name.endsWith(SOCKET_NAME_SUFFIX))
		{
			notificationSocket = true;
			return null;
		}
		else if (name.equals(ROOT_TAG_HEARTBEAT))
		{
			return XmlBuilderServer.getAcknowledgement();
		}
		else
		{
			throw new Throwable("Unknown message type: " + name);
		}
	}
	
	private void sendResponseWithCatch(Document response, String name, OutputStreamWriter osw)
	{
		if (osw == null)
		{
			Debug.append("Failed to send " + name + " response because osw was null.");
			return;
		}
		
		String responseStr = XmlUtil.getStringFromDocument(response);

		try
		{
			responseStr = EncryptionUtil.encrypt(responseStr, symmetricKey);
			osw.write(responseStr + "\n");
			osw.flush();
		}
		catch (Throwable t2)
		{
			Debug.append("Caught " + t2 + " trying to send " + name + " response.");
		}
	}
	
	@Override
	public String toString() 
	{
		String threadName = Thread.currentThread().getName();
		InetAddress address = clientSocket.getInetAddress();
		String ipAddress = address.getHostAddress();
		return threadName + ": " + ipAddress;
	}

	@Override
	public String getDetails()
	{
		if (messageStr != null)
		{
			String messageName = "Encrypted";
			Document xmlMessage = XmlUtil.getDocumentFromXmlString(messageStr);
			if (xmlMessage != null)
			{
				messageName = xmlMessage.getDocumentElement().getNodeName();
			}
			
			return "Handling " + messageName + " message";
		}
		
		return "Handling unknown message";
	}

	@Override
	public UserConnection getUserConnection()
	{
		return usc;
	}
}
