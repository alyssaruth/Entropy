package object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import game.GameSettings;
import game.Suit;
import game.VectropyUtilKt;
import org.w3c.dom.Element;

import util.Debug;
import util.VectropyUtil;
import util.XmlUtil;

import static game.CardsUtilKt.countSuit;

public class VectropyBid extends Bid
{
	private HashMap<Suit, Integer> amounts = new HashMap<>();
	public boolean includeMoons = false;
	public boolean includeStars = false;
	
	/**
	 * Empty constructor, used for factorying from XML
	 */
	public VectropyBid(){}
	
	public VectropyBid(int clubs, int diamonds, int hearts, int moons, int spades, int stars, 
					   boolean includeMoons, boolean includeStars)
	{
		populateMap(clubs, diamonds, hearts, moons, spades, stars);
		this.includeMoons = includeMoons;
		this.includeStars = includeStars;
	}

	public VectropyBid(HashMap<Suit, Integer> amounts, boolean includeMoons, boolean includeStars) {
		this.amounts = amounts;
		this.includeMoons = includeMoons;
		this.includeStars = includeStars;
	}

	private void populateMap(int clubs, int diamonds, int hearts, int moons, int spades, int stars) {
		this.amounts.put(Suit.Clubs, clubs);
		this.amounts.put(Suit.Diamonds, diamonds);
		this.amounts.put(Suit.Hearts, hearts);
		this.amounts.put(Suit.Moons, moons);
		this.amounts.put(Suit.Spades, spades);
		this.amounts.put(Suit.Stars, stars);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amounts.get(Suit.Clubs);
		result = prime * result + amounts.get(Suit.Diamonds);
		result = prime * result + amounts.get(Suit.Hearts);
		result = prime * result + (includeMoons ? 1231 : 1237);
		result = prime * result + (includeStars ? 1231 : 1237);
		result = prime * result + amounts.get(Suit.Moons);
		result = prime * result + amounts.get(Suit.Spades);
		result = prime * result + amounts.get(Suit.Stars);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VectropyBid))
			return false;
		VectropyBid other = (VectropyBid) obj;
		if (!amounts.get(Suit.Clubs).equals(other.amounts.get(Suit.Clubs)))
			return false;
		if (!amounts.get(Suit.Diamonds).equals(other.amounts.get(Suit.Diamonds)))
			return false;
		if (!amounts.get(Suit.Hearts).equals(other.amounts.get(Suit.Hearts)))
			return false;
		if (includeMoons != other.includeMoons)
			return false;
		if (includeStars != other.includeStars)
			return false;
		if (!amounts.get(Suit.Moons).equals(other.amounts.get(Suit.Moons)))
			return false;
		if (!amounts.get(Suit.Spades).equals(other.amounts.get(Suit.Spades)))
			return false;
		if (!amounts.get(Suit.Stars).equals(other.amounts.get(Suit.Stars)))
			return false;
		return true;
	}

	public int getAmount(Suit suit)
	{
		return amounts.get(suit);
	}
	public int getClubs()
	{
		return amounts.get(Suit.Clubs);
	}
	public int getDiamonds()
	{
		return amounts.get(Suit.Diamonds);
	}
	public int getHearts()
	{
		return amounts.get(Suit.Hearts);
	}
	public int getMoons()
	{
		return amounts.get(Suit.Moons);
	}
	public int getSpades()
	{
		return amounts.get(Suit.Spades);
	}
	public int getStars()
	{
		return amounts.get(Suit.Stars);
	}
	
	public int getTotal()
	{
		return amounts.values().stream().mapToInt(Integer::intValue).sum();
	}
	
	public VectropyBid incrementSuitAndGet(Suit suit)
	{
		int current = amounts.get(suit);
		var newAmounts = new HashMap<Suit, Integer>();
		Suit.getEntries().forEach((Suit s) -> {
			if (s == suit) {
				newAmounts.put(s, amounts.get(s) + 1);
			} else {
				newAmounts.put(s, amounts.get(s));
			}
		});

		return new VectropyBid(newAmounts, includeMoons, includeStars);
	}
	
	@Override
	public String getXmlStringPrefix()
	{
		return "V";
	}
	
	@Override
	public String toXmlStringSpecific()
	{
		String xmlStr = getClubs() + XML_DELIM_CHAR + getDiamonds() + XML_DELIM_CHAR + getHearts() + XML_DELIM_CHAR;
		
		if (includeMoons)
		{
			xmlStr += getMoons();
			xmlStr += XML_DELIM_CHAR;
		}
		
		xmlStr += getSpades();
		
		if (includeStars)
		{
			xmlStr += XML_DELIM_CHAR;
			xmlStr += getStars();
		}
		
		return xmlStr;
	}
	
	@Override
	public void populateFromXmlStringSpecific(ArrayList<String> toks,
			boolean includeMoons, boolean includeStars)
	{
		this.includeMoons = includeMoons;
		this.includeStars = includeStars;
		
		var clubs = Integer.parseInt(toks.remove(0));
		var diamonds = Integer.parseInt(toks.remove(0));
		var hearts = Integer.parseInt(toks.remove(0));

		var moons = 0;
		if (includeMoons)
		{
			moons = Integer.parseInt(toks.remove(0));
		}
		
		var spades = Integer.parseInt(toks.remove(0));

		var stars = 0;
		if (includeStars)
		{
			stars = Integer.parseInt(toks.remove(0));
		}

		populateMap(clubs, diamonds, hearts, moons, spades, stars);
	}
	
	@Override
	public String toStringSpecific() 
	{
		String bidStr = "(" + getClubs() + ", " + getDiamonds() + ", " + getHearts();
		if (includeMoons)
		{
			bidStr += ", " + getMoons();
		}
		
		bidStr += ", " + getSpades();
		
		if (includeStars)
		{
			bidStr += ", " + getStars();
		}
		
		bidStr += ")";
		return bidStr;
	}
	
	@Override
	public String toHtmlStringSpecific()
	{
		return toStringSpecific();
	}

	/**
	 * Abstract methods
	 */
	@Override
	public boolean higherThan(Bid bid)
	{
		if (!(bid instanceof VectropyBid))
		{
			Debug.stackTrace("Comparing " + this + " to " + bid);
			return false;
		}
		
		VectropyBid bid2 = (VectropyBid)bid;
		return getClubs() >= bid2.getClubs()
		  && getDiamonds() >= bid2.getDiamonds()
		  && getHearts() >= bid2.getHearts()
		  && getMoons() >= bid2.getMoons()
		  && getSpades() >= bid2.getSpades()
		  && getStars() >= bid2.getStars()
		  && getTotal() > bid2.getTotal();
	}
	
	@Override
	public boolean isPerfect(List<String> cards, GameSettings settings)
	{
		var jokerValue = settings.getJokerValue();
		int maxClubs = countSuit(Suit.Clubs, cards, jokerValue);
		int maxDiamonds = countSuit(Suit.Diamonds, cards, jokerValue);
		int maxHearts = countSuit(Suit.Hearts, cards, jokerValue);
		int maxMoons = countSuit(Suit.Moons, cards, jokerValue);
		int maxSpades = countSuit(Suit.Spades, cards, jokerValue);
		int maxStars = countSuit(Suit.Stars, cards, jokerValue);
		
		return getClubs() == maxClubs
			&& getDiamonds() == maxDiamonds
			&& getHearts() == maxHearts
			&& (getMoons() == maxMoons || !includeMoons)
			&& getSpades() == maxSpades
			&& (getStars() == maxStars || !includeStars);
	}
	
	@Override
	public boolean isOverbid(List<String> cards, int jokerValue)
	{
		return VectropyUtilKt.isOverbid(this, cards, jokerValue);
	}
	
	@Override
	public void populateXmlTag(Element bidElement)
	{
		bidElement.setAttribute("Clubs", "" + getClubs());
		bidElement.setAttribute("Diamonds", "" + getDiamonds());
		bidElement.setAttribute("Hearts", "" + getHearts());
		
		if (includeMoons)
		{
			bidElement.setAttribute("Moons", "" + getMoons());
		}
		
		bidElement.setAttribute("Spades", "" + getSpades());
		
		if (includeStars)
		{
			bidElement.setAttribute("Stars", "" + getStars());
		}
	}
	
	@Override
	public boolean isOverAchievementThreshold()
	{
		return getTotal() >= 5;
	}
	
	/**
	 * Static methods
	 */
	public static VectropyBid factoryFromXmlTag(Element root, boolean includeMoons, boolean includeStars) throws IOException
	{
		int clubs = XmlUtil.getAttributeIntCompulsory(root, "Clubs");
		int diamonds = XmlUtil.getAttributeIntCompulsory(root, "Diamonds");
		int hearts = XmlUtil.getAttributeIntCompulsory(root, "Hearts");
		int spades = XmlUtil.getAttributeIntCompulsory(root, "Spades");
		
		int moons = 0;
		if (includeMoons)
		{
			moons = XmlUtil.getAttributeIntCompulsory(root, "Moons");
		}
		
		int stars = 0;
		if (includeStars)
		{
			stars = XmlUtil.getAttributeIntCompulsory(root, "Stars");
		}
		
		return new VectropyBid(clubs, diamonds, hearts, moons, spades, stars, includeMoons, includeStars);
	}
}
