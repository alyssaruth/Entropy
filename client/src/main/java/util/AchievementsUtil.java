package util;

import achievement.AchievementSetting;
import game.GameMode;
import object.Bid;
import object.Player;
import online.screen.EntropyLobby;
import online.screen.GameRoom;
import screen.HelpDialog;
import screen.MainScreen;
import screen.RewardDialog;
import screen.ScreenCache;
import utils.Achievement;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static achievement.AchievementUtilKt.unlockAchievement;
import static util.ClientGlobals.achievementStore;

public class AchievementsUtil implements Registry
{
	private static final int FULL_GAME_STARTING_CARDS = 5;
	private static final int OMNISCIENT_THRESHOLD = 10;
	private static final int SOCIAL_THRESHOLD = 5;
	private static final int HONEST_THRESHOLD = 5;
	private static final int[] KONAMI_CODE = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN,
											  KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
											  KeyEvent.VK_B, KeyEvent.VK_A};

	public static void updateStreaksForLoss()
	{
		achievementStore.save(AchievementSetting.CurrentStreak, 0);
	}

	public static void recordWin(GameMode gameMode)
	{
		if (gameMode == GameMode.Entropy)
		{
			int newGamesWon = achievementStore.get(AchievementSetting.EntropyGamesWon) + 1;
			achievementStore.save(AchievementSetting.EntropyGamesWon, newGamesWon);
		}
		else if (gameMode == GameMode.Vectropy)
		{
			int newGamesWon = achievementStore.get(AchievementSetting.VectropyGamesWon) + 1;
			achievementStore.save(AchievementSetting.VectropyGamesWon, newGamesWon);
		}
		
		updateStreaksForWin();
		unlockWinningStreakAchievements();
		
		if (gameMode == GameMode.Entropy)
		{
			unlockEntropyWinAchievements();
		}
		else
		{
			unlockVectropyWinAchievements();
		}
	}
	
	private static void updateStreaksForWin()
	{
		int currentStreak = achievementStore.get(AchievementSetting.CurrentStreak) + 1;
		achievementStore.save(AchievementSetting.CurrentStreak, currentStreak);

		int bestStreak = achievementStore.get(AchievementSetting.BestStreak);
		if (currentStreak > bestStreak)
		{
			achievementStore.save(AchievementSetting.BestStreak, currentStreak);
		}
	}
	
	private static void unlockWinningStreakAchievements()
	{
		int currentStreak = achievementStore.get(AchievementSetting.CurrentStreak);
		
		if (currentStreak >= 3)
		{
			unlockAchievement(Achievement.Momentum);
		}

		if (currentStreak >= 6)
		{
			unlockAchievement(Achievement.ChainReaction);
		}

		if (currentStreak >= 10)
		{
			unlockAchievement(Achievement.PerpetualMotion);
		}
	}
	
	private static void unlockEntropyWinAchievements()
	{
		int gamesWon = achievementStore.get(AchievementSetting.EntropyGamesWon);
		
		if (gamesWon > 0)
		{
			unlockAchievement(Achievement.FirstTimer);
		}
		
		if (gamesWon >= 10)
		{
			unlockAchievement(Achievement.CasualStrategist);
		}
		
		if (gamesWon >= 25)
		{
			unlockAchievement(Achievement.ConsistentWinner);
		}
		
		if (gamesWon >= 50)
		{
			unlockAchievement(Achievement.DominantForce);
		}
	}
	
	private static void unlockVectropyWinAchievements()
	{
		int gamesWon = achievementStore.get(AchievementSetting.VectropyGamesWon);

		if (gamesWon > 0)
		{
			unlockAchievement(Achievement.VectropyOne);
		}

		if (gamesWon >= 10)
		{
			unlockAchievement(Achievement.VectropyTen);
		}

		if (gamesWon >= 25)
		{
			unlockAchievement(Achievement.VectropyTwentyFive);
		}

		if (gamesWon >= 50)
		{
			unlockAchievement(Achievement.VectropyFifty);
		}
	}

	public static void checkForPerfectGame(int numberOfRounds, int cards)
	{
		if (cards < FULL_GAME_STARTING_CARDS)
		{
			return;
		}

		int playerCount = achievementStore.get(AchievementSetting.PlayerCount);
		unlockPerfectGameAchievements(numberOfRounds, playerCount);
	}
	
	public static void unlockPerfectGameAchievements(int numberOfRounds, int totalPlayers)
	{
		if (numberOfRounds == 5)
		{
			unlockAchievement(Achievement.Unscathed);
		}
		
		if (numberOfRounds == 10 && totalPlayers == 3)
		{
			unlockAchievement(Achievement.Bulletproof);
		}
		
		if (numberOfRounds == 15 && totalPlayers == 4)
		{
			unlockAchievement(Achievement.Superhuman);
		}
	}

	public static void checkForFullBlindGame(int cards, boolean playedBlind, boolean hasLookedAtCards, boolean cardReveal)
	{
		if (cards < FULL_GAME_STARTING_CARDS)
		{
			return;
		}

		int playerCount = achievementStore.get(AchievementSetting.PlayerCount);
		unlockFullBlindGameAchievements(playerCount, playedBlind, hasLookedAtCards, cardReveal);
	}
	
	public static void unlockFullBlindGameAchievements(int totalPlayers, boolean playedBlind, boolean hasLookedAtCards,
	  boolean cardReveal)
	{
		boolean fullyBlind = playedBlind && !hasLookedAtCards;
		if (!fullyBlind
		  || cardReveal)
		{
			return;
		}

		if (totalPlayers == 2)
		{
			unlockAchievement(Achievement.BlindTwo);
		}
		
		if (totalPlayers == 3)
		{
			unlockAchievement(Achievement.BlindThree);
		}
		
		if (totalPlayers == 4)
		{
			unlockAchievement(Achievement.BlindFour);
		}
	}
	
	public static void unlockNuclearStrike(int myCards, int opponentCards, boolean playedBlind, boolean hasLookedAtCards,
	  boolean cardReveal)
	{
		if (myCards > 1 
		  || opponentCards < FULL_GAME_STARTING_CARDS)
		{
			return;
		}

		int playerCount = achievementStore.get(AchievementSetting.PlayerCount);
		if (playerCount == 4
		  && playedBlind 
		  && !hasLookedAtCards
		  && !cardReveal)
		{
			unlockAchievement(Achievement.NuclearStrike);
		}
	}
	
	public static void unlockHandicapAchievements(int myCards, int opponentCards)
	{
		int diff = opponentCards - myCards;
		if (diff < 2)
		{
			return;
		}

		int playerCount = achievementStore.get(AchievementSetting.PlayerCount);
		if (playerCount == 2)
		{
			unlockAchievement(Achievement.HandicapTwo);
		}
		else if (playerCount == 3)
		{
			unlockAchievement(Achievement.HandicapThree);
		}
		else if (playerCount == 4)
		{
			unlockAchievement(Achievement.HandicapFour);
		}
	}

	public static void recordGamePlayed(GameMode gameMode)
	{
		if (gameMode == GameMode.Entropy)
		{
			int newGamesPlayed = achievementStore.get(AchievementSetting.EntropyGamesPlayed) + 1;
			achievementStore.save(AchievementSetting.EntropyGamesPlayed, newGamesPlayed);
		}
		else if (gameMode == GameMode.Vectropy)
		{
			int newGamesPlayed = achievementStore.get(AchievementSetting.VectropyGamesPlayed) + 1;
			achievementStore.save(AchievementSetting.VectropyGamesPlayed, newGamesPlayed);
		}
		
		unlockGamesPlayedAchievements();
	}
	
	private static void unlockGamesPlayedAchievements()
	{
		int totalGamesPlayed = getTotalGamesPlayed();

		if (totalGamesPlayed >= 10)
		{
			unlockAchievement(Achievement.Participant);
		}
		if (totalGamesPlayed >= 25)
		{
			unlockAchievement(Achievement.Hobbyist);
		}
		if (totalGamesPlayed >= 50)
		{
			unlockAchievement(Achievement.Enthusiast);
		}
		if (totalGamesPlayed >= 100)
		{
			unlockAchievement(Achievement.Professional);
		}
		if (totalGamesPlayed >= 200)
		{
			unlockAchievement(Achievement.Veteran);
		}
	}
	
	private static int getTotalGamesPlayed()
	{
		return achievementStore.get(AchievementSetting.EntropyGamesPlayed)
			 + achievementStore.get(AchievementSetting.VectropyGamesPlayed);
	}
	
	public static void unlockSecondThoughts(String roomId)
	{
		DefaultListModel<Bid> listmodel = ScreenCache.get(MainScreen.class).getListmodel();
		if (!roomId.isEmpty())
		{
			//We're online, so need a different listmodel...
			GameRoom room = ScreenCache.get(EntropyLobby.class).getGameRoomForName(roomId);
			listmodel = room.getListmodel();
		}

		int size = listmodel.size();
		for (int i=0; i<size; i++)
		{
			Bid bid = listmodel.get(i);
			if (bid.isBlind())
			{
				unlockAchievement(Achievement.SecondThoughts);
				return;
			}
		}
	}
	
	public static void unlockEntropyPerfectBidAchievements(boolean caveman, boolean burglar, boolean lion, boolean werewolf, 
														   boolean gardener, boolean spaceman, boolean psychic)
	{
		if (caveman)
		{
			unlockAchievement(Achievement.Caveman);
		}
		
		if (burglar)
		{
			unlockAchievement(Achievement.Burglar);
		}
		
		if (lion)
		{
			unlockAchievement(Achievement.Lion);
		}
		if (werewolf)
		{
			unlockAchievement(Achievement.Werewolf);
		}
		
		if (gardener)
		{
			unlockAchievement(Achievement.Gardener);
		}
		
		if (spaceman)
		{
			unlockAchievement(Achievement.Spaceman);
		}
		
		if (psychic)
		{
			unlockAchievement(Achievement.Psychic);
		}
		
		boolean chimera = areThreeOrMoreTrue(caveman, burglar, lion, werewolf, gardener, spaceman);
		if (chimera)
		{
			unlockAchievement(Achievement.Chimera);
		}
	}
	
	public static void unlockVectropyPerfectBidAchievements(boolean mathematician, boolean psychic)
	{
		if (mathematician)
		{
			unlockAchievement(Achievement.Mathematician);
		}
		
		if (psychic)
		{
			unlockAchievement(Achievement.Psychic);
		}
	}
	
	private static boolean areThreeOrMoreTrue(boolean... b)
	{
		int counter = 0;

        for (boolean value : b) {
            if (value) {
                counter++;
            }
        }
		
		return counter > 2;
	}
	
	public static void unlockSpectator(boolean gameOver, boolean earnedSpectator)
	{
		if (gameOver && earnedSpectator)
		{
			unlockAchievement(Achievement.Spectator);
		}
	}
	
	public static void unlockPrecision(boolean hasOverbid, int totalRounds)
	{
		if (totalRounds >= 5 && !hasOverbid)
		{
			unlockAchievement(Achievement.Precision);
		}
	}
	
	public static void unlockCoward(boolean gameOver, boolean playerEnabled, boolean firstRound)
	{
		if (!gameOver && playerEnabled && !firstRound)
		{
			unlockCoward();
		}
	}
	
	public static void setCowardToBeUnlocked()
	{
		achievementStore.save(AchievementSetting.WillUnlockCoward, true);
	}
	
	public static void unlockCoward()
	{
		unlockAchievement(Achievement.Coward);
	}

	public static void unlockDistracted()
	{
		unlockAchievement(Achievement.Distracted);
	}
	
	public static void unlockCitizensArrest()
	{
		unlockAchievement(Achievement.CitizensArrest);
	}
	
	public static void unlockConnected()
	{
		unlockAchievement(Achievement.Connected);
	}
	
	public static void unlockRailbird()
	{
		unlockAchievement(Achievement.Railbird);
	}
	
	public static void unlockBlueScreenOfDeath()
	{
		unlockAchievement(Achievement.BlueScreenOfDeath);
	}
	
	public static void unlockRewards(int achievementsEarned)
	{
		unlockIfRequired(achievementsEarned, 5, REWARDS_BOOLEAN_FOUR_COLOURS, RewardDialog.REWARD_BANNER_FOUR_COLOUR);
		unlockIfRequired(achievementsEarned, 10, REWARDS_BOOLEAN_NEGATIVE_JACKS, RewardDialog.REWARD_BANNER_NEGATIVE_JACKS);
		unlockIfRequired(achievementsEarned, 15, REWARDS_BOOLEAN_BLIND, RewardDialog.REWARD_BANNER_BLIND);
		unlockIfRequired(achievementsEarned, 20, REWARDS_BOOLEAN_MINIMALIST_DECK, RewardDialog.REWARD_BANNER_MINIMALIST);
		unlockIfRequired(achievementsEarned, 25, REWARDS_BOOLEAN_VECTROPY, RewardDialog.REWARD_BANNER_VECTROPY);
		unlockIfRequired(achievementsEarned, 30, REWARDS_BOOLEAN_CARD_REVEAL, RewardDialog.REWARD_BANNER_CARD_REVEAL);
		unlockIfRequired(achievementsEarned, 35, REWARDS_BOOLEAN_EXTRA_SUITS, RewardDialog.REWARD_BANNER_EXTRA_SUITS);
		unlockIfRequired(achievementsEarned, 40, REWARDS_BOOLEAN_ILLEGAL, RewardDialog.REWARD_BANNER_ILLEGAL);
		unlockIfRequired(achievementsEarned, 45, REWARDS_BOOLEAN_DEVELOPER_JOKERS, RewardDialog.REWARD_BANNER_DEVELOPERS);
		unlockIfRequired(achievementsEarned, 50, REWARDS_BOOLEAN_CHEATS, RewardDialog.REWARD_BANNER_CHEATS);
		
		//Always refresh here in case there are new pages
		ScreenCache.get(HelpDialog.class).refreshNodes("");
	}
	private static void unlockIfRequired(int achievementsEarned, int threshold, String registryNode, String imageName)
	{
		if (achievementsEarned >= threshold
		  && !rewards.getBoolean(registryNode, false))
		{
			rewards.putBoolean(registryNode, true);
			RewardDialog.showDialog(imageName);
		}
	}

	public static void updateAndUnlockSocial(ConcurrentHashMap<Integer, Player> hmPlayerByAdjustedPlayerNumber) 
	{
		// TODO - Rethink this
//		String achievementsList = achievements.get(ACHIEVEMENTS_STRING_SOCIAL_LIST, "");
//
//		for (int i=0; i<4; i++)
//		{
//			Player player = hmPlayerByAdjustedPlayerNumber.get(i);
//			if (player == null)
//			{
//				continue;
//			}
//
//			String username = player.getName();
//			if (!username.isEmpty()
//			  && !containsExact(achievementsList, username))
//			{
//				achievementsList += username;
//				achievementsList += ";";
//			}
//		}
//
//		achievements.put(ACHIEVEMENTS_STRING_SOCIAL_LIST, achievementsList);
//
//		String[] usernames = achievementsList.split(";");
//		int size = usernames.length;
//		if (size >= SOCIAL_THRESHOLD)
//		{
//			unlockSocial();
//		}
	}

