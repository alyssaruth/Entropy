package server;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import object.NotificationSocket;
import object.ServerRunnable;
import object.UserConnection;

import org.w3c.dom.Document;

import util.Debug;
import util.EncryptionUtil;
import util.XmlUtil;

import static utils.InjectedThings.logger;

public class NotificationRunnable implements ServerRunnable
{
	private static final int MAX_RETRIES = 10;
	private static final int SLEEP_TIME_MILLIS = 500;
	
	private EntropyServer server = null;
	private Document message = null;
	private String messageName = null;
	private UserConnection usc = null;
	private AtomicInteger counter = null;
	private String socketName = null;
	private int retries = 0;
	
	public NotificationRunnable(EntropyServer server, Document message, UserConnection usc, AtomicInteger counter, String socketName)
	{
		this.server = server;
		this.usc = usc;
		this.counter = counter;
		this.socketName = socketName;

		usc.addNotificationToQueue(socketName, message);
	}
	
	/**
	 * There are two levels of synchronization here, both are necessary:
	 *  - We sync on the NotificationQueue for this socketName/usc so that only one notification is sent at a time. 
	 *    This synchronization ensures that the notifications are sent in the correct order.
	 *  - We then sync on the NotificationWaitObject - this is so replacing the socket or using the socket can't run
	 *    concurrently. We can't just use the UserConnection synchronization because this object is also used for 
	 *    a wait/notify if we find the Socket is closed - waiting releases the synchronization and could allow us to 
	 *    send multiple Notifications at once.
	 */
	@Override
	public void run()
	{
		//Only send one notification at a time per notification queue
		synchronized (usc.getNotificationQueue(socketName))
		{
			try
			{
				//Pick up the first off the queue so we send notifications in the correct order.
				message = usc.getNextNotificationToSend(socketName);
				messageName = message.getDocumentElement().getNodeName();
				
				//Sync on the wait object so the socket doesn't get replaced while we're doing things with it
				Object waitObj = usc.getNotificationWaitObject(socketName);
				synchronized (waitObj)
				{
					sendNotification();
				}
			}
			finally
			{
				decrementCounter();
			}
		}
	}
	
	private void sendNotification()
	{
		if (message == null)
		{
			Debug.stackTrace("Trying to send notification but there were none queued. Usc: " + usc);
			return;
		}
		
		String xmlStr = XmlUtil.getStringFromDocument(message);
		NotificationSocket notificationSocket = usc.getNotificationSocket(socketName);
		if (notificationSocket == null)
		{
			Debug.append("Not sending " + messageName + " as NotificationSocket is NULL. Usc: " + usc);
			return;
		}
		
		String encryptedXmlStr = EncryptionUtil.encrypt(xmlStr, usc.getSymmetricKey());
		Throwable t = notificationSocket.sendMessageViaSocket(encryptedXmlStr);
		if (t != null)
		{
			if (shouldResend(t))
			{
				retries++;
				
				String debugStr = "Caught " + t + " sending " + messageName + " to usc " + usc;
				debugStr += ", will retry (" + retries + "/" + MAX_RETRIES + ")";
				Debug.append(debugStr);
				
				//Sleep before retrying
				try {Thread.sleep(SLEEP_TIME_MILLIS);} catch (Throwable t2){}
				sendNotification();
			}
			else
			{
				String debugStr = "Failed to send " + messageName + " to usc " + usc;
				if (retries > 0)
				{
					debugStr += ". Retried " + retries + " times";
				}

				logger.error("webSocketError", debugStr, t);
			}
		}
	}
	
	private void decrementCounter()
	{
		if (counter != null)
		{
			counter.decrementAndGet();
		}
	}
	
	private boolean shouldResend(Throwable t)
	{
		if (retries >= MAX_RETRIES)
		{
			return false;
		}
		
		if (shouldWaitAndResend(t))
		{
			//Wait for a new socket.
			usc.waitForNewNotificationSocket(socketName);
			return true;
		}
		
		if (t instanceof SocketException 
		  || t instanceof SocketTimeoutException)
		{
			//Retry other timeouts/socket errors
			return true;
		}
		
		return false;
	}
	
	private boolean shouldWaitAndResend(Throwable t)
	{
		String message = t.getMessage();
		if (t instanceof IOException
		  && message.equals("Stream closed"))
		{
			return true;
		}
		
		//Tend to see this if the client has been killed. Wait on this and let the thread die gracefully once
		//the inactive check kicks them off.
		if (t instanceof SocketException
		  && message.equals("Socket closed"))
		{
			return true;
		}
		
		return false;
	}

	@Override
	public String getDetails()
	{
		String message = "Sending " + messageName + " on " + socketName;
		if (messageName == null)
		{
			int size = usc.getNotificationQueueSize(socketName);
			message = "Waiting to pick up from " + socketName + " queue of size " + size;
		}
		
		if (retries > 0)
		{
			message += ". Retries: " + retries + "/" + MAX_RETRIES;
		}
		
		return message;
	}

	@Override
	public UserConnection getUserConnection()
	{
		return usc;
	}
}
