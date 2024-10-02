package object;

import java.util.ArrayList;
import java.util.HashMap;

import static utils.CoreGlobals.logger;

public class BidHistory 
{
	private HashMap<Integer, ArrayList<Bid>> hmBidVectorByPlayerNumber = new HashMap<>();
	private int lastPlayerToAct = -1;
	private int personToStart = -1;
	
	public BidHistory()
	{
		hmBidVectorByPlayerNumber.put(0, new ArrayList<Bid>());
		hmBidVectorByPlayerNumber.put(1, new ArrayList<Bid>());
		hmBidVectorByPlayerNumber.put(2, new ArrayList<Bid>());
		hmBidVectorByPlayerNumber.put(3, new ArrayList<Bid>());
	}
	
	public boolean addBidForPlayer(int playerNumber, Bid bid)
	{
		ArrayList<Bid> bids = hmBidVectorByPlayerNumber.get(playerNumber);
		
		if (bids.contains(bid))
		{
			logger.info("duplicateBid", "Ignored duplicate bid from player " + playerNumber + ": " + bid);
			return false;
		}
		else
		{
			lastPlayerToAct = playerNumber;
			bids.add(bid);
			return true;
		}
	}
	
	public Bid getNextBidForPlayer(int playerNumber, Bid currentBid)
	{
		ArrayList<Bid> bids = hmBidVectorByPlayerNumber.get(playerNumber);
		int size = bids.size();
		
		if (currentBid == null)
		{
			return size>0? bids.get(0):null;
		}
		
		for (int i=0; i<size; i++)
		{
			Bid bid = bids.get(i);
			if (bid.equals(currentBid))
			{
				if (i == size - 1)
				{
					return bid;
				}
				else
				{
					return bids.get(i+1);
				}
			}
		}
		
		logger.error("bidNotFound", "Could not find bid " + currentBid + " in bidHistory for player " + playerNumber);
		return null;
	}
	
	public Bid getLastBidForPlayer(int playerNumber)
	{
		ArrayList<Bid> bids = hmBidVectorByPlayerNumber.get(playerNumber);
		int size = bids.size();
		if (size > 0)
		{
			return bids.get(size-1);
		}

		return null;
	}
	
	public int getLastPlayerToAct()
	{
		return lastPlayerToAct;
	}
	
	public int getPersonToStart()
	{
		return personToStart;
	}
	public void setPersonToStart(int personToStart)
	{
		this.personToStart = personToStart;
	}
	
	@Override
	public String toString() 
	{
		String s = "";
		
		for (int i=0; i<4; i++)
		{	
			ArrayList<Bid> bidVector = hmBidVectorByPlayerNumber.get(i);
			if (!bidVector.isEmpty())
			{
				if (!s.isEmpty())
				{
					s += ", ";
				}
				
				s += "Player " + i;
				if (personToStart == i)
				{
					s += "*";
				}
				
				s += ": " + bidVector;
			}
		}
		
		return s;
	}
}
