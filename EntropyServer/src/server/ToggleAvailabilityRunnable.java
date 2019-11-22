package server;

import java.util.ArrayList;

import org.w3c.dom.Document;

import object.ServerRunnable;
import object.UserConnection;
import util.Debug;
import util.XmlBuilderServer;
import util.XmlConstants;

/**
 * Runnable to bring the Server offline. A more graceful way to kick off all the users than just killing the App.
 */
public class ToggleAvailabilityRunnable implements ServerRunnable
{
	private static final String offlineTitleStr = " ***OFFLINE***";
	
	private EntropyServer server = null;
	private boolean shuttingDown = false;
	
	public ToggleAvailabilityRunnable(EntropyServer server)
	{
		this.server = server;
	}
	
	@Override
	public void run()
	{
		server.toggleOnline();
		shuttingDown = !server.isOnline();
		if (shuttingDown)
		{
			takeOffline();
		}
		else
		{
			bringOnline();
		}
	}
	
	private void takeOffline()
	{
		Debug.append("Going offline, will deny all new connections");
		
		//Toggle the server title
		String title = server.getTitle();
		server.setTitle(title + offlineTitleStr);
		
		//Deal with existing user connections
		ArrayList<UserConnection> uscs = server.getUserConnections(true);
		if (!uscs.isEmpty())
		{
			Debug.append("Sending notification to " + uscs.size() + " users");
			Document message = XmlBuilderServer.getKickOffResponse("", XmlConstants.REMOVAL_REASON_SERVER_OFFLINE);
			server.sendViaNotificationSocket(uscs, message, XmlConstants.SOCKET_NAME_GAME, true);
			
			Debug.append("All notifications sent, clearing user connections");
			for (int i=0; i<uscs.size(); i++)
			{
				UserConnection usc = uscs.get(i);
				server.removeFromUsersOnline(usc);
			}
		}
	}
	
	private void bringOnline()
	{
		String title = server.getTitle();
		int newLength = title.length() - offlineTitleStr.length();
		title = title.substring(0, newLength);
		server.setTitle(title);
		
		Debug.append("Allowing user connections");
	}

	@Override
	public String getDetails()
	{
		return "Toggline server. Offline = " + shuttingDown;
	}

	@Override
	public UserConnection getUserConnection()
	{
		return null;
	}
}
