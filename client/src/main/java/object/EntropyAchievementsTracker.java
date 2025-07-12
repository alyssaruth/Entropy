package object;

import achievement.AchievementUtilKt;
import game.EntropyBidAction;
import game.Suit;
import util.AchievementsUtil;
import util.Registry;
import utils.Achievement;

import static game.CardsUtilKt.isCardRelevant;

public class EntropyAchievementsTracker implements Registry
{
	private boolean earnedCaveman = false;
	private boolean earnedBurglar = false;
	private boolean earnedLion = false;
	private boolean earnedWerewolf = false;
	private boolean earnedGardener = false;
	private boolean earnedSpaceman = false;
	
	private boolean revealedSameSuit = false;
	private boolean revealedDifferentSuit = false;
	private int cardsRevealed = 0;
	
	private Suit firstSuitBid = null;
	private boolean deviatedFromFirstSuit = false;
	
	public void reset()
	{
		earnedGardener = false;
		earnedLion = false;
		earnedBurglar = false;
		earnedCaveman = false;
		earnedWerewolf = false;
		earnedSpaceman = false;
		
		revealedSameSuit = false;
		revealedDifferentSuit = false;
		cardsRevealed = 0;
		
		firstSuitBid = null;
		deviatedFromFirstSuit = false;
	}
	
	public void loadState()
	{
		earnedCaveman = savedGame.getBoolean(SAVED_GAME_BOOLEAN_CAVEMAN, false);
		earnedBurglar = savedGame.getBoolean(SAVED_GAME_BOOLEAN_BURGLAR, false);
		earnedLion = savedGame.getBoolean(SAVED_GAME_BOOLEAN_LION, false);
		earnedGardener = savedGame.getBoolean(SAVED_GAME_BOOLEAN_GARDENER, false);
		earnedWerewolf = savedGame.getBoolean(SAVED_GAME_BOOLEAN_WEREWOLF, false);
		earnedSpaceman = savedGame.getBoolean(SAVED_GAME_BOOLEAN_SPACEMAN, false);
		
		revealedDifferentSuit = savedGame.getBoolean(SAVED_GAME_BOOLEAN_REVEALED_DIFFERENT_SUIT, false);
		revealedSameSuit = savedGame.getBoolean(SAVED_GAME_BOOLEAN_REVEALED_SAME_SUIT, false);
		cardsRevealed = savedGame.getInt(SAVED_GAME_INT_CARDS_REVEALED, 0);

		var firstSuitBidName = savedGame.get(SAVED_GAME_STRING_FIRST_SUIT_BID, null);
		if (firstSuitBidName != null) {
			firstSuitBid = Suit.valueOf(firstSuitBidName);
		}

		deviatedFromFirstSuit = savedGame.getBoolean(SAVED_GAME_BOOLEAN_DEVIATED_FROM_FIRST_SUIT, false);
	}
	
	public void saveState()
	{
		savedGame.putBoolean(Registry.SAVED_GAME_BOOLEAN_CAVEMAN, earnedCaveman);
		savedGame.putBoolean(Registry.SAVED_GAME_BOOLEAN_BURGLAR, earnedBurglar);
		savedGame.putBoolean(Registry.SAVED_GAME_BOOLEAN_LION, earnedLion);
		savedGame.putBoolean(Registry.SAVED_GAME_BOOLEAN_GARDENER, earnedGardener);
		savedGame.putBoolean(Registry.SAVED_GAME_BOOLEAN_WEREWOLF, earnedWerewolf);
		savedGame.putBoolean(Registry.SAVED_GAME_BOOLEAN_SPACEMAN, earnedSpaceman);
		
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_REVEALED_DIFFERENT_SUIT, revealedDifferentSuit);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_REVEALED_SAME_SUIT, revealedSameSuit);
		savedGame.putInt(SAVED_GAME_INT_CARDS_REVEALED, cardsRevealed);
		
		savedGame.put(SAVED_GAME_STRING_FIRST_SUIT_BID, firstSuitBid.name());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_DEVIATED_FROM_FIRST_SUIT, deviatedFromFirstSuit);
	}
	
	public void unlockPerfectBidAchievements(boolean earnedPsychic)
	{
		AchievementsUtil.unlockEntropyPerfectBidAchievements(earnedCaveman, earnedBurglar, earnedLion, earnedWerewolf, 
				 earnedGardener, earnedSpaceman, earnedPsychic);
	}
	
	public void updatePerfectBidVariables(EntropyBidAction lastBid)
	{
		var bidSuit = lastBid.getSuit();

		if (bidSuit == Suit.Spades)
		{
			earnedGardener = true;
		}
		else if (bidSuit == Suit.Hearts)
		{
			earnedLion = true;
		}
		else if (bidSuit == Suit.Diamonds)
		{
			earnedBurglar = true;
		}
		else if (bidSuit == Suit.Clubs)
		{
			earnedCaveman = true;
		}
		else if (bidSuit == Suit.Moons)
		{
			earnedWerewolf = true;
		}
		else if (bidSuit == Suit.Stars)
		{
			earnedSpaceman = true;
		}
	}
	
	public void update(EntropyBidAction bid)
	{
		updateCardReveal(bid);
		updateMonotone(bid);
	}
	
	private void updateCardReveal(EntropyBidAction bidMade)
	{
		String card = bidMade.getCardToReveal();
		if (card == null)
		{
			return;
		}
		
		cardsRevealed++;

		if (isCardRelevant(card, bidMade.getSuit()))
		{
			revealedSameSuit = true;
		}
		else
		{
			revealedDifferentSuit = true;
		}
	}
	
	private void updateMonotone(EntropyBidAction bid)
	{
		var suit = bid.getSuit();
		if (firstSuitBid == null)
		{
			firstSuitBid = suit;
			return;
		}
		
		if (suit != firstSuitBid)
		{
			deviatedFromFirstSuit = true;
		}
	}
	
	public void unlockEndOfGameAchievements(int startingCards)
	{
		AchievementsUtil.unlockHonestOrDeceitful(cardsRevealed, revealedDifferentSuit, revealedSameSuit);
		
		if (!deviatedFromFirstSuit
		  && startingCards == 5)
		{
			AchievementUtilKt.unlockAchievement(Achievement.Monotone);
		}
	}
}
