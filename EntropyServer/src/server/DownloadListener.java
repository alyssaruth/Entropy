package server;

import java.net.Socket;

public class DownloadListener extends ListenerRunnable
{
	String filename = "";
	
	public DownloadListener(EntropyServer server, int port, String filename)
	{
		super(server, port);
		
		this.filename = filename;
	}

	@Override
	public void handleInboundMessage(EntropyServer server, Socket clientSocket)
	{
		DownloadHandler.handleDownload(server, clientSocket, filename);
	}
}
