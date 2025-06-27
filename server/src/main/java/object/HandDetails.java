package object;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HandDetails 
{
	private ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber = new ConcurrentHashMap<>();
	
	public ConcurrentHashMap<Integer, List<String>> getHands()
	{
		return hmHandByPlayerNumber;
	}
	public void setHands(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber)
	{
		this.hmHandByPlayerNumber = hmHandByPlayerNumber;
	}
	
	public List<String> getHand(int playerNumber)
	{
		return hmHandByPlayerNumber.get(playerNumber);
	}
	
	public String getHandsForLogging()
	{
		String handsStr = "Hands: ";
		for (int i=0; i<4; i++)
		{
			List<String> hand = hmHandByPlayerNumber.get(i);
			if (hand == null)
			{
				continue;
			}
			
			if (i > 0)
			{
				handsStr += ", ";
			}
			
			handsStr += "Player " + i + ": ";
			handsStr += prettyPrintHand(hand);
		}
		
		return handsStr;
	}
	
	private String prettyPrintHand(List<String> hand)
	{
		String ret = "[";
		int handSize = hand.size();
		for (int i=0; i<handSize; i++)
		{
			if (i > 0)
			{
				ret += ", ";
			}
			
			ret += hand.get(i);
		}
		
		ret += "]";
		return ret;
	}
}
