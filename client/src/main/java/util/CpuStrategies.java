package util;

import game.GameMode;
import object.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static utils.CoreGlobals.logger;

public class CpuStrategies
{
	public static final String STRATEGY_BASIC = "Easy";
	public static final String STRATEGY_EV = "Hard";
	
	public static Vector<String> getAllStrategies(boolean entropy, ArrayList<ApiStrategy> apiStrategies)
	{
		Vector<String> allStrategies = getFixedStrategies(entropy);
		
		//Append the relevant API strategies
		if (apiStrategies == null)
		{
			apiStrategies = ApiUtil.getApiStrategiesFromPreferences();
		}
		
		appendRelevantStrategies(allStrategies, apiStrategies, entropy);
		return allStrategies;
	}
	
	private static void appendRelevantStrategies(Vector<String> allStrategies, 
	  ArrayList<ApiStrategy> apiStrategies, boolean entropy)
	{
		int size = apiStrategies.size();
		for (int i=0; i<size; i++)
		{
			ApiStrategy apiStrategy = apiStrategies.get(i);
			boolean supportsMode = entropy ? apiStrategy.getEntropy():apiStrategy.getVectropy();
			String error = apiStrategy.getError();
			if (supportsMode
			  && error.isEmpty())
			{
				String name = ApiUtil.API_PREFIX + apiStrategy.getName();
				allStrategies.add(name);
			}
		}
	}
	
	private static Vector<String> getFixedStrategies(boolean entropy)
	{
		if (entropy)
		{
			return EntCpuStrategies.getAllStrategies();
		}
		
		return VectCpuStrategies.getAllStrategies();
	}
	
	/**
	 * Entry-point for strategy code
	 */
	public static Bid processOpponentTurn(StrategyParms parms, Player opponent)
	{
		boolean entropy = parms.getGameMode() == GameMode.Entropy;
		Bid bid = getOpponentBid(parms, opponent, entropy);
		if (bid == null)
		{
			return bid;
		}
		
		//Set a card to reveal if we need to - specifying this is optional for the API
		setRandomCardToRevealIfNecessary(opponent, bid, parms);
		
		//validate the bid...
		String error = validateBid(opponent, bid, parms);
		if (error != null)
		{
			if (opponent.isApiStrategy())
			{
				//Show an error message to help diagnosing.
				String msg = "The bid sent back by the third-party software [" + bid + "] "
				             + "failed validation with the following error:\n\n" + error;
				
				String strategyStr = opponent.getStrategy();
				ApiUtil.saveStrategyErrorAndUnsetStrategies(ApiUtil.getApiStrategy(strategyStr), msg);
				DialogUtil.showError(msg);
			}
			else
			{
				logger.error("invalidBid", "Error validating bid [" + bid + "]. Error: " + error);
			}
			
			return null;
		}
		
		//Add the revealed card on the opponent object. Do this here so we don't have to duplicate the logic
		//in the simulator & actual game
		if (parms.getCardReveal()
		  && opponent.hasMoreCardsToReveal())
		{
			String cardToReveal = bid.getCardToReveal();
			opponent.addRevealedCard(cardToReveal);
		}
		
		return bid;
	}
	
	private static Bid getOpponentBid(StrategyParms parms, Player opponent, boolean entropy)
	{
		if (opponent.isApiStrategy())
		{
			return ApiUtil.processApiTurn(parms, opponent);
		}
		else if (entropy)
		{
			return EntCpuStrategies.processOpponentTurn(opponent, parms);
		}
		else
		{
			return VectCpuStrategies.processOpponentTurn(opponent, parms);
		}
	}
	
	/**
	 * If we're playing in reveal mode, there are still cards to reveal and there isn't a revealCard set on the bid,
	 * then just pick one at random. This is what most built-in strategies will do, and implementing for API too
	 * so that worrying about revealing cards is optional.
	 */
	private static void setRandomCardToRevealIfNecessary(Player opponent, Bid bid, StrategyParms parms)
	{
		if (parms.getCardReveal()
		  && opponent.hasMoreCardsToReveal()
		  && bid.getCardToReveal().isEmpty())
		{
			//Pick a card at random to reveal. 
			ArrayList<String> cardsNotOnShow = opponent.getCardsNotOnShow();
			int size = cardsNotOnShow.size();
			
			Random rand = new Random();
			int idx = rand.nextInt(size);
			String cardToShow = cardsNotOnShow.get(idx);
			bid.setCardToReveal(cardToShow);
		}
	}
	
