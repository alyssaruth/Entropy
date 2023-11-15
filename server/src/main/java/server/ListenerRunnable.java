package server;

import java.net.ServerSocket;
import java.net.Socket;

import object.ServerRunnable;
import object.UserConnection;
import util.Debug;

public abstract class ListenerRunnable implements ServerRunnable
{
	private EntropyServer server = null;
	private int portNumber = -1;
	
	public ListenerRunnable(EntropyServer server, int portNumber)
	{
		this.server = server;
		this.portNumber = portNumber;
	}
	
	@Override
	@SuppressWarnings("resource")
	public void run()
	{
		try (ServerSocket serverSocket = new ServerSocket(portNumber))
		{
			while (!serverSocket.isClosed())
			{
				Socket clientSocket = serverSocket.accept();
				
				String ipAddress = clientSocket.getInetAddress().getHostAddress();
				if (server.isBlacklisted(ipAddress))
				{
					clientSocket.close();
				}
				else
				{
					handleInboundMessage(server, clientSocket);
				}
			} 
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	public abstract void handleInboundMessage(EntropyServer server, Socket clientSocket);

	@Override
	public String getDetails()
	{
		return "Listening on port " + portNumber;
	}

	@Override
	public UserConnection getUserConnection()
	{
		return null;
	}
}
