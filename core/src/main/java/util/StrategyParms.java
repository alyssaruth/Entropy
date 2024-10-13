package util;

import game.GameMode;
import object.Bid;
import object.Player;

import java.util.ArrayList;

public class StrategyParms
{
	private GameMode gameMode = null;
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
	public GameMode getGameMode()
	{
		return gameMode;
	}
	public void setGameMode(GameMode gameMode)
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
}
