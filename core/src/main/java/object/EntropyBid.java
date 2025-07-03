package object;

import game.GameSettings;
import game.Suit;
import org.w3c.dom.Element;
import util.Debug;
import util.XmlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static game.CardsUtilKt.countSuit;
import static game.EntropyUtilKt.perfectBidAmount;
import static game.EntropyUtilKt.perfectBidSuit;

public class EntropyBid extends Bid
{
	private Suit bidSuit;
	private int bidAmount = 0;
	
	/**
	 * Empty constructor, used for factorying from XML
	 */
	public EntropyBid(){}
	
	public EntropyBid(Suit bidSuit, int bidAmount)
	{
		this.bidSuit = bidSuit;
		this.bidAmount = bidAmount;
	}
	
	public Suit getBidSuit()
	{
		return bidSuit;
	}

	public int getBidAmount()
	{
		return bidAmount;
	}
	
	@Override
	public String toStringSpecific() 
	{
		return bidAmount + " " + bidSuit.getDescription(bidAmount);
	}
	
	@Override
	public String toHtmlStringSpecific()
	{
		String suitSymbol = bidSuit.getUnicodeStr();
		String colour = bidSuit.getColourHex();
		
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
		result = prime * result + bidSuit.ordinal();
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
		if (bidSuit != other.bidSuit)
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
		  && entropyBid.bidSuit.lessThan(bidSuit))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isPerfect(List<String> cards, GameSettings settings)
	{
		int perfectBidAmount = perfectBidAmount(cards, settings.getJokerValue());
		var perfectBidSuit = perfectBidSuit(cards, settings.getJokerValue(), settings.getIncludeStars());
		
		return bidSuit == perfectBidSuit && bidAmount == perfectBidAmount;
	}
	
	@Override
	public boolean isOverbid(List<String> cards, int jokerValue)
	{
		int total = countSuit(bidSuit, cards, jokerValue);
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
		return bidSuit.name() + ";" + bidAmount;
	}
	
	@Override
	public void populateFromXmlStringSpecific(ArrayList<String> toks,
			boolean moons, boolean stars)
	{
		this.bidSuit = Suit.valueOf(toks.get(0));
		this.bidAmount = Integer.parseInt(toks.get(1));
	}
	
	@Override
	public void populateXmlTag(Element bidElement)
	{
		bidElement.setAttribute("BidSuit", bidSuit.name());
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
		String bidSuitName = XmlUtil.getCompulsoryAttribute(root, "BidSuit");
		int bidAmount = XmlUtil.getAttributeIntCompulsory(root, "BidAmount");
		return new EntropyBid(Suit.valueOf(bidSuitName), bidAmount);
	}
}
