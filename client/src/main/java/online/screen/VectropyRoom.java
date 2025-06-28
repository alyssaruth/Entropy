package online.screen;

import java.awt.BorderLayout;
import java.util.UUID;

import game.GameSettings;
import object.Bid;
import screen.VectropyBidPanel;
import util.*;

import static game.CardsUtilKt.extractCards;

public class VectropyRoom extends GameRoom
{
	private boolean earnedMathematician = false;
	
	public VectropyRoom(UUID id, String roomName, GameSettings settings, int players)
	{
		super(id, roomName, settings, players);
		
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
			handPanel.displayAndHighlightHands(null);
			AchievementsUtil.unlockVectropyPerfectBidAchievements(earnedMathematician, earnedPsychic);

			String resultStr = VectropyUtil.getResult(extractCards(hmHandByAdjustedPlayerNumber), getJokerValue(), null, getIncludeMoons(), getIncludeStars());
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
