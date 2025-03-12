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
import object.ServerRunnable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import room.Room;
import util.*;
import utils.CoreGlobals;

public class MessageHandlerRunnable implements ServerRunnable,
											   XmlConstants
{
	private EntropyServer server = null;
	private Socket clientSocket = null;
	private String messageStr = null;
	private UserConnection usc = null;
	private SecretKey symmetricKey = LegacyConstants.INSTANCE.getSYMMETRIC_KEY();
	private boolean notificationSocket = false;
	
	public MessageHandlerRunnable(EntropyServer server, Socket clientSocket)
	{
		this.server = server;
		this.clientSocket = clientSocket;
	}

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
			
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			encryptedMessage = in.readLine();
			processMessage(encryptedMessage);
			
			//If messageStr is null here, just return out
			if (messageStr == null)
			{
				return;
			}

			//Now get the response
			Document message = XmlUtil.getDocumentFromXmlString(messageStr);
			Element root = message.getDocumentElement();
			name = root.getNodeName();
			String username = root.getAttribute("Username");
			usc = ServerGlobals.INSTANCE.getUscStore().find(username);

			Document response = getResponse();
			if (notificationSocket)
			{
				CoreGlobals.logger.info("socket", "Setting up notification socket " + name + " for " + username);
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
	
	/**
	 * Gets the response to be sent to the client. Returns null if there is an error.
	 */
	private void processMessage(String encryptedMessage) {
		Document unencryptedDocument = XmlUtil.getDocumentFromXmlString(encryptedMessage, true);
		if (unencryptedDocument != null)
		{
			//We've been sent an unencrypted XML message. Either this is the client agreeing a new
			//symmetric key, or else it's someone using an old version of Entropy/sending crap.
			messageStr = encryptedMessage;
			handleUnencryptedMessage(unencryptedDocument);
		}

		//Decrypt the message with the symmetric key
		messageStr = EncryptionUtil.decrypt(encryptedMessage, symmetricKey);
		if (messageStr == null)
		{
			Debug.append("Failed to decrypt message " + encryptedMessage);
		}
	}
	private Document getResponse() throws Throwable
	{
		if (messageStr == null)
		{
			return null;
		}
		
		return getResponseForMessage();
	}
	
	private void handleUnencryptedMessage(Document message)
	{
		Element root = message.getDocumentElement();
		String name = root.getNodeName();

		CoreGlobals.logger.info("unencrypted.message", "Received unencrypted " + name + " message. Probably using out of date version? "
				  + "Message: " + messageStr);
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
		Room room = ServerGlobals.INSTANCE.getRoomStore().findForName(id);

		usc.setLastActiveNow();
		
		if (name.equals(ROOT_TAG_CLOSE_ROOM_REQUEST))
		{
			return XmlBuilderServer.getCloseRoomResponse(room, username);
		}
		else if (name.equals(ROOT_TAG_OBSERVER_REQUEST))
		{
			return XmlBuilderServer.getObserverResponse(room);
		}
		else if (name.equals(ROOT_TAG_NEW_GAME_REQUEST))
		{
			String currentGameId = root.getAttribute("CurrentGameId");
			
			return XmlBuilderServer.getNewGameResponse(room, currentGameId);
		}
		else if (name.equals(ROOT_TAG_BID))
		{
			String gameId = root.getAttribute("GameId");
			int roundNumber = XmlUtil.getAttributeInt(root, "RoundNumber");
			String bidStr = root.getAttribute("Bid");
			int previousBidder = XmlUtil.getAttributeInt(root, "PreviousBidder", -1);
			
			return XmlBuilderServer.getBidAck(room, gameId, roundNumber, bidStr, previousBidder);
		}
		else if (name.equals(ROOT_TAG_LEADERBOARD_REQUEST))
		{
			List<Room> rooms = ServerGlobals.INSTANCE.getRoomStore().getAll();
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
