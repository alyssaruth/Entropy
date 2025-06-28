package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import game.GameSettings;
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
	
	/**
	 * Overloading to allow the seed to be passed through from the Server, allowing this to be secure.
	 * http://www.datamation.com/entdev/article.php/11070_616221_3/How-We-Learned-to-Cheat-at-Online-Poker-A-Study-in-Software-Security.htm
	 */
	public static List<String> createAndShuffleDeck(GameSettings settings)
	{
		return createAndShuffleDeck(settings, -1);
	}
	public static List<String> createAndShuffleDeck(GameSettings settings, long seed)
	{
		// Creating the pack of cards
		List<String> list = new ArrayList<>();
		ArrayList<String> suits = new ArrayList<>();
		suits.add("c");
		suits.add("d");
		suits.add("h");
		suits.add("s");
		
		if (settings.getIncludeMoons())
		{
			suits.add("m");
		}
		
		if (settings.getIncludeStars())
		{
			suits.add("x");
		}
		
		String jackStr = "J";
		if (settings.getNegativeJacks())
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

		for (int i=0; i<settings.getJokerQuantity(); i++)
		{
			list.add("Jo" + i); //i.e. Joc, Jod, Joh, Jos, but more generic
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
	
	public static boolean containsNonJoker(List<String> cards)
	{
		int size = cards.size();
		for (int i=0; i<size; i++)
		{
			String card = cards.get(i);
			if (!card.contains("Jo"))
			{
				return true;
			}
		}
		
		return false;
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

	public static String getSuitSymbolForCode(int suitCode)
	{
		SuitWrapper wrapper = getSuitWrapperHashMap().get(suitCode);
		return wrapper.getUnicodeSymbol();
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
}
