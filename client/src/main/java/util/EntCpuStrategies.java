package util;

import game.ChallengeAction;
import game.EntropyBidAction;
import game.PlayerAction;
import game.Suit;
import object.ChallengeBid;
import object.Player;
import strategy.MarkStrategySuitWrapper;

import java.util.*;

import static game.CardsUtilKt.countSuit;
import static game.CardsUtilKt.getEvMap;
import static strategy.MarkStrategySuitWrapperKt.factoryMarkStrategySuitWrapper;

public class EntCpuStrategies 
{
	private static final String STRATEGY_MARK = "Medium";
	private static final String STRATEGY_RANDOMISE_PER_MOVE = "Randomise (per move)";
	private static final String[] STRATEGIES_TO_CHOOSE_AT_RANDOM = {CpuStrategies.STRATEGY_BASIC, STRATEGY_MARK, CpuStrategies.STRATEGY_EV};

	public static Vector<String> getAllStrategies()
	{
		Vector<String> allStrategies = new Vector<>();
		allStrategies.add(CpuStrategies.STRATEGY_BASIC);
		allStrategies.add(STRATEGY_MARK);
		allStrategies.add(CpuStrategies.STRATEGY_EV);
		allStrategies.add(STRATEGY_RANDOMISE_PER_MOVE);
		
		return allStrategies;
	}
	
	public static PlayerAction processOpponentTurn(Player opponent, StrategyParms parms)
	{
		String strategy = opponent.getStrategy();
		return processOpponentTurn(strategy, opponent, parms);
	}
	private static PlayerAction processOpponentTurn(String strategy, Player opponent, StrategyParms parms)
	{
		if (strategy.equals(CpuStrategies.STRATEGY_BASIC))
		{
			return processBasicTurn(opponent, parms);
		}
		else if (strategy.equals(STRATEGY_MARK))
		{
			return processMarkTurn(opponent, parms);
		}
		else if (strategy.equals(CpuStrategies.STRATEGY_EV))
		{
			return processEvTurnAndRevealCard(opponent, parms);
		}
		else
		{
			Random rand = new Random();
			int numberOfChoices = STRATEGIES_TO_CHOOSE_AT_RANDOM.length;
			int choice = rand.nextInt(numberOfChoices);
			strategy = STRATEGIES_TO_CHOOSE_AT_RANDOM[choice];
			return processOpponentTurn(strategy, opponent, parms);
		}
	}
	
