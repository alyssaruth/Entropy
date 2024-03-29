package screen;

import java.util.prefs.Preferences;

import object.Bid;
import util.BidListener;

public abstract class BidPanel extends TransparentPanel
{
	public BidListener listener = null;
	public int maxBid = -1;
	
	private boolean logging = false;
	
	public abstract void showBidPanel(boolean visible);
	public abstract void enableBidPanel(boolean enable);
	public abstract void enableChallenge(boolean enable);
	public abstract void loadState(Preferences savedGame);
	public abstract void saveState(Preferences savedGame);
	public abstract void fireAppearancePreferencesChange();
	public abstract void init(int maxBid, int totalNumberOfCards, boolean online, boolean includeMoons, boolean includeStars, boolean illegalAllowed);
	public abstract void adjust(Bid bid);
	
	public void addBidListener(BidListener listener)
	{
		this.listener = listener;
	}
	
	public boolean getLogging()
	{
		return logging;
	}
	public void setLogging(boolean logging)
	{
		this.logging = logging;
	}
	
	public int getMaxBid()
	{
		return maxBid;
	}
}
