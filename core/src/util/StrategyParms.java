package util;

import java.io.IOException;
import java.util.ArrayList;

import object.Bid;
import object.EntropyBid;
import object.Player;
import object.VectropyBid;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class StrategyParms
{
	private int gameMode = -1;
	private int playerCards = -1;
	private int opponentOneCards = -1; 
	private int opponentTwoCards = -1;
	private int opponentThreeCards = -1; 
	private int jokerQuantity = -1;
	private int jokerValue = -1;
	private boolean includeMoons = false;
	private boolean includeStars = false;
	private boolean negativeJacks = false;
	private boolean cardReveal = false;
	private Bid lastBid = null;
	private boolean logging = true;
	private ArrayList<String> cardsOnShowFromOpponents = new ArrayList<>();
	
	/**
	 * Helpers
	 */
	public int getTotalNumberOfCards()
	{
		return opponentOneCards + opponentTwoCards + opponentThreeCards + playerCards;
	}
	
	/**
	 * Gets / sets
	 */
	public int getPlayerCards()
	{
		return playerCards;
	}
	public void setPlayerCards(int playerCards)
	{
		this.playerCards = playerCards;
	}
	public int getOpponentOneCards()
	{
		return opponentOneCards;
	}
	public void setOpponentOneCards(int opponentOneCards)
	{
		this.opponentOneCards = opponentOneCards;
	}
	public int getOpponentTwoCards()
	{
		return opponentTwoCards;
	}
	public void setOpponentTwoCards(int opponentTwoCards)
	{
		this.opponentTwoCards = opponentTwoCards;
	}
	public int getOpponentThreeCards()
	{
		return opponentThreeCards;
	}
	public void setOpponentThreeCards(int opponentThreeCards)
	{
		this.opponentThreeCards = opponentThreeCards;
	}
	public int getJokerQuantity()
	{
		return jokerQuantity;
	}
	public void setJokerQuantity(int jokerQuantity)
	{
		this.jokerQuantity = jokerQuantity;
	}
	public int getJokerValue()
	{
		return jokerValue;
	}
	public void setJokerValue(int jokerValue)
	{
		this.jokerValue = jokerValue;
	}
	public boolean getIncludeMoons()
	{
		return includeMoons;
	}
	public void setIncludeMoons(boolean includeMoons)
	{
		this.includeMoons = includeMoons;
	}
	public boolean getIncludeStars()
	{
		return includeStars;
	}
	public void setIncludeStars(boolean includeStars)
	{
		this.includeStars = includeStars;
	}
	public boolean getNegativeJacks()
	{
		return negativeJacks;
	}
	public void setNegativeJacks(boolean negativeJacks)
	{
		this.negativeJacks = negativeJacks;
	}
	public boolean getCardReveal()
	{
		return cardReveal;
	}
	public void setCardReveal(boolean cardReveal)
	{
		this.cardReveal = cardReveal;
	}
	public Bid getLastBid()
	{
		return lastBid;
	}
	public void setLastBid(Bid lastBid)
	{
		this.lastBid = lastBid;
	}
	public int getGameMode()
	{
		return gameMode;
	}
	public void setGameMode(int gameMode)
	{
		this.gameMode = gameMode;
	}
	public boolean getLogging()
	{
		return logging;
	}
	public void setLogging(boolean logging)
	{
		this.logging = logging;
	}
	public ArrayList<String> getCardsOnShowFromOpponents()
	{
		return cardsOnShowFromOpponents;
	}
	public void appendCardsOnShowFromOpponent(Player parmsPlayer, Player opponent)
	{
		if (parmsPlayer == opponent)
		{
			return;
		}
		
		ArrayList<String> cardsRevealed = opponent.getRevealedCards();
		cardsOnShowFromOpponents.addAll(cardsRevealed);
	}
	public void setCardsOnShowFromOpponents(ArrayList<String> cardsOnShowFromOpponents)
	{
		this.cardsOnShowFromOpponents = cardsOnShowFromOpponents;
	}
	
	public static StrategyParms factoryFromXml(Element root) throws IOException
	{
		StrategyParms parms = new StrategyParms();
		
		String gameModeDesc = root.getAttribute("GameMode");
		int gameMode = GameConstants.GAME_MODE_VECTROPY;
		if (gameModeDesc.equals("Entropy"))
		{
			gameMode = GameConstants.GAME_MODE_ENTROPY;
		}
		
		parms.setGameMode(gameMode);
		parms.setIncludeMoons(XmlUtil.getAttributeBoolean(root, "IncludeMoons"));
		parms.setIncludeStars(XmlUtil.getAttributeBoolean(root, "IncludeStars"));
		parms.setJokerQuantity(XmlUtil.getAttributeInt(root, "JokerQuantity"));
		parms.setJokerValue(XmlUtil.getAttributeInt(root, "JokerValue"));
		parms.setNegativeJacks(XmlUtil.getAttributeBoolean(root, "NegativeJacks"));
		parms.setCardReveal(XmlUtil.getAttributeBoolean(root, "ShowCards"));
		
		ArrayList<String> opponentCardsOnShow = XmlUtil.getListFromElement(root, "OpponentCardsOnShow", "Card");
		parms.setCardsOnShowFromOpponents(opponentCardsOnShow);
		
		parms.setPlayerCards(XmlUtil.getAttributeInt(root, "TotalCards"));
		parms.setOpponentOneCards(0);
		parms.setOpponentTwoCards(0);
		parms.setOpponentThreeCards(0);

		NodeList bids = root.getElementsByTagName("LastBid");
		if (bids.getLength() > 0)
		{
			Element bidElement = (Element)bids.item(0);
			Bid lastBid = factoryBidFromXmlTag(bidElement, parms);
			parms.setLastBid(lastBid);
		}
		
		return parms;
	}
	
	private static Bid factoryBidFromXmlTag(Element bidElement, StrategyParms parms) throws IOException
	{
		int gameMode = parms.getGameMode();
		if (gameMode == GameConstants.GAME_MODE_ENTROPY)
		{
			return EntropyBid.factoryFromXmlTag(bidElement);
		}
		else
		{
			return VectropyBid.factoryFromXmlTag(bidElement, parms.getIncludeMoons(), parms.getIncludeStars());
		}
	}
}
