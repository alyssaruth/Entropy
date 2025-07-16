package util;

import game.BidAction;
import object.Bid;

public interface BidListener <B extends BidAction<B>>
{
	public void bidMade(B bid);
	public void challengeMade();
	public void illegalCalled();
}
