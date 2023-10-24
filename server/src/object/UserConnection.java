package object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.SecretKey;

import org.w3c.dom.Document;

import server.EntropyServer;
import server.NotificationRunnable;
import util.ColourGenerator;
import util.Debug;
import util.EncryptionUtil;
import util.XmlConstants;

public class UserConnection 
{
	private String ipAddress = null;
	private SecretKey symmetricKey = null;
	private String username = null;
	private String colour = null;
	private long lastActive = -1;
	private boolean mobile = false;
	private HashMap<String, ArrayList<Document>> hmNotificationQueueBySocketName = new HashMap<>();
	private HashMap<String, NotificationSocket> hmSocketBySocketName = new HashMap<>();
	private HashMap<String, Object> hmWaitObjBySocketName = new HashMap<>();
	
	/**
	 * When a connection is initiated
	 */
	public UserConnection(String ipAddress, SecretKey symmetricKey)
	{
		this.ipAddress = ipAddress;
		this.symmetricKey = symmetricKey;
		
		initialiseSocketHashMaps();
	}
	
	/**
	 * When the logon process is completed
	 */
	public void update(String username, boolean mobile)
	{
		this.username = username;
		this.mobile = mobile;
		
		colour = ColourGenerator.generateNextColour();
		setLastActiveNow();
			
		Debug.append("New user connected: " + username);
	}
	
	public SecretKey getSymmetricKey()
	{
		return symmetricKey;
	}
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getColour()
	{
		return colour;
	}
	public void setColour(String colour)
	{
		this.colour = colour;
	}
	public String getIpAddress()
	{
		return ipAddress;
	}
	
	public long getLastActive()
	{
		return lastActive;
	}
	public void setLastActiveNow()
	{
		this.lastActive = System.currentTimeMillis();
	}
	public boolean getMobile()
	{
		return mobile;
	}
	
	public void destroyNotificationSockets()
	{
		Iterator<String> it = hmSocketBySocketName.keySet().iterator();
		for (; it.hasNext(); )
		{
			String socketType = it.next();
			replaceNotificationSocket(socketType, null);
		}
	}
	
	/**
	 * Synchronize on the wait object so we wait for any current attempt to send a Notification to go into its
	 * wait() block. This way we ensure it gets our notify() call. If we sneak in here before it that's fine too,
	 * as when it tries to send there'll be a fresh new socket.
	 */
	public void replaceNotificationSocket(String socketType, NotificationSocket socket)
	{
		Object notificationWaitObj = hmWaitObjBySocketName.get(socketType);
		
		synchronized (notificationWaitObj)
		{
			NotificationSocket existingSocket = hmSocketBySocketName.get(socketType);
			if (existingSocket != null)
			{
				existingSocket.closeResources();
			}
			
			hmSocketBySocketName.put(socketType, socket);
			notificationWaitObj.notify();
		}
	}
	
	/**
	 * This is here to allow us to notify from the command line in case there is a bug with the synchronization.
	 * If I haven't got the wait/notify stuff right everything can get stuck!
	 */
	public void notifySockets()
	{
		Iterator<Map.Entry<String, Object>> it = hmWaitObjBySocketName.entrySet().iterator();
		for (; it.hasNext();)
		{
			Map.Entry<String, Object> entry = it.next();
			Object waitObj = entry.getValue();
			synchronized (waitObj)
			{
				waitObj.notify();
			}
		}
	}
	
	public NotificationSocket getNotificationSocket(String socketType)
	{
		return hmSocketBySocketName.get(socketType);
	}
	
	@Override
	public String toString() 
	{
		String desc =  username + " @ " + ipAddress;
		
		if (mobile)
		{
			desc += " (mob)";
		}
		
		if (symmetricKey != null)
		{
			desc += ", " + EncryptionUtil.convertSecretKeyToString(symmetricKey);
		}
		
		return desc;
	}
	
	public void sendNotificationInWorkerPool(Document message, EntropyServer server, String socketName, AtomicInteger counter)
	{
		NotificationRunnable runnable = new NotificationRunnable(server, message, this, counter, socketName);
		server.executeInWorkerPool(runnable);
	}
	
	public void addNotificationToQueue(String socketType, Document message)
	{
		ArrayList<Document> notificationQueue = hmNotificationQueueBySocketName.get(socketType);
		notificationQueue.add(message);
	}
	public ArrayList<Document> getNotificationQueue(String socketName)
	{
		return hmNotificationQueueBySocketName.get(socketName);
	}
	public Document getNextNotificationToSend(String socketType)
	{
		ArrayList<Document> notificationQueue = hmNotificationQueueBySocketName.get(socketType);
		int size = notificationQueue.size();
		if (size > 0)
		{
			return notificationQueue.remove(0);
		}
		else
		{
			return null;
		}
	}
	public int getNotificationQueueSize(String socketName)
	{
		ArrayList<Document> notificationQueue = hmNotificationQueueBySocketName.get(socketName);
		return notificationQueue.size();
	}
	
	public void waitForNewNotificationSocket(String socketName)
	{
		Object waitObj = hmWaitObjBySocketName.get(socketName);
		
		try
		{
			waitObj.wait();
		}
		catch (InterruptedException t)
		{
			//Not expecting interruptions
			Debug.stackTrace(t);
		}
	}
	
	private void initialiseSocketHashMaps()
	{
		initialiaseHashMaps(XmlConstants.SOCKET_NAME_CHAT);
		initialiaseHashMaps(XmlConstants.SOCKET_NAME_LOBBY);
		initialiaseHashMaps(XmlConstants.SOCKET_NAME_GAME);
	}
	
	private void initialiaseHashMaps(String socketType)
	{
		hmWaitObjBySocketName.put(socketType, new Object());
		hmNotificationQueueBySocketName.put(socketType, new ArrayList<Document>());
	}
	
	public Object getNotificationWaitObject(String socketType)
	{
		return hmWaitObjBySocketName.get(socketType);
	}
}
