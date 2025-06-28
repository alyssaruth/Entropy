package util;

import java.util.*;

import game.Suit;
import object.VectropyBid;

import static game.CardsUtilKt.countSuit;

public class VectropyUtil 
{
	public static String getResult(List<String> cards, int jokerValue, Suit suit, boolean includeMoons, boolean includeStars)
	{
		String clubs = "" + countSuit(Suit.Clubs, cards, jokerValue);
		String diamonds = "" + countSuit(Suit.Diamonds, cards, jokerValue);
		String hearts = "" + countSuit(Suit.Hearts, cards, jokerValue);
		String moons = "" + countSuit(Suit.Moons, cards, jokerValue);
		String spades = "" + countSuit(Suit.Spades, cards, jokerValue);
		String stars = "" + countSuit(Suit.Stars, cards, jokerValue);

		if (suit != null) {
			switch (suit)
			{
				case Clubs:
					clubs = "<b>" + clubs + "</b>";
					break;
				case Diamonds:
					diamonds = "<b>" + diamonds + "</b>";
					break;
				case Hearts:
					hearts = "<b>" + hearts + "</b>";
					break;
				case Moons:
					moons = "<b>" + moons + "</b>";
					break;
				case Spades:
					spades = "<b>" + spades + "</b>";
					break;
				case Stars:
					stars = "<b>" + stars + "</b>";
					break;
				default:
					break;
			}
		}

		
		String clubsColour = Suit.Clubs.getColourHex();
		String diamondsColour = Suit.Diamonds.getColourHex();
		String moonsColour = Suit.Moons.getColourHex();
		
		String resultStr = "(<font color=\"" + clubsColour + "\">" + clubs + "</font>, <font color=\"" + diamondsColour + "\">" + diamonds + "</font>, <font color=\"red\">" + hearts + "</font>, ";
		if (includeMoons)
		{
			resultStr += "<font color=\"" + moonsColour + "\">" + moons + "</font>, ";
		}
		
		resultStr += spades;
		if (includeStars)
		{
			resultStr += ", <font color=\"" + Suit.Stars.getColourHex() + "\">" + stars + "</font>";
		}
		
		resultStr += ")";
		
		return resultStr;
	}
	
	public static VectropyBid getEmptyBid(boolean includeMoons, boolean includeStars)
	{
		return new VectropyBid(0, 0, 0, 0, 0, 0, includeMoons, includeStars);
	}
}
