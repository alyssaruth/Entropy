package util;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import object.Bid;
import object.ChallengeBid;
import object.Player;
import object.VectropyBid;

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
		Debug.append("Basic strategy for this turn", logging);
		Random coin = new Random();
		
		//Get the variables we're interested in
		var settings = parms.getSettings();
		List<String> hand = opponent.getHand();
		VectropyBid lastBid = (VectropyBid)parms.getLastBid();
		double totalCards = parms.getCardsInPlay();
		int jokerValue = settings.getJokerValue();
		boolean includeMoons = settings.getIncludeMoons();
		boolean includeStars = settings.getIncludeStars();
		
		int clubsCount = CardsUtil.countClubs(hand, jokerValue);
		int diamondsCount = CardsUtil.countDiamonds(hand, jokerValue);
		int heartsCount = CardsUtil.countHearts(hand, jokerValue);
		int moonsCount = CardsUtil.countMoons(hand, jokerValue);
		int spadesCount = CardsUtil.countSpades(hand, jokerValue);
		int starsCount = CardsUtil.countStars(hand, jokerValue);

		if (lastBid == null)
		{
			Debug.append("Starting this round", logging);

			if (totalCards <= 4)
			{
				VectropyBid bid = VectropyUtil.getEmptyBid(includeMoons, includeStars);
				return bid.incrementSuitAndGet(StrategyUtil.getRandomSuit(includeMoons, includeStars));
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
					int suit = StrategyUtil.getRandomSuit(includeMoons, includeStars);
					bid = bid.incrementSuitAndGet(suit);
				}

				return bid;
			}
		}
		else
		{
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			double unseenCards = parms.getCardsInPlay() - hand.size();
			
			double thirdThreshold = Math.floor(totalCards/3);
			
			double[] diffVector = VectropyUtil.getDifferenceVector(lastBid, hand, jokerValue, includeMoons, includeStars);
			int suitWithHighestDiff = VectropyUtil.getSuitWithMostPositiveDifference(diffVector, includeMoons);
			Debug.append("Diff vector: " + VectropyUtil.getReadableString(diffVector), logging);
			Debug.append("Biggest difference is for suit " + suitWithHighestDiff, logging);

			if (VectropyUtil.canSeeBid(diffVector))
			{
				Debug.append("Auto-minbid as I could see everything.", logging);
				return opponentMinBidSuit(lastBid, StrategyUtil.getRandomSuit(includeMoons, includeStars));
			}
			else if (VectropyUtil.shouldAutoChallengeForIndividualSuit(diffVector, thirdThreshold))
			{
				Debug.append("Auto-challenged for individual suit.", logging);
				return new ChallengeBid();
			}
			else if (VectropyUtil.shouldAutoChallengeForOverallBid(diffVector, unseenCards))
			{
				Debug.append("Auto-challenged for overall.", logging);
				return new ChallengeBid();
			}
			else if (VectropyUtil.bidIsSensible(diffVector, unseenCards, logging))
			{
				int choice = coin.nextInt(10); //0-9
				
				if (choice < 5) //0,2,3,4
				{
					return opponentMinBidSuit(lastBid, suitWithHighestDiff);
				}
				else //5,6,7,8,9
				{
					return opponentMinBidSuit(lastBid, StrategyUtil.getRandomSuit(includeMoons, includeStars));
				}
			}
			else
			{
				//no automatic case so flip a coin
				int choice = coin.nextInt(2);
				if (choice == 0)
				{
					return opponentMinBidSuit(lastBid, StrategyUtil.getRandomSuit(includeMoons, includeStars));
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
		Debug.append("EV strategy for this turn", logging);
		Random coin = new Random();
		List<String> hand = opponent.getHand();
		
		//Parms
		VectropyBid lastBid = (VectropyBid)parms.getLastBid();
		boolean includeMoons = parms.getSettings().getIncludeMoons();
		boolean includeStars = parms.getSettings().getIncludeStars();
		
		if (lastBid == null)
		{
			Debug.append("Starting this round", logging);
			HashMap<Integer, Double> hmEvBySuit = StrategyUtil.getEvBySuitHashMapIncludingMyHand(hand, parms);
			Debug.append("EV HashMap = " + hmEvBySuit, logging);
			
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
				int suit = -1;
				if (choice < 6)
				{
					suit = VectropyUtil.getSuitWithHighestValue(hmEvBySuit);
				}
				else
				{
					suit = StrategyUtil.getRandomSuit(includeMoons, includeStars);
				}
				
				bid = bid.incrementSuitAndGet(suit);
			}

			return bid;
		}
		else
		{
			hand = CpuStrategies.getCombinedArrayOfCardsICanSee(hand, parms);
			HashMap<Integer, Double> hmEvBySuit = StrategyUtil.getEvBySuitHashMapIncludingMyHand(hand, parms);
			Debug.append("EV HashMap = " + hmEvBySuit, logging);
			
			double[] diffVector = VectropyUtil.getEvDifferenceVector(lastBid, hmEvBySuit, includeMoons, includeStars);
			
			int suitWithHighestDiff = VectropyUtil.getSuitWithMostPositiveDifference(diffVector, includeMoons);
			Debug.append("EV diff vector: " + VectropyUtil.getReadableString(diffVector), logging);
			Debug.append("Biggest difference is for suit " + suitWithHighestDiff, logging);

			if (VectropyUtil.isBelowEvInAllSuits(diffVector))
			{
				Debug.append("Auto-minbid as bid is below EV in all suits.", logging);
				return opponentMinBidSuit(lastBid, suitWithHighestDiff);
			}
			else if (VectropyUtil.shouldAutoChallengeForEvOfIndividualSuit(diffVector))
			{
				return new ChallengeBid();
			}
			else if (VectropyUtil.shouldAutoChallengeForOverallEvOfBid(diffVector))
			{
				return new ChallengeBid();
			}
			else if (VectropyUtil.shouldAutoChallengeForMultipleSuitsOverEv(diffVector))
			{
				return new ChallengeBid();
			}
			else
			{
				return opponentMinBidSuit(lastBid, suitWithHighestDiff);
			}
		}
	}
	
	private static int getOpeningBidForSuitBasedOnEv(int suit, HashMap<Integer, Double> hmEvBySuit)
	{
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

	private static VectropyBid opponentMinBidSuit(VectropyBid lastBid, int suit) 
	{
		return lastBid.incrementSuitAndGet(suit);
	}
}