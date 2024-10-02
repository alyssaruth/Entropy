package server;

import java.util.List;

import auth.UserConnection;
import object.ServerRunnable;
import util.Debug;
import util.ServerGlobals;

import static utils.CoreGlobals.logger;

public class InactiveCheckRunnable implements ServerRunnable
{
	private static int SLEEP_TIME_MILLIS = 5 * 1000; //10 seconds
	private static int KICK_OFF_TIME_MILLIS = 60 * 1000; //60 seconds
	
	private EntropyServer server;
	private String statusText = "";
	
	public InactiveCheckRunnable(EntropyServer server)
	{
		this.server = server;
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				runInactiveCheck();
			}
			catch (Throwable t)
			{
				Debug.stackTrace(t);
			}
		}
	}
	
	private void runInactiveCheck() throws InterruptedException
	{
		List<UserConnection> userConnections = ServerGlobals.INSTANCE.getUscStore().getAll();
		int size = userConnections.size();

		statusText = "Running for " + size + " uscs";
		
		for (int i=size-1; i>=0; i--)
		{
			UserConnection usc = userConnections.get(i);
			String username = usc.getName();
			long lastActiveMillis = usc.getLastActive();
			long currentMillis = System.currentTimeMillis();
			long timeSinceLastActive = currentMillis - lastActiveMillis;
			
			if (timeSinceLastActive >= KICK_OFF_TIME_MILLIS)
			{
				server.removeFromUsersOnline(usc);
				
				if (username != null)
				{
					logger.info("removedInactiveUser", "Removed " + username + " due to inactivity.");
				}
				else
				{
					logger.info("removedInactiveUser", "Removed unused connection for IP " + usc.getIpAddress());
				}
			}
		}

		statusText = "Sleeping between checks";
		Thread.sleep(SLEEP_TIME_MILLIS);
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
}
