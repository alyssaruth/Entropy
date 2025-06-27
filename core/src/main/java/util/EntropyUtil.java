package util;

import game.Suit;
import object.EntropyBid;

import java.util.List;


public class EntropyUtil 
{	
	public static int amountRequiredToBidInSuit(int desiredSuit, int suitFacedWith, int bidNumberFacedWith)
	{
		if (desiredSuit > suitFacedWith)
		{
			return bidNumberFacedWith;
		}
		return bidNumberFacedWith + 1;
	}
	
	public static EntropyBid getEmptyBid()
	{
		EntropyBid bid = new EntropyBid(Suit.Clubs, 0);
		return bid;
	}

	public static String getColourForPlayerNumber(int playerNumber)
	{
		switch (playerNumber)
		{
			case 0:
				return "red";
			case 1:
				return "blue";
			case 2:
				return "green";
			case 3:
				return "purple";
			default:
				return "gray";
		}
	}
}