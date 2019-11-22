package server;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import object.BlacklistEntry;
import object.ServerRunnable;
import object.UserConnection;
import util.Debug;

public class BlacklistRunnable implements ServerRunnable
{
	private static int SLEEP_TIME_MILLIS = 5 * 1000; //5 seconds
	
	private EntropyServer server;
	private String statusText = "";
	
	public BlacklistRunnable(EntropyServer server)
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
				ConcurrentHashMap<String, BlacklistEntry> blacklist = server.getBlacklist();
				int blacklistMinutes = server.getBlacklistDurationMinutes();

				statusText = "Removing entries older than " + blacklistMinutes + " minutes";
				
				Iterator<String> it = blacklist.keySet().iterator();
				for (; it.hasNext();)
				{
					String ipAddress = it.next();
					BlacklistEntry entry = blacklist.get(ipAddress);
					long timeRemaining = entry.getTimeRemainingOnBlacklist(blacklistMinutes);
					
					if (timeRemaining == 0)
					{
						blacklist.remove(ipAddress);
						Debug.append("Removed " + ipAddress + " from blacklist as entry has expired.");
					}
				}
				
				statusText = "Sleeping between checks";
				Thread.sleep(SLEEP_TIME_MILLIS);
			}
			catch (Throwable t)
			{
				Debug.stackTrace(t);
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
}
