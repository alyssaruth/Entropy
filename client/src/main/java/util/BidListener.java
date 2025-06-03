package util;

import game.BidAction;

public interface BidListener<B extends BidAction<B>>
{
	public void bidMade(B bid);
	public void challengeMade();
	public void illegalCalled();
}
