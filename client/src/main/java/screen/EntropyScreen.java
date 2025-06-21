package screen;

import java.awt.BorderLayout;
import java.util.List;

import game.GameMode;
import object.EntropyAchievementsTracker;
import object.EntropyBid;
import util.CardsUtil;
import util.EntropyUtil;
import util.Registry;

public class EntropyScreen extends GameScreen
{
	private static final long serialVersionUID = 1L;

	private EntropyAchievementsTracker achievementTracker = new EntropyAchievementsTracker();

	public EntropyScreen()
	{
		setFocusable(true);
		bidPanel = new EntropyBidPanel();
		bidPanel.showBidPanel(false);
		
		setLayout(new BorderLayout(0, 0));
		add(handPanel, BorderLayout.CENTER);
		add(bidPanel, BorderLayout.SOUTH);
		
		handPanel.setOpaque(false);
		bidPanel.setLogging(true);
		bidPanel.addBidListener(this);
	}

//David added this comment!
	
	@Override
	public void showResult()
	{
		int lastBidSuitCode = ((EntropyBid)lastBid).getBidSuitCode();
		handPanel.displayAndHighlightHands(lastBidSuitCode);
		
		int total = countSuit(lastBidSuitCode);
		String suitsStr = CardsUtil.getSuitDesc(total, lastBidSuitCode);
		if (total == 1)
		{
			ScreenCache.get(MainScreen.class).setResultText("There was " + total + " " + suitsStr);
		}
		else
		{
			ScreenCache.get(MainScreen.class).setResultText("There were " + total + " " + suitsStr);
		}
	}

	@Override
	protected void initVariablesForNewGame()
	{
		super.initVariablesForNewGame();
		
		achievementTracker.reset();
	}

	@Override
	public void saveGame()
	{
		super.saveGame();

		//save bid amounts and bid suits
		if (lastBid != null)
		{
			savedGame.putInt(Registry.SAVED_GAME_INT_LAST_BID_SUIT_CODE, ((EntropyBid)lastBid).getBidSuitCode());
			savedGame.putInt(Registry.SAVED_GAME_INT_LAST_BID_AMOUNT, ((EntropyBid)lastBid).getBidAmount());
		}
			
		//other booleans
		achievementTracker.saveState();

		//save screen state
		bidPanel.saveState(savedGame);
	}

	@Override
	protected void saveRoundForReplay()
	{
		int roundsSoFar = inGameReplay.getInt(Registry.REPLAY_INT_ROUNDS_SO_FAR, 0) + 1;
		inGameReplay.putInt(roundsSoFar + Registry.REPLAY_INT_LAST_BID_SUIT_CODE, ((EntropyBid)lastBid).getBidSuitCode());
		super.saveRoundForReplay();
	}
	
	@Override
	public void loadLastBid()
	{
		int lastBidSuitCode = savedGame.getInt(Registry.SAVED_GAME_INT_LAST_BID_SUIT_CODE, -1);
		int lastBidAmount = savedGame.getInt(Registry.SAVED_GAME_INT_LAST_BID_AMOUNT, -1);
		
		if (lastBidSuitCode > -1)
		{
			lastBid = new EntropyBid(lastBidSuitCode, lastBidAmount);
		}
	}
	
	@Override
	public void loadSpecificVariables()
	{
		achievementTracker.loadState();
	}
	
	@Override
	public String processCommand(String command)
	{
		if (!bidPanel.isVisible())
		{
			return "";
		}
		
		if (command.equals("showmethecards"))
		{
			handPanel.displayAndHighlightHands(-1);
			cheatUsed = true;
		}
		else if (command.equals("perfectbid"))
		{
			cheatUsed = true;
			List<String> playerHand = player.getHand();
			List<String> opponentOneHand = opponentOne.getHand();
			List<String> opponentTwoHand = opponentTwo.getHand();
			List<String> opponentThreeHand = opponentThree.getHand();
			int perfectBidSuitCode = EntropyUtil.getPerfectBidSuitCode(playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, settings.getJokerValue(), settings.getIncludeStars());
			int perfectBidAmount = EntropyUtil.getPerfectBidAmount(playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand, settings.getJokerValue());
			String suit = CardsUtil.getSuitDesc(perfectBidAmount, perfectBidSuitCode);
			return perfectBidAmount + " " + suit;
		}
		else if (command.equals("maxbids"))
		{
			cheatUsed = true;
			return getMaxBidsStr();
		}
		else if (command.equals("rainingjokers"))
		{
			cheatUsed = true;
			randomlyReplaceCardsWithJokers();
		}

		return "";
	}

	@Override
	public GameMode getGameMode()
	{
		return GameMode.Entropy;
	}
	
	@Override
	public int getLastBidSuitCode()
	{
		return ((EntropyBid)lastBid).getBidSuitCode();
	}
	
	@Override
	public void unlockPerfectBidAchievements()
	{
		achievementTracker.unlockPerfectBidAchievements(earnedPsychic);
	}
	
	@Override
	public void setPerfectBidBooleans()
	{
		achievementTracker.updatePerfectBidVariables(lastBid);
	}
	
	@Override
	public void updateAchievementVariables()
	{
		achievementTracker.update(lastBid);
	}
	
	@Override
	public void unlockEndOfGameAchievements(int startingCards)
	{
		achievementTracker.unlockEndOfGameAchievements(startingCards);
	}
}