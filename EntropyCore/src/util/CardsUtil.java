package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import object.SuitWrapper;

public class CardsUtil
{
	public static final int SUIT_CLUBS = 0;
	public static final int SUIT_DIAMONDS = 1;
	public static final int SUIT_HEARTS = 2;
	public static final int SUIT_MOONS = 3;
	public static final int SUIT_SPADES = 4;
	public static final int SUIT_STARS = 5;
	
	public static final String CLUBS_SYMBOL = "\u2663";
	public static final String DIAMONDS_SYMBOL = "\u2666";
	public static final String HEARTS_SYMBOL = "\u2665";
	public static final String MOONS_SYMBOL = "\uD83C\uDF19";
	public static final String SPADES_SYMBOL = "\u2660";
	public static final String STARS_SYMBOL = "\u2605";
	
	private static HashMap<Integer, SuitWrapper> hmSuitCodeToWrapper = null;
	
	public static boolean isRelevant(String card, int suit)
	{
		switch(suit)
		{
		case -1:
			return true;
		case SUIT_CLUBS: 
			return isClub(card);
		case SUIT_DIAMONDS:
			return isDiamond(card);
		case SUIT_HEARTS:
			return isHeart(card);
		case SUIT_MOONS:
			return isMoon(card);
		case SUIT_SPADES:
			return isSpade(card);
		case SUIT_STARS:
			return isStar(card);
		default:
			Debug.stackTrace("Unexpected relevant suit: " + suit);
			return true;
		}
	}

	public static int countSuit(String hand[], int suit, int jokerValue)
	{
		int count = 0;
		if (suit == SUIT_CLUBS)
		{
			count = countClubs(hand, jokerValue);
		}
		else if (suit == SUIT_DIAMONDS)
		{
			count = countDiamonds(hand, jokerValue);
		}
		else if (suit == SUIT_HEARTS)
		{
			count = countHearts(hand, jokerValue);
		}
		else if (suit == SUIT_MOONS)
		{
			count = countMoons(hand, jokerValue);
		}
		else if (suit == SUIT_SPADES)
		{
			count = countSpades(hand, jokerValue);
		}
		else if (suit == SUIT_STARS)
		{
			count = countStars(hand, jokerValue);
		}
		else
		{
			Debug.stackTrace("Trying to count unexpected suit: " + suit);
		}
	
		return count;
	}
	
