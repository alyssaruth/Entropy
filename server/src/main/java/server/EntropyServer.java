package server;

import logging.LoggerUncaughtExceptionHandler;
import object.*;
import org.w3c.dom.Document;
import screen.DebugConsole;
import util.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.List;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static util.LoggingUtilKt.dumpServerThreads;
import static utils.InjectedThings.logger;
import static utils.ThreadUtilKt.dumpThreadStacks;

public final class EntropyServer extends JFrame
						   implements ActionListener,
						   			  KeyListener,
						   			  OnlineConstants,
						   			  ServerCommands
{
	//Statics
	private static final int CORE_POOL_SIZE = 50;
	private static final int MAX_POOL_SIZE = 500;
	private static final int MAX_QUEUE_SIZE = 100;
	private static final int KEEP_ALIVE_TIME = 20;
	
	//Files
	private static final Path FILE_PATH_USED_KEYS = Paths.get("C:\\EntropyServer\\UsedKeys.txt");
	private static final Path FILE_PATH_CLIENT_VERSION = Paths.get("C:\\EntropyServer\\Version.txt");
	
	//Console
	private static DebugConsole console = new DebugConsole();
	
	//Caches
	private ExtendedConcurrentHashMap<String, UserConnection> hmUserConnectionByIpAndPort = new ExtendedConcurrentHashMap<>();
	private ConcurrentHashMap<String, Room> hmRoomByName = new ConcurrentHashMap<>();
	private ArrayList<OnlineMessage> lobbyMessages = new ArrayList<>();
	private ArrayList<SecretKey> usedSymmetricKeys = new ArrayList<>();
	
	//Stats stuff
	private ConcurrentHashMap<String, AtomicInteger> hmFunctionsReceivedByMessageType = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, AtomicInteger> hmFunctionsHandledByMessageType = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, AtomicInteger> hmNotificationsSentByNotificationType = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, AtomicInteger> hmNotificationsAttemptedByNotificationType = new ConcurrentHashMap<>();
	private AtomicInteger functionsReceived = new AtomicInteger(0);
	private AtomicInteger functionsHandled = new AtomicInteger(0);
	private AtomicInteger notificationsAttempted = new AtomicInteger(0);
	private AtomicInteger notificationsSent = new AtomicInteger(0);
	private AtomicInteger totalFunctionsHandled = new AtomicInteger(0);
	private AtomicInteger totalNotificationsSent = new AtomicInteger(0);
	private AtomicInteger mostFunctionsReceived = new AtomicInteger(0);
	private AtomicInteger mostFunctionsHandled = new AtomicInteger(0);
	
	//Blacklist
	private ConcurrentHashMap<String, AtomicInteger> hmMessagesReceivedByIp = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, BlacklistEntry> blacklist = new ConcurrentHashMap<>();
	private boolean usingBlacklist = true;
	private int blacklistMinutes = 15;
	private int messagesPerSecondThreshold = 15;
	
	//Tracing
	private ArrayList<String> tracedMessages = new ArrayList<>();
	private ArrayList<String> tracedUsers = new ArrayList<>();
	private boolean traceAll = false;

	//Properties
	private static boolean devMode = false;
	private boolean online = false;
	private boolean notificationSocketLogging = false;
	
	//Seed
	private static long currentSeedLong = 4613352884640512L;
	
	//Thread Pool
	private BlockingQueue<ServerRunnable> blockQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
	private EntropyThreadPoolExecutor tpe = new EntropyThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 
			KEEP_ALIVE_TIME, TimeUnit.SECONDS, blockQueue, this);
	
	//Other
	private String lastCommand = "";
	private SuperHashMap<String, String> hmClientJarToVersion = new SuperHashMap<>();
	
	private PrivateKey privateKey = null;
	
	public EntropyServer() 
	{
		try
		{
			setTitle("Entropy Server");
			getContentPane().setLayout(new BorderLayout(0, 0));
			getContentPane().add(panel, BorderLayout.NORTH);
			panel.add(commandLine);
			commandLine.setColumns(13);
			panel.add(btnKill);
			panel.add(btnThreads);
			panel.add(lblFunctionsReceived);
			lblFunctionsReceived.setHorizontalAlignment(SwingConstants.CENTER);
			lblFunctionsReceived.setPreferredSize(new Dimension(40, 20));
			panel.add(lblFunctionsHandled);
			lblFunctionsHandled.setHorizontalAlignment(SwingConstants.CENTER);
			lblFunctionsHandled.setPreferredSize(new Dimension(40, 20));
			getContentPane().add(panel_1, BorderLayout.CENTER);
			
			panel_1.add(btnMemory);
			panel_1.add(btnConsole);
			panel_1.add(btnSendLogs);
			tglbtnScrollLock.setPreferredSize(new Dimension(26, 26));
			tglbtnScrollLock.setIcon(new ImageIcon(EntropyServer.class.getResource("/buttons/key.png")));
			tglbtnScrollLock.setSelectedIcon(new ImageIcon(EntropyServer.class.getResource("/buttons/keySelected.png")));
			panel_1.add(tglbtnScrollLock);
			lblNotificationsAttempted.setPreferredSize(new Dimension(40, 20));
			lblNotificationsAttempted.setHorizontalAlignment(SwingConstants.CENTER);
			
			panel_1.add(lblNotificationsAttempted);
			lblNotificationsSent.setPreferredSize(new Dimension(40, 20));
			lblNotificationsSent.setHorizontalAlignment(SwingConstants.CENTER);
			
			panel_1.add(lblNotificationsSent);
			
			tglbtnScrollLock.addActionListener(this);
			btnThreads.addActionListener(this);
			btnKill.addActionListener(this);
			commandLine.addActionListener(this);
			btnConsole.addActionListener(this);
			btnSendLogs.addActionListener(this);
			btnMemory.addActionListener(this);
			
			commandLine.addKeyListener(this);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private final JButton btnKill = new JButton("Kill");
	private final JButton btnThreads = new JButton("Threads");
	private final JTextField commandLine = new JTextField();
	private final JLabel lblFunctionsHandled = new JLabel("");
	private final JLabel lblFunctionsReceived = new JLabel("");
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private final JButton btnConsole = new JButton("Console");
	private final JButton btnSendLogs = new JButton("Send Logs");
	private final JToggleButton tglbtnScrollLock = new JToggleButton("");
	private final JButton btnMemory = new JButton("Memory");
	private final JLabel lblNotificationsAttempted = new JLabel("");
	private final JLabel lblNotificationsSent = new JLabel("");
	
	public static void main(String args[])
	{
		EntropyServer server = new EntropyServer();
		Thread.setDefaultUncaughtExceptionHandler(new LoggerUncaughtExceptionHandler());
		
		//Initialise interfaces etc
		EncryptionUtil.setBase64Interface(new Base64Desktop());
		Debug.setDebugExtension(new ServerDebugExtension());
		Debug.initialise(console);
		
		//Set other variables on Debug
		Debug.setLogToSystemOut(devMode);
		
		int length = args.length;
		for (int i=0; i<length; i++)
		{
			String arg = args[i];
			applyArgument(arg, server);
		}
		
		server.setSize(420, 110);
		server.setResizable(false);
		server.setVisible(true);
		server.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		server.onStart();
	}
	
	private static void applyArgument(String arg, EntropyServer server)
	{
		if (arg.equals("devMode"))
		{
			Debug.appendBanner("Running in DEV mode");
			server.setTitle("Entropy Server (DEV)");
			devMode = true;
		}
	}
	
	private void onStart()
	{
		try
		{
			Debug.appendBanner("Start-Up");
			
			totalNotificationsSent = new AtomicInteger(StatisticsUtil.getTotalNotificationsSent());
			totalFunctionsHandled = new AtomicInteger(StatisticsUtil.getTotalFunctionsHandled());
			mostFunctionsHandled = new AtomicInteger(StatisticsUtil.getMostFunctionsHandled());
			mostFunctionsReceived = new AtomicInteger(StatisticsUtil.getMostFunctionsReceived());
			
			readInPrivateKey();
			readUsedKeysFromFile();
			readClientVersion();
			registerDefaultRooms();
			
			Debug.append("Starting permanent threads");
			
			startInactiveCheckRunnable();
			startBlacklistRunnable();
			startListenerThreads();
			startFunctionThread();
			
			toggleOnline();
			
			Debug.appendBanner("Server is ready - accepting connections");
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t, "Caught a thrown exception in onStart()");
		}
	}
	
	private void onStop()
	{
		StatisticsUtil.saveTotalNotificationsSent(totalNotificationsSent);
		StatisticsUtil.saveTotalFunctionsHandled(totalFunctionsHandled);
		StatisticsUtil.saveMostFunctionsHandled(mostFunctionsHandled);
		StatisticsUtil.saveMostFunctionsReceived(mostFunctionsReceived);
		
		writeUsedKeysToFile();
	}
	
	private void readInPrivateKey()
	{
		try (InputStream in = getClass().getResourceAsStream("/private.key");
		  ObjectInputStream	oin = new ObjectInputStream(new BufferedInputStream(in)))
		{
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			privateKey = fact.generatePrivate(keySpec);
		} 
		catch (Throwable e) 
		{
			Debug.stackTrace(e, "COULD NOT READ PRIVATE KEY - SERVER WILL NOT FUNCTION CORRECTLY");
		} 
	}
	
	private void registerDefaultRooms()
	{
		Debug.append("Creating rooms...");
		
		//Entropy
		//2 player
		registerNewRoom("Potassium 1", 1, 2, 0, 2);
		registerNewRoom("Zinc 1", 1, 2, 2, 2);
		registerNewRoom("Helium 1", 1, 2, 4, 3);
		registerNewRoom("Magnesium 1", 1, 2, 2, 2, true, true, true, false, false);
		registerNewRoom("Cobalt 1", 1, 2, 0, 2, true, true, false, false, false);
		registerNewRoom("Chlorine 1", 1, 2, 0, 2, true, true, false, true, false);
		registerNewRoom("Gold 1", 1, 2, 0, 2, false, false, true, false, false);
		registerNewRoom("Lithium 1", 1, 2, 2, 2, false, false, false, true, true);
		registerNewRoom("Beryllium 1", 1, 2, 0, 2, true, true, false, false, true);
		
		//3 player
		registerNewRoom("Bromine 1", 1, 3, 0, 2);
		registerNewRoom("Argon 1", 1, 3, 2, 2);
		registerNewRoom("Hydrogen 1", 1, 3, 4, 3);
		registerNewRoom("Zirconium 1", 1, 3, 2, 2, true, true, true, false, false);
		registerNewRoom("Calcium 1", 1, 3, 0, 2, true, true, false, false, false);
		registerNewRoom("Iron 1", 1, 3, 2, 2, true, false, false, true, false);
		registerNewRoom("Palladium 1", 1, 3, 3, 3, true, true, true, false, true);
		
		//4 player
		registerNewRoom("Nickel 1", 1, 4, 0, 2);
		registerNewRoom("Sodium 1", 1, 4, 2, 2);
		registerNewRoom("Phosphorus 1", 1, 4, 4, 3);
		registerNewRoom("Titanium 1", 1, 4, 0, 2, true, true, false, true, false);
		registerNewRoom("Gallium 1", 1, 4, 2, 2, true, true, false, false, true);
		
		//Vectropy
		//2 player
		registerNewRoom("Oxygen 1", 2, 2, 0, 2);
		registerNewRoom("Neon 1", 2, 2, 2, 2);
		registerNewRoom("Copper 1", 2, 2, 4, 3);
		registerNewRoom("Manganese 1", 2, 2, 2, 2, true, true, true, false, false);
		registerNewRoom("Selenium 1", 2, 2, 0, 2, true, true, false, false, false);
		registerNewRoom("Chromium 1", 2, 2, 0, 2, true, true, false, true, false);
		registerNewRoom("Silver 1", 2, 2, 0, 2, false, false, true, false, false);
		registerNewRoom("Antimony 1", 2, 2, 2, 2, false, false, false, true, true);
		registerNewRoom("Tungsten 1", 2, 2, 0, 2, true, true, false, false, true);
		
		//3 player
		registerNewRoom("Carbon 1", 2, 3, 0, 2);
		registerNewRoom("Silicon 1", 2, 3, 2, 2);
		registerNewRoom("Nitrogen 1", 2, 3, 4, 3);
		registerNewRoom("Sulphur 1", 2, 3, 2, 2, true, true, true, false, false);
		registerNewRoom("Fluorine 1", 2, 3, 0, 2, true, true, false, false, false);
		registerNewRoom("Tin 1", 2, 3, 2, 2, true, false, false, true, false);
		registerNewRoom("Indium 1", 2, 3, 3, 3, true, true, true, false, true);
		
		//4 player
		registerNewRoom("Iodine 1", 2, 4, 0, 2);
		registerNewRoom("Lead 1", 2, 4, 2, 2);
		registerNewRoom("Uranium 1", 2, 4, 4, 3);
		registerNewRoom("Vanadium 1", 2, 4, 0, 2, true, true, false, true, false);
		registerNewRoom("Xenon 1", 2, 4, 2, 2, true, true, false, false, true);
		
		Debug.append("Finished creating " + hmRoomByName.size() + " rooms");
	}
	
	private void startInactiveCheckRunnable()
	{
		InactiveCheckRunnable runnable = new InactiveCheckRunnable(this);
		
		ServerThread inactiveCheckThread = new ServerThread(runnable, "InactiveCheck");
		inactiveCheckThread.start();
	}
	
	private void startBlacklistRunnable()
	{
		BlacklistRunnable runnable = new BlacklistRunnable(this);
		
		ServerThread inactiveCheckThread = new ServerThread(runnable, "BlacklistCheck");
		inactiveCheckThread.start();
	}
	
	private void startListenerThreads()
	{
		int lowerBound = SERVER_PORT_NUMBER_LOWER_BOUND;
		int upperBound = SERVER_PORT_NUMBER_UPPER_BOUND;
		
		for (int i=lowerBound; i<upperBound; i++)
		{
			try
			{
				ServerThread listenerThread = new ServerThread(new MessageListener(this, i));
				listenerThread.setName("Listener-" + i);
				listenerThread.start();
			}
			catch (Throwable t)
			{
				Debug.stackTrace(t, "Unable to start listener thread on port " + i);
			}
		}
		
		startDownloadListener(FILE_NAME_ENTROPY_JAR, SERVER_PORT_NUMBER_DOWNLOAD);
	}
	private void startDownloadListener(String filename, int port)
	{
		DownloadListener listener = new DownloadListener(this, port, filename);
		ServerThread downloadListener = new ServerThread(listener, "DownloadListener-" + port);
		downloadListener.start();
	}
	
	private void startFunctionThread()
	{
		ServerRunnable functionRunnable = new ServerRunnable()
		{
			private String statusText = "";
			
			@Override
			public void run()
			{
				while (true)
				{
					statusText = "Updating labels";
					
					lblFunctionsReceived.setText("" + functionsReceived);
					lblFunctionsHandled.setText("" + functionsHandled);
					
					lblNotificationsAttempted.setText("" + notificationsAttempted);
					lblNotificationsSent.setText("" + notificationsSent);

					clearFunctionStats();

					try 
					{
						statusText = "Sleeping between updates";
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) 
					{
						Debug.stackTrace(e);
					}
				}
			}

			@Override
			public String getDetails()
			{
				return statusText;
			}

			@Override
			public UserConnection getUserConnection()
			{
				return null;
			}
		};
		
		ServerThread functionThread = new ServerThread(functionRunnable, "CounterThread");
		functionThread.start();
	}
	
	public int getBlacklistDurationMinutes()
	{
		return blacklistMinutes;
	}
	
	public void executeInWorkerPool(ServerRunnable runnable)
	{
		tpe.executeServerRunnable(runnable);
	}
	
	/**
	 * Blacklist
	 */
	public void incrementMessageCountForIp(String ip)
	{
		incrementStatForMessage(ip, hmMessagesReceivedByIp);
		
		AtomicInteger integer = hmMessagesReceivedByIp.get(ip);
		if (integer != null
		  && integer.intValue() > messagesPerSecondThreshold)
		{
			addToBlacklist(ip, ">" + messagesPerSecondThreshold + " msg/s");
		}
		
	}
	private void clearIpMessageCounts()
	{
		hmMessagesReceivedByIp = new ConcurrentHashMap<>();
	}
	
	public void incrementFunctionsReceived()
	{
		functionsReceived.addAndGet(1);
	}
	public void incrementFunctionsHandled()
	{
		functionsHandled.addAndGet(1);
		totalFunctionsHandled.addAndGet(1);
	}
	public void incrementFunctionsReceivedAndHandledForMessage(String message)
	{
		incrementFunctionsReceivedForMessage(message);
		incrementFunctionsHandledForMessage(message);
	}
	public void incrementFunctionsReceivedForMessage(String message)
	{
		incrementStatForMessage(message, hmFunctionsReceivedByMessageType);
	}
	public void incrementFunctionsHandledForMessage(String message)
	{
		incrementStatForMessage(message, hmFunctionsHandledByMessageType);
	}
	public void incrementNotificationsSentForMessage(String message)
	{
		incrementStatForMessage(message, hmNotificationsSentByNotificationType);
		notificationsSent.addAndGet(1);
		totalNotificationsSent.addAndGet(1);
	}
	public void incrementNotificationsAttemptedForMessage(String message)
	{
		incrementStatForMessage(message, hmNotificationsAttemptedByNotificationType);
		notificationsAttempted.addAndGet(1);
	}
	private void incrementStatForMessage(String message, ConcurrentHashMap<String, AtomicInteger> statsHm)
	{
		AtomicInteger functions = statsHm.get(message);
		if (functions == null)
		{
			functions = new AtomicInteger(0);
		}
		
		functions.incrementAndGet();
		statsHm.put(message, functions);
	}
	
	private void clearFunctionStats()
	{
		int functionsReceivedInt = functionsReceived.intValue();
		int functionsHandledInt = functionsHandled.intValue();
		int notificationsAttemptedInt = notificationsAttempted.intValue();
		int notificationsSentInt = notificationsSent.intValue();
		
		if (functionsReceivedInt > mostFunctionsReceived.intValue())
		{
			mostFunctionsReceived.set(functionsReceivedInt);
		}
		
		if (functionsHandledInt > mostFunctionsHandled.intValue())
		{
			mostFunctionsHandled.set(functionsHandledInt);
		}
		
		int newFunctionsReceived = functionsReceivedInt - functionsHandledInt;
		functionsReceived.set(newFunctionsReceived);
		functionsHandled.set(0);
		
		int newNotificationsAttempted = notificationsAttemptedInt - notificationsSentInt;
		notificationsAttempted.set(newNotificationsAttempted);
		notificationsSent.set(0);
		
		clearIpMessageCounts();
	}
	
	public boolean isAlreadyOnline(String username)
	{
		if (username.equalsIgnoreCase("Admin"))
		{
			return true;
		}
		
		return getUserConnectionForUsername(username) != null;
	}
	
	public ArrayList<UserConnection> getUserConnectionsForUsernames(HashSet<String> usernames)
	{
		ArrayList<UserConnection> uscs = new ArrayList<>();
		
		Iterator<String> it = usernames.iterator();
		for (; it.hasNext(); )
		{
			String username = it.next();
			UserConnection usc = getUserConnectionForUsername(username);
			if (usc != null)
			{
				uscs.add(usc);
			}
		}
		
		return uscs;
	}
	
	public UserConnection getUserConnectionForUsername(String username)
	{
		Iterator<String> it = hmUserConnectionByIpAndPort.keySet().iterator();
		for (; it.hasNext();)
		{
			String ip = it.next();
			UserConnection usc = hmUserConnectionByIpAndPort.get(ip);
			String usernameOnUsc = usc.getUsername();
			if (usernameOnUsc != null
			  && usernameOnUsc.equals(username))
			{
				return usc;
			}
		}
		
		return null;
	}
	
	public void updateMostConcurrentUsers()
	{
		StatisticsUtil.updateMostConcurrentUsers(getCurrentUserCount());
	}
	
	private int getCurrentUserCount()
	{
		int count = 0;
		
		Iterator<String> it = hmUserConnectionByIpAndPort.keySet().iterator();
		for (; it.hasNext();)
		{
			String ip = it.next();
			UserConnection usc = hmUserConnectionByIpAndPort.get(ip);
			String usernameOnUsc = usc.getUsername();
			if (usernameOnUsc != null)
			{
				count++;
			}
		}
		
		return count;
	}
	
	public void removeFromUsersOnline(UserConnection usc)
	{
		//Null these out so we don't try to send any more notifications
		usc.destroyNotificationSockets();
		
		//Cache this key as used so it can't be re-used by someone else in a replay attack
		SecretKey symmetricKey = usc.getSymmetricKey();
		cacheKeyAsUsed(symmetricKey);
		
		//Need to remove them from rooms too
		String username = usc.getUsername();
		if (username != null)
		{
			ColourGenerator.freeUpColour(usc.getColour());
			
			List<Room> rooms = getRooms();
			for (int i=0; i<rooms.size(); i++)
			{
				Room room = rooms.get(i);
				room.removeFromObservers(username);
				room.removePlayer(username, false);
			}
			
			Debug.append(username + " has disconnected");
		}
		
		//Now remove the user connection.
		hmUserConnectionByIpAndPort.removeAllWithValue(usc);
		if (hmUserConnectionByIpAndPort.isEmpty()
		  && username != null)
		{
			resetLobby();
			return;
		}
		
		lobbyChanged();
	}
	
	private void resetLobby()
	{
		int countRemoved = 0;
		
		List<Room> rooms = getRooms();
		int size = rooms.size();
		for (int i=0; i<size; i++)
		{
			Room room = rooms.get(i);
			if (room.getIsCopy())
			{
				String roomName = room.getRoomName();
				hmRoomByName.remove(roomName);
				countRemoved++;
			}
		}
		
		//Log out if we've actually removed some rooms
		if (countRemoved > 0)
		{
			Debug.append("Removed " + countRemoved + " excess rooms");
		}
		
		Debug.append("Cleared lobby messages");
		lobbyMessages.clear();
	}
	
	public void addToChatHistory(String id, String message, String colour, String username)
	{
		OnlineMessage messageObj = new OnlineMessage(colour, message, username);
		addToChatHistory(id, messageObj);
	}
	public void addAdminMessage(String message)
	{
		OnlineMessage messageObj = new OnlineMessage("black", message, "Admin");
		Iterator<String> it = hmRoomByName.keySet().iterator();
		for (; it.hasNext();)
		{
			String name = it.next();
			addToChatHistory(name, messageObj);
		}
		
		addToChatHistory(LOBBY_ID, messageObj);
	}
	private void addToChatHistory(String name, OnlineMessage message)
	{
		if (name.equals(LOBBY_ID))
		{
			lobbyMessages.add(message);
			ArrayList<UserConnection> usersToNotify = getUserConnections(true);
			
			Document chatMessage = XmlBuilderServer.getChatNotification(name, message);
			sendViaNotificationSocket(usersToNotify, chatMessage, XmlConstants.SOCKET_NAME_CHAT);
		}
		else
		{
			Room room = hmRoomByName.get(name);
			room.addToChatHistoryAndNotifyUsers(message);
		}
	}
	
	public void lobbyChanged()
	{
		lobbyChanged(null);
	}
	public void lobbyChanged(UserConnection userToExclude)
	{
		ArrayList<UserConnection> usersToNotify = getUserConnections(true);
		if (userToExclude != null)
		{
			usersToNotify.remove(userToExclude);
		}
		
		Document lobbyMessage = XmlUtil.factoryNewDocument();
		XmlBuilderServer.appendLobbyResponse(lobbyMessage, this);
		
		sendViaNotificationSocket(usersToNotify, lobbyMessage, XmlConstants.SOCKET_NAME_LOBBY);
	}
	
	private void sendViaNotificationSocket(ArrayList<UserConnection> uscs, Document message, String socketName)
	{
		sendViaNotificationSocket(uscs, message, socketName, false);
	}
	public void sendViaNotificationSocket(ArrayList<UserConnection> uscs, Document message, String socketName, boolean blocking)
	{
		AtomicInteger counter = null;
		if (blocking)
		{
			int size = uscs.size();
			counter = new AtomicInteger(size);
		}
		
		sendViaNotificationSocket(uscs, message, socketName, counter);
	}
	
	private void sendViaNotificationSocket(ArrayList<UserConnection> uscs, Document message, String socketName, AtomicInteger counter)
	{
		int size = uscs.size();
		for (int i=0; i<size; i++)
		{
			UserConnection usc = uscs.get(i);
			usc.sendNotificationInWorkerPool(message, this, socketName, counter);
		}
		
		if (counter != null)
		{
			while (counter.get() > 0)
			{
				//So you wait. You waiiiit. Yoouuu waiiiiiiit.
				try {Thread.sleep(500);} catch (Throwable t) {}
			}
		}
	}
	
	public List<OnlineMessage> getChatHistory(String id)
	{
		if (id.equals(LOBBY_ID))
		{
			return lobbyMessages;
		}
		
		Room room = hmRoomByName.get(id);
		return room.getChatHistory();
	}
	
	public PrivateKey getPrivateKey()
	{
		return privateKey;
	}
	
	private void registerNewRoom(String roomName, int mode, int players, int jokerQuantity, int jokerValue)
	{
		registerNewRoom(roomName, mode, players, jokerQuantity, jokerValue, false, false, false, false, false);
	}
	private Room registerNewRoom(String roomName, int mode, int players, int jokerQuantity, int jokerValue,
								boolean includeMoons, boolean includeStars, boolean illegalAllowed, boolean negativeJacks,
								boolean cardReveal)
	{
		Iterator<String> it = hmRoomByName.keySet().iterator();
		for (; it.hasNext(); )
		{
			String id = it.next();
			Room room = hmRoomByName.get(id);
			
			String nameToCheck = room.getRoomName();
			if (nameToCheck.equals(roomName))
			{
				Debug.append("Not creating room " + nameToCheck + " as a room with that name already exists.");
				return null;
			}
		}
		
		Room room = new Room(roomName, mode, players, this);
		room.setJokerQuantity(jokerQuantity);
		room.setJokerValue(jokerValue);
		room.setIncludeMoons(includeMoons);
		room.setIncludeStars(includeStars);
		room.setIllegalAllowed(illegalAllowed);
		room.setNegativeJacks(negativeJacks);
		room.setCardReveal(cardReveal);
		room.initialiseGame();
		hmRoomByName.put(roomName, room);
		
		Debug.append("Room created: " + roomName);
		return room;
	}
	public void registerCopy(Room room)
	{
		String roomName = room.getRoomName();
		int mode = room.getMode();
		int players = room.getPlayers();
		int jokerQuantity = room.getJokerQuantity();
		int jokerValue = room.getJokerValue();
		boolean includeMoons = room.getIncludeMoons();
		boolean includeStars = room.getIncludeStars();
		boolean illegalAllowed = room.getIllegalAllowed();
		boolean negativeJacks = room.getNegativeJacks();
		boolean cardReveal = room.getCardReveal();
		
		int index = roomName.indexOf(' ') + 1;
		String roomNumberStr = roomName.substring(index);
		int roomNumber = Integer.parseInt(roomNumberStr);
		
		String newRoomName = roomName.substring(0, index) + (roomNumber+1);
		
		Room newRoom = registerNewRoom(newRoomName, mode, players, jokerQuantity, jokerValue, 
						includeMoons, includeStars, illegalAllowed, negativeJacks, cardReveal);
		
		if (newRoom != null)
		{
			newRoom.setIsCopy(true);
			lobbyChanged();
		}
	}
	public void removeRoom(String roomName)
	{
		hmRoomByName.remove(roomName);
		lobbyChanged();
	}
	
	public ArrayList<Room> getRooms()
	{
		ArrayList<Room> list = new ArrayList<>();
		Iterator<String> it = hmRoomByName.keySet().iterator();
		
		for (; it.hasNext(); )
		{
			String id = it.next();
			Room room = hmRoomByName.get(id);
			list.add(room);
		}
		
		return list;
	}
	
	public Room getRoomForName(String name)
	{
		return hmRoomByName.get(name);
	}
	public String getClientVersion(String jar)
	{
		return hmClientJarToVersion.get(jar);
	}
	public void toggleOnline()
	{
		online = !online;
	}
	public boolean isOnline()
	{
		return online;
	}
	
	/**
	 * Generate a secure seed based on:
	 *  - Current time in nanos
	 *  - A private seed known to the Server which is incremented on each hit
	 *  - The total number of notifications that have been sent (generate a random long from this)
	 */
	public long generateSeed()
	{
		currentSeedLong++;
		
		Random rand = new Random(totalNotificationsSent.get());
		return System.nanoTime() + currentSeedLong + rand.nextLong();
	}
	
	public ArrayList<UserConnection> getUserConnections(boolean onlyLoggedIn)
	{
		ArrayList<UserConnection> uscs = new ArrayList<>();
		
		Iterator<String> it = hmUserConnectionByIpAndPort.keySet().iterator();
		for (; it.hasNext(); )
		{
			String ip = it.next();
			UserConnection usc = hmUserConnectionByIpAndPort.get(ip);
			if (onlyLoggedIn
			  && usc.getUsername() == null)
			{
				continue;
			}
			
			uscs.add(usc);
		}
		
		return uscs;
	}
	public UserConnection getUserConnectionForIpAndPort(String ipAndPort)
	{
		return hmUserConnectionByIpAndPort.get(ipAndPort);
	}
	public void setUserConnectionForIpAndPort(String ipAndPort, UserConnection usc)
	{
		hmUserConnectionByIpAndPort.put(ipAndPort, usc);
	}
	public ArrayList<String> getAllPortsCurrentlyUsedByIp(String ipAddress)
	{
		Iterator<String> it = hmUserConnectionByIpAndPort.keySet().iterator();
		ArrayList<String> list = new ArrayList<>();
		
		for (; it.hasNext();)
		{
			String ipAndPort = it.next();
			int index = ipAndPort.indexOf("_");
			String ipToCheck = ipAndPort.substring(0, index);
			if (ipToCheck.equals(ipAddress))
			{
				String port = ipAndPort.substring(index+1);
				list.add(port);
			}
		}
		
		return list;
	}
	
	public boolean getDevMode()
	{
		return devMode;
	}
	
	public boolean getNotificationSocketLogging()
	{
		return notificationSocketLogging;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		try
		{
			Component source = (Component)arg0.getSource();
			if (source == btnKill)
			{
				onStop();
				
				System.exit(0);
			}
			else if (source == btnThreads)
			{
				processCommand(COMMAND_DUMP_THREADS);
			}
			else if (source == btnMemory)
			{
				processCommand("memory");
			}
			else if (source == commandLine)
			{
				String command = commandLine.getText();
				commandLine.setText("");
				processCommand(command);
			}
			else if (source == btnConsole)
			{
				if (!console.isVisible())
				{
					console.setTitle("Console");
					console.setSize(1000, 600);
					console.setLocationRelativeTo(null);
					console.setVisible(true);
				}
				else
				{
					console.toFront();
				}
			}
			else if (source == tglbtnScrollLock)
			{
				boolean scrollLock = tglbtnScrollLock.isSelected();
				console.setScrollLock(scrollLock);
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	public void cacheKeyAsUsed(SecretKey symmetricKey) 
	{
		usedSymmetricKeys.add(symmetricKey);
	}
	public boolean keyHasAlreadyBeenUsed(SecretKey symmetricKey)
	{
		return usedSymmetricKeys.contains(symmetricKey);
	}
	private void writeUsedKeysToFile()
	{
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH_USED_KEYS, charset)) 
		{
		    for (SecretKey key : usedSymmetricKeys) 
		    {
		    	String word = EncryptionUtil.convertSecretKeyToString(key);
		        writer.write(word);
		        writer.newLine();
		    }
		    
		    //Write out current ones as well
		    Iterator<String> it = hmUserConnectionByIpAndPort.keySet().iterator();
		    for (; it.hasNext(); )
		    {
		    	String ip = it.next();
		    	UserConnection usc = hmUserConnectionByIpAndPort.get(ip);
		    	
		    	SecretKey symmetricKey = usc.getSymmetricKey();
		    	if (symmetricKey != null)
		    	{
			    	String word = EncryptionUtil.convertSecretKeyToString(symmetricKey);
			    	writer.write(word);
			    	writer.newLine();
		    	}
		    }
		}
		catch (IOException x) 
		{
			Debug.stackTrace(x);
		}
	}
	private void readUsedKeysFromFile()
	{
		try
		{
			Charset charset = Charset.forName("US-ASCII");
			List<String> usedKeys = Files.readAllLines(FILE_PATH_USED_KEYS, charset);
			
			int keys = 0;
			for (String key : usedKeys)
			{
				SecretKey secretKey = EncryptionUtil.reconstructKeyFromString(key);
				if (secretKey != null)
				{
					keys++;
					usedSymmetricKeys.add(secretKey);
				}
			}
			
			Debug.append("Read in " + keys + " used keys");
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to read in used keys");
		}
	}
	
	private void readClientVersion()
	{
		try
		{
			Charset charset = Charset.forName("US-ASCII");
			List<String> jarsAndVersions = Files.readAllLines(FILE_PATH_CLIENT_VERSION, charset);
			
			for (int i=0; i<jarsAndVersions.size(); i++)
			{
				String jarAndVersionStr = jarsAndVersions.get(i);
				ArrayList<String> jarAndVersion = StringUtil.getListFromDelims(jarAndVersionStr, ";");
				
				String jar = jarAndVersion.get(0);
				String version = jarAndVersion.get(1);
				
				hmClientJarToVersion.put(jar, version);
			}
			
			logClientVersions();
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to read in client version");
		}
	}
	private void logClientVersions()
	{
		Iterator<Map.Entry<String, String>> it = hmClientJarToVersion.entrySet().iterator();
		for (; it.hasNext(); )
		{
			Map.Entry<String, String> jarAndVersion = it.next();
			
			String jar = jarAndVersion.getKey();
			String version = jarAndVersion.getValue();
			
			Debug.append(FileUtil.stripFileExtension(jar) + " Version: " + version);
		}
	}
	
	public ConcurrentHashMap<String, BlacklistEntry> getBlacklist()
	{
		return blacklist;
	}
	
	public void addToBlacklist(String ipAndPort, String reason)
	{
		ArrayList<String> tokens = StringUtil.getListFromDelims(ipAndPort, "_");
		String ip = tokens.get(0);
		
		if (usingBlacklist)
		{
			BlacklistEntry entry = new BlacklistEntry(reason);
			blacklist.put(ip, entry);
				
			Debug.append("Blacklisted " + ip + ". Reason: " + reason);
		}
		else
		{
			Debug.append("Not blacklisting " + ip + " because this is turned off. Reason: " + reason);
		}
	}
	
	public boolean isBlacklisted(String ipAddress)
	{
		if (!usingBlacklist)
		{
			return false;
		}
		
		BlacklistEntry entry = blacklist.get(ipAddress);
		return entry != null;
	}
	
	public boolean messageIsTraced(String nodeName, String username)
	{
		if (traceAll)
		{
			return true;
		}
		
		if (tracedMessages.contains(nodeName))
		{
			return true;
		}
		
		if (tracedUsers.contains(username))
		{
			return true;
		}
		
		return false;
	}
	
	private void toggleServerOnline()
	{
		ToggleAvailabilityRunnable runnable = new ToggleAvailabilityRunnable(this);
		executeInWorkerPool(runnable);
	}
	
	private void processCommand(String command)
	{
		Debug.append("[Command entered: " + command + "]");
		if (command.equals("help"))
		{
			printCommands();
		}
		else if (command.equals(COMMAND_DUMP_THREADS))
		{
			dumpServerThreads();
		}
		else if (command.equals(COMMAND_DUMP_THREAD_STACKS))
		{
			dumpThreadStacks();
		}
		else if (command.equals(COMMAND_DUMP_USERS))
		{
			Debug.append(getCurrentUserCount() + " user(s) online:");
			Iterator<String> it = hmUserConnectionByIpAndPort.keySet().iterator();
			for (; it.hasNext();)
			{
				String ip = it.next();
				UserConnection usc = hmUserConnectionByIpAndPort.get(ip);
				Debug.appendWithoutDate("" + usc);
			}
		}
		else if (command.equals(COMMAND_TRACE_ALL))
		{
			traceAll = !traceAll;
			Debug.append("Tracing all messages: " + traceAll);
		}
		else if (command.startsWith(COMMAND_TRACE_USER))
		{
			String username = command.substring(COMMAND_TRACE_USER.length());
			if (tracedUsers.contains(username))
			{
				Debug.append("Stopped tracing user " + username);
				tracedUsers.remove(username);
			}
			else
			{
				Debug.append("Tracing user " + username);
				tracedUsers.add(username);
			}
		}
		else if (command.startsWith(COMMAND_TRACE_MESSAGE_AND_RESPONSE))
		{
			String messageStr = command.substring(COMMAND_TRACE_MESSAGE_AND_RESPONSE.length());
			
			if (tracedMessages.contains(messageStr))
			{
				Debug.append("Stopped tracing " + messageStr + " and its responses");
				tracedMessages.remove(messageStr);
			}
			else
			{
				Debug.append("Tracing message " + messageStr + " and its responses");
				tracedMessages.add(messageStr);
			}
		}
		else if (command.equals(COMMAND_SHUT_DOWN))
		{
			toggleServerOnline();
		}
		else if (command.startsWith(COMMAND_RESET))
		{
			String roomIdStr = command.substring(COMMAND_RESET.length());
			Room room = hmRoomByName.get(roomIdStr);
			if (room == null)
			{
				Debug.append("No room found for ID " + roomIdStr);
				return;
			}
			
			Debug.append("Resetting currentPlayers for room " + roomIdStr);
			room.resetCurrentPlayers();
		}
		else if (command.equals(COMMAND_CLEAR_ROOMS))
		{
			if (!hmUserConnectionByIpAndPort.isEmpty())
			{
				Debug.append("Not clearing down rooms as there are active user connections.");
				return;
			}
			
			resetLobby();
		}
		else if (command.equals(COMMAND_DUMP_STATS))
		{
			Debug.appendBanner("STATS DUMP");
			Debug.appendWithoutDate("Most messages received per second: " + mostFunctionsReceived.get());
			Debug.appendWithoutDate("Most messages handled per second: " + mostFunctionsHandled.get());
			Debug.appendWithoutDate("Total Messages Handled: " + totalFunctionsHandled.get());
			Debug.appendWithoutDate("Total Notifications Sent: " + totalNotificationsSent.get());
			Debug.appendWithoutDate("Most concurrent users: " + StatisticsUtil.getMostConcurrentUsers());
			StatisticsUtil.doGlobalStatsDump();
			Debug.appendWithoutDate("");
		}
		else if (command.equals(COMMAND_MESSAGE_STATS))
		{
			dumpMessageStats();
		}
		else if (command.equals(COMMAND_DECRYPTION_LOGGING))
		{
			EncryptionUtil.failedDecryptionLogging = !EncryptionUtil.failedDecryptionLogging;
			Debug.appendWithoutDate("Decryption logging: " + EncryptionUtil.failedDecryptionLogging);
		}
		else if (command.equals(COMMAND_CLEAR_STATS))
		{
			StatisticsUtil.clearServerData();
			
			totalFunctionsHandled.set(0);
			mostFunctionsHandled.set(0);
			mostFunctionsReceived.set(0);
		}
		else if (command.startsWith(COMMAND_SET_CORE_POOL_SIZE))
		{
			int newCoreSize = parseArgumentAsInt(command, COMMAND_SET_CORE_POOL_SIZE);
			if (newCoreSize > -1)
			{
				tpe.setCorePoolSize(newCoreSize);
				Debug.append("Core pool size is now " + newCoreSize);
			}
		}
		else if (command.startsWith(COMMAND_SET_MAX_POOL_SIZE))
		{
			int newMaxSize = parseArgumentAsInt(command, COMMAND_SET_MAX_POOL_SIZE);
			if (newMaxSize > -1)
			{
				tpe.setMaximumPoolSize(newMaxSize);
				Debug.append("Max pool size is now " + newMaxSize);
			}
		}
		else if (command.startsWith(COMMAND_SET_KEEP_ALIVE_TIME))
		{
			String newKeepAliveTime = command.substring(COMMAND_SET_KEEP_ALIVE_TIME.length());
			tpe.setKeepAliveTime(Integer.parseInt(newKeepAliveTime), TimeUnit.SECONDS);
			Debug.append("Keep Alive Time is now " + newKeepAliveTime + "s");
		}
		else if (command.equals(COMMAND_POOL_STATS))
		{
			Debug.appendWithoutDate("-----------------------------------------");
			Debug.appendWithoutDate("Max size: " + tpe.getMaximumPoolSize());
			Debug.appendWithoutDate("Core size: " + tpe.getCorePoolSize());
			Debug.appendWithoutDate("Alive time: " + tpe.getKeepAliveTime(TimeUnit.SECONDS));
			Debug.appendWithoutDate("-----------------------------------------");
			Debug.appendWithoutDate("Current queue size: " + blockQueue.size());
			Debug.appendWithoutDate("Remaining queue capacity: " + blockQueue.remainingCapacity());
			Debug.appendWithoutDate("Active threads: " + tpe.getActiveCount());
			Debug.appendWithoutDate("Pool size: " + tpe.getPoolSize());
			Debug.appendWithoutDate("Largest pool size: " + tpe.getLargestPoolSize());
			Debug.appendWithoutDate("Completion status: " + tpe.getCompletedTaskCount() + " / " + tpe.getTaskCount());
			Debug.appendWithoutDate("-----------------------------------------");
		}
		else if (command.startsWith(COMMAND_FAKE_USERS))
		{
			if (!devMode)
			{
				Debug.append("Not running command as not running in DEV mode");
				return;
			}
			
			int users = parseArgumentAsInt(command, COMMAND_FAKE_USERS);
			for (int i=0; i<users; i++)
			{
				UserConnection usc = new UserConnection(null, null);
				usc.update("Fake " + i, false);
				setUserConnectionForIpAndPort("" + i, usc);
			}
		}
		else if (command.equals(COMMAND_DUMP_BLACKLIST))
		{
			dumpBlacklist();
		}
		else if (command.equals(COMMAND_BLACKLIST_FULL))
		{
			usingBlacklist = true;
			blacklistMinutes = -1;
			Debug.appendBanner("Blacklist is FULL");
		}
		else if (command.equals(COMMAND_BLACKLIST_OFF))
		{
			usingBlacklist = false;
			Debug.appendBanner("Blacklist is OFF");
		}
		else if (command.startsWith(COMMAND_BLACKLIST_TIME))
		{
			int minutes = parseArgumentAsInt(command, COMMAND_BLACKLIST_TIME);
			
			if (minutes > -1)
			{
				blacklistMinutes = minutes;
				usingBlacklist = true;
				Debug.appendBanner("Blacklist is ON and set to " + minutes + " minutes");
			}
		}
		else if (command.startsWith(COMMAND_BLACKLIST))
		{
			String ip = command.substring(COMMAND_BLACKLIST.length());
			addToBlacklist(ip, "Manual");
		}
		else if (command.startsWith(COMMAND_SET_BLACKLIST_THRESHOLD))
		{
			int threshold = parseArgumentAsInt(command, COMMAND_SET_BLACKLIST_THRESHOLD);
			if (threshold > -1)
			{
				messagesPerSecondThreshold = threshold;
				Debug.append("Will now blacklist anyone who sends more than " + threshold + " message/s");
			}
		}
		else if (command.equals(COMMAND_USED_KEYS))
		{
			for (SecretKey key : usedSymmetricKeys)
			{
				String keyStr = EncryptionUtil.convertSecretKeyToString(key);
				Debug.appendWithoutDate(keyStr);
			}
		}
		else if (command.equals(COMMAND_DUMP_HASH_MAPS))
		{
			dumpHashMaps();
		}
		else if (command.startsWith(COMMAND_MEMORY))
		{
			boolean forceGc = false;
			
			int index = command.indexOf(" ");
			if (index > -1)
			{
				String gcBool = command.substring((COMMAND_MEMORY + " ").length());
				forceGc = gcBool.equals("true");
			}
			
			dumpMemory(forceGc);
		}
		else if (command.startsWith(COMMAND_NOTIFICATION_LOGGING))
		{
			notificationSocketLogging = !notificationSocketLogging;
			Debug.append("Notification logging: " + notificationSocketLogging);
		}
		else if (command.startsWith(COMMAND_NOTIFY_USER))
		{
			String username = command.substring(COMMAND_NOTIFY_USER.length());
			UserConnection usc = getUserConnectionForUsername(username);
			if (usc == null)
			{
				Debug.append("Failed to find usc for " + username);
				return;
			}
			
			usc.notifySockets();
		}
		else if (command.equals(COMMAND_SERVER_VERSION))
		{
			Debug.append("Server version: " + OnlineConstants.SERVER_VERSION);
			logClientVersions();
		}
		else if (command.equals(COMMAND_SERVER_RESET_CLIENT_VERSION))
		{
			readClientVersion();
		}
		else
		{
			Debug.append("Unrecognised command - type 'help' for a list of available commands");
			return;
		}
		
		lastCommand = command;
	}
	
	private int parseArgumentAsInt(String fullCommand, String prefix)
	{
		int ret = -1;
		String integerStr = fullCommand.substring(prefix.length());
		try
		{
			ret = Integer.parseInt(integerStr);
		}
		catch (NumberFormatException nfe)
		{
			Debug.append("Failed to parse " + integerStr + " as int");
		}
		
		return ret;
	}
	
	private void printCommands()
	{
		Debug.append("The available commands are:");
		Debug.appendWithoutDate(COMMAND_DUMP_THREADS);
		Debug.appendWithoutDate(COMMAND_DUMP_THREAD_STACKS);
		Debug.appendWithoutDate(COMMAND_DUMP_USERS);
		Debug.appendWithoutDate(COMMAND_TRACE_ALL);
		Debug.appendWithoutDate(COMMAND_TRACE_USEFUL);
		Debug.appendWithoutDate(COMMAND_TRACE_USER + "<username>");
		Debug.appendWithoutDate(COMMAND_TRACE_MESSAGE_AND_RESPONSE + "<MessageName>");
		Debug.appendWithoutDate(COMMAND_SHUT_DOWN);
		Debug.appendWithoutDate(COMMAND_RESET + "<RoomId>");
		Debug.appendWithoutDate(COMMAND_CLEAR_ROOMS);
		Debug.appendWithoutDate(COMMAND_DUMP_STATS);
		Debug.appendWithoutDate(COMMAND_MESSAGE_STATS);
		Debug.appendWithoutDate(COMMAND_DECRYPTION_LOGGING);
		Debug.appendWithoutDate(COMMAND_CLEAR_STATS);
		Debug.appendWithoutDate(COMMAND_LAUNCH_DAY);
		Debug.appendWithoutDate(COMMAND_POOL_STATS);
		Debug.appendWithoutDate(COMMAND_SET_CORE_POOL_SIZE + "<core pool size>");
		Debug.appendWithoutDate(COMMAND_SET_MAX_POOL_SIZE + "<max pool size>");
		Debug.appendWithoutDate(COMMAND_SET_KEEP_ALIVE_TIME + "<keep alive time>");
		Debug.appendWithoutDate(COMMAND_DUMP_BLACKLIST);
		Debug.appendWithoutDate(COMMAND_BLACKLIST_TIME + "<minutes to stay blacklisted>");
		Debug.appendWithoutDate(COMMAND_BLACKLIST_FULL);
		Debug.appendWithoutDate(COMMAND_BLACKLIST_OFF);
		Debug.appendWithoutDate(COMMAND_BLACKLIST + "<IP address to blacklist>");
		Debug.appendWithoutDate(COMMAND_SET_BLACKLIST_THRESHOLD + "<message/s>");
		Debug.appendWithoutDate(COMMAND_USED_KEYS);
		Debug.appendWithoutDate(COMMAND_DUMP_HASH_MAPS);
		Debug.appendWithoutDate(COMMAND_MEMORY + "<do gc>");
		Debug.appendWithoutDate(COMMAND_NOTIFICATION_LOGGING);
		Debug.appendWithoutDate(COMMAND_NOTIFY_USER + "<username>");
		Debug.appendWithoutDate(COMMAND_SERVER_VERSION);
		Debug.appendWithoutDate(COMMAND_SERVER_RESET_CLIENT_VERSION);
	}
	
	private void dumpMessageStats()
	{
		dumpMessageStats("Message		Received	Handled", hmFunctionsReceivedByMessageType, hmFunctionsHandledByMessageType);
		dumpMessageStats("Notification		Attempted	Sent", hmNotificationsAttemptedByNotificationType, hmNotificationsSentByNotificationType);
	}
	private void dumpMessageStats(String titles, ConcurrentHashMap<String, AtomicInteger> hmReceived,
	  ConcurrentHashMap<String, AtomicInteger> hmHandled)
	{
		if (hmReceived.isEmpty())
		{
			return;
		}
		
		Debug.appendWithoutDate("*********************************************************");
		Debug.appendWithoutDate(titles);
		Debug.appendWithoutDate("*********************************************************");
		
		int totalReceived = 0;
		int totalHandled = 0;
		
		Iterator<String> it = hmReceived.keySet().iterator();
		for (; it.hasNext();)
		{
			String message = it.next();
			AtomicInteger functionsReceived = hmReceived.get(message);
			AtomicInteger functionsHandled = hmHandled.get(message);
			if (functionsHandled == null)
			{
				functionsHandled = new AtomicInteger(0);
			}
			
			totalReceived += functionsReceived.get();
			totalHandled += functionsHandled.get();
			
			if (message.length() < 10
			  || message.equals("ClientMail"))
			{
				message += "	";
			}
			
			String row = message + "	" + functionsReceived + "	" + functionsHandled;
			Debug.appendWithoutDate(row);
		}
		
		Debug.appendWithoutDate("*********************************************************");
		Debug.appendWithoutDate("Total		" + totalReceived + "	" + totalHandled);
		Debug.appendWithoutDate("*********************************************************");
		
		Debug.appendWithoutDate("");
	}
	
	private void dumpBlacklist()
	{
		if (blacklist.isEmpty())
		{
			return;
		}
		
		Debug.appendWithoutDate("*********************************************************");
		Debug.appendWithoutDate("IP		Reason	Time Remaining");
		Debug.appendWithoutDate("*********************************************************");
		
		Iterator<String> it = blacklist.keySet().iterator();
		for (; it.hasNext(); )
		{
			String ip = it.next();
			BlacklistEntry entry = blacklist.get(ip);
			String reason = entry.getBlacklistReason();
			String timeRemaining = entry.getRenderedTimeRemainingOnBlacklist(blacklistMinutes);
			
			if (ip.length() <= 10)
			{
				ip += "	";
			}
			
			String row = ip + "	" + reason + "	" + timeRemaining;
			Debug.appendWithoutDate(row);
		}
		
		Debug.appendWithoutDate("");
	}
	
	private void dumpHashMaps()
	{
		Debug.appendWithoutDate("hmUserConnectionByIp: " + hmUserConnectionByIpAndPort);
		Debug.appendWithoutDate("hmRoomById: " + hmRoomByName);
		Debug.appendWithoutDate("hmFunctionsReceivedByMessageType: " + hmFunctionsReceivedByMessageType);
		Debug.appendWithoutDate("hmFunctionsHandledByMessageType: " + hmFunctionsHandledByMessageType);
		Debug.appendWithoutDate("hmNotificationsAttemptedByNotificationType: " + hmNotificationsAttemptedByNotificationType);
		Debug.appendWithoutDate("hmNotificationsSentByNotificationType: " + hmNotificationsSentByNotificationType);
		Debug.appendWithoutDate("hmMessagesReceivedByIp: " + hmMessagesReceivedByIp);
		Debug.appendWithoutDate("blacklist: " + blacklist);
	}
	
	private void dumpMemory(boolean forceGc)
	{
		if (forceGc)
		{
			Debug.append("Forcing a GC before dumping memory...");
			System.gc();
		}
		
		Debug.appendWithoutDate("-------------------------------------------------------------------------------------------------");
		Debug.appendWithoutDate("Name		Max	Used	Remaining");
		Debug.appendWithoutDate("-------------------------------------------------------------------------------------------------");
		
	    List<MemoryPoolMXBean> memoryList = ManagementFactory.getMemoryPoolMXBeans();
	    for (MemoryPoolMXBean tmpMem : memoryList)
	    {
	    	String name = tmpMem.getName();
	    	MemoryUsage usage = tmpMem.getUsage();
	    	long max = usage.getMax();
	    	long used = usage.getUsed();
	    	long remaining = max - used;
	    	
	    	String maxStr = (max / (1024 * 1024)) + "Mb";
	    	String usedStr = (used / (1024 * 1024)) + "Mb";
	    	String remainingStr = (remaining / (1024 * 1024)) + "Mb";
	    	
	    	//Metaspace is Java8's version of PermGen. It doesn't have a fixed maximum, it grows to what's needed.
	    	if (name.equals("Metaspace"))
	    	{
	    		maxStr = "N/A";
	    		remainingStr = "N/A";
	    	}
	    	
	    	//Don't need everything to end " Space"...
	    	if (name.endsWith(" Space"))
	    	{
	    		int length = name.length();
	    		name = name.substring(0, length - 6);
	    	}
	    	
	    	//Sort out tabbing so it's a nice looking table
	    	if (name.length() < 10)
			{
	    		name += "	";
			}
	    	
	    	Debug.appendWithoutDate(name + "	" + maxStr + "	" + usedStr + "	" + remainingStr);
	    }
	    
	    Debug.appendWithoutDate("");
	}

	/**
	 * KeyListener
	 */
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(arg0);
		int keyCode = keyStroke.getKeyCode();
		if (keyCode == 38)
		{
			commandLine.setText(lastCommand);
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
}
