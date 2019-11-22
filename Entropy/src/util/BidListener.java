package util;

import object.Bid;

public interface BidListener 
{
	public void bidMade(Bid bid);
	public void challengeMade();
	public void illegalCalled();
}
