package object;


public class LeftBid extends FakeBid
{
	@Override
	public String toStringSpecific() 
	{
		return "Left";
	}
	
	@Override
	public String getXmlStringPrefix()
	{
		return "L";
	}
	
	@Override
	public boolean equals(Object arg0) 
	{
		return arg0 instanceof LeftBid;
	}
}
