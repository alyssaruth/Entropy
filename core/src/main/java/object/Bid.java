
package object;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import game.GameSettings;
import org.w3c.dom.Element;

import util.CardsUtil;
import util.Debug;
import util.StringUtil;

import static game.CardsUtilKt.extractCards;

public abstract class Bid 
{
	protected static final String XML_DELIM_CHAR = ";";
	private static final String BID_XML_VERSION = "1";
	
	private Player player = null; //The player who made the bid
	private String cardToReveal = "";
	private boolean blind = false;
	
	public abstract boolean higherThan(Bid bid);
	public abstract boolean isOverAchievementThreshold();
	public abstract boolean isPerfect(List<String> cards, GameSettings settings);
	public abstract boolean isOverbid(List<String> cards, int jokerValue);
	
	/**
	 *  Used for:
	 *  - Saving all bids to a saved game/replay
	 *  - Serializiing bids for client/server communication
	 */
	public abstract String getXmlStringPrefix();
	public abstract String toXmlStringSpecific();
	public abstract void populateFromXmlStringSpecific(ArrayList<String> toks, boolean moons, boolean stars);
	public abstract String toHtmlStringSpecific();
	
	public abstract String toStringSpecific();
	
	/**
	 * Used for the API. More user-friendly form of a bid, without unnecessary "fluff" (e.g. whether it was blind and
	 * info on the player such as the player's colour)
	 */
	public abstract void populateXmlTag(Element bidElement);
	
	public String toXmlString()
	{
		String xmlStr = BID_XML_VERSION;
		xmlStr += XML_DELIM_CHAR;
		xmlStr += getXmlStringPrefix();
		xmlStr += XML_DELIM_CHAR;
		
		if (blind)
		{
			xmlStr += "B";
		}
		else
		{
			xmlStr += "N";
		}
		
		xmlStr += XML_DELIM_CHAR;
		xmlStr += player.getPlayerNumber();
		xmlStr += XML_DELIM_CHAR;
		xmlStr += player.getName();
		xmlStr += XML_DELIM_CHAR;
		xmlStr += player.getColour();
		xmlStr += XML_DELIM_CHAR;
		xmlStr += cardToReveal;
		
		String specificXml = toXmlStringSpecific();
		if (!specificXml.isEmpty())
		{
			xmlStr += XML_DELIM_CHAR;
			xmlStr += specificXml;
		}
		
		return xmlStr;
	}

	public boolean isOverbid(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber, int jokerValue) {
		return isOverbid(extractCards(hmHandByPlayerNumber), jokerValue);
	}
	
	public boolean isPerfect(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber, GameSettings settings)
	{
		return isPerfect(extractCards(hmHandByPlayerNumber), settings);
	}
	
	public boolean isChallenge()
	{
		return (this instanceof ChallengeBid);
	}
	public boolean isIllegal()
	{
		return (this instanceof IllegalBid);
	}
	
	public boolean hasLeft()
	{
		return (this instanceof LeftBid);
	}
	public Player getPlayer()
	{
		return player;
	}
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	public String getCardToReveal()
	{
		return cardToReveal;
	}
	public void setCardToReveal(String cardToReveal)
	{
		this.cardToReveal = cardToReveal;
	}
	public boolean isBlind()
	{
		return blind;
	}
	public void setBlind(boolean blind)
	{
		this.blind = blind;
	}
	
	public static Bid factoryFromXmlString(String xmlStr, boolean includeMoons, boolean includeStars)
	{
		ArrayList<String> toks = StringUtil.getListFromDelims(xmlStr, XML_DELIM_CHAR);
		String version = toks.remove(0);
		if (!version.equals(BID_XML_VERSION))
		{
			Debug.stackTrace("Factorying bid from XML that isn't on current version ( " + BID_XML_VERSION + "). Bid: " + xmlStr);
			return null;
		}
		
		//Get the other generic variables...
		String prefix = toks.remove(0);
		String blindStr = toks.remove(0);
		String playerNoStr = toks.remove(0);
		String playerName = toks.remove(0);
		String colour = toks.remove(0);
		String cardToReveal = toks.remove(0);
		
		int playerNo = Integer.parseInt(playerNoStr);
		Player player = new Player(playerNo, colour);
		player.setName(playerName);
		
		boolean blind = blindStr.equals("B");
		
		Bid bid = getEmptyBidBasedOnPrefix(prefix);
		bid.setPlayer(player);
		bid.setBlind(blind);
		bid.setCardToReveal(cardToReveal);
		
		bid.populateFromXmlStringSpecific(toks, includeMoons, includeStars);
		return bid;
	}
	private static Bid getEmptyBidBasedOnPrefix(String prefix)
	{
		Bid[] bids = {new EntropyBid(), new VectropyBid(), new ChallengeBid(), new IllegalBid(), new LeftBid()};
		for (int i=0; i<bids.length; i++)
		{
			String bidPrefix = bids[i].getXmlStringPrefix();
			if (bidPrefix.equals(prefix))
			{
				return bids[i];
			}
		}
		
		Debug.stackTrace("Unknown bid prefix: " + prefix);
		return null;
	}
	
	@Override
	public String toString()
	{
		String s = toStringSpecific();
		if (!cardToReveal.isEmpty())
		{
			s += " (Shows: " + cardToReveal + ")";
		}
		
		return s;
	}
	
	public String toHtmlString()
	{
		String playerName = player.getName();
		playerName = StringUtil.escapeHtml(playerName);

		String colour = player.getColour();
		String playerNamePrefix = playerName + ":&nbsp";

		if (blind)
		{
			playerNamePrefix = "[" + playerName + "]:&nbsp";
		}

		String text = "<html><b><font color=\"" + colour + "\">" + playerNamePrefix;
		text += "</b></font>";
		text += toHtmlStringSpecific();

		if (!cardToReveal.isEmpty()
		  && !isChallenge()
		  && !isIllegal())
		{
			text += "<i><font color=\"#5C5C3D\">&emsp(Shows:&nbsp</i></font>";
			text += CardsUtil.getCardHtml(cardToReveal);
			text += "<i><font color=\"#5C5C3D\">)</i></font>";
		}

		//The unicode for a moon doesn't work in HTML. Also shrink to match the size of the other suits.
		text = text.replaceAll(CardsUtil.MOONS_SYMBOL, "<font size=\"2\">&#127769</font>");
		return text;
	}
}