//	private static boolean containsExact(String socialStr, String username)
//	{
//		return socialStr.startsWith(username + ";")
//		  || socialStr.contains(";" + username + ";");
//	}
//
//	private static void unlockSocial()
//	{
//		unlockAchievement(Achievement.Social);
//	}
	
	public static void unlockKonamiCode()
	{
		unlockAchievement(Achievement.KonamiCode);
	}
	
	public static boolean hasEnteredKonamiCode(ArrayList<Integer> lastTenKeys)
	{
		int size = lastTenKeys.size();
		if (size != 10)
		{
			return false;
		}
		
		for (int i=0; i<10; i++)
		{
			int keyPressed = lastTenKeys.get(i);
			if (keyPressed != KONAMI_CODE[i])
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static void unlockOmniscient(ArrayList<String> revealedCards)
	{
		if (revealedCards.size() >= OMNISCIENT_THRESHOLD)
		{
			unlockAchievement(Achievement.Omniscient);
		}
	}
	
	public static void unlockHonestOrDeceitful(int cardsRevealed, boolean revealedDifferentSuit, boolean revealedSameSuit)
	{
		if (cardsRevealed < HONEST_THRESHOLD)
		{
			return;
		}
		
		if (!revealedDifferentSuit)
		{
			unlockAchievement(Achievement.Honest);
		}
		else if (!revealedSameSuit)
		{
			unlockAchievement(Achievement.Deceitful);
		}
	}
	
	public static class UnlockAchievementTask extends TimerTask
	{
		private final Achievement achievement;
		
		public UnlockAchievementTask(Achievement achievement)
		{
			this.achievement = achievement;
		}
		
		@Override
		public void run() 
		{
			unlockAchievement(achievement);
		}
	}
}