package screen;

import achievement.AchievementSetting;
import game.GameMode;
import game.GameSettings;
import game.Suit;
import object.Bid;
import object.ChallengeBid;
import object.IllegalBid;
import object.Player;
import util.*;

import javax.swing.*;
import java.util.Timer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static game.CardsUtilKt.countSuit;
import static game.CardsUtilKt.createAndShuffleDeck;
import static game.CheatUtilKt.containsNonJoker;
import static screen.ScreenCacheKt.IN_GAME_REPLAY;
import static util.ClientGlobals.achievementStore;
import static utils.CoreGlobals.logger;

public abstract class GameScreen extends TransparentPanel
								 implements BidListener,
								 			RevealListener,
								 			Registry
{
	//Common variables
	protected GameSettings settings = null;
	private int totalNumberOfCards;
	private int personToStart = -1;
	private Player currentPlayer = null;
	private int handicapAmount;
	
	public Bid lastBid = null;

	private boolean playBlind;
	private boolean playWithHandicap;
	public boolean gameOver = true;
	public boolean firstRound = true;
	private boolean logging = true;	
	public boolean hasOverbid;
	public boolean hasActedBlindThisGame = false;
	
	private boolean earnedSpectator = false;
	public boolean earnedPsychic = false;
	private boolean currentlyOnChallenge = false;
	public boolean cheatUsed = false;
	
	private Timer nextRoundTimer = new Timer("Timer-NextRound");
	private Timer cpuTurn = new Timer("Timer-CpuTurn");
	
	public Player player = null;
	public Player opponentOne = null;
	public Player opponentTwo = null;
	public Player opponentThree = null;
	
	public BidPanel bidPanel = null;
	public HandPanelMk2 handPanel = new HandPanelMk2(this);
	
	//Abstract methods
	public abstract void loadLastBid();
	public abstract void loadSpecificVariables();
	public abstract void showResult();
	
	public abstract GameMode getGameMode();
	public abstract String processCommand(String command);
	public abstract Suit getLastBidSuit();
	public abstract void unlockPerfectBidAchievements();
	public abstract void unlockEndOfGameAchievements(int startingCards);
	public abstract void setPerfectBidBooleans();
	public abstract void updateAchievementVariables();
	
	public void startNewGame()
	{	
		try
		{	
			Debug.appendBanner("New Game", logging);
			cancelNewRound();
			
			boolean playerEnabled = player != null && player.isEnabled();
			AchievementsUtil.unlockCoward(gameOver, playerEnabled, firstRound);
			ScreenCache.get(MainScreen.class).dismissCurrentReplay();

			initVariablesForNewGame();
			initVariables();
			
			startRound();
		}
		catch (Throwable e)
		{
			Debug.stackTrace(e);
		}
	}
	
	public void startNewRound() 
	{
		Debug.appendBanner("New Round", logging);
		
		currentlyOnChallenge = false;
		subtractCardsFromPlayers();
		knockOutPlayers();
		correctPersonToStart();
		initVariables();

		startRound();
	}
	
	private void startRound()
	{
		List<String> deck = createAndShuffleDeck(settings);
		populateHands(deck);
		displayHands();
		
		Debug.append("Player " + personToStart + " to start", logging);
		
		if (personToStart == 0)
		{
			doHumanTurn();
			bidPanel.enableChallenge(false);
		}
		else
		{
			processCpuTurn(personToStart);
		}
	}
	
	private void populateHands(List<String> deck)
	{
		Debug.append("Dealing hands...", logging);

		GameUtil.populateHand(player, deck, logging);
		GameUtil.populateHand(opponentOne, deck, logging);
		GameUtil.populateHand(opponentTwo, deck, logging);
		GameUtil.populateHand(opponentThree, deck, logging);
	}
	
	private void setRandomPersonToStart()
	{
		Random random = new Random();

		if (opponentTwo.isEnabled() && opponentThree.isEnabled())
		{
			personToStart = random.nextInt(4);
		}
		else if (opponentTwo.isEnabled())
		{
			personToStart = random.nextInt(3);
		}
		else
		{
			personToStart = random.nextInt(2);
		}
	}
	
	protected void initVariablesForNewGame()
	{
		Debug.append("Initing variables for new game...", logging);
		
		resetPlayers();
		
		cheatUsed = false;
		earnedPsychic = false;
		hasOverbid = false;
		hasActedBlindThisGame = false;
		
		RegistryUtil.clearNode(inGameReplay);
		getNewGameVariablesFromRegistry();
		
		opponentOne.setEnabled(true);
		player.setEnabled(true);
		
		initNumberOfCards();
		
		handPanel.setInitted(true);
		handPanel.setHasViewedHandThisGame(false);
		
		gameOver = false;
		firstRound = true;

		//variables to use for perfect game achievements
		var allPlayers = new Player[]{player, opponentOne, opponentTwo, opponentThree};
		var playerCount = Arrays.stream(allPlayers).filter(Player::isEnabled).count();
		achievementStore.save(AchievementSetting.PlayerCount, (int)playerCount);
		
		bidPanel.showBidPanel(true);
		setRandomPersonToStart();
		
		Debug.append("numberOfCards = " + settings.getStartingCards(), logging);
	}
	
	private void resetPlayers()
	{
		player = new Player(0, "red");
		opponentOne = new Player(1, "blue");
		opponentTwo = new Player(2, "green");
		opponentThree = new Player(3, "purple");
	}
	
	private void initVariables()
	{
		Debug.append("Initing variables...", logging);
		
		lastBid = null;
		totalNumberOfCards = player.getNumberOfCards() + opponentOne.getNumberOfCards() 
						   + opponentTwo.getNumberOfCards() + opponentThree.getNumberOfCards();

		int maxBid = GameUtil.getMaxBid(settings, totalNumberOfCards);
		bidPanel.init(maxBid, totalNumberOfCards, false, settings.getIncludeMoons(), settings.getIncludeStars(), false);
		
		DefaultListModel<Bid> listmodel = ScreenCache.get(MainScreen.class).getListmodel();
		listmodel.removeAllElements();
		
		player.resetHand();
		opponentOne.resetHand();
		opponentTwo.resetHand();
		opponentThree.resetHand();
		
		handPanel.displayLabels(player.isEnabled(), opponentOne.isEnabled(), 
								opponentTwo.isEnabled(), opponentThree.isEnabled());
		handPanel.assignAsteriskToStartingPlayer(personToStart);
		handPanel.setViewCardsVisibility(playBlind && player.isEnabled());

		earnedSpectator = !player.isEnabled();
	}
	
	private void subtractCardsFromPlayers()
	{
		player.doSubtraction();
		opponentOne.doSubtraction();
		opponentTwo.doSubtraction();
		opponentThree.doSubtraction();
	}
	
	private void knockOutPlayers()
	{
		Debug.append("Knocking out players...", logging);

		if (player.disable())
		{
			handPanel.hideLabelForPlayer(0);
			Debug.append("Player disabled", logging);
		}

		if (opponentOne.disable())
		{
			handPanel.hideLabelForPlayer(1);
			Debug.append("Opponent 1 disabled", logging);
		}
		
		if (opponentTwo.disable())
		{
			handPanel.hideLabelForPlayer(2);
			Debug.append("Opponent 2 disabled", logging);
		}
		
		if (opponentThree.disable())
		{
			handPanel.hideLabelForPlayer(3);
			Debug.append("Opponent 3 disabled", logging);
		}
	}
	
	private void correctPersonToStart()
	{
		if (personToStart == 3 && !opponentThree.isEnabled())
		{
			if (player.isEnabled())
			{
				personToStart = 0;
			}
			else if (opponentTwo.isEnabled())
			{
				personToStart = 2;
			}
		}
		else if (personToStart == 2 && !opponentTwo.isEnabled())
		{
			if (opponentOne.isEnabled())
			{
				personToStart = 1;
			}
			else if (opponentThree.isEnabled())
			{
				personToStart = 3;
			}
		}
		else if (personToStart == 1 && !opponentOne.isEnabled())
		{
			if (opponentThree.isEnabled())
			{
				personToStart = 3;
			}
			else if (player.isEnabled())
			{
				personToStart = 0;
			}
		}
		else if (personToStart == 0 && !player.isEnabled())
		{
			if (opponentTwo.isEnabled())
			{
				personToStart = 2;
			}
			else if (opponentOne.isEnabled())
			{
				personToStart = 1;
			}
		}
	}
	
	private void getNewGameVariablesFromRegistry()
	{
		playBlind = prefs.getBoolean(PREFERENCES_BOOLEAN_PLAY_BLIND, false);
		playWithHandicap = prefs.getBoolean(PREFERENCES_BOOLEAN_PLAY_WITH_HANDICAP, false);
		handicapAmount = prefs.getInt(PREFERENCES_INT_HANDICAP_AMOUNT, 1);
		opponentTwo.setEnabled(prefs.getBoolean(PREFERENCES_BOOLEAN_OPPONENT_TWO_ENABLED, true));
		opponentThree.setEnabled(prefs.getBoolean(PREFERENCES_BOOLEAN_OPPONENT_THREE_ENABLED, true));
		opponentOne.setStrategy(prefs.get(PREFERENCES_STRING_OPPONENT_ONE_STRATEGY, "Basic"));
		opponentTwo.setStrategy(prefs.get(PREFERENCES_STRING_OPPONENT_TWO_STRATEGY, "Basic"));
		opponentThree.setStrategy(prefs.get(PREFERENCES_STRING_OPPONENT_THREE_STRATEGY, "Basic"));

		settings = GameSettings.fromRegistry(prefs, getGameMode());

		handPanel.fireAppearancePreferencesChange();
		handPanel.initPlayerNames();
		
		setPlayerNamesFromHandPanel();
	}
	
	private void setPlayerNamesFromHandPanel()
	{
		player.setName(handPanel.getPlayerName());
		opponentOne.setName(handPanel.getOpponentOneName());
		opponentTwo.setName(handPanel.getOpponentTwoName());
		opponentThree.setName(handPanel.getOpponentThreeName());
	}
	
	private void initNumberOfCards() 
	{
		opponentOne.setNumberOfCards(settings.getStartingCards());
		opponentTwo.setNumberOfCards(opponentTwo.isEnabled()? settings.getStartingCards():0);
		opponentThree.setNumberOfCards(opponentThree.isEnabled()? settings.getStartingCards():0);
		int handicapCoeff = playWithHandicap? 1:0;
		player.setNumberOfCards(settings.getStartingCards() - (handicapCoeff * handicapAmount));
		
		totalNumberOfCards = player.getNumberOfCards() + opponentOne.getNumberOfCards() 
						   + opponentTwo.getNumberOfCards() + opponentThree.getNumberOfCards();
		
		player.setCardsToSubtract(0);
		opponentOne.setCardsToSubtract(0);
		opponentTwo.setCardsToSubtract(0);
		opponentThree.setCardsToSubtract(0);
	}
	
	public void setCardsToSubtract(Player playerToSubtract)
	{
		playerToSubtract.setCardsToSubtract(1);
		personToStart = playerToSubtract.getPlayerNumber();
		
		if (playerToSubtract == player
		  && player.getActualNumberOfCards() == 0)
		{
			AchievementsUtil.recordGamePlayed(getGameMode());
			AchievementsUtil.updateStreaksForLoss();
			inGameReplay.putInt(REPLAY_INT_PLAYER_WON, -1);
		}
	}
	
	protected boolean checkForResult()
	{
		//Do this a weird way - we put the xNumberOfCards variables into the saved game *after* this, so if we reduce them here when you 
		//continue the game that person will have one less card visible on the screen. 
		int playerCards = player.getActualNumberOfCards();
		int opponentOneCards = opponentOne.getActualNumberOfCards();
		int opponentTwoCards = opponentTwo.getActualNumberOfCards();
		int opponentThreeCards = opponentThree.getActualNumberOfCards();

		int winningPlayer = GameUtil.getWinningPlayer(playerCards, opponentOneCards, opponentTwoCards, opponentThreeCards);

		if (winningPlayer == -1)
		{
			return false;
		}
		else if (winningPlayer == 0)
		{
			AchievementsUtil.recordGamePlayed(getGameMode());
			AchievementsUtil.recordWin(getGameMode());

			int numberOfRounds = inGameReplay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0);
			int startNumberOfCards = inGameReplay.getInt(1 + REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS, 0);
			int myCards = inGameReplay.getInt(1 + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, 0);

			AchievementsUtil.checkForPerfectGame(numberOfRounds, startNumberOfCards);
			AchievementsUtil.checkForFullBlindGame(startNumberOfCards, playBlind, handPanel.getHasViewedHandThisGame(), settings.getCardReveal());
			AchievementsUtil.unlockNuclearStrike(myCards, startNumberOfCards, playBlind, handPanel.getHasViewedHandThisGame(), settings.getCardReveal());
			AchievementsUtil.unlockHandicapAchievements(myCards, startNumberOfCards);
			AchievementsUtil.unlockPrecision(hasOverbid, numberOfRounds);
			
			unlockEndOfGameAchievements(startNumberOfCards);

			inGameReplay.putInt(REPLAY_INT_PLAYER_WON, 1);
			inGameReplay.putInt(REPLAY_INT_GAME_COMPLETE, 1);
			GameUtil.showResultDialog(winningPlayer, handPanel);
			handPanel.cancelTimer();
			return true;
		}
		else
		{
			inGameReplay.putInt(REPLAY_INT_PLAYER_WON, -1);
			inGameReplay.putInt(REPLAY_INT_GAME_COMPLETE, 1);
			GameUtil.showResultDialog(winningPlayer, handPanel);
			handPanel.cancelTimer();
			return true;
		}
	}
	
	protected void roundEnded(int playerLastToAct)
	{
		ScreenCache.get(MainScreen.class).enableNewGameOption(true);
		saveRoundForReplay();

		firstRound = false;
		
		handPanel.selectPlayerInAwtThread(playerLastToAct, false);
		handPanel.setViewCardsVisibility(false);
		
		gameOver = checkForResult();
		
		AchievementsUtil.unlockSpectator(gameOver, earnedSpectator);

		if (gameOver)
		{
			ReplayFileUtil.saveInGameReplayToFile();
		}
		else
		{
			currentlyOnChallenge = true;
			boolean autoStart = prefs.getBoolean(PREFERENCES_BOOLEAN_AUTO_START_NEXT_ROUND, false);
			
			if (autoStart)
			{
				int seconds = prefs.getInt(PREFERENCES_INT_AUTO_START_SECONDS, 2);
				nextRoundTimer.schedule(new NewRoundTask(), seconds * 1000);
			}
			else
			{
				ScreenCache.get(MainScreen.class).showNextRoundButton();
			}
		}
	}
	
	protected void saveRoundForReplay()
	{
		inGameReplay.putInt(REPLAY_INT_GAME_MODE, ReplayConstantsKt.toReplayConstant(getGameMode()));
		inGameReplay.put(REPLAY_STRING_OPPONENT_ONE_STRATEGY, opponentOne.getStrategy());
		inGameReplay.put(REPLAY_STRING_OPPONENT_TWO_STRATEGY, opponentTwo.getStrategy());
		inGameReplay.put(REPLAY_STRING_OPPONENT_THREE_STRATEGY, opponentThree.getStrategy());		
		
		int roundsSoFar = inGameReplay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0) + 1;
		inGameReplay.putInt(REPLAY_INT_ROUNDS_SO_FAR, roundsSoFar);
		
		//save the listmodel
		DefaultListModel<Bid> listmodel = ScreenCache.get(MainScreen.class).getListmodel();
		int historySize = listmodel.size();
		inGameReplay.putInt(roundsSoFar + REPLAY_INT_HISTORY_SIZE, historySize);
		for (int i = 0; i < historySize; i++)
		{
			Bid bid = listmodel.get(i);
			inGameReplay.put(roundsSoFar + REPLAY_STRING_LISTMODEL + i, bid.toXmlString());
		}
		
		inGameReplay.putBoolean(REPLAY_BOOLEAN_PLAY_BLIND, playBlind);
		inGameReplay.putBoolean(REPLAY_BOOLEAN_PLAY_WITH_HANDICAP, playWithHandicap);
		inGameReplay.putInt(REPLAY_INT_HANDICAP_AMOUNT, handicapAmount);
		inGameReplay.putBoolean(REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME, hasActedBlindThisGame);
		inGameReplay.putBoolean(REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME, handPanel.getHasViewedHandThisGame());
		inGameReplay.putBoolean(REPLAY_BOOLEAN_INCLUDE_MOONS, settings.getIncludeMoons());
		inGameReplay.putBoolean(REPLAY_BOOLEAN_INCLUDE_STARS, settings.getIncludeStars());
		inGameReplay.putBoolean(REPLAY_BOOLEAN_CHEAT_USED, cheatUsed);
		
		//save the player hands
		inGameReplay.putInt(roundsSoFar + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, player.getNumberOfCards());
		inGameReplay.putInt(roundsSoFar + REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS, opponentOne.getNumberOfCards());
		inGameReplay.putInt(roundsSoFar + REPLAY_INT_OPPONENT_TWO_NUMBER_OF_CARDS, opponentTwo.getNumberOfCards());
		inGameReplay.putInt(roundsSoFar + REPLAY_INT_OPPONENT_THREE_NUMBER_OF_CARDS, opponentThree.getNumberOfCards());

		player.saveHandToRegistry(inGameReplay, roundsSoFar + REPLAY_STRING_PLAYER_HAND);
		opponentOne.saveHandToRegistry(inGameReplay, roundsSoFar + REPLAY_STRING_OPPONENT_ONE_HAND);
		opponentTwo.saveHandToRegistry(inGameReplay, roundsSoFar + REPLAY_STRING_OPPONENT_TWO_HAND);
		opponentThree.saveHandToRegistry(inGameReplay, roundsSoFar + REPLAY_STRING_OPPONENT_THREE_HAND);
		
		//save non-round-dependent stuff
		inGameReplay.put(REPLAY_STRING_PLAYER_NAME, handPanel.getPlayerName());
		inGameReplay.put(REPLAY_STRING_OPPONENT_ONE_NAME, handPanel.getOpponentOneName());
		inGameReplay.put(REPLAY_STRING_OPPONENT_TWO_NAME, handPanel.getOpponentTwoName());
		inGameReplay.put(REPLAY_STRING_OPPONENT_THREE_NAME, handPanel.getOpponentThreeName());
		inGameReplay.putInt(REPLAY_INT_JOKER_VALUE, settings.getJokerValue());
		
		//save who is enabled
		inGameReplay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_PLAYER_ENABLED, player.isEnabled());
		inGameReplay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_OPPONENT_ONE_ENABLED, opponentOne.isEnabled());
		inGameReplay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_OPPONENT_TWO_ENABLED, opponentTwo.isEnabled());
		inGameReplay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_OPPONENT_THREE_ENABLED, opponentThree.isEnabled());
		
		//save who started
		inGameReplay.putInt(roundsSoFar + "PERSON_TO_START", personToStart);
		
		ScreenCache.getReplayDialog(IN_GAME_REPLAY).roundAdded();
	}
	
	protected void saveGame()
	{
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_IS_GAME_TO_CONTINUE, true);
		savedGame.put(SAVED_GAME_STRING_GAME_MODE, getGameMode().name());

		//settings
		settings.exportToRegistry(savedGame);
		
		//save the listmodel
		DefaultListModel<Bid> listmodel = ScreenCache.get(MainScreen.class).getListmodel();
		int historySize = listmodel.size();
		savedGame.putInt(SAVED_GAME_INT_HISTORY_SIZE, historySize);
		for (int i=0; i<historySize; i++)
		{
			Bid bid = listmodel.get(i);
			savedGame.put(SAVED_GAME_STRING_LISTMODEL + i, bid.toXmlString());
		}

		savedGame.putInt(SAVED_GAME_INT_PERSON_TO_START, personToStart);

		//save the player hands
		savedGame.putInt(SAVED_GAME_INT_PLAYER_NUMBER_OF_CARDS, player.getNumberOfCards());
		savedGame.putInt(SAVED_GAME_INT_OPPONENT_ONE_NUMBER_OF_CARDS, opponentOne.getNumberOfCards());
		savedGame.putInt(SAVED_GAME_INT_OPPONENT_TWO_NUMBER_OF_CARDS, opponentTwo.getNumberOfCards());
		savedGame.putInt(SAVED_GAME_INT_OPPONENT_THREE_NUMBER_OF_CARDS, opponentThree.getNumberOfCards());

		player.saveHandToRegistry(savedGame, SAVED_GAME_STRING_PLAYER_HAND);
		opponentOne.saveHandToRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_ONE_HAND);
		opponentTwo.saveHandToRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_TWO_HAND);
		opponentThree.saveHandToRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_THREE_HAND);
		
		player.saveRevealedCardsToRegistry(savedGame, SAVED_GAME_STRING_PLAYER_REVEALED_CARD);
		opponentOne.saveRevealedCardsToRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_ONE_REVEALED_CARD);
		opponentTwo.saveRevealedCardsToRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_TWO_REVEALED_CARD);
		opponentThree.saveRevealedCardsToRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_THREE_REVEALED_CARD);

		//save the names
		savedGame.put(SAVED_GAME_STRING_PLAYER_NAME, handPanel.getPlayerName());
		savedGame.put(SAVED_GAME_STRING_OPPONENT_ONE_NAME, handPanel.getOpponentOneName());
		savedGame.put(SAVED_GAME_STRING_OPPONENT_TWO_NAME, handPanel.getOpponentTwoName());
		savedGame.put(SAVED_GAME_STRING_OPPONENT_THREE_NAME, handPanel.getOpponentThreeName());

		//save who is enabled
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_PLAYER_ENABLED, player.isEnabled());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_OPPONENT_ONE_ENABLED, opponentOne.isEnabled());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_OPPONENT_TWO_ENABLED, opponentTwo.isEnabled());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_OPPONENT_THREE_ENABLED, opponentThree.isEnabled());
		
		//save the current player
		savedGame.putInt(SAVED_GAME_INT_CURRENT_PLAYER, currentPlayer.getPlayerNumber());
		
		//save strategies
		savedGame.put(SAVED_GAME_STRING_OPPONENT_ONE_STRATEGY, opponentOne.getStrategy());
		savedGame.put(SAVED_GAME_STRING_OPPONENT_TWO_STRATEGY, opponentTwo.getStrategy());
		savedGame.put(SAVED_GAME_STRING_OPPONENT_THREE_STRATEGY, opponentThree.getStrategy());
		
		//exitedOnChallenge stuff
		boolean exitedOnChallenge = currentlyOnChallenge;
		
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_EXITED_ON_CHALLENGE, exitedOnChallenge);
		savedGame.put(SAVED_GAME_STRING_RESULT_TEXT, ScreenCache.get(MainScreen.class).getResultText());
		savedGame.putInt(SAVED_GAME_INT_PLAYER_CARDS_TO_SUBTRACT, player.getCardsToSubtract());
		savedGame.putInt(SAVED_GAME_INT_OPPONENT_ONE_CARDS_TO_SUBTRACT, opponentOne.getCardsToSubtract());
		savedGame.putInt(SAVED_GAME_INT_OPPONENT_TWO_CARDS_TO_SUBTRACT, opponentTwo.getCardsToSubtract());
		savedGame.putInt(SAVED_GAME_INT_OPPONENT_THREE_CARDS_TO_SUBTRACT, opponentThree.getCardsToSubtract());

		//other booleans
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_FIRST_ROUND, firstRound);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_PLAY_BLIND, playBlind);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_PLAY_WITH_HANDICAP, playWithHandicap);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME, hasActedBlindThisGame);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME, handPanel.getHasViewedHandThisGame());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_VIEW_CARDS_VISIBLE, handPanel.isPlayingBlind());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_HAS_OVERBID, hasOverbid);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_CHEAT_USED, cheatUsed);
		
		//other stuff
		savedGame.putInt(SAVED_GAME_INT_MAX_BID, bidPanel.getMaxBid());
		savedGame.putInt(SAVED_GAME_INT_HANDICAP_AMOUNT, handicapAmount);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_REVEAL_LISTENER, handPanel.isRevealListenerActive());
	}
	
	/**
	 * Continue Game
	 */
	public void continueGame()
	{
		try
		{
			//set up deck stuff
			settings = GameSettings.fromRegistry(savedGame, getGameMode());
			cheatUsed = savedGame.getBoolean(SAVED_GAME_BOOLEAN_CHEAT_USED, false);
			
			resetPlayers();
			loadLastBid();
			initialiseBidPanel();

			//set up the listmodel
			DefaultListModel<Bid> listmodel = ScreenCache.get(MainScreen.class).getListmodel();
			int historySize = savedGame.getInt(SAVED_GAME_INT_HISTORY_SIZE, 0);
			for (int i = 0; i < historySize; i++)
			{
				String modelItem = savedGame.get(SAVED_GAME_STRING_LISTMODEL + i, "");
				Bid bid = Bid.factoryFromXmlString(modelItem, settings.getIncludeMoons(), settings.getIncludeStars());
				listmodel.addElement(bid);
			}

			//get who is enabled
			player.setEnabled(savedGame.getBoolean(SAVED_GAME_BOOLEAN_PLAYER_ENABLED, false));
			opponentOne.setEnabled(savedGame.getBoolean(SAVED_GAME_BOOLEAN_OPPONENT_ONE_ENABLED, false));
			opponentTwo.setEnabled(savedGame.getBoolean(SAVED_GAME_BOOLEAN_OPPONENT_TWO_ENABLED, false));
			opponentThree.setEnabled(savedGame.getBoolean(SAVED_GAME_BOOLEAN_OPPONENT_THREE_ENABLED, false));

			setPlayerNames();
			setPlayerHandsAndRevealedCards();

			//blind, handicap etc
			playBlind = savedGame.getBoolean(SAVED_GAME_BOOLEAN_PLAY_BLIND, false);
			playWithHandicap = savedGame.getBoolean(SAVED_GAME_BOOLEAN_PLAY_WITH_HANDICAP, false);
			handicapAmount = savedGame.getInt(SAVED_GAME_INT_HANDICAP_AMOUNT, 1);
			handPanel.setInitted(true);
			handPanel.setHasViewedHandThisGame(savedGame.getBoolean(SAVED_GAME_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME, false));
			hasActedBlindThisGame = savedGame.getBoolean(SAVED_GAME_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME, false);
			
			hasOverbid = savedGame.getBoolean(SAVED_GAME_BOOLEAN_HAS_OVERBID, false);
			loadSpecificVariables();

			boolean viewCardsVisible = savedGame.getBoolean(SAVED_GAME_BOOLEAN_VIEW_CARDS_VISIBLE, false);
			handPanel.setViewCardsVisibility(viewCardsVisible && player.isEnabled());
			displayHands();

			//set the strategies
			opponentOne.setStrategy(savedGame.get(SAVED_GAME_STRING_OPPONENT_ONE_STRATEGY, "Basic"));
			opponentTwo.setStrategy(savedGame.get(SAVED_GAME_STRING_OPPONENT_TWO_STRATEGY, "Basic"));
			opponentThree.setStrategy(savedGame.get(SAVED_GAME_STRING_OPPONENT_THREE_STRATEGY, "Basic"));
			
			personToStart = savedGame.getInt(SAVED_GAME_INT_PERSON_TO_START, 0);
			handPanel.assignAsteriskToStartingPlayer(personToStart);
			
			//other booleans
			firstRound = savedGame.getBoolean(SAVED_GAME_BOOLEAN_FIRST_ROUND, false);
			gameOver = false;

			int currentPlayerNumber = savedGame.getInt(SAVED_GAME_INT_CURRENT_PLAYER, 0);
			currentPlayer = getPlayer(currentPlayerNumber);

			boolean exitedOnChallenge = savedGame.getBoolean(SAVED_GAME_BOOLEAN_EXITED_ON_CHALLENGE, false);

			if (exitedOnChallenge)
			{
				updateScreenForChallenge();
			}
			else if (currentPlayer == player)
			{
				if (savedGame.getBoolean(SAVED_GAME_BOOLEAN_REVEAL_LISTENER, false))
				{
					bidMade(lastBid);
				}
				else
				{
					handPanel.selectPlayerInAwtThread(0, true);
				}
			}
			else 
			{
				processCpuTurn(currentPlayer.getPlayerNumber());
			}

			savedGame.clear();

		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
			DialogUtil.showError("A serious error occurred retrieving your game.");
		}
	}
	
	public void initialiseBidPanel()
	{
		bidPanel.showBidPanel(true);
		bidPanel.loadState(savedGame);
		bidPanel.enableBidPanel(true);
		
		if (lastBid != null)
		{
			bidPanel.adjust(lastBid);
		}
	}
	public void setPlayerNames()
	{
		handPanel.loadPlayerNames(savedGame);
		handPanel.displayLabels(player.isEnabled(), opponentOne.isEnabled(), 
								opponentTwo.isEnabled(), opponentThree.isEnabled());
		
		setPlayerNamesFromHandPanel();
	}
	
	private void setPlayerHandsAndRevealedCards()
	{
		handPanel.fireAppearancePreferencesChange();
		bidPanel.fireAppearancePreferencesChange();

		player.populateHandFromRegistry(savedGame, SAVED_GAME_STRING_PLAYER_HAND, 
		  SAVED_GAME_STRING_PLAYER_REVEALED_CARD, SAVED_GAME_INT_PLAYER_NUMBER_OF_CARDS);
		
		opponentOne.populateHandFromRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_ONE_HAND, 
		  SAVED_GAME_STRING_OPPONENT_ONE_REVEALED_CARD, SAVED_GAME_INT_OPPONENT_ONE_NUMBER_OF_CARDS);
		
		opponentTwo.populateHandFromRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_TWO_HAND, 
		  SAVED_GAME_STRING_OPPONENT_TWO_REVEALED_CARD, SAVED_GAME_INT_OPPONENT_TWO_NUMBER_OF_CARDS);
		
		opponentThree.populateHandFromRegistry(savedGame, SAVED_GAME_STRING_OPPONENT_THREE_HAND, 
		  SAVED_GAME_STRING_OPPONENT_THREE_REVEALED_CARD, SAVED_GAME_INT_OPPONENT_THREE_NUMBER_OF_CARDS);
	}
	
	private void updateScreenForChallenge()
	{
		handPanel.displayAndHighlightHands(getLastBidSuit());
		bidPanel.enableBidPanel(false);
		ScreenCache.get(MainScreen.class).showNextRoundButton();
		String resultText = savedGame.get(SAVED_GAME_STRING_RESULT_TEXT, "");
		ScreenCache.get(MainScreen.class).setResultText(resultText);
		
		player.setCardsToSubtract(savedGame.getInt(SAVED_GAME_INT_PLAYER_CARDS_TO_SUBTRACT, 0));
		opponentOne.setCardsToSubtract(savedGame.getInt(SAVED_GAME_INT_OPPONENT_ONE_CARDS_TO_SUBTRACT, 0));
		opponentTwo.setCardsToSubtract(savedGame.getInt(SAVED_GAME_INT_OPPONENT_TWO_CARDS_TO_SUBTRACT, 0));
		opponentThree.setCardsToSubtract(savedGame.getInt(SAVED_GAME_INT_OPPONENT_THREE_CARDS_TO_SUBTRACT, 0));
	}
	
	public void cancelNewRound()
	{
		currentlyOnChallenge = false;
		nextRoundTimer.cancel();
		nextRoundTimer = new Timer("Timer-NextRound");
	}
	
	protected void randomlyReplaceCardsWithJokers()
	{
		if (!currentlyOnChallenge)
		{
			Random random = new Random();
			int jokersToAdd = random.nextInt(5) + 1; //1-5
			logger.info("rainingJokers", "Adding " + jokersToAdd + " jokers");
			
			List<String> allCards = getConcatenatedHands();
			
			while (containsNonJoker(allCards) && jokersToAdd > 0)
			{
				replaceRandomCardWithJoker();
				allCards = getConcatenatedHands();
				jokersToAdd--;
			}

			displayHands();
		}
	}
	
	private void replaceRandomCardWithJoker()
	{
		Random rand = new Random();
		String jokerToAdd = "Jo" + rand.nextInt(4); //0, 1, 2, 3
		List<String> hand = pickHandWithNonJokerAtRandom();
		
		int length = hand.size();
		int position = (rand.nextInt(length));
		String cardToReplace = hand.get(position);
		if (cardToReplace.contains("Jo"))
		{
			replaceRandomCardWithJoker();
			return;
		}

		hand.set(position, jokerToAdd);
	}
	
	private List<String> pickHandWithNonJokerAtRandom()
	{
		List<String>[] allHands = new List[]{player.getHand(), opponentOne.getHand(), opponentTwo.getHand(), opponentThree.getHand()};
		Random rand = new Random();
		List<String> hand = allHands[rand.nextInt(4)];
		
		if (!containsNonJoker(hand))
		{
			return pickHandWithNonJokerAtRandom();
		}
		else
		{
			return hand;
		}
	}
	
	public List<String> getConcatenatedHands()
	{
		List<String> playerHand = player.getHand();
		List<String> opponentOneHand = opponentOne.getHand();
		List<String> opponentTwoHand = opponentTwo.getHand();
		List<String> opponentThreeHand = opponentThree.getHand();

		var result = new ArrayList<String>();
		result.addAll(playerHand);
		result.addAll(opponentOneHand);
		result.addAll(opponentTwoHand);
		result.addAll(opponentThreeHand);
		return result;
	}
	
	private void processPlayerBid()
	{
		handPanel.selectPlayerInAwtThread(0, false);
		Debug.append("Player bid " + lastBid, true);
		
		boolean actedBlind = handPanel.isPlayingBlind();
		hasActedBlindThisGame &= actedBlind;
		lastBid.setBlind(actedBlind);
		addToListmodel(lastBid);
		
		updateAchievementVariables();
		if (lastBid.isPerfect(getConcatenatedHands(), settings))
		{
			handlePerfectBid(lastBid);
		}
		
		if (lastBid.isOverbid(getConcatenatedHands(), settings.getJokerValue()))
		{
			hasOverbid = true;
		}
		
		processNextTurn(0);
	}
	
	private void handlePerfectBid(Bid bid)
	{
		Debug.append("Player made a perfect bid.", logging);
		if (bid.isOverAchievementThreshold())
		{
			if (handPanel.isPlayingBlind())
			{
				earnedPsychic = true;
			}
			
			setPerfectBidBooleans();
		}
	}
	
	public void processChallenge(Player challenger)
	{
		Debug.appendBanner("Processing Challenge", logging);
		Debug.append("Challenger: " + challenger, logging);

		unlockPerfectBidAchievements();
		
		Player playerChallenged = lastBid.getPlayer();
		if (!lastBid.isOverbid(getConcatenatedHands(), settings.getJokerValue()))
		{
			Debug.append("not an overbid", logging);
			setCardsToSubtract(challenger);
		}
		else
		{
			Debug.append("overbid", logging);
			setCardsToSubtract(playerChallenged);
		}
		
		bidPanel.enableBidPanel(false);
		showResult();
		roundEnded(challenger.getPlayerNumber());
	}
	
	public void processIllegal(Player illegaller)
	{
		Debug.appendBanner("Processing Illegal", logging);
		
		unlockPerfectBidAchievements();
		Player bidder = lastBid.getPlayer();
		Debug.append("Bidder: " + bidder, logging);
		
		if (lastBid.isPerfect(getConcatenatedHands(), settings))
		{
			Debug.append("bid was perfect", logging);
			
			if (illegaller == player)
			{
				AchievementsUtil.unlockCitizensArrest();
			}
			
			setCardsToSubtract(bidder);
		}
		else
		{
			Debug.append("bid was not perfect", logging);
			setCardsToSubtract(illegaller);
		}
		
		bidPanel.enableBidPanel(false);
		showResult();
		roundEnded(illegaller.getPlayerNumber());
	}
	
	public void processCpuTurn(int opponentNumber)
	{
		ScreenCache.get(MainScreen.class).enableNewGameOption(false);
		currentPlayer = getPlayer(opponentNumber);
		handPanel.selectPlayerInAwtThread(opponentNumber, true);
		bidPanel.enableBidPanel(false);
		
		int gameSpeed = prefs.getInt(PREFERENCES_INT_GAME_SPEED, 1000);
		
		cpuTurn.schedule(new DelayedOpponentTurn(currentPlayer), gameSpeed);
	}
	
	private Player getPlayer(int playerNumber)
	{
		if (playerNumber == 0)
		{
			return player;
		}
		else if (playerNumber == 1)
		{
			return opponentOne;
		}
		else if (playerNumber == 2)
		{
			return opponentTwo;
		}
		else if (playerNumber == 3)
		{
			return opponentThree;
		}
		else
		{
			Debug.stackTrace("Unexpected opponentNumber: " + playerNumber);
			return null;
		}
	}
	
	public void processNextTurn(int previousPlayer)
	{
		unlockOmniscient();
		
		int nextPlayer = GameUtil.getNextPlayer(previousPlayer);
		if ((nextPlayer == 1 && opponentOne.isEnabled())
		  || (nextPlayer == 2 && opponentTwo.isEnabled())
		  || (nextPlayer == 3 && opponentThree.isEnabled()))
		{
			processCpuTurn(nextPlayer);
		}
		else if (nextPlayer == 0 && player.isEnabled())
		{
			bidPanel.adjust(lastBid);
			doHumanTurn();
		}
		else
		{
			processNextTurn(nextPlayer);
		}
	}
	
	private void unlockOmniscient()
	{
		ArrayList<String> revealedCards = new ArrayList<>();
		revealedCards.addAll(opponentOne.getRevealedCards());
		revealedCards.addAll(opponentTwo.getRevealedCards());
		revealedCards.addAll(opponentThree.getRevealedCards());
		
		AchievementsUtil.unlockOmniscient(revealedCards);
	}
	
	private void doHumanTurn()
	{
		ScreenCache.get(MainScreen.class).enableNewGameOption(true);
		currentPlayer = player;
		handPanel.selectPlayerInAwtThread(0, true);
		bidPanel.enableBidPanel(true);
	}
	
	private void displayHands()
	{
		handPanel.displayHandsInGame(player.getHand(), opponentOne.getHand(), 
		  opponentTwo.getHand(), opponentThree.getHand());
		
		ArrayList<String> revealedCards = new ArrayList<>();
		revealedCards.addAll(player.getRevealedCards());
		revealedCards.addAll(opponentOne.getRevealedCards());
		revealedCards.addAll(opponentTwo.getRevealedCards());
		revealedCards.addAll(opponentThree.getRevealedCards());
		
		for (int i=0; i<revealedCards.size(); i++)
		{
			String card = revealedCards.get(i);
			handPanel.revealCard(card);
		}
	}
	
	public StrategyParams factoryStrategyParms(Player opponent)
	{
		var cardsOnShow = Stream.of(player, opponentOne, opponentTwo, opponentThree)
				.filter(p -> p != opponent)
				.flatMap(p -> p.getRevealedCards().stream())
				.collect(Collectors.toList());

		return new StrategyParams(settings, totalNumberOfCards, cardsOnShow, lastBid, true);
	}
	
	public void fireAppearancePreferencesChange()
	{
		handPanel.fireAppearancePreferencesChange();
		bidPanel.fireAppearancePreferencesChange();
	}
	
	/**
	 * BidListener
	 */
	@Override
	public void bidMade(Bid bid) 
	{
		bidPanel.enableBidPanel(false);
		bid.setPlayer(player);
		lastBid = bid;
		
		if (settings.getCardReveal()
		  && player.hasMoreCardsToReveal())
		{
			handPanel.activateRevealListener();
		}
		else
		{
			processPlayerBid();
		}
	}
	
	@Override
	public void challengeMade() 
	{
		Debug.append("Player challenged.", logging);
		boolean actedBlind = handPanel.isPlayingBlind();
		hasActedBlindThisGame &= actedBlind;
		
		Bid bid = new ChallengeBid();
		bid.setPlayer(player);
		bid.setBlind(actedBlind);
		addToListmodel(bid);
		
		processChallenge(player);
	}
	
	@Override
	public void illegalCalled() 
	{
		Debug.append("Player called Illegal!", logging);
		boolean actedBlind = handPanel.isPlayingBlind();
		hasActedBlindThisGame &= actedBlind;
		
		Bid bid = new IllegalBid();
		bid.setPlayer(player);
		bid.setBlind(actedBlind);
		addToListmodel(bid);
		
		processIllegal(player);
	}
	
	/**
	 * RevealListener
	 */
	@Override
	public void cardRevealed(String card)
	{
		player.addRevealedCard(card);
		lastBid.setCardToReveal(card);
		
		processPlayerBid();
	}
	
	private void addToListmodel(Bid bid)
	{
		DefaultListModel<Bid> listmodel = ScreenCache.get(MainScreen.class).getListmodel();
		listmodel.add(0, bid);
	}
	
	/**
	 * Gets / Sets
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Inner classes
	 */
	class NewRoundTask extends TimerTask
	{
		@Override
		public void run()
		{
			ScreenCache.get(MainScreen.class).hideResult();
			
			currentlyOnChallenge = false;
			MainScreen scrn = ScreenCache.get(MainScreen.class);
			scrn.startNewRound();
		}
	}
	
	class DelayedOpponentTurn extends TimerTask
	{
		private Player opponent = null;
		
		public DelayedOpponentTurn(Player opponent)
		{
			this.opponent = opponent;
		}
		
		@Override
		public void run()
		{
			Debug.appendBanner("Opponent " + opponent, logging);
			
			StrategyParams parms = factoryStrategyParms(opponent);
			Bid bid = CpuStrategies.processOpponentTurn(parms, opponent);
			if (bid == null)
			{
				//Something's gone wrong - probably an API strategy that timed out or did something invalid. 
				String info = opponent.getName() + " has had their strategy reset to "
							+ CpuStrategies.STRATEGY_BASIC;
				DialogUtil.showInfo(info);
				opponent.setStrategy(CpuStrategies.STRATEGY_BASIC);
				bid = CpuStrategies.processOpponentTurn(parms, opponent);
			}
			
			if (bid == null)
			{
				//Something's gone very wrong...
				handPanel.selectPlayerInAwtThread(opponent.getPlayerNumber(), false);
				ScreenCache.get(MainScreen.class).enableNewGameOption(true);
				return;
			}
			
			bid.setPlayer(opponent);
			addToListmodel(bid);
			
			if (bid.isChallenge())
			{
				processChallenge(opponent);
			}
			else if (bid.isIllegal())
			{
				processIllegal(opponent);
			}
			else
			{
				lastBid = bid;
				
				if (settings.getCardReveal())
				{
					String card = bid.getCardToReveal();
					handPanel.revealCard(card);
				}
				
				processNextTurn(opponent.getPlayerNumber());
			}
		}
	}
}