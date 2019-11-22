package server;

import java.net.Socket;

public class MessageListener extends ListenerRunnable
{
	public MessageListener(EntropyServer server, int portNumber)
	{
		super(server, portNumber);
	}
	
	@Override
	public void handleInboundMessage(EntropyServer server, Socket clientSocket)
	{
		MessageHandlerRunnable runnable = new MessageHandlerRunnable(server, clientSocket);
		server.executeInWorkerPool(runnable);
	}
}
