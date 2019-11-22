package util;

import java.text.NumberFormat;
import java.util.Vector;

public class SimulationResults
{
	private double wins = 0;
	private double perfectGames = 0;
	private double goodChallenges = 0;
	private double totalChallenges = 0;
	private double opportunitiesToChallenge = 0;
	
	public void incrementWins()
	{
		wins++;
	}
	
	public void incrementPerfectGames()
	{
		perfectGames++;
	}
	
	public void incrementGoodChallenges()
	{
		goodChallenges++;
	}
	
	public void incrementTotalChallenges()
	{
		totalChallenges++;
	}
	
	public void incrementOpportunitiesToChallenge()
	{
		opportunitiesToChallenge++;
	}
	
	public Vector<String> generateRow(int opponentNumber, String strategy, double totalGames)
	{
		String winRate = formatPercent(wins, totalGames);
		String challengeRate = formatPercent(totalChallenges, opportunitiesToChallenge);
		String challengeSuccessRate = formatPercent(goodChallenges, totalChallenges);
		String perfectRate = formatPercent(perfectGames, wins);
		
		Vector<String> row = new Vector<>();
		row.add("" + opponentNumber);
		row.add(strategy);
		row.add(winRate);
		row.add(challengeRate);
		row.add(challengeSuccessRate);
		row.add(perfectRate);
		
		return row;
	}
	
	private String formatPercent(double numerator, double denominator)
	{
		if (denominator == 0)
		{
			return "";
		}
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(2);
		return percentFormat.format(numerator/denominator);
	}
}
