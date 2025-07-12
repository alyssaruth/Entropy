package online.screen;

import java.awt.BorderLayout;
import java.util.UUID;

import game.EntropyBidAction;
import game.GameSettings;
import object.EntropyAchievementsTracker;
import object.EntropyBid;
import screen.EntropyBidPanel;
import util.ClientUtil;
import util.Registry;
import util.ReplayConstants;

import static game.CardsUtilKt.countSuit;
import static game.CardsUtilKt.extractCards;

public class EntropyRoom extends GameRoom<EntropyBidAction>
{
	private EntropyAchievementsTracker achievementTracker = new EntropyAchievementsTracker();
	
	public EntropyRoom(UUID id, String roomName, GameSettings settings, int players)
	{
		super(id, roomName, settings, players);
		
		bidPanel = new EntropyBidPanel(ClientUtil.getUsername(), handPanel);
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
		lastBid = null;
		hmBidByPlayerNumber.clear();
	}
	
	@Override
	public void updateScreenForChallengeOrIllegal() 
	{
		if (isVisible())
		{
			var lastBidSuit = lastBid.getSuit();
			handPanel.displayAndHighlightHands(lastBidSuit);
			
			achievementTracker.unlockPerfectBidAchievements(earnedPsychic);

			int total = countSuit(lastBidSuit, extractCards(hmHandByAdjustedPlayerNumber), getJokerValue());
			String suitsStr = lastBidSuit.getDescription(total);
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
		replay.put(roundsSoFar + Registry.REPLAY_STRING_LAST_BID_SUIT_NAME, lastBid.getSuit().name());
		replayDialog.roundAdded();
	}

	@Override
	public void updatePerfectBidVariables(EntropyBidAction bid)
	{
		achievementTracker.updatePerfectBidVariables(bid);
	}
	
	@Override
	public void updateAchievementVariables(EntropyBidAction bid)
	{
		achievementTracker.update(bid);
	}
	
	@Override
	public void unlockEndOfGameAchievements()
	{
		achievementTracker.unlockEndOfGameAchievements(5);
	}
}
