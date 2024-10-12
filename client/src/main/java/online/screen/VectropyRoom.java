package online.screen;

import java.awt.BorderLayout;

import object.Bid;
import screen.VectropyBidPanel;
import util.AchievementsUtil;
import util.GameConstants;
import util.Registry;
import util.VectropyUtil;

public class VectropyRoom extends GameRoom
{
	private boolean earnedMathematician = false;
	
	public VectropyRoom(String roomName, int players)
	{
		super(roomName, GameConstants.GAME_MODE_VECTROPY, players);
		
		bidPanel = new VectropyBidPanel();
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

			String resultStr = VectropyUtil.getResult(hmHandByAdjustedPlayerNumber, jokerValue, -1, getIncludeMoons(), getIncludeStars());
			showResult("Result: " + resultStr);
		}
	}
	
	@Override
	public void saveModeSpecificVariablesForReplay()
	{
		replay.putInt(Registry.REPLAY_INT_GAME_MODE, GameConstants.GAME_MODE_VECTROPY_ONLINE);
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
