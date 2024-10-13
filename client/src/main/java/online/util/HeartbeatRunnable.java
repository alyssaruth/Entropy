package online.util;

import online.screen.EntropyLobby;

import org.w3c.dom.Document;

import util.ClientUtil;
import util.MessageUtil;

import static utils.CoreGlobals.logger;

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
			long lastSentMessageMillis = ClientUtil.getLastSentMessageMillis();
			long currentMillis = System.currentTimeMillis();
			long difference = currentMillis - lastSentMessageMillis;
			if (difference >= HEARTBEAT_THREADHOLD)
			{
				Document lobbyRequest = XmlBuilderClient.factoryHeartbeat(lobby.getUsername());
				MessageUtil.sendMessage(lobbyRequest, 0);
				logger.info("heartbeat", "Sent heartbeat message, time since last: " + difference);
			}

			try { Thread.sleep(SLEEP_TIME_MILLIS); } catch (InterruptedException ignored) {}
		}
	}
}