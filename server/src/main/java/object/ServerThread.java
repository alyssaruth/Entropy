package object;

import util.Debug;

public class ServerThread extends Thread
{
	private ServerRunnable runnable = null;
	private long startMillis = -1;
	
	public ServerThread(ServerRunnable r)
	{
		super(r);
		
		this.runnable = r;
	}
	
	public ServerThread(ServerRunnable r, String threadName)
	{
		super(r, threadName);
		
		this.runnable = r;
	}
	
	public ServerRunnable getRunnable()
	{
		return runnable;
	}
	
	@Override
	public synchronized void start()
	{
		setStartMillis();
		super.start();
	}
	
	@Override
	public void run()
	{
		setStartMillis();
		super.run();
	}
	
	public void dumpDetails(long dumpTimeMillis)
	{
		String debugStr = "";
		if (isAlive())
		{
			String dateStr = "";
			if (dumpTimeMillis > -1)
			{
				long totalTime = dumpTimeMillis - startMillis;
				dateStr = totalTime + "	";
			}
			
			debugStr = dateStr + getName() + " (" + getState() + ") - " + runnable.getDetails();
			UserConnection usc = runnable.getUserConnection();
			if (usc != null)
			{
				debugStr += " (" + usc + ")";
			}
		}
		else
		{
			debugStr = getName() + " (IDLE)";
		}
		
		Debug.appendWithoutDate(debugStr);
	}
	
	public void setStartMillis()
	{
		startMillis = System.currentTimeMillis();
	}
}
