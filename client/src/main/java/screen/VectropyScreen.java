package screen;

import java.awt.BorderLayout;

import game.GameMode;
import object.Bid;
import object.VectropyBid;
import util.AchievementsUtil;
import util.Debug;
import util.Registry;
import util.VectropyUtil;

public class VectropyScreen extends GameScreen
{
	private static final long serialVersionUID = 1L;
	
	private boolean earnedMathematician = false;

	public VectropyScreen()
	{
		try
		{
			setFocusable(true);
			setLayout(new BorderLayout(0, 0));
			add(handPanel, BorderLayout.CENTER);
			handPanel.setOpaque(false);
			bidPanel = new VectropyBidPanel();
			add(bidPanel, BorderLayout.SOUTH);
			bidPanel.showBidPanel(false);
			
			bidPanel.addBidListener(this);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	@Override
	public void showResult()
	{
		handPanel.displayAndHighlightHands(-1);
		String result = VectropyUtil.getResult(player.getHand(), opponentOne.getHand(), opponentTwo.getHand(), 
						opponentThree.getHand(), jokerValue, -1, includeMoons, includeStars);
		ScreenCache.getMainScreen().setResultText("Result: " + result);
	}

	@Override
	protected void initVariablesForNewGame()
	{
		super.initVariablesForNewGame();
		
		earnedMathematician = false;
	}

	@Override
	public void saveGame()
	{
		super.saveGame();

		//save bid amounts and bid suits
		if (lastBid != null)
		{
			savedGame.put(Registry.SAVED_GAME_STRING_LAST_BID, lastBid.toXmlString());
		}

		//other booleans
		savedGame.putBoolean(Registry.SAVED_GAME_BOOLEAN_MATHEMATICIAN, earnedMathematician);

		//save screen state
		bidPanel.saveState(savedGame);
	}
	
	@Override
	public void loadLastBid()
	{
		String lastBidStr = savedGame.get(Registry.SAVED_GAME_STRING_LAST_BID, "");
		if (!lastBidStr.isEmpty())
		{
			lastBid = Bid.factoryFromXmlString(lastBidStr, includeMoons, includeStars);
		}
	}
	
	@Override
	public void loadSpecificVariables()
	{
		earnedMathematician = savedGame.getBoolean(Registry.SAVED_GAME_BOOLEAN_MATHEMATICIAN, false);
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
		else if (command.equals("maxbids") || command.equals("perfectbid"))
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

	/*
	 *  Get/sets
	 */
	public void setLastBid(VectropyBid lastBid)
	{
		this.lastBid = lastBid;
	}

	@Override
	public GameMode getGameMode()
	{
		return GameMode.Vectropy;
	}
	
	@Override
	public int getLastBidSuitCode()
	{
		return -1;
	}
	
	@Override
	public void unlockPerfectBidAchievements()
	{
		AchievementsUtil.unlockVectropyPerfectBidAchievements(earnedMathematician, earnedPsychic);
	}
	
	@Override
	public void setPerfectBidBooleans()
	{
		earnedMathematician = true;
	}

	@Override
	public void unlockEndOfGameAchievements(int startingCards)
	{
		//do nothing
	}
	
	@Override
	public void updateAchievementVariables()
	{
		///do nothing
	}
}