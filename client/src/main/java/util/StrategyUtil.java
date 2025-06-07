package util;

import game.GameSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static util.CardsUtil.*;

public class StrategyUtil 
{
	public static int getRandomSuit(boolean includeMoons, boolean includeStars)
	{
		Random rand = new Random();
		
		if (includeStars && includeMoons)
		{
			return rand.nextInt(6);
		}
		else if (includeMoons)
		{
			return rand.nextInt(5);
		}
		else if (includeStars)
		{
			int suit = rand.nextInt(5);
			if (suit >= 3)
			{
				suit++;
			}
			
			return suit;
		}
		else
		{
			int suit = rand.nextInt(4);
			if (suit == 3)
			{
				suit++;
			}
			
			return suit;
		}
	}

	public static HashMap<Integer, Double> getEvBySuitHashMapIncludingMyHand(List<String> hand, StrategyParams parms)
	{
		double evClubs = getExpectedValueForSuitIncludingMyHand(hand, SUIT_CLUBS, parms);
		double evDiamonds = getExpectedValueForSuitIncludingMyHand(hand, SUIT_DIAMONDS, parms);
		double evHearts = getExpectedValueForSuitIncludingMyHand(hand, SUIT_HEARTS, parms);
		double evMoons = getExpectedValueForSuitIncludingMyHand(hand, SUIT_MOONS, parms);
		double evSpades = getExpectedValueForSuitIncludingMyHand(hand, SUIT_SPADES, parms);
		double evStars = getExpectedValueForSuitIncludingMyHand(hand, SUIT_STARS, parms);

		HashMap<Integer, Double> hmEvBySuit = new HashMap<>();
		hmEvBySuit.put(0, evClubs);
		hmEvBySuit.put(1, evDiamonds);
		hmEvBySuit.put(2, evHearts);
		hmEvBySuit.put(3, evMoons);
		hmEvBySuit.put(4, evSpades);
		hmEvBySuit.put(5, evStars);

		return hmEvBySuit;
	}

	private static double getExpectedValueForSuitIncludingMyHand(List<String> hand, int suitCode, StrategyParams parms)
	{
		double ev = 0;

		var settings = parms.getSettings();
		int jokerValue = settings.getJokerValue();

		List<String> deck = createAndShuffleDeck(settings);

		int handSize = hand.size();
		int totalCardsNotIncludingMine = parms.getCardsInPlay() - handSize;

		//go through the deck and take out the cards that we can see
		for (int i=0; i<handSize; i++)
		{
			String handCard = hand.get(i);
			int deckSize = deck.size();

			for (int j=0; j<deckSize; j++)
			{
				String deckCard = deck.get(j);

				if (deckCard.equals(handCard))
				{
					deck.remove(deckCard);
					break; //break to avoid indexOutOfBounds, and to only remove one (e.g. jokers).
				}
			}
		}

		int remainingCards = deck.size();

		for (int i=0; i<remainingCards; i++)
		{
			ArrayList<String> cardToCheck = new ArrayList<>();
			cardToCheck.add(deck.get(i));
			ev += countSuit(cardToCheck, suitCode, jokerValue);
		}

		return countSuit(hand, suitCode, jokerValue) + (ev * totalCardsNotIncludingMine)/remainingCards;
	}
}
