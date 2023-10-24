package online.util;

import online.screen.EntropyLobby;

import org.w3c.dom.Document;

import util.AbstractClient;
import util.Debug;
import util.MessageUtil;

public class HeartbeatRunnable implements Runnable
{
	private static int SLEEP_TIME_MILLIS = 5 * 1000; //5 seconds
	private static int HEARTBEAT_THREADHOLD = 30 * 1000; //30 seconds
	
	private EntropyLobby lobby;
	
	public HeartbeatRunnable(EntropyLobby lobby)
	{
		this.lobby = lobby;
	}
	
	@Override
	public void run()
	{
		while (lobby.isVisible())
		{
			try
			{
				long lastSentMessageMillis = AbstractClient.getInstance().getLastSentMessageMillis();
				long currentMillis = System.currentTimeMillis();
				long difference = currentMillis - lastSentMessageMillis;
				if (difference < HEARTBEAT_THREADHOLD)
				{
					Thread.sleep(SLEEP_TIME_MILLIS);
				}
				else
				{
					Document lobbyRequest = XmlBuilderClient.factoryHeartbeat(lobby.getUsername());
					MessageUtil.sendMessage(lobbyRequest, 0);
					Debug.append("Sent message from the heartbeat thread. Difference was: " + difference, !AbstractClient.devMode);
					Thread.sleep(SLEEP_TIME_MILLIS);
				}
			}
			catch (Throwable t)
			{
				Debug.stackTrace(t);
			}
		}
	}
}