package object;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Simple wrapper for a Room.
 * Used directly by the RoomTable to display a row representing a room.
 * Extended by 'Room', which is the Server's version of a room.
 * Extended by 'GameRoom' (and hence extends JFrame), which is the Client's version of a room.
 */
public class RoomWrapper extends JFrame
{
	protected String roomName = null;
	protected int mode = -1;
	protected int players = -1;
	protected List<String> currentPlayers = new ArrayList<>();
	protected List<String> observers = new ArrayList<>();
	
	protected int jokerQuantity = 0;
	protected int jokerValue = 2;
	protected boolean includeMoons = false;
	protected boolean includeStars = false;
	protected boolean negativeJacks = false;
	protected boolean illegalAllowed = false;
	protected boolean cardReveal = false;
	
	public RoomWrapper(String roomName, int mode, int players)
	{
		this.roomName = roomName;
		this.mode = mode;
		this.players = players;
	}
	
	public String getRoomName()
	{
		return roomName;
	}
	public int getMode()
	{
		return mode;
	}
	public int getPlayers()
	{
		return players;
	}
	
	public ArrayList<String> getCurrentPlayers()
	{
		return new ArrayList<>(currentPlayers);
	}
	public int getCurrentPlayerCount()
	{
		return currentPlayers.size();
	}
	public void setCurrentPlayers(List<String> currentPlayers)
	{
		this.currentPlayers = currentPlayers;
	}
	public ArrayList<String> getObservers()
	{
		return new ArrayList<>(observers);
	}
	public int getObserverCount()
	{
		return observers.size();
	}
	public void setObservers(List<String> observers)
	{
		this.observers = observers;
	}
	
	/**
	 * Returns a HashSet since it's possible for a player to be present as a player AND an observer.
	 * This occurs if they've left but the game is still going - we keep the reference as a player so
	 * others can't take the seat. They obviously then have the option to join as an observer.
	 */
	public HashSet<String> getAllUsersInRoom()
	{
		ArrayList<String> ret = getCurrentPlayers();
		ret.addAll(getObservers());
		
		HashSet<String> hs = new HashSet<>(ret);
		return hs;
	}

	public int getJokerQuantity() 
	{
		return jokerQuantity;
	}
	public void setJokerQuantity(int jokerQuantity) 
	{
		this.jokerQuantity = jokerQuantity;
	}
	public int getJokerValue() 
	{
		return jokerValue;
	}
	public void setJokerValue(int jokerValue) 
	{
		this.jokerValue = jokerValue;
	}
	public boolean getIncludeMoons() 
	{
		return includeMoons;
	}
	public void setIncludeMoons(boolean includeMoons) 
	{
		this.includeMoons = includeMoons;
	}
	public boolean getIncludeStars() 
	{
		return includeStars;
	}
	public void setIncludeStars(boolean includeStars) 
	{
		this.includeStars = includeStars;
	}
	public boolean getNegativeJacks()
	{
		return negativeJacks;
	}
	public void setNegativeJacks(boolean negativeJacks)
	{
		this.negativeJacks = negativeJacks;
	}
	public boolean getIllegalAllowed() 
	{
		return illegalAllowed;
	}
	public void setIllegalAllowed(boolean illegalAllowed) 
	{
		this.illegalAllowed = illegalAllowed;
	}
	public boolean getCardReveal()
	{
		return cardReveal;
	}
	public void setCardReveal(boolean cardReveal)
	{
		this.cardReveal = cardReveal;
	}

	@Override
	public String toString()
	{
		return roomName;
	}
}
