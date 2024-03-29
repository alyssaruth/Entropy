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

import object.NotificationSocket;
import object.Room;
import object.ServerRunnable;
import object.UserConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.AccountUtil;
import util.Debug;
import util.EncryptionUtil;
import util.EntropyEmailUtil;
import util.XmlBuilderServer;
import util.XmlConstants;
import util.XmlUtil;

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
		server.incrementFunctionsReceived();
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

			server.incrementFunctionsReceivedForMessage(name);
			
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
			if (server.messageIsTraced(name, username))
			{
				Debug.append("Message: " + messageStr);
				Debug.appendWithoutDate("Response: " + responseStr);
			}

			if (symmetricKey != null
			  && !DownloadHandler.isDownloadMessage(name))
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
			server.incrementFunctionsHandled();
			
			if (!name.isEmpty())
			{
				server.incrementFunctionsHandledForMessage(name);
			}
			
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
		ipAddress = address.getHostAddress() + "_" + clientSocket.getLocalPort();
		usc = server.getUserConnectionForIpAndPort(ipAddress);
		
		if (usc != null)
		{
			symmetricKey = usc.getSymmetricKey();
		}
		
		server.incrementMessageCountForIp(ipAddress);
	}
	
	/**
	 * Gets the response to be sent to the client. Returns null if there is an error.
	 */
	private Document getResponse(String encryptedMessage) throws Throwable
	{
		if (!server.isOnline())
		{
			//Don't send any kind of a response, they'll be getting a notification if they were online
			//already and otherwise they'll just get "unable to connect"
			return null;
		}
		
		Document unencryptedDocument = XmlUtil.getDocumentFromXmlString(encryptedMessage);
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
			server.incrementFunctionsReceivedAndHandledForMessage("DECRYPTION_ERROR");
			return null;
		}
		
		return getResponseForMessage();
	}
	
	private Document handleUnencryptedMessage(Document message)
	{
		//Set the USC variable here so ClientMail and CrcCheck messages show in the right place in threads
		//We'll set it again correctly if it's a symmetric key message.
		usc = new UserConnection(ipAddress, null);
		
		Element root = message.getDocumentElement();
		String name = root.getNodeName();
		if (DownloadHandler.isDownloadMessage(name))
		{
			return DownloadHandler.processMessage(message, server);
		}
		
		if (name.equals(ROOT_TAG_CLIENT_MAIL))
		{
			return EntropyEmailUtil.handleClientMail(server, root);
		}
		
		if (!name.equals(ROOT_TAG_NEW_SYMMETRIC_KEY))
		{
			Debug.append("Received unencrypted " + name + " message. Probably using out of date version? "
					  + "Message: " + messageStr);
			server.incrementFunctionsReceivedAndHandledForMessage("UNENCRYPTED");
			return null;
		}
		
		String encryptedKey = root.getAttribute("EncryptedKey");
		String symmetricKeyStr = EncryptionUtil.decrypt(encryptedKey, server.getPrivateKey(), true);
		if (symmetricKeyStr == null)
		{
			Debug.stackTrace("Failed to decrypt symmetricKeyStr " + encryptedKey + ". IP: " + ipAddress);
			server.incrementFunctionsReceivedAndHandledForMessage(name);
			return null;
		}
		
		SecretKey symmetricKeyPassedUp = EncryptionUtil.reconstructKeyFromString(symmetricKeyStr);
		if (symmetricKeyPassedUp == null)
		{
			Debug.appendWithoutDate("IP: " + ipAddress);
			server.incrementFunctionsReceivedAndHandledForMessage(name);
			return null;
		}
		
		if (server.keyHasAlreadyBeenUsed(symmetricKeyPassedUp))
		{
			Debug.append("Possible attempt at replay attack - received request to re-use key " 
						 + symmetricKeyStr + ". IP: " + ipAddress);

			server.addToBlacklist(ipAddress, "Re-used key");
			return null;
		}
		
		if (symmetricKey != null)
		{
			//An IP we already have has requested a new symmetric key. Tell them to use another port.
			symmetricKey = symmetricKeyPassedUp;
			return XmlBuilderServer.getChangePortResponse(server, ipAddress);
		}
		
		usc = new UserConnection(ipAddress, symmetricKeyPassedUp);
		usc.setLastActiveNow();
		symmetricKey = usc.getSymmetricKey();
		server.setUserConnectionForIpAndPort(ipAddress, usc);
		
		return XmlBuilderServer.getSymmetricKeyAcknowledgement();
	}
	
	private Document getResponseForMessage() throws Throwable
	{
		Document document = XmlUtil.getDocumentFromXmlString(messageStr);
		if (document == null)
		{
			Debug.append("Received unexpected message: " + messageStr);
			server.incrementFunctionsReceivedForMessage("???");
			server.incrementFunctionsHandledForMessage("???");
			return null;
		}
		
		Element root = document.getDocumentElement();
		String id = root.getAttribute("RoomId");
		String username = root.getAttribute("Username");
		String name = root.getNodeName();
		
		String usernameForThisConnection = usc.getUsername();
		if (XmlBuilderServer.isSessionMessage(name))
		{
			if (usernameForThisConnection == null
			  || !usernameForThisConnection.equals(username))
			{
				Debug.stackTrace("Failed username check for IP " + ipAddress + ": client passed up " + username 
						   	   + " but connected as " + usernameForThisConnection);
				Debug.appendWithoutDate("Message passed up: " + messageStr);
				
				server.addToBlacklist(ipAddress, "Username mismatch");
				server.removeFromUsersOnline(usc);
				
				return XmlBuilderServer.getKickOffResponse(usernameForThisConnection, REMOVAL_REASON_FAILED_USERNAME_CHECK);
			}
		}
		
		usc.setLastActiveNow();
		
		if (name.equals(ROOT_TAG_NEW_ACCOUNT_REQUEST))
		{
			String passwordHash = root.getAttribute("Password");
			String email = root.getAttribute("Email");
			return XmlBuilderServer.getNewAccountResponse(username, passwordHash, email);
		}
		else if (name.equals(ROOT_TAG_CHANGE_PASSWORD_REQUEST))
		{
			String oldPasswordHash = root.getAttribute("PasswordOld");
			String newPasswordHash = root.getAttribute("PasswordNew");
			return XmlBuilderServer.getChangePasswordResponse(username, oldPasswordHash, newPasswordHash);
		}
		else if (name.equals(ROOT_TAG_CHANGE_EMAIL_REQUEST))
		{
			String oldEmail = root.getAttribute("EmailOld");
			String newEmail = root.getAttribute("EmailNew");
			boolean sendTest = XmlUtil.getAttributeBoolean(root, "SendTest");
			return XmlBuilderServer.getChangeEmailResponse(username, oldEmail, newEmail, sendTest);
		}
		else if (name.equals(ROOT_TAG_CONNECTION_REQUEST))
		{
			String version = root.getAttribute("Version");
			String hashedPassword = root.getAttribute("Password");
			boolean mobile = XmlUtil.getAttributeBoolean(root, "Mobile");
			return XmlBuilderServer.getConnectResponse(username, hashedPassword, version, usc, server, mobile);
		}
		
		else if (name.equals(ROOT_TAG_RESET_PASSWORD_REQUEST))
		{
			String email = root.getAttribute("Email");
			return XmlBuilderServer.getResetPasswordResponse(username, email);
		}
		else if (name.equals(ROOT_TAG_DISCONNECT_REQUEST))
		{
			server.removeFromUsersOnline(usc);
			return null;
		}
		else if (name.equals(ROOT_TAG_ACHIEVEMENTS_UPDATE))
		{
			int achievementCount = XmlUtil.getAttributeInt(root, "AchievementCount");
			String achievementName = root.getAttribute("AchievementName");
			return XmlBuilderServer.getAchievementUpdateAck(server, username, achievementName, achievementCount);
		}
		else if (name.equals(ROOT_TAG_NEW_CHAT))
		{
			String newMessage = root.getAttribute("MessageText");
			String colour = root.getAttribute("Colour");
			String admin = root.getAttribute("Admin");
			
			if (!admin.isEmpty())
			{
				if (AccountUtil.isAdmin(username))
				{
					server.addAdminMessage(newMessage);
				}
				else
				{
					return XmlBuilderServer.getKickOffResponse(username, "You are not allowed to send admin messages.");
				}
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
