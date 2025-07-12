package util;

import java.util.*;

import game.Suit;
import object.Bid;
import object.ChallengeBid;
import object.Player;
import object.VectropyBid;

import static game.CardsUtilKt.countSuit;
import static game.StrategyUtilKt.*;
import static utils.CoreGlobals.logger;

public class VectCpuStrategies 
{
	public static final String STRATEGY_RANDOMISE_PER_MOVE = "Randomise (per move)";
	private static final String[] STRATEGIES_TO_CHOOSE_AT_RANDOM = {CpuStrategies.STRATEGY_BASIC, CpuStrategies.STRATEGY_EV};

	public static Vector<String> getAllStrategies()
	{
		Vector<String> allStrategies = new Vector<>();
		
		allStrategies.add(CpuStrategies.STRATEGY_BASIC);
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

	private static Bid processBasicTurn(Player opponent, StrategyParams parms)
	{
		boolean logging = parms.getLogging();
		log("Basic strategy for this turn", logging);
		Random coin = new Random();
		
		//Get the variables we're interested in
		var settings = parms.getSettings();
		List<String> hand = opponent.getHand();
		VectropyBid lastBid = (VectropyBid)parms.getLastBid();
		double totalCards = parms.getCardsInPlay();
		int jokerValue = settings.getJokerValue();
		boolean includeMoons = settings.getIncludeMoons();
		boolean includeStars = settings.getIncludeStars();
		
		int clubsCount = countSuit(Suit.Clubs, hand, jokerValue);
		int diamondsCount = countSuit(Suit.Diamonds, hand, jokerValue);
		int heartsCount = countSuit(Suit.Hearts, hand, jokerValue);
		int moonsCount = countSuit(Suit.Moons, hand, jokerValue);
		int spadesCount = countSuit(Suit.Spades, hand, jokerValue);
		int starsCount = countSuit(Suit.Stars, hand, jokerValue);

		if (lastBid == null)
		{
			log("Starting this round", logging);

			if (totalCards <= 4)
			{
				VectropyBid bid = VectropyBid.factoryEmpty(includeMoons, includeStars);
				return bid.incrementSuitAndGet(Suit.random(includeMoons, includeStars));
			}
			else
			{
				int clubsBid = Math.max(0, clubsCount + coin.nextInt(3) - 1);
				int diamondsBid = Math.max(0, diamondsCount + coin.nextInt(3) - 1);
				int heartsBid = Math.max(0, heartsCount + coin.nextInt(3) - 1);
				
				int moonsBid = 0;
				if (includeMoons)
				{
					moonsBid = Math.max(0, moonsCount + coin.nextInt(3) - 1);
				}
				
				int spadesBid = Math.max(0, spadesCount + coin.nextInt(3) - 1);
				
				int starsBid = 0;
				if (includeStars)
				{
					starsBid = Math.max(0, starsCount + coin.nextInt(3) - 1);
				}

				VectropyBid bid = new VectropyBid(clubsBid, diamondsBid, heartsBid, moonsBid, spadesBid,
												  starsBid, includeMoons, includeStars);

				int newTotal = bid.getTotal();
				if (newTotal == 0)
				{
					var suit = Suit.random(includeMoons, includeStars);
					bid = bid.incrementSuitAndGet(suit);
				}

				return bid;
			}
		}
		else
		{
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			int unseenCards = parms.getCardsInPlay() - hand.size();
			
			int thirdThreshold = (int)Math.floor(totalCards/3);
			
			var diffMap = getDifferenceMap(lastBid, hand, jokerValue, includeMoons, includeStars);
			var suitWithHighestDiff = getSuitWithMostPositiveValue(diffMap);
			log("Diff vector: " + diffMap, logging);
			log("Biggest difference is for suit " + suitWithHighestDiff, logging);

			if (allNonNegative(diffMap))
			{
				log("Auto-minbid as I could see everything.", logging);
				return opponentMinBidSuit(lastBid, Suit.random(includeMoons, includeStars));
			}
			else if (shouldAutoChallengeForIndividualSuit(diffMap, thirdThreshold))
			{
				log("Auto-challenged for individual suit.", logging);
				return new ChallengeBid();
			}
			else if (shouldAutoChallengeForOverall(diffMap, unseenCards))
			{
				log("Auto-challenged for overall.", logging);
				return new ChallengeBid();
			}
			else if (bidIsSensible(diffMap, unseenCards))
			{
				int choice = coin.nextInt(10); //0-9
				
				if (choice < 5) //0,2,3,4
				{
					return opponentMinBidSuit(lastBid, suitWithHighestDiff);
				}
				else //5,6,7,8,9
				{
					return opponentMinBidSuit(lastBid, Suit.random(includeMoons, includeStars));
				}
			}
			else
			{
				//no automatic case so flip a coin
				int choice = coin.nextInt(2);
				if (choice == 0)
				{
					return opponentMinBidSuit(lastBid, Suit.random(includeMoons, includeStars));
				}
				else 
				{
					return new ChallengeBid();
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
		boolean logging = parms.getLogging();
		log("EV strategy for this turn", logging);
		Random coin = new Random();
		List<String> hand = opponent.getHand();
		
		//Parms
		VectropyBid lastBid = (VectropyBid)parms.getLastBid();
		boolean includeMoons = parms.getSettings().getIncludeMoons();
		boolean includeStars = parms.getSettings().getIncludeStars();
		
		if (lastBid == null)
		{
			log("Starting this round", logging);
			Map<Suit, Double> hmEvBySuit = getEvMap(hand, parms.getSettings(), parms.getCardsInPlay());
			log("EV HashMap = " + hmEvBySuit, logging);
			
			int clubsBid = getOpeningBidForSuitBasedOnEv(Suit.Clubs, hmEvBySuit);
			int diamondsBid = getOpeningBidForSuitBasedOnEv(Suit.Diamonds, hmEvBySuit);
			int heartsBid = getOpeningBidForSuitBasedOnEv(Suit.Hearts, hmEvBySuit);
			int moonsBid = getOpeningBidForSuitBasedOnEv(Suit.Moons, hmEvBySuit);
			int spadesBid = getOpeningBidForSuitBasedOnEv(Suit.Spades, hmEvBySuit);
			int starsBid = getOpeningBidForSuitBasedOnEv(Suit.Stars, hmEvBySuit);
			
			VectropyBid bid = new VectropyBid(clubsBid, diamondsBid, heartsBid, moonsBid, spadesBid,
											  starsBid, includeMoons, includeStars);

			int newTotal = bid.getTotal();
			if (newTotal == 0)
			{
				int choice = coin.nextInt(10);
				Suit suit;
				if (choice < 6)
				{
					suit = getSuitWithMostPositiveValue(hmEvBySuit);
				}
				else
				{
					suit = Suit.random(includeMoons, includeStars);
				}
				
				bid = bid.incrementSuitAndGet(suit);
			}

			return bid;
		}
		else
		{
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			Map<Suit, Double> hmEvBySuit = getEvMap(hand, parms.getSettings(), parms.getCardsInPlay());
			log("EV HashMap = " + hmEvBySuit, logging);

			Map<Suit, Double> hmEvDifferenceBySuit = computeEvDifferences(lastBid, hmEvBySuit);
			
			Suit suitWithHighestDiff = getSuitWithMostPositiveValue(hmEvDifferenceBySuit);
			log("EV diff vector: " + hmEvDifferenceBySuit, logging);
			log("Biggest difference is for suit " + suitWithHighestDiff, logging);

			if (belowEvInAllSuits(hmEvDifferenceBySuit))
			{
				log("Auto-minbid as bid is below EV in all suits.", logging);
				return opponentMinBidSuit(lastBid, suitWithHighestDiff);
			}
			else if (shouldAutoChallengeForEvDiffOfIndividualSuit(hmEvDifferenceBySuit))
			{
				return new ChallengeBid();
			}
			else if (shouldAutoChallengeForMultipleSuitsOverEv(hmEvDifferenceBySuit))
			{
				return new ChallengeBid();
			}
			else
			{
				return opponentMinBidSuit(lastBid, suitWithHighestDiff);
			}
		}
	}
	
	private static int getOpeningBidForSuitBasedOnEv(Suit suit, Map<Suit, Double> hmEvBySuit)
	{
		if (!hmEvBySuit.containsKey(suit)) {
			return 0;
		}

		double ev = hmEvBySuit.get(suit);
		int evFloor = (int)Math.floor(ev);
		
		Random random = new Random();
		int adjustmentChoice = random.nextInt(20);
		
		if (adjustmentChoice < 11)
		{
			evFloor -= 1;
		}
		else if (adjustmentChoice < 18)
		{
			evFloor -= 2;
		}
		else
		{
			evFloor -= 3;
		}
		
		return Math.max(evFloor, 0);
	}

	private static VectropyBid opponentMinBidSuit(VectropyBid lastBid, Suit suit)
	{
		return lastBid.incrementSuitAndGet(suit);
	}

	private static void log(String text, boolean logging) {
		if (logging) {
			logger.info("strategy.debug", text);
		}
	}
}