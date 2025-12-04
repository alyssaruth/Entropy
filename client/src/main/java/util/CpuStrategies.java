package util;

import game.*;
import object.*;
import strategy.ApiStrategy;
import strategy.StrategyParams;
import strategy.StrategyUtilKt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static utils.CoreGlobals.logger;

public class CpuStrategies
{
	public static final String STRATEGY_BASIC = "Easy";
	public static final String STRATEGY_EV = "Hard";
	
	public static Vector<String> getAllStrategies(GameMode gameMode, List<ApiStrategy> apiStrategies)
	{
		Vector<String> allStrategies = getFixedStrategies(gameMode);
		
		//Append the relevant API strategies
		if (apiStrategies == null)
		{
			apiStrategies = StrategyUtilKt.getApiStrategiesFromPreferences();
		}
		
		appendRelevantStrategies(allStrategies, apiStrategies, gameMode);
		return allStrategies;
	}
	
	private static void appendRelevantStrategies(Vector<String> allStrategies, 
	  List<ApiStrategy> apiStrategies, GameMode gameMode)
	{
		int size = apiStrategies.size();
		for (int i=0; i<size; i++)
		{
			ApiStrategy apiStrategy = apiStrategies.get(i);
			boolean supportsMode = apiStrategy.getSupportedModes().contains(gameMode);
			String error = apiStrategy.getLastError();
			if (supportsMode
			  && error == null)
			{
				String name = ApiUtil.API_PREFIX + apiStrategy.getName();
				allStrategies.add(name);
			}
		}
	}
	
	private static Vector<String> getFixedStrategies(GameMode gameMode)
	{
		if (gameMode == GameMode.Entropy)
		{
			return EntCpuStrategies.getAllStrategies();
		}
		
		return VectCpuStrategies.getAllStrategies();
	}
	
	/**
	 * Entry-point for strategy code
	 */
	public static PlayerAction processOpponentTurn(StrategyParams parms, Player opponent)
	{
		var settings = parms.getSettings();
		boolean entropy = settings.getMode() == GameMode.Entropy;
		PlayerAction action = getOpponentBid(parms, opponent, entropy);
		if (action == null)
		{
			return action;
		}
		
		//Set a card to reveal if we need to - specifying this is optional for the API
		setRandomCardToRevealIfNecessary(opponent, action, settings);
		
		//validate the bid...
		String error = validateAction(opponent, action, parms);
		if (error != null)
		{
			var strategy = opponent.getStrategy();
			if (strategy instanceof ApiStrategy)
			{
				//Show an error message to help diagnosing.
				String msg = "The action sent back by the third-party software [" + action + "] "
				             + "failed validation with the following error:\n\n" + error;

				StrategyUtilKt.saveStrategyErrorAndUnsetStrategies(((ApiStrategy) strategy).getId(), msg);
				DialogUtilNew.showError(msg);
			}
			else
			{
				logger.error("invalidBid", "Error validating action [" + action + "]. Error: " + error);
			}
			
			return null;
		}
		
		//Add the revealed card on the opponent object. Do this here so we don't have to duplicate the logic
		//in the simulator & actual game
		if (parms.getSettings().getCardReveal()
		  && opponent.hasMoreCardsToReveal()
		  && action instanceof BidAction)
		{
			String cardToReveal = ((BidAction)action).getCardToReveal();
			opponent.addRevealedCard(cardToReveal);
		}
		
		return action;
	}
	
	private static PlayerAction getOpponentBid(StrategyParams parms, Player opponent, boolean entropy)
	{
		var strategy = opponent.getStrategy();
		if (strategy instanceof ApiStrategy)
		{
			return ApiUtil.processApiTurn(parms, (ApiStrategy) strategy);
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
	private static void setRandomCardToRevealIfNecessary(Player opponent, PlayerAction action, GameSettings settings)
	{
		if (!(action instanceof BidAction)) {
			return;
		}

		var bid = (BidAction)action;
		if (settings.getCardReveal()
		  && opponent.hasMoreCardsToReveal()
		  && bid.getCardToReveal() == null)
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
	
	private static String validateAction(Player opponent, PlayerAction action, StrategyParams params)
	{
		var settings = params.getSettings();
		if (action instanceof ChallengeAction
		  || action instanceof IllegalAction)
		{
			return validateChallengeOrIllegal(action, params);
		}
		
		if (action instanceof EntropyBidAction)
		{
			String error = validateEntropyBid((EntropyBidAction)action, settings);
			if (error != null)
			{
				return error;
			}
		}
		
		if (action instanceof VectropyBidAction)
		{
			String error = validateVectropyBid((VectropyBidAction)action);
			if (error != null)
			{
				return error;
			}
		}

		var bid = (BidAction)action;

		BidAction lastBid = params.getLastBid();
		if (lastBid != null
		  && !bid.higherThan(lastBid))
		{
			return bid + " is not greater than " + lastBid;
		}
		
		//Validate card reveal
		if (settings.getCardReveal()
		  && opponent.hasMoreCardsToReveal())
		{
			String cardToReveal = bid.getCardToReveal();
			if (cardToReveal == null)
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
	
	private static String validateChallengeOrIllegal(PlayerAction action, StrategyParams parms)
	{
		var lastBid = parms.getLastBid();
		if (lastBid == null)
		{
			return "Called " + action.plainString() + " as an opening bid.";
		}
		
		return null;
	}
	
	private static String validateEntropyBid(EntropyBidAction bid, GameSettings settings)
	{
		Suit bidSuit = bid.getSuit();
		
		int bidAmount = bid.getAmount();
		if (bidAmount < 1)
		{
			return "Invalid bidAmount: " + bidAmount;
		}
		
		if (bidSuit == Suit.Moons
		  && !settings.getIncludeMoons())
		{
			return "Tried to bid Moons when these haven't been included.";
		}
		
		if (bidSuit == Suit.Stars
		  && !settings.getIncludeStars())
		{
			return "Tried to bid Stars when these haven't been included.";
		}
		
		return null;
	}
	
	private static String validateVectropyBid(VectropyBidAction bid)
	{
		if (bid.getTotal() < 1)
		{
			return "Elements sum to less than 1.";
		}

		for (var suit : Suit.getEntries()) {
			var amount = bid.getAmount(suit);
			if (amount != null && amount < 0) {
				return "Negative amount specified for suit " + suit + ".";
			}
		}
		
		return null;
	}

	/**
	 * Card reveal helpers
	 */
	public static List<String> getCombinedArrayOfCardsICanSee(List<String> hand, StrategyParams parms)
	{
		List<String> revealedCards = parms.getOpponentCardsOnShow();
		var result = new ArrayList<String>();
		result.addAll(hand);
		result.addAll(revealedCards);
		return result;
	}
	
	/**
	 * Used by EV strategies. Slightly more refined version of card reveal - this tries to show a card
	 * which isn't an Ace or a Joker (as these reveal more information than average)
	 */
	public static void setCardToReveal(PlayerAction action, GameSettings settings, Player opponent)
	{
		if (!(action instanceof BidAction bid)) {
			return;
		}

        if (settings.getCardReveal()
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
