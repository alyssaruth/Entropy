package util;

import online.screen.EntropyLobby;
import org.w3c.dom.Document;
import screen.ScreenCache;

import java.util.ArrayList;
import java.util.Locale;

import static utils.CoreGlobals.logger;

/**
 * Interface used by Entropy Android & Desktop for anything to do with the online session
 */
public class ClientUtil
{
	public static boolean devMode = false;
	public static String operatingSystem = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
	public static boolean justUpdated = false;
	public static int instanceNumber = 1;
	
	//Properties on the instance
	private static long lastSentMessageMillis = -1;
	private final static ArrayList<MessageSenderParams> pendingMessages = new ArrayList<>();


	public static String getUsername() {
		return ScreenCache.get(EntropyLobby.class).getUsername();
	}
	public static boolean isOnline() {
		return ScreenCache.get(EntropyLobby.class).isVisible();
	}
	public static void sendAsyncInSingleThread(MessageSenderParams message) {
		addToPendingMessages(message);

		MessageSender senderRunnable = new MessageSender();
		Thread senderThread = new Thread(senderRunnable, "MessageSender-" + System.currentTimeMillis());
		senderThread.start();
	}
	
	/**
	 * Helpers during startup
	 */
	public static void parseProgramArguments(String[] args)
	{
		for (int i=0; i<args.length; i++)
		{
			String arg = args[i];
			parseProgramArgument(arg);
		}
	}
	private static void parseProgramArgument(String arg)
	{
		if (arg.equals("justUpdated"))
		{
			justUpdated = true;
			logger.info("justUpdated", "Running in justUpdated mode");
		}
		else if (arg.equals("devMode"))
		{
			devMode = true;
			logger.info("devMode", "Running in devMode");
		}
		else
		{
			logger.info("unexpectedArgument", "Unexpected program argument: " + arg);
		}
	}
	public static boolean isAppleOs()
	{
		return operatingSystem.contains("mac") || operatingSystem.contains("darwin");
	}
	public static boolean isWindowsOs()
	{
		return operatingSystem.contains("windows");
	}
	
	public static void checkForUpdatesIfRequired()
	{
		if (justUpdated)
		{
			logger.info("justUpdated", "Just updated - not checking for updates");
			return;
		}

		ClientGlobals.INSTANCE.getUpdateManager().checkForUpdates(OnlineConstants.ENTROPY_VERSION_NUMBER);
	}
	
	public static long getLastSentMessageMillis()
	{
		return lastSentMessageMillis;
	}
	public static void setLastSentMessageMillis(long newValue)
	{
		lastSentMessageMillis = newValue;
	}

	public static String sendSync(Document message, boolean encrypt, int readTimeOut, boolean alwaysRetryOnSoTimeout)
	{
		String messageString = XmlUtil.getStringFromDocument(message);
		String encryptedMessageString = messageString;
		if (encrypt)
		{
			encryptedMessageString = EncryptionUtil.encrypt(encryptedMessageString, MessageUtil.symmetricKey);
		}
		
		MessageSenderParams wrapper = new MessageSenderParams(messageString, 0, 5);
		wrapper.setEncryptedMessageString(encryptedMessageString);
		wrapper.setIgnoreResponse(true);
		wrapper.setReadTimeOut(readTimeOut);
		wrapper.setAlwaysRetryOnSoTimeout(alwaysRetryOnSoTimeout);
		
		MessageSender sender = new MessageSender(wrapper);
		return sender.sendMessage();
	}
	
	public static void startNotificationThreads()
	{
		startNotificationThread(XmlConstants.SOCKET_NAME_GAME);
		startNotificationThread(XmlConstants.SOCKET_NAME_CHAT);
		startNotificationThread(XmlConstants.SOCKET_NAME_LOBBY);
	}
	
	private static void startNotificationThread(String socketType)
	{
		ClientNotificationRunnable runnable = new ClientNotificationRunnable(socketType);
		Thread notificationThread = new Thread(runnable, socketType + "Thread");
		notificationThread.start();
	}
	
	public static void addToPendingMessages(MessageSenderParams message)
	{
		pendingMessages.add(message);
	}
	public static MessageSenderParams getNextMessageToSend()
	{
		synchronized (pendingMessages) {
			return pendingMessages.remove(0);
		}
	}
}