	private static String validateBid(Player opponent, Bid bid, StrategyParms parms)
	{
		if (bid.isChallenge()
		  || bid.isIllegal())
		{
			return validateChallengeOrIllegal(bid, parms);
		}
		
		if (bid instanceof EntropyBid)
		{
			String error = validateEntropyBid((EntropyBid)bid, parms);
			if (error != null)
			{
				return error;
			}
		}
		
		if (bid instanceof VectropyBid)
		{
			String error = validateVectropyBid((VectropyBid)bid);
			if (error != null)
			{
				return error;
			}
		}
		
		Bid lastBid = parms.getLastBid();
		if (lastBid != null
		  && !bid.higherThan(lastBid))
		{
			return bid + " is not greater than " + lastBid;
		}
		
		//Validate card reveal
		if (parms.getCardReveal()
		  && opponent.hasMoreCardsToReveal())
		{
			String cardToReveal = bid.getCardToReveal();
			if (cardToReveal.isEmpty())
			{
				return "A card was not specified to be revealed.";
			}
			
			if (!opponent.handContainsCard(cardToReveal))
			{
				return "Strategy specified [" + cardToReveal + "] to show, but this is not in the players hand.";
			}
			
			ArrayList<String> revealedCards = opponent.getRevealedCards();
			if (revealedCards.contains(cardToReveal))
			{
				return "Strategy specified [" + cardToReveal + "] to show, but this has already been revealed.";
			}
		}
		
		return null;
	}
	
	private static String validateChallengeOrIllegal(Bid bid, StrategyParms parms)
	{
		Bid lastBid = parms.getLastBid();
		if (lastBid == null)
		{
			if (bid.isChallenge())
			{
				return "Challenged as an opening bid.";
			}
			
			return "Called 'Illegal!' as an opening bid.";
		}
		
		return null;
	}
	
	private static String validateEntropyBid(EntropyBid bid, StrategyParms parms)
	{
		int bidSuitCode = bid.getBidSuitCode();
		if (bidSuitCode < CardsUtil.SUIT_CLUBS
		  || bidSuitCode > CardsUtil.SUIT_STARS)
		{
			return "Invalid suitCode: " + bidSuitCode;
		}
		
		int bidAmount = bid.getBidAmount();
		if (bidAmount < 1)
		{
			return "Invalid bidAmount: " + bidAmount;
		}
		
		if (bidSuitCode == CardsUtil.SUIT_MOONS
		  && !parms.getIncludeMoons())
		{
			return "Tried to bid Moons when these haven't been included.";
		}
		
		if (bidSuitCode == CardsUtil.SUIT_STARS
		  && !parms.getIncludeStars())
		{
			return "Tried to bid Stars when these haven't been included.";
		}
		
		return null;
	}
	
	private static String validateVectropyBid(VectropyBid bid)
	{
		if (bid.getTotal() < 1)
		{
			return "Elements sum to less than 1.";
		}
		
		if (bid.getClubs() < 0
		  || bid.getDiamonds() < 0
		  || bid.getHearts() < 0
		  || bid.getMoons() < 0
		  || bid.getSpades() < 0
		  || bid.getStars() < 0)
		{
			return "Negative amount specified for a suit.";
		}
		
		return null;
	}

	/**
	 * Card reveal helpers
	 */
	public static List<String> getCombinedArrayOfCardsICanSee(List<String> hand, StrategyParms parms)
	{
		ArrayList<String> revealedCards = parms.getCardsOnShowFromOpponents();
		var result = new ArrayList<String>();
		result.addAll(hand);
		result.addAll(revealedCards);
		return result;
	}
	
	/**
	 * Used by EV strategies. Slightly more refined version of card reveal - this tries to show a card
	 * which isn't an Ace or a Joker (as these reveal more information than average)
	 */
	public static void setCardToReveal(Bid bid, StrategyParms parms, Player opponent)
	{
		if (!bid.isIllegal()
		  && !bid.isChallenge()
		  && parms.getCardReveal()
		  && opponent.hasMoreCardsToReveal())
		{
			ArrayList<String> cardsToChooseFrom = opponent.getCardsNotOnShow();
			int size = cardsToChooseFrom.size();
			for (int i=size-1; i>=0; i--)
			{
				String card = cardsToChooseFrom.get(i);
				if (card.startsWith("A")
				  || card.startsWith("Jo"))
				{
					cardsToChooseFrom.remove(i);
				}
			}
			
			int newSize = cardsToChooseFrom.size();
			if (newSize > 0)
			{
				Random rand = new Random();
				int idx = rand.nextInt(newSize);
				String cardToReveal = cardsToChooseFrom.get(idx);
				bid.setCardToReveal(cardToReveal);
			}
		}
	}
}
