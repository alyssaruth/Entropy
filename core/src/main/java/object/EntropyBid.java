package object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Element;

import util.CardsUtil;
import util.Debug;
import util.EntropyUtil;
import util.XmlUtil;

public class EntropyBid extends Bid
{
	private int bidSuitCode = 0;
	private int bidAmount = 0;
	
	/**
	 * Empty constructor, used for factorying from XML
	 */
	public EntropyBid(){}
	
	public EntropyBid(int bidSuitCode, int bidAmount)
	{
		this.bidSuitCode = bidSuitCode;
		this.bidAmount = bidAmount;
	}
	
	public EntropyBid(String bidSuitCode, String bidAmount)
	{
		this.bidSuitCode = Integer.parseInt(bidSuitCode);
		this.bidAmount = Integer.parseInt(bidAmount);
	}
	
	public int getBidSuitCode()
	{
		return bidSuitCode;
	}

	public int getBidAmount()
	{
		return bidAmount;
	}
	
	@Override
	public String toStringSpecific() 
	{
		return bidAmount + " " + CardsUtil.getSuitDesc(bidAmount, bidSuitCode);
	}
	
	@Override
	public String toHtmlStringSpecific()
	{
		String suitSymbol = CardsUtil.getSuitSymbolForCode(bidSuitCode);
		String colour = CardsUtil.getColourForSuitCode(bidSuitCode);
		
		String htmlStr = "<font color=\"" + colour + "\" face=\"Segoe UI Symbol\">";
		htmlStr += bidAmount + suitSymbol;
		htmlStr += "</font>";
		
		return htmlStr;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bidAmount;
		result = prime * result + bidSuitCode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EntropyBid))
			return false;
		EntropyBid other = (EntropyBid) obj;
		if (bidAmount != other.bidAmount)
			return false;
		if (bidSuitCode != other.bidSuitCode)
			return false;
		return true;
	}

	/**
	 * Abstract methods
	 */
	@Override
	public boolean higherThan(Bid bid)
	{
		if (!(bid instanceof EntropyBid))
		{
			Debug.stackTrace("Comparing EntropyBid " + this + " to " + bid);
			return false;
		}
		
		EntropyBid entropyBid = (EntropyBid)bid;
		if (bidAmount > entropyBid.getBidAmount())
		{
			return true;
		}
		
		if (bidAmount == entropyBid.getBidAmount()
		  && bidSuitCode > entropyBid.getBidSuitCode())
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isPerfect(List<String> handOne, List<String> handTwo, List<String> handThree, List<String> handFour,
							 int jokerValue, boolean includeMoons, boolean includeStars) 
	{
		int perfectBidAmount = EntropyUtil.getPerfectBidAmount(handOne, handTwo, handThree, handFour, jokerValue);
		int perfectBidSuitCode = EntropyUtil.getPerfectBidSuitCode(handOne, handTwo, handThree, handFour, jokerValue, includeStars);
		
		return bidSuitCode == perfectBidSuitCode && bidAmount == perfectBidAmount;
	}
	@Override
	public boolean isOverbid(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber, int jokerValue)
	{
		int total = CardsUtil.countSuit(bidSuitCode, hmHandByPlayerNumber, jokerValue);
		return bidAmount > total;
	}
	
	@Override
	public boolean isOverbid(List<String> handOne, List<String> handTwo, List<String> handThree, List<String> handFour, int jokerValue)
	{
		int total = CardsUtil.countSuit(bidSuitCode, handOne, handTwo, handThree, handFour, jokerValue);
		return bidAmount > total;
	}
	
	@Override
	public String getXmlStringPrefix()
	{
		return "E";
	}
	
	@Override
	public String toXmlStringSpecific()
	{
		return bidSuitCode + ";" + bidAmount;
	}
	
	@Override
	public void populateFromXmlStringSpecific(ArrayList<String> toks,
			boolean moons, boolean stars)
	{
		String bidSuitCodeStr = toks.get(0);
		String bidAmountStr = toks.get(1);
		
		this.bidSuitCode = Integer.parseInt(bidSuitCodeStr);
		this.bidAmount = Integer.parseInt(bidAmountStr);
	}
	
	@Override
	public void populateXmlTag(Element bidElement)
	{
		bidElement.setAttribute("BidSuitCode", "" + bidSuitCode);
		bidElement.setAttribute("BidAmount", "" + bidAmount);
	}
	
	@Override
	public boolean isOverAchievementThreshold()
	{
		return bidAmount >= 5;
	}
	
	/**
	 * Static methods
	 */
	public static EntropyBid factoryFromXmlTag(Element root) throws IOException
	{
		int bidSuitCode = XmlUtil.getAttributeIntCompulsory(root, "BidSuitCode");
		int bidAmount = XmlUtil.getAttributeIntCompulsory(root, "BidAmount");
		return new EntropyBid(bidSuitCode, bidAmount);
	}
}
