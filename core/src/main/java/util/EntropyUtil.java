package util;

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

	public static int getPerfectBidAmount(List<String> playerHand, List<String> opponentOneHand,
			List<String> opponentTwoHand, List<String> opponentThreeHand, int jokerValue)
	{
		int perfectBidAmount = 0;
		for (int i=0; i<6; i++)
		{
			int total = CardsUtil.countSuit(i, playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);
			if (total > perfectBidAmount)
			{
				perfectBidAmount = total;
			}
		}

		return perfectBidAmount;
	}

	public static int getPerfectBidSuitCode(List<String> playerHand, List<String> opponentOneHand,
											List<String> opponentTwoHand, List<String> opponentThreeHand, int jokerValue, boolean includeStars)
	{
		int diamondsTotal = CardsUtil.countDiamonds(playerHand, jokerValue) 
				+ CardsUtil.countDiamonds(opponentOneHand, jokerValue) 
				+ CardsUtil.countDiamonds(opponentTwoHand, jokerValue) 
				+ CardsUtil.countDiamonds(opponentThreeHand, jokerValue);

		int heartsTotal = CardsUtil.countHearts(playerHand, jokerValue) 
				+ CardsUtil.countHearts(opponentOneHand, jokerValue) 
				+ CardsUtil.countHearts(opponentTwoHand, jokerValue) 
				+ CardsUtil.countHearts(opponentThreeHand, jokerValue);
		
		int moonsTotal = CardsUtil.countMoons(playerHand, jokerValue) 
				+ CardsUtil.countMoons(opponentOneHand, jokerValue) 
				+ CardsUtil.countMoons(opponentTwoHand, jokerValue) 
				+ CardsUtil.countMoons(opponentThreeHand, jokerValue);

		int spadesTotal = CardsUtil.countSpades(playerHand, jokerValue) 
				+ CardsUtil.countSpades(opponentOneHand, jokerValue) 
				+ CardsUtil.countSpades(opponentTwoHand, jokerValue) 
				+ CardsUtil.countSpades(opponentThreeHand, jokerValue);
		
		int starsTotal = CardsUtil.countStars(playerHand, jokerValue) 
				+ CardsUtil.countStars(opponentOneHand, jokerValue) 
				+ CardsUtil.countStars(opponentTwoHand, jokerValue) 
				+ CardsUtil.countStars(opponentThreeHand, jokerValue);

		int perfectBidAmount = getPerfectBidAmount(playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, jokerValue);

		//need includeStars here - if there are only jokers in play stars could come out on top
		if (starsTotal == perfectBidAmount
		  && includeStars)
		{
			return CardsUtil.SUIT_STARS;
		}
		else if (spadesTotal == perfectBidAmount)
		{
			return CardsUtil.SUIT_SPADES;
		}
		else if (moonsTotal == perfectBidAmount)
		{
			return CardsUtil.SUIT_MOONS;
		}
		else if (heartsTotal == perfectBidAmount)
		{
			return CardsUtil.SUIT_HEARTS;
		}
		else if (diamondsTotal == perfectBidAmount)
		{
			return CardsUtil.SUIT_DIAMONDS;
		}
		else
		{
			return CardsUtil.SUIT_CLUBS;
		}
	}
	
	public static EntropyBid getEmptyBid()
	{
		EntropyBid bid = new EntropyBid(CardsUtil.SUIT_CLUBS, 0);
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