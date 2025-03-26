package online.screen;

import java.awt.BorderLayout;
import java.util.UUID;

import game.GameSettings;
import object.Bid;
import object.EntropyAchievementsTracker;
import object.EntropyBid;
import screen.EntropyBidPanel;
import util.CardsUtil;
import util.Registry;
import util.ReplayConstants;

public class EntropyRoom extends GameRoom
{
	private EntropyAchievementsTracker achievementTracker = new EntropyAchievementsTracker();
	
	public EntropyRoom(UUID id, String roomName, GameSettings settings, int players)
	{
		super(id, roomName, settings, players);
		
		bidPanel = new EntropyBidPanel();
		leftPaneSouth.add(bidPanel, BorderLayout.CENTER);
		bidPanel.addBidListener(this);
	}
	
	@Override
	public void doSpecificResetGameVariables()
	{
		achievementTracker.reset();
	}
	
	@Override
	public void resetBids()
	{
		lastBid = new EntropyBid(0, 0);
		hmBidByPlayerNumber.clear();
	}
	
	@Override
	public void updateScreenForChallengeOrIllegal() 
	{
		if (isVisible())
		{
			int lastBidSuitCode = ((EntropyBid)lastBid).getBidSuitCode();
			handPanel.displayAndHighlightHands(lastBidSuitCode);
			
			achievementTracker.unlockPerfectBidAchievements(earnedPsychic);

			int total = CardsUtil.countSuit(lastBidSuitCode, hmHandByAdjustedPlayerNumber, getJokerValue());
			String suitsStr = CardsUtil.getSuitDesc(total, lastBidSuitCode);
			if (total == 1)
			{
				showResult("There was " + total + " " + suitsStr);
			}
			else
			{
				showResult("There were " + total + " " + suitsStr);
			}
		}
	}
	
	@Override
	public void saveModeSpecificVariablesForReplay()
	{
		int roundsSoFar = replay.getInt(Registry.REPLAY_INT_ROUNDS_SO_FAR, 0);
		replay.putInt(Registry.REPLAY_INT_GAME_MODE, ReplayConstants.GAME_MODE_ENTROPY_ONLINE);
		replay.putInt(roundsSoFar + Registry.REPLAY_INT_LAST_BID_SUIT_CODE, ((EntropyBid)lastBid).getBidSuitCode());
		replayDialog.roundAdded();
	}

	@Override
	public void updatePerfectBidVariables(Bid bid) 
	{
		achievementTracker.updatePerfectBidVariables(bid);
	}
	
	@Override
	public void updateAchievementVariables(Bid bid)
	{
		achievementTracker.update(bid);
	}
	
	@Override
	public void unlockEndOfGameAchievements()
	{
		achievementTracker.unlockEndOfGameAchievements(5);
	}
}