	private static PlayerAction processMarkTurn(Player opponent, StrategyParms parms)
	{
		EntropyBidAction bid = (EntropyBidAction)parms.getLastBid();
		
		Random rand = new Random();
		List<String> hand = opponent.getHand();
		
		//Parms
		boolean includeMoons = parms.getIncludeMoons();
		boolean includeStars = parms.getIncludeStars();
		int jokerValue = parms.getJokerValue();
		int jokerQuantity = parms.getJokerQuantity();
		double totalCards = parms.getTotalNumberOfCards();
		boolean logging = parms.getLogging();
		
		Debug.append("Mark strategy for this turn", logging);

		MarkStrategySuitWrapper suitWrapper = factoryMarkStrategySuitWrapper(hand, jokerValue, includeMoons, includeStars, bid);
		Debug.append("SuitWrapper: " + suitWrapper, logging);
		
		var bestSuit = suitWrapper.getBestSuit();
		int bestSuitCount = countSuit(bestSuit, hand, jokerValue);
		var worstSuit = suitWrapper.getWorstSuit();
		
		int suitsInPlay = 4 + (includeMoons?1:0) + (includeStars?1:0);
		int halfThreshold = (int) Math.ceil(totalCards/2);
		int quarterThreshold = (int) Math.floor(totalCards/4);

		if (bid == null)
		{
			Debug.append("Starting this round", logging);
			var suit = getSuitForMarkBid(suitWrapper, logging);
			int bidAmount = markBid(halfThreshold, (int)totalCards, suitsInPlay);
			
			return new EntropyBidAction(opponent.getName(), false, bidAmount, suit);
		}
		else
		{
			//Set the 'hand' variable to be everything I can see.
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			
			int bidAmountFacedWith = bid.getAmount();
			var bidSuitFacedWith = bid.getSuit();
			
			int bidSuitCount = countSuit(bidSuitFacedWith, hand, jokerValue);
			int minBiddableSuitCount = suitWrapper.getSuitsPossibleToMinBid().size();
			int choice = rand.nextInt(4);
			
			if (minBiddableSuitCount == suitsInPlay)
			{
				Debug.append("I can safely minbid in all suits.", logging);
				if (choice <= 1)
				{
					//0 or 1
					Debug.append("One-upped (50%)", logging);
					return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
				}
				else if (choice == 2)
				{
					Debug.append("Minbidding best or worst (25%)", logging);
					if (bestSuit == bidSuitFacedWith)
					{
						Debug.append("BestSuit = BidSuitCodeFacedWith = " + bestSuit + ". Bidding worstSuit: " + worstSuit, logging);
						return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, worstSuit, logging);
					}
					else
					{
						Debug.append("Bidding bestSuit as it's different from what I'm faced with.", logging);
						return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, bestSuit, logging);
					}
				}
				else
				{
					Debug.append("Minbid random middle suit (25%)", logging);
					var suitToBid = suitWrapper.getRandomMiddleSuit();
					return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
				}
			}
			else if (minBiddableSuitCount >= 3)
			{
				Debug.append("I can safely minbid at least 3 suits", logging);
				if (bidSuitCount > bidAmountFacedWith)
				{
					Debug.append("This includes the suit I'm faced with", logging);
					if (choice <= 1)
					{
						Debug.append("One-upping (50%)", logging);
						return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
					}
					else
					{
						Debug.append("Bidding random other suit (50%)", logging);
						var suitToBid = suitWrapper.randomSuitNot(bidSuitFacedWith);
						return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
					}
				}
				else
				{
					Debug.append("This doesn't include the suit I'm faced with", logging);
					if (choice <= 1)
					{
						Debug.append("Minbidding my best suit (50%)", logging);
						return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, bestSuit, logging);
					}
					else
					{
						Debug.append("Minbidding random other suit (50%)", logging);
						var suitToBid = suitWrapper.randomSuitNot(bestSuit);
						return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
					}
				}
			}
			else if (minBiddableSuitCount == 2)
			{
				Debug.append("I can safely minbid 2 suits", logging);
				if (choice <= 1)
				{
					Debug.append("One-upping (50%)", logging);
					return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
				}
				else
				{
					///minbid one of the two suits I can
					Debug.append("Minbidding one of the two suits (50%)", logging);
					var suitToBid = suitWrapper.randomSuitPossibleToMinBid();
					return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
				}
			}
			else if (minBiddableSuitCount == 1)
			{
				var suitToBid = suitWrapper.randomSuitPossibleToMinBid();
				Debug.append("Could only minbid suit " + suitToBid + ", so minbidding that.", logging);
				return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
			}
			else
			{
				Debug.append("Couldn't automatically minbid any suit", logging);
				double jokerThreshold = Math.floor((jokerQuantity * jokerValue)/(double)(suitsInPlay*2));
				double threshold = bidSuitCount + totalCards - hand.size() + jokerThreshold - 1;
				
				Debug.append("Bonkers threshold is " + threshold, logging);
				if (bidAmountFacedWith > threshold)
				{
					Debug.append("Auto-challenging because " + bidAmountFacedWith + " > " + threshold, logging);
					return new ChallengeAction(opponent.getName());
				}
				else if (bidAmountFacedWith > bestSuitCount + bidSuitCount + quarterThreshold)
				{
					Debug.append("Auto-challenging because bestSuitCount + bidSuitCount + quarterThreshold = " + (bestSuitCount + bidSuitCount + quarterThreshold), logging);
					return new ChallengeAction(opponent.getName());
				}
				else if (bidAmountFacedWith > 1 + quarterThreshold)
				{
					Debug.append("BidAmountFacedWith > " + (1+quarterThreshold), logging);
					if (choice <= 2)
					{
						Debug.append("One-upping (75%)", logging);
						return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
					}
					else
					{
						Debug.append("Challenging (25%)", logging);
						return new ChallengeAction(opponent.getName());
					}
				}
				else
				{
					Debug.append("BidAmountFacedWith is only 1, so not challenging.", logging);
					if (choice <= 1)
					{
						Debug.append("One-upping (50%)", logging);
						return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
					}
					else
					{
						Debug.append("Minbidding my best suit (50%)", logging);
						return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, bestSuit, logging);
					}
				}
			}
		}
	}
	private static Suit getSuitForMarkBid(MarkStrategySuitWrapper suitWrapper, boolean logging)
	{
		Random rand = new Random();
		int choice = rand.nextInt(3);
		if (choice == 0)
		{
			var bestSuit = suitWrapper.getBestSuit();
			Debug.append("Chose best suit: " + bestSuit, logging);
			return bestSuit;
		}
		else if (choice == 1)
		{
			var worstSuit = suitWrapper.getWorstSuit();
			Debug.append("Chose worst suit: " + worstSuit, logging);
			return worstSuit;
		}
		else
		{
			var randomSuit = suitWrapper.getRandomMiddleSuit();
			Debug.append("Chose random middle suit: " + randomSuit, logging);
			return randomSuit;
		}
	}
	private static int markBid(int halfThreshold, int totalCards, int suitsInPlay)
	{
		Random rand = new Random();
		
		if (totalCards > (suitsInPlay - 1))
		{
			int choice = rand.nextInt(3);
			if (choice == 0)
			{
				return halfThreshold;
			}
			
			return choice; //1 or 2
		}
		else
		{
			int choice = rand.nextInt(2);
			if (choice == 0)
			{
				return halfThreshold;
			}
			else
			{
				return 1;
			}
		}
	}
	
	private static PlayerAction processBasicTurn(Player opponent, StrategyParms parms)
	{
		//Parms
		EntropyBidAction bid = (EntropyBidAction)parms.getLastBid();
		boolean includeMoons = parms.getIncludeMoons();
		boolean includeStars = parms.getIncludeStars();
		int jokerValue = parms.getJokerValue();
		boolean logging = parms.getLogging();
		
		Debug.append("Basic strategy for this turn", logging);
		Random coin = new Random();
		int decision = coin.nextInt(2);
		int decisionTwo = coin.nextInt(2);
		List<String> hand = opponent.getHand();

		if (bid == null)
		{
			Debug.append("Starting this round", logging);

			var suit = Suit.random(includeMoons, includeStars);
			int bidAmount = Math.max(1, countSuit(suit, hand, jokerValue) + coin.nextInt(2));
			
			return new EntropyBidAction(opponent.getName(), false, bidAmount, suit);
		}
		else
		{
			//Set the 'hand' variable to be everything I can see.
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			
			int bidAmountFacedWith = bid.getAmount();
			Suit bidSuitFacedWith = bid.getSuit();
			
			int bidSuitCount = countSuit(bidSuitFacedWith, hand, jokerValue);
			double totalCards = parms.getTotalNumberOfCards();

			int thirdThreshold = (int) Math.floor(totalCards/3.5);
			int halfThreshold = (int) Math.ceil(totalCards/2);
			Debug.append("thirdThreshold = " + thirdThreshold, logging);
			Debug.append("halfThreshold = " + halfThreshold, logging);

			if (bidAmountFacedWith <= bidSuitCount)
			{
				if (decisionTwo == 1)
				{
					Debug.append("Opponent " + opponent + " auto-minbid", logging);
					return opponentMinBid(opponent, bidSuitFacedWith, bidAmountFacedWith, includeMoons, includeStars);
				}
				else
				{
					Debug.append("Opponent " + opponent + " auto-oneupped", logging);
					return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
				}
			}
			else if (bidAmountFacedWith < bidSuitCount + thirdThreshold)
			{
				if (decisionTwo == 1)
				{
					Debug.append("Opponent " + opponent + " auto-minbid", logging);
					return opponentMinBid(opponent, bidSuitFacedWith, bidAmountFacedWith, includeMoons, includeStars);
				}
				else
				{
					Debug.append("Opponent " + opponent + " auto-oneupped", logging);
					return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
				}
			}
			else if (bidAmountFacedWith > halfThreshold)
			{
				Debug.append("Opponent " + opponent + " auto-challenged", logging);
				return new ChallengeAction(opponent.getName());
			}
			else if (decision == 0)
			{
				Debug.append("Opponent " + opponent + " flip-challenged", logging);
				return new ChallengeAction(opponent.getName());
			}
			else
			{
				if (decisionTwo == 0)
				{
					Debug.append("Opponent " + opponent + " flip-minbid", logging);
					return opponentMinBid(opponent, bidSuitFacedWith, bidAmountFacedWith, includeMoons, includeStars);
				}
				else
				{
					Debug.append("Opponent " + opponent + " flip-oneupped", logging);
					return opponentOneUp(opponent, bidSuitFacedWith, bidAmountFacedWith, logging);
				}
			}
		}
	}
	
	private static PlayerAction processEvTurnAndRevealCard(Player opponent, StrategyParms parms)
	{
		PlayerAction bid = processEvTurn(opponent, parms);
		CpuStrategies.setCardToReveal(bid, parms, opponent);
		return bid;
	}
	
	private static PlayerAction processEvTurn(Player opponent, StrategyParms parms)
	{
		//Parms
		EntropyBidAction bid = (EntropyBidAction)parms.getLastBid();
		int totalCards = parms.getTotalNumberOfCards();
		boolean includeMoons = parms.getIncludeMoons();
		boolean includeStars = parms.getIncludeStars();
		int jokerValue = parms.getJokerValue();
		boolean logging = parms.getLogging();
		
		Debug.append("EV strategy for this turn", logging);
		Random coin = new Random();
		List<String> hand = opponent.getHand();
		
		int totalOpponentCards = totalCards - hand.size();
		if (bid == null)
		{
			Debug.append("Starting this round", logging);
			Map<Suit, Double> hmEvBySuit = getEvMap(hand, parms);
			
			var suit = Suit.random(includeMoons, includeStars);
			double suitEv = hmEvBySuit.get(suit);
			
			int suitEvRounded = (int) Math.ceil(suitEv);
			int randomAmount = coin.nextInt(5) - 4; //-4, -3, -2, 1, 0
			int bidAmount = Math.max(1, suitEvRounded + randomAmount);
			
			return new EntropyBidAction(opponent.getName(), false, bidAmount, suit);
		}
		else
		{
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			Map<Suit, Double> hmEvBySuit = getEvMap(hand, parms);
			
			int bidAmountFacedWith = bid.getAmount();
			var bidSuitFacedWith = bid.getSuit();
			double expectedValueForBid = hmEvBySuit.get(bidSuitFacedWith);
			
			Debug.append("EV calculation for bid of " + bid.plainString() + ": " + expectedValueForBid, logging);

			Suit.filter(includeMoons, includeStars).stream().max((suit) -> hmEvBySuit.get(suit));
			ArrayList<Integer> suits = CardsUtil.getSuitCodesVector(includeMoons, includeStars);
			int suitsSize = suits.size();
			double maxEv = 0;
			for (int i=0; i<suitsSize; i++)
			{
				Integer suitObj = suits.get(i);
				double expectedValue = hmEvBySuit.get(suitObj);
				if (expectedValue > maxEv)
				{
					maxEv = expectedValue;
				}
			}
			
			String suitsWithMax = "";
			
			for (int i=0; i<suitsSize; i++)
			{
				int suit = suits.get(i);
				double ev = hmEvBySuit.get(suit);
				if (ev == maxEv)
				{
					suitsWithMax += suit;
				}
			}
			
			if (bidAmountFacedWith > expectedValueForBid + 1)
			{
				return new ChallengeAction(opponent.getName());
			}
			else
			{
				int suitsToChooseFrom = suitsWithMax.length();
				int choice = coin.nextInt(suitsToChooseFrom);
				
				int suit = Integer.parseInt(suitsWithMax.substring(choice, choice+1));

				if (maxEv > bidAmountFacedWith - 1 && totalOpponentCards > 1)
				{
					return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, suit, logging);
				}
				else if (maxEv > bidAmountFacedWith - 1)
				{
					//bit friggy - special case for being up against one card. Don't bid higher if it means you need them to have the ace/joker.
					suit = Integer.parseInt(suitsWithMax.substring(suitsToChooseFrom-1, suitsToChooseFrom));
					int bidAmount = EntropyUtil.amountRequiredToBidInSuit(suit, bidSuitCodeFacedWith, bidAmountFacedWith);
					int amountRequiredInOneCard = bidAmount - CardsUtil.countSuit(hand, suit, jokerValue);
					
					if (amountRequiredInOneCard < 2)
					{
						return opponentMinBidSuit(opponent, bidSuitFacedWith, bidAmountFacedWith, suit, logging);
					}
					else
					{
						Debug.append("Bidding would've needed >1 in one card, so challenged", logging);
						return new ChallengeAction(opponent.getName());
					}
				}
				else
				{
					Debug.append("Couldn't bid anything 'safely', so challenged.", logging);
					return new ChallengeAction(opponent.getName());
				}
			}
		}
	}

	private static EntropyBidAction opponentMinBid(Player opponent, Suit bidSuitFacedWith, int bidAmount,
	  boolean includeMoons, boolean includeStars) 
	{
		var nextSuit = bidSuitFacedWith.next(includeMoons, includeStars);
		var myAmount = nextSuit.lessThan(bidSuitFacedWith) ? bidAmount + 1 : bidAmount;
		return new EntropyBidAction(opponent.getName(), false, myAmount, nextSuit);
	}

	private static EntropyBidAction opponentOneUp(Player opponent, Suit bidSuitFacedWith, int bidAmount, boolean logging)
	{
		Debug.append("One Up", logging);
		return new EntropyBidAction(opponent.getName(), false, bidAmount + 1, bidSuitFacedWith);
	}

	private static EntropyBidAction opponentMinBidSuit(Player opponent, Suit bidSuitFacedWith, int bidAmountFacedWith, Suit desiredSuit, boolean logging)
	{
		Debug.append("MinBidSuit " + desiredSuit, logging);

		var bidAmount = desiredSuit.lessThan(bidSuitFacedWith) ? bidAmountFacedWith + 1 : bidAmountFacedWith;
		
		return new EntropyBidAction(opponent.getName(), false, bidAmount, desiredSuit);
	}
}