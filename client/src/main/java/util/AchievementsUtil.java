package util;

import game.GameMode;
import object.Bid;
import object.Player;
import online.screen.GameRoom;
import org.w3c.dom.Element;
import screen.MainScreen;
import screen.RewardDialog;
import screen.ScreenCache;
import utils.Achievement;
import utils.AchievementKt;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class AchievementsUtil implements Registry
{
	private static final int FULL_GAME_STARTING_CARDS = 5;
	private static final int CHATTY_THRESHOLD = 25;
	private static final int OMNISCIENT_THRESHOLD = 10;
	private static final int SOCIAL_THRESHOLD = 5;
	private static final int HONEST_THRESHOLD = 5;
	private static final int[] KONAMI_CODE = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN,
											  KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
											  KeyEvent.VK_B, KeyEvent.VK_A};

	public static void updateStreaksForLoss()
	{
		int currentStreak = achievements.getInt(STATISTICS_INT_CURRENT_STREAK, 0);
		int worstStreak = achievements.getInt(STATISTICS_INT_WORST_STREAK, 0);

		if (currentStreak > 0)
		{
			currentStreak = -1;
			achievements.putInt(STATISTICS_INT_CURRENT_STREAK, currentStreak);
		}
		else
		{
			currentStreak--;
			achievements.putInt(STATISTICS_INT_CURRENT_STREAK, currentStreak);
		}

		if (currentStreak < -worstStreak)
		{
			achievements.putInt(STATISTICS_INT_WORST_STREAK, -currentStreak);
		}
	}

	public static void recordWin(GameMode gameMode)
	{
		if (gameMode == GameMode.Entropy)
		{
			int newGamesWon = achievements.getInt(STATISTICS_INT_ENTROPY_GAMES_WON, 0) + 1;
			achievements.putInt(STATISTICS_INT_ENTROPY_GAMES_WON, newGamesWon);
		}
		else if (gameMode == GameMode.Vectropy)
		{
			int newGamesWon = achievements.getInt(STATISTICS_INT_VECTROPY_GAMES_WON, 0) + 1;
			achievements.putInt(STATISTICS_INT_VECTROPY_GAMES_WON, newGamesWon);
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
		int currentStreak = achievements.getInt(STATISTICS_INT_CURRENT_STREAK, 0);
		int bestStreak = achievements.getInt(STATISTICS_INT_BEST_STREAK, 0);
		if (currentStreak < 0)
		{
			achievements.putInt(STATISTICS_INT_CURRENT_STREAK, 1);
		}
		else
		{
			currentStreak++;
			achievements.putInt(STATISTICS_INT_CURRENT_STREAK, currentStreak);
		}

		if (currentStreak > bestStreak)
		{
			achievements.putInt(STATISTICS_INT_BEST_STREAK, currentStreak);
		}
	}
	
	private static void unlockWinningStreakAchievements()
	{
		int currentStreak = achievements.getInt(STATISTICS_INT_CURRENT_STREAK, 0);
		
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
		int gamesWon = achievements.getInt(STATISTICS_INT_ENTROPY_GAMES_WON, 0)
					 + achievements.getInt(STATISTICS_INT_ENTROPY_ONLINE_GAMES_WON, 0);
		
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
		int gamesWon = achievements.getInt(STATISTICS_INT_VECTROPY_GAMES_WON, 0)
				 + achievements.getInt(STATISTICS_INT_VECTROPY_ONLINE_GAMES_WON, 0);

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

		int opponentTwoCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_TWO_COEFF, 1);
		int opponentThreeCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_THREE_COEFF, 1);
		int totalPlayers = 2 + opponentTwoCoeff + opponentThreeCoeff;
		
		unlockPerfectGameAchievements(numberOfRounds, totalPlayers);
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

		int opponentTwoCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_TWO_COEFF, 1);
		int opponentThreeCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_THREE_COEFF, 1);
		int totalPlayers = 2 + opponentTwoCoeff + opponentThreeCoeff;

		unlockFullBlindGameAchievements(totalPlayers, playedBlind, hasLookedAtCards, cardReveal);
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
		
		int opponentTwoCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_TWO_COEFF, 1);
		int opponentThreeCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_THREE_COEFF, 1);
		int totalPlayers = 2 + opponentTwoCoeff + opponentThreeCoeff;
		
		if (totalPlayers == 4 
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
		
		int opponentTwoCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_TWO_COEFF, 1);
		int opponentThreeCoeff = achievements.getInt(ACHIEVEMENTS_INT_OPPONENT_THREE_COEFF, 1);
		int totalPlayers = 2 + opponentTwoCoeff + opponentThreeCoeff;
		
		if (totalPlayers == 2)
		{
			unlockAchievement(Achievement.HandicapTwo);
		}
		else if (totalPlayers == 3)
		{
			unlockAchievement(Achievement.HandicapThree);
		}
		else if (totalPlayers == 4)
		{
			unlockAchievement(Achievement.HandicapFour);
		}
	}

	public static void recordGamePlayed(GameMode gameMode)
	{
		int gamesPlayed = 1;
		
		if (gameMode == GameMode.Entropy)
		{
			gamesPlayed += achievements.getInt(STATISTICS_INT_ENTROPY_GAMES_PLAYED, 0);
			achievements.putInt(STATISTICS_INT_ENTROPY_GAMES_PLAYED, gamesPlayed);
		}
		else if (gameMode == GameMode.Vectropy)
		{
			gamesPlayed += achievements.getInt(STATISTICS_INT_VECTROPY_GAMES_PLAYED, 0);
			achievements.putInt(STATISTICS_INT_VECTROPY_GAMES_PLAYED, gamesPlayed);
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
		return achievements.getInt(STATISTICS_INT_ENTROPY_GAMES_PLAYED, 0) 
			 + achievements.getInt(STATISTICS_INT_VECTROPY_GAMES_PLAYED, 0)
			 + achievements.getInt(STATISTICS_INT_ENTROPY_ONLINE_GAMES_PLAYED, 0)
			 + achievements.getInt(STATISTICS_INT_VECTROPY_ONLINE_GAMES_PLAYED, 0);
	}
	
	public static void updateOnlineStats(Element rootElement)
	{
		int entropyWins = getTotalWins("Entropy", rootElement);
		int vectropyWins = getTotalWins("Vectropy", rootElement);
		int entropyPlayed = entropyWins + getTotalLosses("Entropy", rootElement);
		int vectropyPlayed = vectropyWins + getTotalLosses("Vectropy", rootElement);
		
		achievements.putInt(STATISTICS_INT_ENTROPY_ONLINE_GAMES_WON, entropyWins);
		achievements.putInt(STATISTICS_INT_ENTROPY_ONLINE_GAMES_PLAYED, entropyPlayed);		
		achievements.putInt(STATISTICS_INT_VECTROPY_ONLINE_GAMES_WON, vectropyWins);
		achievements.putInt(STATISTICS_INT_VECTROPY_ONLINE_GAMES_PLAYED, vectropyPlayed);
		
		unlockGamesPlayedAchievements();
		unlockEntropyWinAchievements();
		unlockVectropyWinAchievements();
	}
	private static int getTotalWins(String mode, Element rootElement)
	{
		return getTotal(mode, rootElement, "Won");
	}
	private static int getTotalLosses(String mode, Element rootElement)
	{
		return getTotal(mode, rootElement, "Lost");
	}
	private static int getTotal(String mode, Element rootElement, String wonOrLost)
	{
		return XmlUtil.getAttributeInt(rootElement, mode + "2" + wonOrLost)
				 + XmlUtil.getAttributeInt(rootElement, mode + "3" + wonOrLost)
				 + XmlUtil.getAttributeInt(rootElement, mode + "4" + wonOrLost);
	}
	
	public static void unlockSecondThoughts(String roomId)
	{
		DefaultListModel<Bid> listmodel = ScreenCache.getMainScreen().getListmodel();
		if (!roomId.isEmpty())
		{
			//We're online, so need a different listmodel...
			GameRoom room = ScreenCache.getEntropyLobby().getGameRoomForName(roomId);
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
		achievements.putBoolean(ACHIEVEMENTS_BOOLEAN_WILL_UNLOCK_COWARD, true);
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
	
	public static void updateAndUnlockVanity()
	{
		int vanityCount = achievements.getInt(ACHIEVEMENTS_INT_VANITY_COUNT, 0) + 1;
		achievements.putInt(ACHIEVEMENTS_INT_VANITY_COUNT, vanityCount);

		if (vanityCount == 20)
		{
			unlockAchievement(Achievement.Vanity);
		}
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
		ScreenCache.getHelpDialog().refreshNodes("");
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
		String achievementsList = achievements.get(ACHIEVEMENTS_STRING_SOCIAL_LIST, "");
		
		for (int i=0; i<4; i++)
		{
			Player player = hmPlayerByAdjustedPlayerNumber.get(i);
			if (player == null)
			{
				continue;
			}
			
			String username = player.getName();
			if (!username.isEmpty()
			  && !containsExact(achievementsList, username))
			{
				achievementsList += username;
				achievementsList += ";";
			}
		}
		
		achievements.put(ACHIEVEMENTS_STRING_SOCIAL_LIST, achievementsList);
		
		String[] usernames = achievementsList.split(";");
		int size = usernames.length;
		if (size >= SOCIAL_THRESHOLD)
		{
			unlockSocial();
		}
	}
	
	private static boolean containsExact(String socialStr, String username)
	{
		return socialStr.startsWith(username + ";")
		  || socialStr.contains(";" + username + ";");
	}
	
	private static void unlockSocial()
	{
		unlockAchievement(Achievement.Social);
	}
	
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
	
	public static void incrementChatCount()
	{
		int chatCount = achievements.getInt(ACHIEVEMENTS_INT_CHAT_COUNT, 0) + 1;
		achievements.putInt(ACHIEVEMENTS_INT_CHAT_COUNT, chatCount);
		
		if (chatCount >= CHATTY_THRESHOLD)
		{
			unlockAchievement(Achievement.Chatty);
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
	
	public static void unlockAchievement(Achievement achievement)
	{
		//If we've already got the achievement, do nothing
		var registryLocation = achievement.getRegistryLocation();
		if (achievements.getBoolean(registryLocation, false))
		{
			return;
		}

		achievements.putBoolean(registryLocation, true);

		ScreenCache.getAchievementsDialog().refresh(false);
		ImageIcon icon = AchievementKt.getIcon(achievement);
		
		MainScreen screen = ScreenCache.getMainScreen();
		screen.showAchievementPopup(achievement.getTitle(), icon);
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