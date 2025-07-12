package online.screen;

import game.GameSettings;
import game.VectropyBidAction;
import screen.VectropyBidPanel;
import util.AchievementsUtil;
import util.ClientUtil;
import util.Registry;
import util.ReplayConstants;

import java.awt.*;
import java.util.UUID;

import static game.CardsUtilKt.extractCards;
import static game.RenderingUtilKt.getVectropyResult;


public class VectropyRoom extends GameRoom<VectropyBidAction>
{
	private boolean earnedMathematician = false;
	
	public VectropyRoom(UUID id, String roomName, GameSettings settings, int players)
	{
		super(id, roomName, settings, players);
		
		bidPanel = new VectropyBidPanel(ClientUtil.getUsername(), handPanel);
		leftPaneSouth.add(bidPanel, BorderLayout.CENTER);
		bidPanel.addBidListener(this);
	}
	
	@Override
	public void doSpecificResetGameVariables()
	{
		earnedMathematician = false;
	}
	
	@Override
	public void resetBids()
	{
		lastBid = null;
		hmBidByPlayerNumber.clear();
	}

	@Override
	public void updatePerfectBidVariables(VectropyBidAction bid)
	{
		earnedMathematician = true;
	}
	
	@Override
	public void updateScreenForChallengeOrIllegal() 
	{
		if (isVisible())
		{
			handPanel.displayAndHighlightHands(null);
			AchievementsUtil.unlockVectropyPerfectBidAchievements(earnedMathematician, earnedPsychic);

			String resultStr = getVectropyResult(extractCards(hmHandByAdjustedPlayerNumber), getJokerValue(), null, getIncludeMoons(), getIncludeStars());
			showResult("Result: " + resultStr);
		}
	}
	
	@Override
	public void saveModeSpecificVariablesForReplay()
	{
		replay.putInt(Registry.REPLAY_INT_GAME_MODE, ReplayConstants.GAME_MODE_VECTROPY_ONLINE);
		replayDialog.roundAdded();
	}
	
	@Override
	public void unlockEndOfGameAchievements()
	{
		//do nothing
	}

	@Override
	public void updateAchievementVariables(VectropyBidAction bid)
	{
		//do nothing
	}
}
