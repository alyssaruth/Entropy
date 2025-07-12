package util;

import java.util.*;

import game.Suit;
import object.Bid;
import object.ChallengeBid;
import object.EntropyBid;
import object.Player;
import strategy.MarkStrategySuitWrapper;

import static game.CardsUtilKt.countSuit;
import static game.EntropyUtilKt.amountRequiredToBid;
import static game.StrategyUtilKt.*;
import static strategy.MarkStrategySuitWrapperKt.factoryMarkStrategySuitWrapper;
import static utils.CoreGlobals.logger;

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
	
	public static Bid processOpponentTurn(Player opponent, StrategyParams parms)
	{
		String strategy = opponent.getStrategy();
		return processOpponentTurn(strategy, opponent, parms);
	}
	private static Bid processOpponentTurn(String strategy, Player opponent, StrategyParams parms)
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
	
	private static Bid processMarkTurn(Player opponent, StrategyParams parms)
	{
		var settings = parms.getSettings();
		EntropyBid bid = (EntropyBid)parms.getLastBid();
		
		Random rand = new Random();
		List<String> hand = opponent.getHand();
		
		//Parms
		boolean includeMoons = settings.getIncludeMoons();
		boolean includeStars = settings.getIncludeStars();
		int jokerValue = settings.getJokerValue();
		int jokerQuantity = settings.getJokerQuantity();
		double totalCards = parms.getCardsInPlay();
		boolean logging = parms.getLogging();

		log("Mark strategy for this turn", logging);

		var suitWrapper = factoryMarkStrategySuitWrapper(hand, jokerValue, includeMoons, includeStars, bid);
		log("SuitWrapper: " + suitWrapper, logging);
		
		var bestSuit = suitWrapper.getBestSuit();
		int bestSuitCount = countSuit(bestSuit, hand, jokerValue);
		var worstSuit = suitWrapper.getWorstSuit();
		
		int suitsInPlay = 4 + (includeMoons?1:0) + (includeStars?1:0);
		int halfThreshold = (int) Math.ceil(totalCards/2);
		int quarterThreshold = (int) Math.floor(totalCards/4);

		if (bid == null)
		{
			log("Starting this round", logging);
			var suit = getSuitForMarkBid(suitWrapper, logging);
			int bidAmount = markBid(halfThreshold, (int)totalCards, suitsInPlay);
			
			return new EntropyBid(suit, bidAmount);
		}
		else
		{
			//Set the 'hand' variable to be everything I can see.
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			
			int bidAmountFacedWith = bid.getBidAmount();
			var bidSuitFacedWith = bid.getBidSuit();
			
			int bidSuitCount = countSuit(bidSuitFacedWith, hand, jokerValue);
			int minBiddableSuitCount = suitWrapper.getSuitsPossibleToMinBid().size();
			int choice = rand.nextInt(4);
			
			if (minBiddableSuitCount == suitsInPlay)
			{
				log("I can safely minbid in all suits.", logging);
				if (choice <= 1)
				{
					//0 or 1
					log("One-upped (50%)", logging);
					return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
				}
				else if (choice == 2)
				{
					log("Minbidding best or worst (25%)", logging);
					if (bestSuit == bidSuitFacedWith)
					{
						log("BestSuit = bidSuitFacedWith = " + bestSuit + ". Bidding worstSuit: " + worstSuit, logging);
						return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, worstSuit, logging);
					}
					else
					{
						log("Bidding bestSuit as it's different from what I'm faced with.", logging);
						return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, bestSuit, logging);
					}
				}
				else
				{
					log("Minbid random middle suit (25%)", logging);
					var suitToBid = suitWrapper.getRandomMiddleSuit();
					return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
				}
			}
			else if (minBiddableSuitCount >= 3)
			{
				log("I can safely minbid at least 3 suits", logging);
				if (bidSuitCount > bidAmountFacedWith)
				{
					log("This includes the suit I'm faced with", logging);
					if (choice <= 1)
					{
						log("One-upping (50%)", logging);
						return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
					}
					else
					{
						log("Bidding random other suit (50%)", logging);
						var suitToBid = suitWrapper.randomSuitNot(bidSuitFacedWith);
						return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
					}
				}
				else
				{
					log("This doesn't include the suit I'm faced with", logging);
					if (choice <= 1)
					{
						log("Minbidding my best suit (50%)", logging);
						return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, bestSuit, logging);
					}
					else
					{
						log("Minbidding random other suit (50%)", logging);
						var suitToBid = suitWrapper.randomSuitNot(bestSuit);
						return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
					}
				}
			}
			else if (minBiddableSuitCount == 2)
			{
				log("I can safely minbid 2 suits", logging);
				if (choice <= 1)
				{
					log("One-upping (50%)", logging);
					return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
				}
				else
				{
					///minbid one of the two suits I can
					log("Minbidding one of the two suits (50%)", logging);
					var suitToBid = suitWrapper.randomSuitPossibleToMinBid();
					return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
				}
			}
			else if (minBiddableSuitCount == 1)
			{
				var suitToBid = suitWrapper.randomSuitPossibleToMinBid();
				log("Could only minbid suit " + suitToBid + ", so minbidding that.", logging);
				return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, suitToBid, logging);
			}
			else
			{
				log("Couldn't automatically minbid any suit", logging);
				double jokerThreshold = Math.floor((jokerQuantity * jokerValue)/(double)(suitsInPlay*2));
				double threshold = bidSuitCount + totalCards - hand.size() + jokerThreshold - 1;
				
				log("Bonkers threshold is " + threshold, logging);
				if (bidAmountFacedWith > threshold)
				{
					log("Auto-challenging because " + bidAmountFacedWith + " > " + threshold, logging);
					return new ChallengeBid();
				}
				else if (bidAmountFacedWith > bestSuitCount + bidSuitCount + quarterThreshold)
				{
					log("Auto-challenging because bestSuitCount + bidSuitCount + quarterThreshold = " + (bestSuitCount + bidSuitCount + quarterThreshold), logging);
					return new ChallengeBid();
				}
				else if (bidAmountFacedWith > 1 + quarterThreshold)
				{
					log("BidAmountFacedWith > " + (1+quarterThreshold), logging);
					if (choice <= 2)
					{
						log("One-upping (75%)", logging);
						return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
					}
					else
					{
						log("Challenging (25%)", logging);
						return new ChallengeBid();
					}
				}
				else
				{
					log("BidAmountFacedWith is only 1, so not challenging.", logging);
					if (choice <= 1)
					{
						log("One-upping (50%)", logging);
						return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
					}
					else
					{
						log("Minbidding my best suit (50%)", logging);
						return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, bestSuit, logging);
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
			log("Chose best suit: " + bestSuit, logging);
			return bestSuit;
		}
		else if (choice == 1)
		{
			var worstSuit = suitWrapper.getWorstSuit();
			log("Chose worst suit: " + worstSuit, logging);
			return worstSuit;
		}
		else
		{
			var randomSuit = suitWrapper.getRandomMiddleSuit();
			log("Chose random middle suit: " + randomSuit, logging);
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
	
	private static Bid processBasicTurn(Player opponent, StrategyParams parms)
	{
		//Parms
		var settings = parms.getSettings();
		EntropyBid bid = (EntropyBid)parms.getLastBid();
		boolean includeMoons = settings.getIncludeMoons();
		boolean includeStars = settings.getIncludeStars();
		int jokerValue = settings.getJokerValue();
		boolean logging = parms.getLogging();
		
		log("Basic strategy for this turn", logging);
		Random coin = new Random();
		int decision = coin.nextInt(2);
		int decisionTwo = coin.nextInt(2);
		List<String> hand = opponent.getHand();

		if (bid == null)
		{
			log("Starting this round", logging);

			var suit = Suit.random(includeMoons, includeStars);
			int bidAmount = Math.max(1, countSuit(suit, hand, jokerValue) + coin.nextInt(2));
			
			return new EntropyBid(suit, bidAmount);
		}
		else
		{
			//Set the 'hand' variable to be everything I can see.
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			
			int bidAmountFacedWith = bid.getBidAmount();
			var bidSuitFacedWith = bid.getBidSuit();
			
			int bidSuitCount = countSuit(bidSuitFacedWith, hand, jokerValue);
			double totalCards = parms.getCardsInPlay();

			int thirdThreshold = (int) Math.floor(totalCards/3.5);
			int halfThreshold = (int) Math.ceil(totalCards/2);
			log("thirdThreshold = " + thirdThreshold, logging);
			log("halfThreshold = " + halfThreshold, logging);

			if (bidAmountFacedWith <= bidSuitCount)
			{
				if (decisionTwo == 1)
				{
					log("Opponent " + opponent + " auto-minbid", logging);
					return opponentMinBid(bidSuitFacedWith, bidAmountFacedWith, includeMoons, includeStars);
				}
				else
				{
					log("Opponent " + opponent + " auto-oneupped", logging);
					return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
				}
			}
			else if (bidAmountFacedWith < bidSuitCount + thirdThreshold)
			{
				if (decisionTwo == 1)
				{
					log("Opponent " + opponent + " auto-minbid", logging);
					return opponentMinBid(bidSuitFacedWith, bidAmountFacedWith, includeMoons, includeStars);
				}
				else
				{
					log("Opponent " + opponent + " auto-oneupped", logging);
					return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
				}
			}
			else if (bidAmountFacedWith > halfThreshold)
			{
				log("Opponent " + opponent + " auto-challenged", logging);
				return new ChallengeBid();
			}
			else if (decision == 0)
			{
				log("Opponent " + opponent + " flip-challenged", logging);
				return new ChallengeBid();
			}
			else
			{
				if (decisionTwo == 0)
				{
					log("Opponent " + opponent + " flip-minbid", logging);
					return opponentMinBid(bidSuitFacedWith, bidAmountFacedWith, includeMoons, includeStars);
				}
				else
				{
					log("Opponent " + opponent + " flip-oneupped", logging);
					return opponentOneUp(bidSuitFacedWith, bidAmountFacedWith, logging);
				}
			}
		}
	}
	
	private static Bid processEvTurnAndRevealCard(Player opponent, StrategyParams parms)
	{
		Bid bid = processEvTurn(opponent, parms);
		CpuStrategies.setCardToReveal(bid, parms.getSettings(), opponent);
		return bid;
	}
	
	private static Bid processEvTurn(Player opponent, StrategyParams parms)
	{
		//Parms
		var settings = parms.getSettings();
		EntropyBid bid = (EntropyBid)parms.getLastBid();
		int totalCards = parms.getCardsInPlay();
		boolean includeMoons = settings.getIncludeMoons();
		boolean includeStars = settings.getIncludeStars();
		int jokerValue = settings.getJokerValue();
		boolean logging = parms.getLogging();
		
		log("EV strategy for this turn", logging);
		Random coin = new Random();
		List<String> hand = opponent.getHand();
		
		int totalOpponentCards = totalCards - hand.size();
		if (bid == null)
		{
			log("Starting this round", logging);
			Map<Suit, Double> hmEvBySuit = getEvMap(hand, parms.getSettings(), parms.getCardsInPlay());
			
			var suit = Suit.random(includeMoons, includeStars);
			double suitEv = hmEvBySuit.get(suit);
			
			int suitEvRounded = (int) Math.ceil(suitEv);
			int randomAmount = coin.nextInt(5) - 4; //-4, -3, -2, 1, 0
			int bidAmount = Math.max(1, suitEvRounded + randomAmount);
			
			return new EntropyBid(suit, bidAmount);
		}
		else
		{
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			Map<Suit, Double> hmEvBySuit = getEvMap(hand, parms.getSettings(), parms.getCardsInPlay());
			
			int bidAmountFacedWith = bid.getBidAmount();
			var bidSuitFacedWith = bid.getBidSuit();
			double expectedValueForBid = hmEvBySuit.get(bidSuitFacedWith);
			
			log("EV calculation for bid of " + bidAmountFacedWith + " " + bidSuitFacedWith.getDescription(bidAmountFacedWith) + ": " + expectedValueForBid, logging);

			double maxEv = getMaxValue(hmEvBySuit);
			var suitsWithMax = getSuitsWithMostPositiveValue(hmEvBySuit);
			
			if (bidAmountFacedWith > expectedValueForBid + 1)
			{
				return new ChallengeBid();
			}
			else
			{
				int suitsToChooseFrom = suitsWithMax.size();
				int choice = coin.nextInt(suitsToChooseFrom);

				Suit suit = suitsWithMax.get(choice);

				if (maxEv > bidAmountFacedWith - 1 && totalOpponentCards > 1)
				{
					return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, suit, logging);
				}
				else if (maxEv > bidAmountFacedWith - 1)
				{
					//bit friggy - special case for being up against one card. Don't bid higher if it means you need them to have the ace/joker.
					suit = suitsWithMax.get(suitsToChooseFrom-1);
					int bidAmount = amountRequiredToBid(suit, bidSuitFacedWith, bidAmountFacedWith);
					int amountRequiredInOneCard = bidAmount - countSuit(suit, hand, jokerValue);
					
					if (amountRequiredInOneCard < 2)
					{
						return opponentMinBidSuit(bidSuitFacedWith, bidAmountFacedWith, suit, logging);
					}
					else
					{
						log("Bidding would've needed >1 in one card, so challenged", logging);
						return new ChallengeBid();
					}
				}
				else
				{
					log("Couldn't bid anything 'safely', so challenged.", logging);
					return new ChallengeBid();
				}
			}
		}
	}

	private static EntropyBid opponentMinBid(Suit bidSuitFacedWith, int bidAmount,
	  boolean includeMoons, boolean includeStars) 
	{
		var nextSuit = bidSuitFacedWith.next(includeMoons, includeStars);
		var myAmount = nextSuit.lessThan(bidSuitFacedWith) ? bidAmount + 1 : bidAmount;
		return new EntropyBid(nextSuit, myAmount);
	}

	private static EntropyBid opponentOneUp(Suit bidSuitFacedWith, int bidAmount, boolean logging)
	{
		log("One Up", logging);
		return new EntropyBid(bidSuitFacedWith, bidAmount + 1);
	}

	private static EntropyBid opponentMinBidSuit(Suit bidSuitFacedWith, int bidAmountFacedWith, Suit suitToBid, boolean logging)
	{
		log("MinBidSuit " + suitToBid, logging);

		int bidAmount = 0;
		if (bidSuitFacedWith.lessThan(suitToBid))
		{
			bidAmount = bidAmountFacedWith;
		}
		else
		{
			bidAmount = bidAmountFacedWith + 1;
		}
		
		return new EntropyBid(suitToBid, bidAmount);
	}

	private static void log(String text, boolean logging) {
		if (logging) {
			logger.info("strategy.debug", text);
		}
	}
}