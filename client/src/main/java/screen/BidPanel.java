package screen;

import java.util.prefs.Preferences;

import game.BidAction;
import object.Bid;
import util.BidListener;

public abstract class BidPanel<B extends BidAction<B>> extends TransparentPanel
{
	protected final String playerName;
	protected final HandPanelMk2 handPanel;

	BidPanel(String playerName, HandPanelMk2 handPanel) {
		this.playerName = playerName;
		this.handPanel = handPanel;
	}

	public BidListener<B> listener = null;
	public int maxBid = -1;
	
	private boolean logging = false;
	
	public abstract void showBidPanel(boolean visible);
	public abstract void enableBidPanel(boolean enable);
	public abstract void enableChallenge(boolean enable);
	public abstract void loadState(Preferences savedGame);
	public abstract void saveState(Preferences savedGame);
	public abstract void fireAppearancePreferencesChange();
	public abstract void init(int maxBid, int totalNumberOfCards, boolean online, boolean includeMoons, boolean includeStars, boolean illegalAllowed);
	public abstract void adjust(B bid);
	
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
