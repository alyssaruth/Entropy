package util;

import game.GameMode;
import object.Bid;
import object.ChallengeBid;
import object.EntropyBid;
import object.IllegalBid;
import object.LeftBid;
import object.Player;
import object.VectropyBid;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static utils.CoreGlobals.logger;

public class ReplayConverterVersion0
{
	public static boolean convertReplay(Element root)
	{
		boolean success = false;
		
		try
		{
			int gameMode = XmlUtil.getAttributeInt(root, ReplayFileUtil.XML_REPLAY_INT_GAME_MODE);
			boolean includeMoons = XmlUtil.getAttributeBoolean(root, ReplayFileUtil.XML_REPLAY_BOOLEAN_INCLUDE_MOONS);
			boolean includeStars = XmlUtil.getAttributeBoolean(root, ReplayFileUtil.XML_REPLAY_BOOLEAN_INCLUDE_STARS);
			
			NodeList roundElements = root.getElementsByTagName(ReplayFileUtil.REPLAY_ELEMENT_ROUND);
			for (int i=0; i<roundElements.getLength(); i++)
			{
				Element roundElement = (Element)roundElements.item(i);
				convertRound(roundElement, gameMode, includeMoons, includeStars);
			}
			
			//set the updated version
			
			success = true;
		}
		catch (Throwable t)
		{
			//We're going to email logs at the end of the conversion anyway
			logger.error("conversionFailed", "Failed to convert replay");
		}
		
		return success;
	}
	private static void convertRound(Element roundElement, int gameMode, boolean includeMoons, boolean includeStars) throws Throwable
	{
		NodeList listmodelElements = roundElement.getElementsByTagName(ReplayFileUtil.ROUND_ELEMENT_BID_HISTORY);
		Element bidHistoryElement = (Element)listmodelElements.item(0);
		
		int size = XmlUtil.getAttributeInt(bidHistoryElement, ReplayFileUtil.BID_HISTORY_INT_HISTORY_SIZE);
		for (int i=0; i<size; i++)
		{
			String historyStr = bidHistoryElement.getAttribute(ReplayFileUtil.BID_HISTORY_STRING_HISTORY + i);
			String convertedBidStr = convertBidStr(historyStr, gameMode, includeMoons, includeStars);
			bidHistoryElement.setAttribute(ReplayFileUtil.BID_HISTORY_STRING_HISTORY + i, convertedBidStr);
		}
	}
	private static String convertBidStr(String bidStr, int gameMode, boolean includeMoons, boolean includeStars) throws Throwable
	{
		try
		{
			boolean entropy = ReplayConstants.toGameMode(gameMode) == GameMode.Entropy;
			
			int colorTagIndex = bidStr.indexOf("\"");
			int closingQuoteIndex = bidStr.indexOf("\"", colorTagIndex + 1);
			String color = bidStr.substring(colorTagIndex + 1, closingQuoteIndex);
			
			int endOfFontTagIndex = bidStr.indexOf(">", closingQuoteIndex);
			bidStr = bidStr.substring(endOfFontTagIndex+1);
			
			boolean blind = false;
			if (bidStr.startsWith("("))
			{
				blind = true;
				bidStr = bidStr.substring(1);
			}
			
			int colonIndex = bidStr.indexOf(":");
			if (colonIndex == -1)
			{
				//Could be "Alex left" or "Alex challenged", use the space index in this case
				colonIndex = bidStr.indexOf(" ");
			}
			
			String name = bidStr.substring(0, colonIndex);
			
			Player player = new Player(-1, color);
			player.setName(name);
			
			bidStr = bidStr.substring(colonIndex + 1);
			bidStr = bidStr.trim();
			
			Bid bid = getBidFromString(bidStr, entropy, includeMoons, includeStars);
			bid.setPlayer(player);
			bid.setBlind(blind);
			
			return bid.toXmlString();
		}
		catch (Throwable t)
		{
			Debug.append("Caught exception for bidStr " + bidStr);
			throw t;
		}
	}
	private static Bid getBidFromString(String bidStr, boolean entropy, boolean includeMoons, boolean includeStars)
	{
		if (bidStr.toLowerCase().contains("challenge"))
		{
			return new ChallengeBid();
		}
		
		if (bidStr.toLowerCase().contains("illegal"))
		{
			return new IllegalBid();
		}
		
		if (bidStr.toLowerCase().contains("illlegal"))
		{
			Debug.append("Found typo, will convert anyway: " + bidStr);
			return new IllegalBid();
		}
		
		if (bidStr.toLowerCase().contains("left"))
		{
			return new LeftBid();
		}
		
		if (entropy)
		{
			//Entropy bid
			int spaceIndex = bidStr.indexOf(" ");
			int bidAmount = Integer.parseInt(bidStr.substring(0, spaceIndex));
			
			int bracketIndex = bidStr.indexOf("<");
			String suitStr = bidStr.substring(spaceIndex+1, bracketIndex);
			
			int suitCode = CardsUtil.getSuitCodeForSuitDesc(suitStr);
			
			return new EntropyBid(suitCode, bidAmount);
		}
		else
		{
			//Vectropy bid
			int bracketIndex = bidStr.indexOf("(");
			int commaIndex = bidStr.indexOf(",", bracketIndex);
			int clubs = Integer.parseInt(bidStr.substring(bracketIndex+1, commaIndex));
			
			int oldCommaIndex = commaIndex;
			commaIndex = bidStr.indexOf(",", commaIndex + 1);
			int diamonds = Integer.parseInt(bidStr.substring(oldCommaIndex+2, commaIndex));
			
			oldCommaIndex = commaIndex;
			commaIndex = bidStr.indexOf(",", commaIndex + 1);
			int hearts = Integer.parseInt(bidStr.substring(oldCommaIndex+2, commaIndex));
			
			oldCommaIndex = commaIndex;
			commaIndex = bidStr.indexOf(",", commaIndex + 1);
			
			int moons = 0;
			if (includeMoons)
			{
				moons = Integer.parseInt(bidStr.substring(oldCommaIndex+2, commaIndex));
				oldCommaIndex = commaIndex;
				commaIndex = bidStr.indexOf(",", commaIndex + 1);
			}
			
			if (!includeStars)
			{
				commaIndex = bidStr.indexOf(")", commaIndex + 1);
			}
			
			int spades = Integer.parseInt(bidStr.substring(oldCommaIndex+2, commaIndex));
			oldCommaIndex = commaIndex;
			commaIndex = bidStr.indexOf(")", commaIndex + 1);
			
			int stars = 0;
			if (includeStars)
			{
				stars = Integer.parseInt(bidStr.substring(oldCommaIndex+2, commaIndex));
			}
			
			return new VectropyBid(clubs, diamonds, hearts, moons, spades, stars, includeMoons, includeStars);
		}
	}
}
