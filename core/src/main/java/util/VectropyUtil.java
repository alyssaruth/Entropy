package util;

import java.util.*;

import game.Suit;
import object.VectropyBid;

import static game.CardsUtilKt.countSuit;

public class VectropyUtil 
{
	public static boolean isOverbid(VectropyBid bid, List<String> allCards, int jokerValue)
	{
		int maxClubs = countSuit(Suit.Clubs, allCards, jokerValue);
		int maxDiamonds = countSuit(Suit.Diamonds, allCards, jokerValue);
		int maxHearts = countSuit(Suit.Hearts, allCards, jokerValue);
		int maxMoons = countSuit(Suit.Moons, allCards, jokerValue);
		int maxSpades = countSuit(Suit.Spades, allCards, jokerValue);
		int maxStars = countSuit(Suit.Stars, allCards, jokerValue);
		
		return bid.getClubs() > maxClubs 
			|| bid.getDiamonds() > maxDiamonds 
			|| bid.getHearts() > maxHearts 
			|| bid.getMoons() > maxMoons
			|| bid.getSpades() > maxSpades
			|| bid.getStars() > maxStars;
	}
	
	public static String getResult(List<String> cards, int jokerValue, Suit suit, boolean includeMoons, boolean includeStars)
	{
		String clubs = "" + countSuit(Suit.Clubs, cards, jokerValue);
		String diamonds = "" + countSuit(Suit.Diamonds, cards, jokerValue);
		String hearts = "" + countSuit(Suit.Hearts, cards, jokerValue);
		String moons = "" + countSuit(Suit.Moons, cards, jokerValue);
		String spades = "" + countSuit(Suit.Spades, cards, jokerValue);
		String stars = "" + countSuit(Suit.Stars, cards, jokerValue);
		
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
	
	public static String getReadableString(double[] diffVector)
	{
		int size = diffVector.length;
		String ret = "(";
		for (int i=0; i<size; i++)
		{
			if (i > 0)
			{
				ret += ", ";
			}
			
			String element = "" + diffVector[i];
			int decimalPointIndex = element.indexOf('.');
			
			int endIndex = Math.min(decimalPointIndex + 3, element.length());
			element = element.substring(0, endIndex);
			
			ret += element;
		}
		
		ret += ")";
		return ret;
	}
	
	public static HashMap<Suit, Integer> getDifferenceMap(VectropyBid bid, List<String> hand, int jokerValue,
											boolean includeMoons, boolean includeStars)
	{
		var suits = Suit.filter(includeMoons, includeStars);
		var map = new HashMap<Suit, Integer>();
		suits.forEach((Suit suit) -> {
			var count = countSuit(suit, hand, jokerValue) - bid.getAmount(suit);
			map.put(suit, count);
		});

		return map;
	}
	
	public static Suit getSuitWithMostPositiveDifference(HashMap<Suit, Integer> diffMap)
	{
		Suit suit = null;
		double difference = -1000;
		
		for (Map.Entry<Suit, Integer> entry : diffMap.entrySet())
		{
			if (entry.getValue() >= difference)
			{
				difference = entry.getValue();
				suit = entry.getKey();
			}
		}

		return suit;
	}
	
	public static boolean canSeeBid(double[] diffVector)
	{
		int length = diffVector.length;
		for (int i=0; i<length; i++)
		{
			if (diffVector[i] < 0)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean shouldAutoChallengeForIndividualSuit(double[] diffVector, double threshold)
	{
		int length = diffVector.length;
		for (int i=0; i<length; i++)
		{
			if (diffVector[i] < -threshold)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean shouldAutoChallengeForOverallBid(double[] diffVector, double threshold)
	{
		int total = 0;
		for (int i=0; i<diffVector.length; i++)
		{
			total += diffVector[i];
		}
		
		return total < -threshold;
	}
	
	public static boolean bidIsSensible(double[] diffVector, double unseenCards, boolean logging)
	{
		int total = 0;
		for (int i=0; i<diffVector.length; i++)
		{
			total += diffVector[i];
		}
		
		int comparison = (int)-Math.ceil(unseenCards/2);
		
		if (total >= comparison)
		{
			Debug.append("Bid was sensible: " + total + " >= " + comparison, logging);
		}
		else
		{
			Debug.append("Bid was not 'sensible': " + total + " < " + comparison, logging);
		}
		
		return total >= comparison;
	}
	
	public static VectropyBid getEmptyBid(boolean includeMoons, boolean includeStars)
	{
		return new VectropyBid(0, 0, 0, 0, 0, 0, includeMoons, includeStars);
	}
	
	/**
	 * EV Strategy
	 */
	public static double[] getEvDifferenceVector(VectropyBid bid, HashMap<Integer, Double> hmEvBySuit, 
			  boolean includeMoons, boolean includeStars)
	{
		double clubsCount = hmEvBySuit.get(Suit.Clubs) - bid.getClubs();
		double diamondsCount = hmEvBySuit.get(Suit.Diamonds) - bid.getDiamonds();
		double heartsCount = hmEvBySuit.get(Suit.Hearts) - bid.getHearts();
		double moonsCount = hmEvBySuit.get(Suit.Moons) - bid.getMoons();
		double spadesCount = hmEvBySuit.get(Suit.Spades) - bid.getSpades();
		double starsCount = hmEvBySuit.get(Suit.Stars) - bid.getStars();

		if (includeMoons && includeStars)
		{
			double[] diffVector = {clubsCount, diamondsCount, heartsCount, moonsCount, spadesCount, starsCount};
			return diffVector;
		}
		else if (includeMoons)
		{
			double[] diffVector = {clubsCount, diamondsCount, heartsCount, moonsCount, spadesCount};
			return diffVector;
		}
		else if (includeStars)
		{
			double[] diffVector = {clubsCount, diamondsCount, heartsCount, spadesCount, starsCount};
			return diffVector;
		}
		else
		{
			double[] diffVector = {clubsCount, diamondsCount, heartsCount, spadesCount};
			return diffVector;
		}
	}
	public static boolean isBelowEvInAllSuits(double[] evVector)
	{
		return canSeeBid(evVector);
	}
	public static boolean shouldAutoChallengeForEvOfIndividualSuit(double[] evDiffVector)
	{
		int size = evDiffVector.length;
		for (int i=0; i<size; i++)
		{
			double evDiff = evDiffVector[i];
			if (evDiff < -0.5)
			{
				return true;
			}
		}
		
		return false;
	}
	public static boolean shouldAutoChallengeForOverallEvOfBid(double[] evDiffVector)
	{
		double overallDiff = 0;
		int length = evDiffVector.length;
		for (int i=0; i<length; i++)
		{
			overallDiff += evDiffVector[i];
		}
		
		if (overallDiff < 0)
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean shouldAutoChallengeForMultipleSuitsOverEv(double[] evDiffVector)
	{
		int numberOfSuitsOverEv = 0;
		int length = evDiffVector.length;
		for (int i=0; i<length; i++)
		{
			if (evDiffVector[i] < -0.5)
			{
				numberOfSuitsOverEv++;
			}
		}
		
		return numberOfSuitsOverEv >= 2;
	}
	
	public static int getSuitWithHighestValue(HashMap<Integer, Double> hmEvBySuit)
	{
		int highestSuit = -1;
		double highestValue = -1;
		
		Iterator<Map.Entry<Integer, Double>> it = hmEvBySuit.entrySet().iterator();
		for (; it.hasNext();)
		{
			Map.Entry<Integer, Double> entry = it.next();
			double ev = entry.getValue();
			if (ev > highestValue)
			{
				highestValue = ev;
				highestSuit = entry.getKey().intValue();
			}
		}
		
		return highestSuit;
	}
}
