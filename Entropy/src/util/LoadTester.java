package util;

import org.w3c.dom.Document;

import object.ProgressBar;
import screen.ScreenCache;

public class LoadTester implements Runnable, OnlineConstants
{
	private Document message;
	private int stopCount;
	private ProgressBar progressBar;
	
	public LoadTester(Document message, int stopCount, ProgressBar progressBar)
	{
		this.message = message;
		this.stopCount = stopCount;
		this.progressBar = progressBar;
	}
	
	@Override
	public void run()
	{
		for (int i=0; i<stopCount; i++)
		{
			AbstractClient.getInstance().sendSync(message, true);
			progressBar.incrementProgressLater();
		}
		
		ScreenCache.getLoadTesterDialog().threadFinished();
	}
	
	/*private void sendMessage(InetAddress address)
	{
		int port = MessageUtil.getRandomLoadTestPortNumber();
		try (Socket socket = new Socket(address, port);
		  PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
		{
			socket.setSoTimeout(20000);
			out.write(message + "\n");
			out.flush();
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t);
			sendMessage(address);
		}
	}*/
}
