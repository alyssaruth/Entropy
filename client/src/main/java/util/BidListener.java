package util;

import game.BidAction;

public interface BidListener 
{
	public void bidMade(BidAction bid);
	public void challengeMade();
	public void illegalCalled();
}
