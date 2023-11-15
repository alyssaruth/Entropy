package util;

import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import object.Bid;
import object.Player;
import online.screen.GameRoom;

import org.w3c.dom.Element;

import screen.AchievementBadges;
import screen.MainScreen;
import screen.RewardDialog;
import screen.ScreenCache;

public class AchievementsUtil implements AchievementBadges
{
	private static final int FULL_GAME_STARTING_CARDS = 5;
	private static final int CHATTY_THRESHOLD = 25;
	private static final int OMNISCIENT_THRESHOLD = 10;
	private static final int SOCIAL_THRESHOLD = 5;
	private static final int HONEST_THRESHOLD = 5;
	private static final int[] KONAMI_CODE = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN,
											  KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
											  KeyEvent.VK_B, KeyEvent.VK_A};
	
	private static final HashMap<String, String> hmRegistryLocationToAchievementName;
	static
	{
		hmRegistryLocationToAchievementName = new HashMap<>();
		
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CAVEMAN, "The Caveman");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_BURGLAR, "The Burglar");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_LION, "The Lion");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_GARDENER, "The Gardener");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_PSYCHIC, "The Psychic");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_FIVE_MINUTES, "Sluggish");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_FIFTEEN_MINUTES, "Warming Up");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_THIRTY_MINUTES, "Breaking a Sweat");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_SIXTY_MINUTES, "World Class");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_TWO_HOURS, "Record Breaker");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_COWARD, "Coward");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_SPECTATOR, "Spectator");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_VANITY, "Vanity");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_UNSCATHED, "Unscathed");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_BULLETPROOF, "Bulletproof");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_SUPERHUMAN, "Superhuman");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_MOMENTUM, "Momentum");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CHAIN_REACTION, "Chain Reaction");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_PERPETUAL_MOTION, "Perpetual Motion");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_PARTICIPANT, "Participant");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_HOBBYIST, "Hobbyist");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_ENTHUSIAST, "Enthusiast");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_PROFESSIONAL, "Professional");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_VETERAN, "Veteran");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_FIRST_TIMER, "First-Timer");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CASUAL_STRATEGIST, "Casual Strategist");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CONSISTENT_WINNER, "Consistent Winner");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_DOMINANT_FORCE, "Dominant Force");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_SECOND_THOUGHTS, "Second Thoughts");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_TWO, "Blind Luck");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_THREE, "Against the Odds");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_FOUR, "Lottery Winner");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CHIMERA, "The Chimera");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_PRECISION, "Precision");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_MATHEMATICIAN, "The Mathematician");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_NUCLEAR_STRIKE, "Nuclear Strike");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_HANDICAP_TWO, "Underdog I");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_HANDICAP_THREE, "Underdog II");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_HANDICAP_FOUR, "Underdog III");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_VECTROPY_ONE, "Easy as 1, 2, 3");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_VECTROPY_TEN, "Base Ten");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_VECTROPY_TWENTY_FIVE, "Five Squared");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_VECTROPY_FIFTY, "Half-Century");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_DISTRACTED, "Distracted");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CITIZENS_ARREST, "Citizen's Arrest");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CONNECTED, "Connected");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_RAILBIRD, "Railbird");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_SOCIAL, "Social");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_BLUE_SCREEN_OF_DEATH, "Blue Screen of Death");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_WEREWOLF, "The Werewolf");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_SPACEMAN, "The Spaceman");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_KONAMI_CODE, "Nintendo");
		
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_BOOKWORM, "Bookworm");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_CHATTY, "Chatty");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_DECEITFUL, "Deceitful");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_HONEST, "Honest");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_LOOK_AT_ME, "Look At Me!");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_MONOTONE, "Monotone");
		hmRegistryLocationToAchievementName.put(ACHIEVEMENTS_BOOLEAN_OMNISCIENT, "Omniscient");
	}

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

	public static void recordWin(int gameMode)
	{
		if (gameMode == GameConstants.GAME_MODE_ENTROPY)
		{
			int newGamesWon = achievements.getInt(STATISTICS_INT_ENTROPY_GAMES_WON, 0) + 1;
			achievements.putInt(STATISTICS_INT_ENTROPY_GAMES_WON, newGamesWon);
		}
		else if (gameMode == GameConstants.GAME_MODE_VECTROPY)
		{
			int newGamesWon = achievements.getInt(STATISTICS_INT_VECTROPY_GAMES_WON, 0) + 1;
			achievements.putInt(STATISTICS_INT_VECTROPY_GAMES_WON, newGamesWon);
		}
		
		updateStreaksForWin();
		unlockWinningStreakAchievements();
		
		if (GameUtil.isEntropy(gameMode))
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
		
		boolean momentum = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_MOMENTUM, false);
		boolean chainReaction = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_CHAIN_REACTION, false);
		boolean perpetualMotion = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_PERPETUAL_MOTION, false);
		
		if (currentStreak >= 3 && !momentum)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_MOMENTUM);
		}

		if (currentStreak >= 6 && !chainReaction)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_CHAIN_REACTION);
		}

		if (currentStreak >= 10 && !perpetualMotion)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_PERPETUAL_MOTION);
		}
	}
	
	private static void unlockEntropyWinAchievements()
	{
		int gamesWon = achievements.getInt(STATISTICS_INT_ENTROPY_GAMES_WON, 0)
					 + achievements.getInt(STATISTICS_INT_ENTROPY_ONLINE_GAMES_WON, 0);
		
		boolean firstTimer = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_FIRST_TIMER, false);
		boolean casualStrategist = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_CASUAL_STRATEGIST, false);
		boolean consistentWinner = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_CONSISTENT_WINNER, false);
		boolean dominantForce = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_DOMINANT_FORCE, false);
		
		if (gamesWon > 0 && !firstTimer)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_FIRST_TIMER);
		}
		
		if (gamesWon >= 10 && !casualStrategist)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_CASUAL_STRATEGIST);
		}
		
		if (gamesWon >= 25 && !consistentWinner)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_CONSISTENT_WINNER);
		}
		
		if (gamesWon >= 50 && !dominantForce)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_DOMINANT_FORCE);
		}
	}
	
	private static void unlockVectropyWinAchievements()
	{
		int gamesWon = achievements.getInt(STATISTICS_INT_VECTROPY_GAMES_WON, 0)
				 + achievements.getInt(STATISTICS_INT_VECTROPY_ONLINE_GAMES_WON, 0);

		if (gamesWon > 0)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_VECTROPY_ONE);
		}

		if (gamesWon >= 10)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_VECTROPY_TEN);
		}

		if (gamesWon >= 25)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_VECTROPY_TWENTY_FIVE);
		}

		if (gamesWon >= 50)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_VECTROPY_FIFTY);
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
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_UNSCATHED);
		}
		
		if (numberOfRounds == 10 && totalPlayers == 3)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_BULLETPROOF);
		}
		
		if (numberOfRounds == 15 && totalPlayers == 4)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_SUPERHUMAN);
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
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_TWO);
		}
		
		if (totalPlayers == 3)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_THREE);
		}
		
		if (totalPlayers == 4)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_FOUR);
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
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_NUCLEAR_STRIKE);
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
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_HANDICAP_TWO);
		}
		else if (totalPlayers == 3)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_HANDICAP_THREE);
		}
		else if (totalPlayers == 4)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_HANDICAP_FOUR);
		}
	}

	public static void recordGamePlayed(int gameMode)
	{
		int gamesPlayed = 1;
		
		if (gameMode == GameConstants.GAME_MODE_ENTROPY)
		{
			gamesPlayed += achievements.getInt(STATISTICS_INT_ENTROPY_GAMES_PLAYED, 0);
			achievements.putInt(STATISTICS_INT_ENTROPY_GAMES_PLAYED, gamesPlayed);
		}
		else if (gameMode == GameConstants.GAME_MODE_VECTROPY)
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
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_PARTICIPANT);
		}
		if (totalGamesPlayed >= 25)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_HOBBYIST);
		}
		if (totalGamesPlayed >= 50)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_ENTHUSIAST);
		}
		if (totalGamesPlayed >= 100)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_PROFESSIONAL);
		}
		if (totalGamesPlayed >= 200)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_VETERAN);
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
		
		boolean secondThoughts = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_SECOND_THOUGHTS, false);
		if (secondThoughts)
		{
			return;
		}

		int size = listmodel.size();
		for (int i=0; i<size; i++)
		{
			Bid bid = listmodel.get(i);
			if (bid.isBlind())
			{
				unlockAchievement(ACHIEVEMENTS_BOOLEAN_SECOND_THOUGHTS);
				return;
			}
		}
	}
	
	public static void unlockEntropyPerfectBidAchievements(boolean caveman, boolean burglar, boolean lion, boolean werewolf, 
														   boolean gardener, boolean spaceman, boolean psychic)
	{
		if (caveman)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_CAVEMAN);
		}
		
		if (burglar)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_BURGLAR);
		}
		
		if (lion)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_LION);
		}
		if (werewolf)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_WEREWOLF);
		}
		
		if (gardener)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_GARDENER);
		}
		
		if (spaceman)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_SPACEMAN);
		}
		
		if (psychic)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_PSYCHIC);
		}
		
		boolean chimera = areThreeOrMoreTrue(caveman, burglar, lion, werewolf, gardener, spaceman);
		if (chimera)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_CHIMERA);
		}
	}
	
	public static void unlockVectropyPerfectBidAchievements(boolean mathematician, boolean psychic)
	{
		if (mathematician)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_MATHEMATICIAN);
		}
		
		if (psychic)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_PSYCHIC);
		}
	}
	
	private static boolean areThreeOrMoreTrue(boolean... b)
	{
		int counter = 0;
		
		int length = b.length;
		for (int i=0; i<length; i++)
		{
			if (b[i])
			{
				counter++;
			}
		}
		
		return counter > 2;
	}
	
	public static void unlockSpectator(boolean gameOver, boolean earnedSpectator)
	{
		if (gameOver && earnedSpectator)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_SPECTATOR);
		}
	}
	
	public static void unlockPrecision(boolean hasOverbid, int totalRounds)
	{
		if (totalRounds >= 5 && !hasOverbid)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_PRECISION);
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
		boolean cowardAchievement = achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_COWARD, false);
		if (!cowardAchievement)
		{
			achievements.putBoolean(ACHIEVEMENTS_BOOLEAN_WILL_UNLOCK_COWARD, true);
		}
	}
	
	public static void unlockCoward()
	{
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_COWARD);
	}
	
	public static boolean isDistractedUnlocked()
	{
		return achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_DISTRACTED, false);
	}
	public static void unlockDistracted()
	{
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_DISTRACTED);
	}
	
	public static void unlockCitizensArrest()
	{
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_CITIZENS_ARREST);
	}
	
	public static void unlockConnected()
	{
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_CONNECTED);
	}
	
	public static void unlockRailbird()
	{
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_RAILBIRD);
	}
	
	public static void unlockBlueScreenOfDeath()
	{
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_BLUE_SCREEN_OF_DEATH);
	}
	
	public static void updateAndUnlockVanity()
	{
		int vanityCount = achievements.getInt(ACHIEVEMENTS_INT_VANITY_COUNT, 0) + 1;
		achievements.putInt(ACHIEVEMENTS_INT_VANITY_COUNT, vanityCount);

		if (vanityCount == 20)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_VANITY);
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
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_SOCIAL);
	}
	
	public static void unlockKonamiCode()
	{
		unlockAchievement(ACHIEVEMENTS_BOOLEAN_KONAMI_CODE);
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
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_OMNISCIENT);
		}
	}
	
	public static void incrementChatCount()
	{
		int chatCount = achievements.getInt(ACHIEVEMENTS_INT_CHAT_COUNT, 0) + 1;
		achievements.putInt(ACHIEVEMENTS_INT_CHAT_COUNT, chatCount);
		
		if (chatCount >= CHATTY_THRESHOLD)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_CHATTY);
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
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_HONEST);
		}
		else if (!revealedSameSuit)
		{
			unlockAchievement(ACHIEVEMENTS_BOOLEAN_DECEITFUL);
		}
	}
	
	public static ImageIcon getIconForAchievement(String registryLocation)
	{
		try
		{
			URL url = AchievementsUtil.class.getResource("/achievements/" + registryLocation + ".png");
			return new ImageIcon(url);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t, "Failed to load icon for achievement " + registryLocation);
			return lockedIcon;
		}
	}
	
	public static String getAchievementName(String registryLocation)
	{
		return hmRegistryLocationToAchievementName.get(registryLocation);
	}
	
	public static void unlockAchievement(String registryLocation)
	{
		//If we've already got the achievement, do nothing
		if (achievements.getBoolean(registryLocation, false))
		{
			return;
		}
		
		achievements.putBoolean(registryLocation, true);
	
		ImageIcon icon = getIconForAchievement(registryLocation);
		String achievementName = getAchievementName(registryLocation);
		
		MainScreen screen = ScreenCache.getMainScreen();
		screen.showAchievementPopup(achievementName, icon);
	}
	
	public static class UnlockAchievementTask extends TimerTask
	{
		private String registryLocation = "";
		
		public UnlockAchievementTask(String registryLocation)
		{
			this.registryLocation = registryLocation;
		}
		
		@Override
		public void run() 
		{
			unlockAchievement(registryLocation);
		}
	}
}