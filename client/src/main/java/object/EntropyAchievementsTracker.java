package object;

import achievement.AchievementUtilKt;
import util.AchievementsUtil;
import util.CardsUtil;
import util.Registry;
import utils.Achievement;

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
	
	private int firstSuitBid = -1;
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
		
		firstSuitBid = -1;
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
		
		firstSuitBid = savedGame.getInt(SAVED_GAME_INT_FIRST_SUIT_BID, -1);
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
		
		savedGame.putInt(SAVED_GAME_INT_FIRST_SUIT_BID, firstSuitBid);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_DEVIATED_FROM_FIRST_SUIT, deviatedFromFirstSuit);
	}
	
	public void unlockPerfectBidAchievements(boolean earnedPsychic)
	{
		AchievementsUtil.unlockEntropyPerfectBidAchievements(earnedCaveman, earnedBurglar, earnedLion, earnedWerewolf, 
				 earnedGardener, earnedSpaceman, earnedPsychic);
	}
	
	public void updatePerfectBidVariables(Bid lastBid)
	{
		EntropyBid entropyBid = (EntropyBid)lastBid;
		int bidSuitCode = entropyBid.getBidSuitCode();
		
		if (bidSuitCode == CardsUtil.SUIT_SPADES)
		{
			earnedGardener = true;
		}
		else if (bidSuitCode == CardsUtil.SUIT_HEARTS)
		{
			earnedLion = true;
		}
		else if (bidSuitCode == CardsUtil.SUIT_DIAMONDS)
		{
			earnedBurglar = true;
		}
		else if (bidSuitCode == CardsUtil.SUIT_CLUBS)
		{
			earnedCaveman = true;
		}
		else if (bidSuitCode == CardsUtil.SUIT_MOONS)
		{
			earnedWerewolf = true;
		}
		else if (bidSuitCode == CardsUtil.SUIT_STARS)
		{
			earnedSpaceman = true;
		}
	}
	
	public void update(Bid bid)
	{
		EntropyBid entropyBid = (EntropyBid)bid;
		
		updateCardReveal(entropyBid);
		updateMonotone(entropyBid);
	}
	
	private void updateCardReveal(EntropyBid bidMade)
	{
		String card = bidMade.getCardToReveal();
		if (card.isEmpty())
		{
			return;
		}
		
		cardsRevealed++;
		
		int bidSuitCode = bidMade.getBidSuitCode();
		if (CardsUtil.isRelevant(card, bidSuitCode))
		{
			revealedSameSuit = true;
		}
		else
		{
			revealedDifferentSuit = true;
		}
	}
	
	private void updateMonotone(EntropyBid bid)
	{
		int suitCode = bid.getBidSuitCode();
		if (firstSuitBid == -1)
		{
			firstSuitBid = suitCode;
			return;
		}
		
		if (suitCode != firstSuitBid)
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
