package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import game.EntropyBidAction;
import game.Suit;
import object.EntropyBid;

public class MarkStrategySuitWrapper 
{
	private HashMap<Integer, Integer> hmSuitCountBySuitCode = new HashMap<>();
	private List<Suit> applicableSuits;
	private ArrayList<Integer> suitsPossibleToMinBid = new ArrayList<>();
	private int bestSuit = -1;
	private int worstSuit = -1;
	
	public MarkStrategySuitWrapper(List<String> hand, int jokerValue, boolean includeMoons, boolean includeStars, EntropyBidAction bid)
	{
		applicableSuits = Suit.filter(includeMoons, includeStars);
		
		int bestSuitCount = 0;
		for (int i=0; i<applicableSuits.size(); i++)
		{
			int suit = applicableSuits.get(i);
			int count = CardsUtil.countSuit(hand, suit, jokerValue);
			hmSuitCountBySuitCode.put(suit, count);
			
			if (count >= bestSuitCount)
			{
				bestSuit = suit;
				bestSuitCount = count;
			}
		}
		
		int worstSuitCount = 1000;
		for (int i=applicableSuits.size()-1; i>=0; i--)
		{
			int suit = applicableSuits.get(i);
			int count = CardsUtil.countSuit(hand, suit, jokerValue);
			if (count <= worstSuitCount)
			{
				worstSuit = suit;
				worstSuitCount = count;
			}
		}
		
		initialiseSuitsPossibleToMinBid(bid);
	}
	
	private void initialiseSuitsPossibleToMinBid(EntropyBid bid)
	{
		//If we're starting this round, we don't care
		if (bid == null)
		{
			return;
		}
		
		int size = applicableSuits.size();
		for (int i=0; i<size; i++)
		{
			int suitCode = applicableSuits.get(i);
			int amount = hmSuitCountBySuitCode.get(suitCode);
			if (suitCode > bid.getBidSuitCode()
			  && amount >= bid.getBidAmount())
			{
				suitsPossibleToMinBid.add(suitCode);
			}
			else if (amount > bid.getBidAmount())
			{
				suitsPossibleToMinBid.add(suitCode);
			}
		}
	}
	
	public int getBestSuit()
	{
		return bestSuit;
	}
	public int getWorstSuit()
	{
		return worstSuit;
	}
	
	public int getRandomMiddleSuit()
	{
		ArrayList<Integer> suitsToChooseFrom = new ArrayList<>();
		suitsToChooseFrom.addAll(applicableSuits);
		suitsToChooseFrom.remove(Integer.valueOf(bestSuit));
		suitsToChooseFrom.remove(Integer.valueOf(worstSuit));
		
		int size = suitsToChooseFrom.size();
		Random rand = new Random();
		int choice = rand.nextInt(size);
		
		return suitsToChooseFrom.get(choice);
	}
	
	public int getRandomSuitExcluding(int... suitsToExclude)
	{
		ArrayList<Integer> suitsToChooseFrom = new ArrayList<>();
		suitsToChooseFrom.addAll(applicableSuits);

		for (int i=0; i<suitsToExclude.length; i++)
		{
			int suit = suitsToExclude[i];
			suitsToChooseFrom.remove(Integer.valueOf(suit));
		}
		
		int size = suitsToChooseFrom.size();
		Random rand = new Random();
		int choice = rand.nextInt(size);
		
		return suitsToChooseFrom.get(choice);
	}
	
	public int getRandomSuitPossibleToMinbid()
	{
		int size = suitsPossibleToMinBid.size();
		Random rand = new Random();
		int choice = rand.nextInt(size);
		
		return suitsPossibleToMinBid.get(choice);
	}
	
	public int getCountOfSuitsPossibleToMinbid()
	{
		return suitsPossibleToMinBid.size();
	}
	
	@Override
	public String toString() 
	{
		String s = "";
		s += "Best Suit: " + bestSuit + ", ";
		s += "Worst Suit: " + worstSuit + ", ";
		s += "Applicable Suits: " + applicableSuits;
		
		return s;
	}
}
