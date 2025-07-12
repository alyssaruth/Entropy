package screen;

import java.awt.BorderLayout;

import com.fasterxml.jackson.core.JsonProcessingException;
import game.EntropyBidAction;
import game.GameMode;
import game.Suit;
import object.EntropyAchievementsTracker;
import object.EntropyBid;
import util.Registry;

import static game.CardsUtilKt.countSuit;
import static game.CheatUtilKt.getMaxBidString;
import static game.EntropyUtilKt.perfectBidAmount;
import static game.EntropyUtilKt.perfectBidSuit;
import static utils.CoreGlobals.jsonMapper;

public class EntropyScreen extends GameScreen<EntropyBidAction>
{
	private static final long serialVersionUID = 1L;

	private EntropyAchievementsTracker achievementTracker = new EntropyAchievementsTracker();

	public EntropyScreen()
	{
		setFocusable(true);
		bidPanel = new EntropyBidPanel(player.getName(), handPanel);
		bidPanel.showBidPanel(false);
		
		setLayout(new BorderLayout(0, 0));
		add(handPanel, BorderLayout.CENTER);
		add(bidPanel, BorderLayout.SOUTH);
		
		handPanel.setOpaque(false);
		bidPanel.setLogging(true);
		bidPanel.addBidListener(this);
	}
	
	@Override
	public void showResult()
	{
		var lastBidSuit = lastBid.getSuit();
		handPanel.displayAndHighlightHands(lastBidSuit);
		
		int total = countSuit(lastBidSuit, getConcatenatedHands(), settings.getJokerValue());
		String suitsStr = lastBidSuit.getDescription(total);
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
	public void saveGame() throws JsonProcessingException
	{
		super.saveGame();

		//save bid amounts and bid suits
		if (lastBid != null)
		{
            savedGame.put(Registry.SAVED_GAME_STRING_LAST_BID, jsonMapper.writeValueAsString(lastBid));
		}
			
		//other booleans
		achievementTracker.saveState();

		//save screen state
		bidPanel.saveState(savedGame);
	}

	@Override
	protected void saveRoundForReplay() throws JsonProcessingException
	{
		int roundsSoFar = inGameReplay.getInt(Registry.REPLAY_INT_ROUNDS_SO_FAR, 0) + 1;
		inGameReplay.put(roundsSoFar + Registry.REPLAY_STRING_LAST_BID_SUIT_NAME, lastBid.getSuit().name());
		super.saveRoundForReplay();
	}
	
	@Override
	public void loadLastBid() throws JsonProcessingException
	{
        String lastBidStr = savedGame.get(Registry.SAVED_GAME_STRING_LAST_BID, "");

        if (!lastBidStr.isEmpty())
        {
            lastBid = jsonMapper.readValue(lastBidStr, EntropyBidAction.class);
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
			handPanel.displayAndHighlightHands(null);
			cheatUsed = true;
		}
		else if (command.equals("perfectbid"))
		{
			cheatUsed = true;
			Suit perfectBidSuit = perfectBidSuit(getConcatenatedHands(), settings.getJokerValue(), settings.getIncludeStars());
			int perfectBidAmount = perfectBidAmount(getConcatenatedHands(), settings.getJokerValue());
			return perfectBidAmount + " " + perfectBidSuit.getDescription(perfectBidAmount);
		}
		else if (command.equals("maxbids"))
		{
			cheatUsed = true;
			return getMaxBidString(getConcatenatedHands(), settings);
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
	public Suit getLastBidSuit()
	{
        return lastBid.getSuit();
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