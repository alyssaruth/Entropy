package object;

import util.DateUtil;

public class BlacklistEntry 
{
	private String blacklistReason = "";
	private long blacklistTimeMillis = -1;
	
	public BlacklistEntry(String blacklistReason)
	{
		this.blacklistReason = blacklistReason;
		
		blacklistTimeMillis = System.currentTimeMillis();
	}
	
	public String getBlacklistReason()
	{
		return blacklistReason;
	}
	
	public long getTimeRemainingOnBlacklist(int minutes)
	{
		if (minutes == -1)
		{
			return Integer.MAX_VALUE;
		}
		
		long timeOnBlacklistMillis = System.currentTimeMillis() - blacklistTimeMillis;
		long timeRemainingMillis = (1000L * minutes * 60) - timeOnBlacklistMillis;
		
		if (timeRemainingMillis < 0)
		{
			timeRemainingMillis = 0;
		}
		
		return timeRemainingMillis;
	}
	
	public String getRenderedTimeRemainingOnBlacklist(int minutes)
	{
		long timeRemainingMillis = getTimeRemainingOnBlacklist(minutes);
		if (timeRemainingMillis == Integer.MAX_VALUE)
		{
			//We're in FULL mode
			return "N/A";
		}
		
		return DateUtil.formatHHMMSS(timeRemainingMillis);
	}
}