	/**
	 * Overloading to allow the seed to be passed through from the Server, allowing this to be secure.
	 * http://www.datamation.com/entdev/article.php/11070_616221_3/How-We-Learned-to-Cheat-at-Online-Poker-A-Study-in-Software-Security.htm
	 */
	public static List<String> createAndShuffleDeck(boolean includeJokers, int jokerQuantity, 
	  boolean includeMoons, boolean includeStars, boolean negativeJacks)
	{
		return createAndShuffleDeck(includeJokers, jokerQuantity, includeMoons, includeStars, negativeJacks, -1);
	}
	public static List<String> createAndShuffleDeck(boolean includeJokers, int jokerQuantity, 
	 boolean includeMoons, boolean includeStars, boolean negativeJacks, long seed)
	{
		// Creating the pack of cards
		List<String> list = new ArrayList<>();
		ArrayList<String> suits = new ArrayList<>();
		suits.add("c");
		suits.add("d");
		suits.add("h");
		suits.add("s");
		
		if (includeMoons)
		{
			suits.add("m");
		}
		
		if (includeStars)
		{
			suits.add("x");
		}
		
		String jackStr = "J";
		if (negativeJacks)
		{
			jackStr = "-J";
		}
		
		String ranks[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", jackStr, "Q", "K"};
		int size = suits.size();
		int rankSize = ranks.length;
		for (int rank=0; rank<rankSize; rank++)
		{
			for (int suit = 0; suit < size; suit++)
			{
				String card = ranks[rank]+suits.get(suit);
				list.add(card);
			}
		}
		
		if (includeJokers)
		{
			for (int i=0; i<jokerQuantity; i++)
			{
				list.add("Jo" + i); //i.e. Joc, Jod, Joh, Jos, but more generic
			}
		}

		//Shuffle the pack using Fisher-Yates.
		if (seed > -1)
		{
			Random rand = new Random(seed);
			Collections.shuffle(list, rand);
		}
		else
		{
			Collections.shuffle(list);
		}
	
		return list;
	}
	
	public static HashMap<Integer, Double> getEvBySuitHashMapIncludingMyHand(String[] hand, StrategyParms parms)
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

	private static double getExpectedValueForSuitIncludingMyHand(String[] hand, int suitCode, StrategyParms parms)
	{
		double ev = 0;
		
		int jokerQuantity = parms.getJokerQuantity();
		int jokerValue = parms.getJokerValue();
		boolean includeMoons = parms.getIncludeMoons();
		boolean includeStars = parms.getIncludeStars();
		boolean includeJokers = jokerQuantity > 0;
		boolean negativeJacks = parms.getNegativeJacks();
		
		List<String> deck = createAndShuffleDeck(includeJokers, jokerQuantity, includeMoons, includeStars, negativeJacks);
		
		int handSize = hand.length;
		int totalCardsNotIncludingMine = parms.getTotalNumberOfCards() - handSize;
		
		//go through the deck and take out the cards that we can see
		for (int i=0; i<handSize; i++)
		{
			String handCard = hand[i];
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
			String[] cardToCheck = {deck.get(i)};
			ev += countSuit(cardToCheck, suitCode, jokerValue);
		}
		
		return countSuit(hand, suitCode, jokerValue) + (ev * totalCardsNotIncludingMine)/remainingCards;
	}

	public static int countClubs(String[] hand, int jokerValue)
	{
		return actuallyCountSuit("c", hand, jokerValue);
	}
	public static int countDiamonds(String[] hand, int jokerValue)
	{
		return actuallyCountSuit("d", hand, jokerValue);
	}
	public static int countHearts(String[] hand, int jokerValue)
	{
		return actuallyCountSuit("h", hand, jokerValue);
	}
	public static int countSpades(String[] hand, int jokerValue)
	{
		return actuallyCountSuit("s", hand, jokerValue);
	}
	public static int countMoons(String[] hand, int jokerValue)
	{
		return actuallyCountSuit("m", hand, jokerValue);
	}
	public static int countStars(String[] hand, int jokerValue)
	{
		return actuallyCountSuit("x", hand, jokerValue);
	}
	
	private static int actuallyCountSuit(String suit, String[] hand, int jokerValue)
	{
		if (hand == null)
		{
			return 0;
		}
		
		int count = 0;
		for (int i=0; i<hand.length; i++)
		{
			if (hand[i].equals("A" + suit))
			{
				count += 2;
			}
			else if (hand[i].contains("A"))
			{
				count++;
			}
			else if (hand[i].startsWith("Jo"))
			{
				count += jokerValue;
			}
			else if (hand[i].equals("-J" + suit))
			{
				count--;
			}
			else if (hand[i].contains(suit))
			{
				count++;
			}
		}
		return count;
	}

	public static int countSuit(int suitCode, String[] playerHand, String[] opponentOneHand, 
			String[] opponentTwoHand, String[] opponentThreeHand, int jokerValue)
	{
		return countSuit(playerHand, suitCode, jokerValue) 
			+ countSuit(opponentOneHand, suitCode, jokerValue)
			+ countSuit(opponentTwoHand, suitCode, jokerValue)
			+ countSuit(opponentThreeHand, suitCode, jokerValue);
	}
	
	public static int countSuit(int suitCode, ConcurrentHashMap<Integer, String[]> hmHandByPlayerNumber, int jokerValue)
	{
		int total = 0;
		for (int i=0; i<4; i++)
		{
			String[] hand = hmHandByPlayerNumber.get(i);
			if (hand != null)
			{
				total += countSuit(hand, suitCode, jokerValue);
			}
		}
		
		return total;
	}

	private static boolean isClub(String card)
	{
		if (card.contains("c"))
		{
			return true;
		}
		
		return isCardAceOrJoker(card);
	}

	private static boolean isHeart(String card)
	{
		if (card.contains("h"))
		{
			return true;
		}
		
		return isCardAceOrJoker(card);
	}

	private static boolean isDiamond(String card)
	{
		if (card.contains("d"))
		{
			return true;
		}
		return isCardAceOrJoker(card);
	}

	private static boolean isSpade(String card)
	{
		if (card.contains("s"))
		{
			return true;
		}
		return isCardAceOrJoker(card);
	}
	
	private static boolean isMoon(String card)
	{
		if (card.contains("m"))
		{
			return true;
		}
		return isCardAceOrJoker(card);
	}
	
	private static boolean isStar(String card)
	{
		if (card.contains("x"))
		{
			return true;
		}
		return isCardAceOrJoker(card);
	}
	
	private static boolean isCardAceOrJoker(String card)
	{
		if (card.contains("A"))
		{
			return true;
		}

		if (card.startsWith("Jo"))
		{
			return true;
		}
		
		return false;
	}
	
	public static double getUnseenCards(int opponentNumber, int zeroCards, int oneCards, int twoCards, int threeCards)
	{
		int total = zeroCards + oneCards + twoCards + threeCards;
		
		switch (opponentNumber)
		{
			case 0:
				return total - zeroCards;
			case 1:
				return total - oneCards;
			case 2:
				return total - twoCards;
			case 3:
				return total - threeCards;
			default:
				Debug.stackTrace("Unexpected opponentNumber [" + opponentNumber + "]");
		}
		
		return 0;
	}
	
	public static ArrayList<Integer> getSuitCodesVector(boolean includeMoons, boolean includeStars)
	{
		ArrayList<Integer> suits = new ArrayList<>();
		
		suits.add(Integer.valueOf(SUIT_CLUBS));
		suits.add(Integer.valueOf(SUIT_DIAMONDS));
		suits.add(Integer.valueOf(SUIT_HEARTS));
		
		if (includeMoons)
		{
			suits.add(Integer.valueOf(SUIT_MOONS));
		}
		
		suits.add(Integer.valueOf(SUIT_SPADES));
		
		if (includeStars)
		{
			suits.add(Integer.valueOf(SUIT_STARS));
		}
		
		return suits;
	}
	
	public static boolean containsNonJoker(String[] cards)
	{
		int size = cards.length;
		for (int i=0; i<size; i++)
		{
			String card = cards[i];
			if (!card.contains("Jo"))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static String getHandStr(String[] hand)
	{
		int length = hand.length;
		String handStr = "[";
		for (int i=0; i<length; i++)
		{
			if (i > 0)
			{
				handStr += ", ";
			}
			
			handStr += hand[i];
		}
		
		handStr += "]";
		return handStr;
	}
	
	private static HashMap<Integer, SuitWrapper> getSuitWrapperHashMap()
	{
		if (hmSuitCodeToWrapper != null)
		{
			return hmSuitCodeToWrapper;
		}
		
		HashMap<Integer, SuitWrapper> hm = new HashMap<>();
		
		hm.put(SUIT_CLUBS, new SuitWrapper("black", "green", CLUBS_SYMBOL, "club", "c"));
		hm.put(SUIT_DIAMONDS, new SuitWrapper("red", "blue", DIAMONDS_SYMBOL, "diamond", "d"));
		hm.put(SUIT_HEARTS, new SuitWrapper("red", HEARTS_SYMBOL, "heart", "h"));
		hm.put(SUIT_MOONS, new SuitWrapper("#E6B800", "purple", MOONS_SYMBOL, "moon", "m"));
		hm.put(SUIT_SPADES, new SuitWrapper("black", SPADES_SYMBOL, "spade", "s"));
		hm.put(SUIT_STARS, new SuitWrapper("#E6B800", STARS_SYMBOL, "star", "x"));
		
		hmSuitCodeToWrapper = hm;
		return hmSuitCodeToWrapper;
	}

	public static String getSuitDesc(int bidNumber, int suitCode)
	{
		if (suitCode < SUIT_CLUBS
		  || suitCode > SUIT_STARS)
		{
			return "Invalid";
		}
		
		SuitWrapper wrapper = getSuitWrapperHashMap().get(suitCode);
		String suit = wrapper.getName();
		
		if (bidNumber != 1)
		{
			suit += "s";
		}
	
		return suit;
	}

	public static String getSuitSymbolForCode(int suitCode)
	{
		SuitWrapper wrapper = getSuitWrapperHashMap().get(suitCode);
		return wrapper.getUnicodeSymbol();
	}
	
	public static String getColourForSuitCode(int suitCode)
	{
		SuitWrapper wrapper = getSuitWrapperHashMap().get(suitCode);
		return wrapper.getColour();
	}
	
	public static String getCardHtml(String card)
	{
		card = card.replaceAll("-", "");
		
		HashMap<Integer, SuitWrapper> hm = getSuitWrapperHashMap();
		Iterator<Map.Entry<Integer, SuitWrapper>> it = hm.entrySet().iterator();
		
		for (; it.hasNext(); )
		{
			Map.Entry<Integer, SuitWrapper> entry = it.next();
			SuitWrapper wrapper = entry.getValue();
			String suitShort = wrapper.getSuitShort();
			if (card.contains(suitShort))
			{
				String unicodeSuit = wrapper.getUnicodeSymbol();
				card = card.replaceAll(suitShort, unicodeSuit);
				
				return "<font color=\"" + wrapper.getColour() + "\" face=\"Segoe UI Symbol\">" + card + "</font>";
			}
		}
		
		if (card.contains("Jo"))
		{
			return "<font color=\"#FF33CC\">Jo</font>";
		}
		
		Debug.stackTrace("Couldn't match " + card + " to suit hashmap");
		return null;
	}
	
	public static int getSuitCodeForSuitDesc(String suitDesc)
	{
		suitDesc = suitDesc.toLowerCase();
		HashMap<Integer, SuitWrapper> hm = getSuitWrapperHashMap();
		Iterator<Map.Entry<Integer, SuitWrapper>> it = hm.entrySet().iterator();
		
		for (; it.hasNext(); )
		{
			Map.Entry<Integer, SuitWrapper> entry = it.next();
			SuitWrapper wrapper = entry.getValue();
			String suitShort = wrapper.getName();
			if (suitDesc.contains(suitShort))
			{
				return entry.getKey();
			}
		}
		
		Debug.append("Failed to get suit code for desc " + suitDesc);
		return -1;
	}
}
