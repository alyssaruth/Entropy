package object;

public class IllegalBid extends FakeBid
{
	@Override
	public String toStringSpecific() 
	{
		return "Illegal!";
	}
	
	@Override
	public String getXmlStringPrefix()
	{
		return "I";
	}
	
	@Override
	public boolean equals(Object arg0) 
	{
		return arg0 instanceof IllegalBid;
	}
}
