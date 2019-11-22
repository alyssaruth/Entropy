package object;

import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Wrapper class for a player
 */
public class Player
{
	private String name = null;
	private int numberOfCards = -1;
	private int cardsToSubtract = 0;
	private boolean enabled = false;
	private String strategy = null;
	private String[] hand = null;
	private ArrayList<String> revealedCards = new ArrayList<>();
	
	private int playerNumber = -1;
	private String colour = "";
	
	public Player(int playerNumber, String colour)
	{
		this.playerNumber = playerNumber;
		this.colour = colour;
	}
	
	public void saveHandToRegistry(Preferences replay, String replayStr)
	{
		for (int i=0; i<numberOfCards; i++)
		{
			replay.put(replayStr + i, hand[i]);
		}
	}
	
	public void saveRevealedCardsToRegistry(Preferences savedGame, String registryStr)
	{
		int size = revealedCards.size();
		for (int i=0; i<size; i++)
		{
			String card = revealedCards.get(i);
			savedGame.put(registryStr + i, card);
		}
	}
	
	public void populateHandFromRegistry(Preferences registry, String handStr, String revealedCardStr, 
	  String numberOfCardsStr)
	{
		numberOfCards = registry.getInt(numberOfCardsStr, 0);
		hand = new String[numberOfCards];
		
		for (int i = 0; i < numberOfCards; i++)
		{
			hand[i] = registry.get(handStr + i, "");
			
			String revealedCard = registry.get(revealedCardStr + i, "");
			if (!revealedCard.isEmpty())
			{
				revealedCards.add(revealedCard);
			}
		}
	}
	
	public int getActualNumberOfCards()
	{
		return numberOfCards - cardsToSubtract;
	}
	
	public void doSubtraction()
	{
		numberOfCards = numberOfCards - cardsToSubtract;
		cardsToSubtract = 0;
	}
	
	public void resetHand()
	{
		hand = new String[numberOfCards];
		revealedCards = new ArrayList<>();
	}
	
	public boolean disable()
	{
		if (numberOfCards == 0 && enabled)
		{
			enabled = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Helper method. When revealing cards is turned on, players must reveal at each bid until they only have
	 * one card left that isn't showing.
	 */
	public boolean hasMoreCardsToReveal()
	{
		return revealedCards.size() < numberOfCards - 1;
	}
	public boolean handContainsCard(String card)
	{
		for (int i=0; i<hand.length; i++)
		{
			String handCard = hand[i];
			if (handCard.equals(card))
			{
				return true;
			}
		}
		
		return false;
	}
	public ArrayList<String> getCardsNotOnShow()
	{
		ArrayList<String> ret = new ArrayList<>();
		for (int i=0; i<hand.length; i++)
		{
			String card = hand[i];
			if (!revealedCards.contains(card))
			{
				ret.add(card);
			}
		}
		
		return ret;
	}
	public void addRevealedCard(String card)
	{
		revealedCards.add(card);
	}
	public void setRevealedCards(ArrayList<String> revealedCards)
	{
		this.revealedCards = revealedCards;
	}
	
	public boolean isApiStrategy()
	{
		return strategy.startsWith("API");
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getNumberOfCards()
	{
		return numberOfCards;
	}
	public void setNumberOfCards(int numberOfCards)
	{
		this.numberOfCards = numberOfCards;
	}
	public int getCardsToSubtract()
	{
		return cardsToSubtract;
	}
	public void setCardsToSubtract(int cardsToSubtract)
	{
		this.cardsToSubtract = cardsToSubtract;
	}
	public boolean isEnabled()
	{
		return enabled;
	}
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	public String getStrategy()
	{
		return strategy;
	}
	public void setStrategy(String strategy)
	{
		this.strategy = strategy;
	}
	public String[] getHand()
	{
		return hand;
	}
	public void setHand(String[] hand)
	{
		numberOfCards = hand.length;
		this.hand = hand;
	}
	public String getColour()
	{
		return colour;
	}
	public int getPlayerNumber()
	{
		return playerNumber;
	}
	public ArrayList<String> getRevealedCards()
	{
		return revealedCards;
	}
	
	@Override
	public String toString()
	{
		return name + " ("  + playerNumber + ")";
	}
}
