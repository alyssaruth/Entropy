package online.screen;

import java.awt.BorderLayout;
import java.util.UUID;

import game.GameSettings;
import object.Bid;
import screen.VectropyBidPanel;
import util.*;

public class VectropyRoom extends GameRoom
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
		lastBid = VectropyUtil.getEmptyBid(getIncludeMoons(), getIncludeStars());
		hmBidByPlayerNumber.clear();
	}

	@Override
	public void updatePerfectBidVariables(Bid bid) 
	{
		earnedMathematician = true;
	}
	
	@Override
	public void updateScreenForChallengeOrIllegal() 
	{
		if (isVisible())
		{
			handPanel.displayAndHighlightHands(-1);
			AchievementsUtil.unlockVectropyPerfectBidAchievements(earnedMathematician, earnedPsychic);

			String resultStr = VectropyUtil.getResult(hmHandByAdjustedPlayerNumber, getJokerValue(), -1, getIncludeMoons(), getIncludeStars());
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
	public void updateAchievementVariables(Bid bid)
	{
		//do nothing
	}
}
