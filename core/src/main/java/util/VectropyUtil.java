package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

import game.Suit;
import object.VectropyBid;

public class VectropyUtil 
{	
	public static boolean isOverbid(VectropyBid bid, ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber, int jokerValue)
	{
		List<String> playerHand = hmHandByPlayerNumber.get(0);
		List<String> opponentOneHand = hmHandByPlayerNumber.get(1);
		List<String> opponentTwoHand = hmHandByPlayerNumber.get(2);
		List<String> opponentThreeHand = hmHandByPlayerNumber.get(3);
		
		return isOverbid(bid, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
	}
	
	public static boolean isOverbid(VectropyBid bid, List<String> playerHand, List<String> opponentOneHand, List<String> opponentTwoHand, List<String> opponentThreeHand, int jokerValue)
	{
		int maxClubs = CardsUtil.countSuit(CardsUtil.SUIT_CLUBS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		int maxDiamonds = CardsUtil.countSuit(CardsUtil.SUIT_DIAMONDS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		int maxHearts = CardsUtil.countSuit(CardsUtil.SUIT_HEARTS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		int maxMoons = CardsUtil.countSuit(CardsUtil.SUIT_MOONS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		int maxSpades = CardsUtil.countSuit(CardsUtil.SUIT_SPADES, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		int maxStars = CardsUtil.countSuit(CardsUtil.SUIT_STARS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		
		return bid.getClubs() > maxClubs 
			|| bid.getDiamonds() > maxDiamonds 
			|| bid.getHearts() > maxHearts 
			|| bid.getMoons() > maxMoons
			|| bid.getSpades() > maxSpades
			|| bid.getStars() > maxStars;
	}
	
	public static String getResult(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber, int jokerValue, int suitCode,
								   boolean includeMoons, boolean includeStars)
	{
		List<String> playerHand = hmHandByPlayerNumber.get(0);
		List<String> opponentOneHand = hmHandByPlayerNumber.get(1);
		List<String> opponentTwoHand = hmHandByPlayerNumber.get(2);
		List<String> opponentThreeHand = hmHandByPlayerNumber.get(3);
		
		return getResult(playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue, suitCode, includeMoons, includeStars);
	}
	
	public static String getResult(List<String> playerHand, List<String> opponentOneHand, List<String> opponentTwoHand, List<String> opponentThreeHand,
								   int jokerValue, int suitCode, boolean includeMoons, boolean includeStars)
	{
		Preferences prefs = Registry.prefs;
		String numberOfColoursStr = prefs.get(Registry.PREFERENCES_STRING_NUMBER_OF_COLOURS, "twocolour");
		boolean fourColours = (numberOfColoursStr.equals("fourcolour"));
		
		String clubs = "" + CardsUtil.countSuit(CardsUtil.SUIT_CLUBS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		String diamonds = "" + CardsUtil.countSuit(CardsUtil.SUIT_DIAMONDS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		String hearts = "" + CardsUtil.countSuit(CardsUtil.SUIT_HEARTS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		String moons = "" + CardsUtil.countSuit(CardsUtil.SUIT_MOONS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		String spades = "" + CardsUtil.countSuit(CardsUtil.SUIT_SPADES, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		String stars = "" + CardsUtil.countSuit(CardsUtil.SUIT_STARS, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
		
		switch (suitCode)
		{
			case CardsUtil.SUIT_CLUBS:
				clubs = "<b>" + clubs + "</b>";
				break;
			case CardsUtil.SUIT_DIAMONDS:
				diamonds = "<b>" + diamonds + "</b>";
				break;
			case CardsUtil.SUIT_HEARTS:
				hearts = "<b>" + hearts + "</b>";
				break;
			case CardsUtil.SUIT_MOONS:
				moons = "<b>" + moons + "</b>";
				break;
			case CardsUtil.SUIT_SPADES:
				spades = "<b>" + spades + "</b>";
				break;
			case CardsUtil.SUIT_STARS:
				stars = "<b>" + stars + "</b>";
				break;
			default:
				break;
		} 
		
		String clubsColour = "black";
		String diamondsColour = "red";
		String moonsColour = "E6B800";
		if (fourColours)
		{
			clubsColour = "green";
			diamondsColour = "blue";
			moonsColour = "purple";
		}
		
		String resultStr = "(<font color=\"" + clubsColour + "\">" + clubs + "</font>, <font color=\"" + diamondsColour + "\">" + diamonds + "</font>, <font color=\"red\">" + hearts + "</font>, ";
		if (includeMoons)
		{
			resultStr += "<font color=\"" + moonsColour + "\">" + moons + "</font>, ";
		}
		
		resultStr += spades;
		if (includeStars)
		{
			resultStr += ", <font color=\"E6B800\">" + stars + "</font>";
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
	
	public static double[] getDifferenceVector(VectropyBid bid, List<String> hand, int jokerValue,
											   boolean includeMoons, boolean includeStars)
	{
		int clubsCount = CardsUtil.countClubs(hand, jokerValue) - bid.getClubs();
		int diamondsCount = CardsUtil.countDiamonds(hand, jokerValue) - bid.getDiamonds();
		int heartsCount = CardsUtil.countHearts(hand, jokerValue) - bid.getHearts();
		int moonsCount = CardsUtil.countMoons(hand, jokerValue) - bid.getMoons();
		int spadesCount = CardsUtil.countSpades(hand, jokerValue) - bid.getSpades();
		int starsCount = CardsUtil.countStars(hand, jokerValue) - bid.getStars();
		
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
	
	public static int getSuitWithMostPositiveDifference(double[] diffVector, boolean includeMoons)
	{
		int index = 0;
		double difference = -1000;
		
		for (int i=0; i<diffVector.length; i++)
		{
			if (diffVector[i] >= difference)
			{
				difference = diffVector[i];
				index = i;
			}
		}
		
		if (!includeMoons && index >= CardsUtil.SUIT_MOONS)
		{
			return index + 1;
		}
		else
		{
			return index;
		}
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
	public static double[] getEvDifferenceVector(VectropyBid bid, Map<Suit, Double> hmEvBySuit,
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
	
	public static Suit getSuitWithHighestValue(Map<Suit, Double> hmEvBySuit)
	{
		Suit highestSuit = null;
		double highestValue = -1;
		
		Iterator<Map.Entry<Suit, Double>> it = hmEvBySuit.entrySet().iterator();
		for (; it.hasNext();)
		{
			Map.Entry<Suit, Double> entry = it.next();
			double ev = entry.getValue();
			if (ev > highestValue)
			{
				highestValue = ev;
				highestSuit = entry.getKey();
			}
		}
		
		return highestSuit;
	}
}
