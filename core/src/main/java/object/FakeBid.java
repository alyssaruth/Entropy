package object;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import game.GameSettings;
import org.w3c.dom.Element;

import util.Debug;

public abstract class FakeBid extends Bid
{
	@Override
	public boolean higherThan(Bid bid)
	{
		Debug.stackTrace("Calling unimplemented method: higherThan");
		return false;
	}
	
	@Override
	public boolean isPerfect(List<String> handOne, List<String> handTwo,
			List<String> handThree, List<String> handFour, GameSettings settings)
	{
		Debug.stackTrace("Calling unimplemented method: isPerfect");
		return false;
	}
	
	@Override
	public boolean isOverbid(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber, int jokerValue)
	{
		Debug.stackTrace("Calling unimplemented method: isOverbid");
		return false;
	}
	
	@Override
	public boolean isOverbid(List<String> handOne, List<String> handTwo,
							 List<String> handThree, List<String> handFour, int jokerValue)
	{
		Debug.stackTrace("Calling unimplemented method: isOverbid");
		return false;
	}
	
	@Override
	public boolean isOverAchievementThreshold()
	{
		Debug.stackTrace("Calling unimplemented method: isOverAchievementThreshold");
		return false;
	}
	
	@Override
	public void populateXmlTag(Element bidElement)
	{
		Debug.stackTrace("Calling populateXmlTag but is hasn't been implemented! BidElement: " + bidElement.getNodeName());
	}
	
	@Override
	public String toXmlStringSpecific()
	{
		return "";
	}
	
	@Override
	public void populateFromXmlStringSpecific(java.util.ArrayList<String> toks, boolean moons, boolean stars) 
	{
		//Do nothing...
	}
	
	@Override
	public String toHtmlStringSpecific()
	{
		return toStringSpecific();
	}
}
