package object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Element;

import util.CardsUtil;
import util.Debug;
import util.VectropyUtil;
import util.XmlUtil;

public class VectropyBid extends Bid
{
	private int clubs = 0;
	private int diamonds = 0;
	private int hearts = 0;
	private int moons = 0;
	private int spades = 0;
	private int stars = 0;
	
	private boolean includeMoons = false;
	private boolean includeStars = false;
	
	/**
	 * Empty constructor, used for factorying from XML
	 */
	public VectropyBid(){}
	
	public VectropyBid(int clubs, int diamonds, int hearts, int moons, int spades, int stars, 
					   boolean includeMoons, boolean includeStars)
	{
		this.clubs = clubs;
		this.diamonds = diamonds;
		this.hearts = hearts;
		this.moons = moons;
		this.spades = spades;
		this.stars = stars;
		this.includeMoons = includeMoons;
		this.includeStars = includeStars;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + clubs;
		result = prime * result + diamonds;
		result = prime * result + hearts;
		result = prime * result + (includeMoons ? 1231 : 1237);
		result = prime * result + (includeStars ? 1231 : 1237);
		result = prime * result + moons;
		result = prime * result + spades;
		result = prime * result + stars;
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
		if (clubs != other.clubs)
			return false;
		if (diamonds != other.diamonds)
			return false;
		if (hearts != other.hearts)
			return false;
		if (includeMoons != other.includeMoons)
			return false;
		if (includeStars != other.includeStars)
			return false;
		if (moons != other.moons)
			return false;
		if (spades != other.spades)
			return false;
		if (stars != other.stars)
			return false;
		return true;
	}

	public int getClubs()
	{
		return clubs;
	}
	public int getDiamonds()
	{
		return diamonds;
	}
	public int getHearts()
	{
		return hearts;
	}
	public int getMoons()
	{
		return moons;
	}
	public int getSpades()
	{
		return spades;
	}
	public int getStars()
	{
		return stars;
	}
	
	public int getTotal()
	{
		int total = clubs + diamonds + hearts + moons + spades + stars;
		if (includeMoons)
		{
			total += moons;
		}
		
		if (includeStars)
		{
			total += stars;
		}
		
		return total;
	}
	
	public VectropyBid incrementSuitAndGet(int suit)
	{
		int clubs = this.clubs;
		int diamonds = this.diamonds;
		int hearts = this.hearts;
		int moons = this.moons;
		int spades = this.spades;
		int stars = this.stars;
		
		if (suit == CardsUtil.SUIT_CLUBS)
		{
			clubs++;
		}
		else if (suit == CardsUtil.SUIT_DIAMONDS)
		{
			diamonds++;
		}
		else if (suit == CardsUtil.SUIT_HEARTS)
		{
			hearts++;
		}
		else if (suit == CardsUtil.SUIT_MOONS)
		{
			moons++;
		}
		else if (suit == CardsUtil.SUIT_SPADES)
		{
			spades++;
		}
		else if (suit == CardsUtil.SUIT_STARS)
		{
			stars++;
		}

		return new VectropyBid(clubs, diamonds, hearts, moons, spades, stars, includeMoons, includeStars);
	}
	
	@Override
	public String getXmlStringPrefix()
	{
		return "V";
	}
	
	@Override
	public String toXmlStringSpecific()
	{
		String xmlStr = clubs + XML_DELIM_CHAR + diamonds + XML_DELIM_CHAR + hearts + XML_DELIM_CHAR;
		
		if (includeMoons)
		{
			xmlStr += moons;
			xmlStr += XML_DELIM_CHAR;
		}
		
		xmlStr += spades;
		
		if (includeStars)
		{
			xmlStr += XML_DELIM_CHAR;
			xmlStr += stars;
		}
		
		return xmlStr;
	}
	
	@Override
	public void populateFromXmlStringSpecific(ArrayList<String> toks,
			boolean includeMoons, boolean includeStars)
	{
		this.includeMoons = includeMoons;
		this.includeStars = includeStars;
		
		clubs = Integer.parseInt(toks.remove(0));
		diamonds = Integer.parseInt(toks.remove(0));
		hearts = Integer.parseInt(toks.remove(0));
		
		if (includeMoons)
		{
			moons = Integer.parseInt(toks.remove(0));
		}
		
		spades = Integer.parseInt(toks.remove(0));
		
		if (includeStars)
		{
			stars = Integer.parseInt(toks.remove(0));
		}
	}
	
	@Override
	public String toStringSpecific() 
	{
		String bidStr = "(" + clubs + ", " + diamonds + ", " + hearts;
		if (includeMoons)
		{
			bidStr += ", " + moons;
		}
		
		bidStr += ", " + spades;
		
		if (includeStars)
		{
			bidStr += ", " + stars;
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
		return clubs >= bid2.getClubs()
		  && diamonds >= bid2.getDiamonds()
		  && hearts >= bid2.getHearts()
		  && moons >= bid2.getMoons()
		  && spades >= bid2.getSpades()
		  && stars >= bid2.getStars()
		  && getTotal() > bid2.getTotal();
	}
	
	@Override
	public boolean isPerfect(List<String> handOne, List<String> handTwo, List<String> handThree, List<String> handFour,
							 int jokerValue, boolean includeMoons, boolean includeStars) 
	{
		int maxClubs = CardsUtil.countSuit(CardsUtil.SUIT_CLUBS, handOne, handTwo, handThree, handFour, jokerValue);
		int maxDiamonds = CardsUtil.countSuit(CardsUtil.SUIT_DIAMONDS, handOne, handTwo, handThree, handFour, jokerValue);
		int maxHearts = CardsUtil.countSuit(CardsUtil.SUIT_HEARTS, handOne, handTwo, handThree, handFour, jokerValue);
		int maxMoons = CardsUtil.countSuit(CardsUtil.SUIT_MOONS, handOne, handTwo, handThree, handFour, jokerValue);
		int maxSpades = CardsUtil.countSuit(CardsUtil.SUIT_SPADES, handOne, handTwo, handThree, handFour, jokerValue);
		int maxStars = CardsUtil.countSuit(CardsUtil.SUIT_STARS, handOne, handTwo, handThree, handFour, jokerValue);
		
		return clubs == maxClubs 
			&& diamonds == maxDiamonds 
			&& hearts == maxHearts 
			&& (moons == maxMoons || !includeMoons)
			&& spades == maxSpades
			&& (stars == maxStars || !includeStars);
	}
	
	@Override
	public boolean isOverbid(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber, int jokerValue)
	{
		return VectropyUtil.isOverbid(this, hmHandByPlayerNumber, jokerValue);
	}
	
	@Override
	public boolean isOverbid(List<String> handOne, List<String> handTwo,
							 List<String> handThree, List<String> handFour, int jokerValue)
	{
		return VectropyUtil.isOverbid(this, handOne, handTwo, handThree, handFour, jokerValue);
	}
	
	@Override
	public void populateXmlTag(Element bidElement)
	{
		bidElement.setAttribute("Clubs", "" + clubs);
		bidElement.setAttribute("Diamonds", "" + diamonds);
		bidElement.setAttribute("Hearts", "" + hearts);
		
		if (includeMoons)
		{
			bidElement.setAttribute("Moons", "" + moons);
		}
		
		bidElement.setAttribute("Spades", "" + spades);
		
		if (includeStars)
		{
			bidElement.setAttribute("Stars", "" + stars);
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
