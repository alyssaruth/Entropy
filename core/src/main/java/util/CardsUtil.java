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
import game.Suit;
import object.SuitWrapper;

public class CardsUtil
{
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
	
	public static String getCardHtml(String card)
	{
		card = card.replaceAll("-", "");

		for (Suit s : Suit.getEntries()) {
			if (card.contains("" + s.getLetter())) {
				card =  card.replace("" + s.getLetter(), s.getUnicodeStr());
				return "<font color=\"" + s.getColourHex() + "\" face=\"Segoe UI Symbol\">" + card + "</font>";
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
