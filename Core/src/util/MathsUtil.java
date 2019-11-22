package util;

import object.HandyArrayList;

public final class MathsUtil
{
	public static boolean isPrime(int x)
	{
		int maxFactor = (int)Math.floor(Math.sqrt(x));
		for (int i=2; i<=maxFactor; i++)
		{
			if (x % i == 0)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static HandyArrayList<Integer> primeFactorise(int x)
	{
		if (isPrime(x))
		{
			return HandyArrayList.factoryAdd(x);
		}
		
		HandyArrayList<Integer> results = new HandyArrayList<>();
		int p = 2;
		while (x > 1)
		{
			int remainder = x % p;
			if (remainder == 0)
			{
				x = x / p;
				results.add(p);
			}
			else
			{
				p = getNextPrime(p);
			}
		}
		
		return results;
	}
	private static int getNextPrime(int x)
	{
		int ret = x+1;
		while (!isPrime(ret))
		{
			ret++;
		}
		
		return ret;
	}
	
	public static void findPerfectNumbers()
	{
		for (int i=1; i<=1000; i++)
		{
			if (isPerfect(i))
			{
				Debug.append("" + i);
			}
		}
	}
	
	public static boolean isPerfect(int x)
	{
		int factorsTotal = 0;
		for (int i=1; i<x; i++)
		{
			if ((x % i) == 0)
			{
				factorsTotal += i;
			}
		}
		
		return factorsTotal == x;
	}
	
	public static int getFactorCount(int x)
	{
		int count = 0;
		for (int i=1; i<=x; i++)
		{
			if ((x % i) == 0)
			{
				count++;
			}
		}
		
		return count;
	}
	
	public static String convertToBase(String x, int fromBase, int toBase) 
	{
		return Integer.toString(Integer.parseInt(x, fromBase),toBase);
	}
}
