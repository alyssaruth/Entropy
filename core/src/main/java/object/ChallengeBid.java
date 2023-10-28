package object;


public class ChallengeBid extends FakeBid
{
	@Override
	public String toStringSpecific() 
	{
		return "Challenge";
	}
	
	/**
	 * Abstract methods
	 */
	@Override
	public String getXmlStringPrefix()
	{
		return "C";
	}
	
	@Override
	public boolean equals(Object arg0) 
	{
		return arg0 instanceof ChallengeBid;
	}
}
