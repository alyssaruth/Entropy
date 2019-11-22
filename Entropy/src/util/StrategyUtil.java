package util;

import java.util.Random;

public class StrategyUtil 
{
	public static int getRandomSuit(boolean includeMoons, boolean includeStars)
	{
		Random rand = new Random();
		
		if (includeStars && includeMoons)
		{
			return rand.nextInt(6);
		}
		else if (includeMoons)
		{
			return rand.nextInt(5);
		}
		else if (includeStars)
		{
			int suit = rand.nextInt(5);
			if (suit >= 3)
			{
				suit++;
			}
			
			return suit;
		}
		else
		{
			int suit = rand.nextInt(4);
			if (suit == 3)
			{
				suit++;
			}
			
			return suit;
		}
	}
}
