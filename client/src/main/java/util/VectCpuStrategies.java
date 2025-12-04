package util;

import java.util.*;

import game.ChallengeAction;
import game.PlayerAction;
import game.Suit;
import game.VectropyBidAction;
import object.Player;
import strategy.StrategyParams;

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
	
	public static PlayerAction processOpponentTurn(Player opponent, StrategyParams parms)
	{
		String strategy = opponent.getStrategy().getName();
		return processOpponentTurn(strategy, opponent, parms);
	}
	private static PlayerAction processOpponentTurn(String strategy, Player opponent, StrategyParams parms)
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

	private static PlayerAction processBasicTurn(Player opponent, StrategyParams parms)
	{
		boolean logging = parms.getLogging();
		log("Basic strategy for this turn", logging);
		Random coin = new Random();
		
		//Get the variables we're interested in
		var settings = parms.getSettings();
		List<String> hand = opponent.getHand();
		VectropyBidAction lastBid = (VectropyBidAction)parms.getLastBid();
		double totalCards = parms.getCardsInPlay();
		int jokerValue = settings.getJokerValue();
		boolean includeMoons = settings.getIncludeMoons();
		boolean includeStars = settings.getIncludeStars();

		if (lastBid == null)
		{
			log("Starting this round", logging);
			return getBasicVectropyOpening(opponent.getName(), hand, parms);
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
				return opponentMinBidSuit(opponent, lastBid, Suit.random(includeMoons, includeStars));
			}
			else if (shouldAutoChallengeForIndividualSuit(diffMap, thirdThreshold))
			{
				log("Auto-challenged for individual suit.", logging);
				return new ChallengeAction(opponent.getName(), false);
			}
			else if (shouldAutoChallengeForOverall(diffMap, unseenCards))
			{
				log("Auto-challenged for overall.", logging);
				return new ChallengeAction(opponent.getName(), false);
			}
			else if (bidIsSensible(diffMap, unseenCards))
			{
				int choice = coin.nextInt(10); //0-9
				
				if (choice < 5) //0,2,3,4
				{
					return opponentMinBidSuit(opponent, lastBid, suitWithHighestDiff);
				}
				else //5,6,7,8,9
				{
					return opponentMinBidSuit(opponent, lastBid, Suit.random(includeMoons, includeStars));
				}
			}
			else
			{
				//no automatic case so flip a coin
				int choice = coin.nextInt(2);
				if (choice == 0)
				{
					return opponentMinBidSuit(opponent, lastBid, Suit.random(includeMoons, includeStars));
				}
				else 
				{
					return new ChallengeAction(opponent.getName(), false);
				}
			}
		}
	}
	
	private static PlayerAction processEvTurnAndRevealCard(Player opponent, StrategyParams parms)
	{
		var action = processEvTurn(opponent, parms);
		CpuStrategies.setCardToReveal(action, parms.getSettings(), opponent);
		return action;
	}
	
	private static PlayerAction processEvTurn(Player opponent, StrategyParams parms)
	{
		boolean logging = parms.getLogging();
		log("EV strategy for this turn", logging);
		List<String> hand = opponent.getHand();
		
		//Parms
		VectropyBidAction lastBid = (VectropyBidAction)parms.getLastBid();
		
		if (lastBid == null)
		{
			log("Starting this round", logging);
			return getEvVectropyOpening(opponent.getName(), hand, parms);
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
				return opponentMinBidSuit(opponent, lastBid, suitWithHighestDiff);
			}
			else if (shouldAutoChallengeForEvDiffOfIndividualSuit(hmEvDifferenceBySuit))
			{
				return new ChallengeAction(opponent.getName(), false);
			}
			else if (shouldAutoChallengeForMultipleSuitsOverEv(hmEvDifferenceBySuit))
			{
				return new ChallengeAction(opponent.getName(), false);
			}
			else
			{
				return opponentMinBidSuit(opponent, lastBid, suitWithHighestDiff);
			}
		}
	}

	private static VectropyBidAction opponentMinBidSuit(Player opponent, VectropyBidAction lastBid, Suit suit)
	{
		var map = lastBid.incrementSuit(suit).getAmounts();
		return new VectropyBidAction(opponent.getName(), false, map);
	}

	private static void log(String text, boolean logging) {
		if (logging) {
			logger.info("strategy.debug", text);
		}
	}
}