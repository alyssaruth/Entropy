package util;

import java.util.ArrayList;

import object.FlagImage;

/**
 * Used to store all the information needed to show a row in the replay table. 
 */
public class ReplayRowWrapper 
{
	private static final String DELIM_CHAR = ">";
	
	//Written out
	private String filename = null;
	private String playerWon = null;
	private String gameComplete = null;
	private String gameMode = null;
	private String roomName = null;
	private String rounds = null;
	private String date = null;
	private String name = null;
	private String numberOfPlayers = null;
	private String numberOfCards = null;
	private FlagImage flag = null;
	
	//Other cached things
	private boolean fromCache = false;
	
	public ReplayRowWrapper(String filename, String playerWon, String gameComplete, String gameMode, String roomName, String rounds, 
							String date, String name, String numberOfPlayers, String numberOfCards, FlagImage flag)
	{
		this.filename = filename;
		this.playerWon = playerWon;
		this.gameComplete = gameComplete;
		this.gameMode = gameMode;
		this.roomName = roomName;
		this.rounds = rounds;
		this.date = date;
		this.name = name;
		this.numberOfPlayers = numberOfPlayers;
		this.numberOfCards = numberOfCards;
		this.flag = flag;
	}
	
	public ReplayRowWrapper(String indexStr)
	{
		ArrayList<String> toks = StringUtil.getListFromDelims(indexStr, DELIM_CHAR);
		
		filename = toks.remove(0);
		playerWon = toks.remove(0);
		gameComplete = toks.remove(0);
		gameMode = toks.remove(0);
		roomName = toks.remove(0);
		rounds = toks.remove(0);
		date = toks.remove(0);
		name = toks.remove(0).replaceAll(StringUtil.escapeHtml(DELIM_CHAR), DELIM_CHAR);
		numberOfPlayers = toks.remove(0);
		numberOfCards = toks.remove(0);
		
		String flagsStr = toks.remove(0);
		flag = new FlagImage(flagsStr);
	}
	
	public String toIndexStr()
	{
		String ret = filename + DELIM_CHAR;
		ret += playerWon + DELIM_CHAR;
		ret += gameComplete + DELIM_CHAR;
		ret += gameMode + DELIM_CHAR;
		ret += roomName + DELIM_CHAR;
		ret += rounds + DELIM_CHAR;
		ret += date + DELIM_CHAR;
		ret += name.replaceAll(DELIM_CHAR, StringUtil.escapeHtml(DELIM_CHAR)) + DELIM_CHAR;
		ret += numberOfPlayers + DELIM_CHAR;
		ret += numberOfCards + DELIM_CHAR;
		ret += flag.toIndexString();
		return ret;
	}
	
	public Object[] getAsRow()
	{
		Object[] row = {date, name, gameMode, roomName, rounds, numberOfPlayers, numberOfCards, flag, filename, gameComplete, playerWon};
		return row;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPlayerWon() {
		return playerWon;
	}

	public void setPlayerWon(String playerWon) {
		this.playerWon = playerWon;
	}

	public String getGameComplete() {
		return gameComplete;
	}

	public void setGameComplete(String gameComplete) {
		this.gameComplete = gameComplete;
	}

	public String getGameMode() {
		return gameMode;
	}

	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRounds() {
		return rounds;
	}

	public void setRounds(String rounds) {
		this.rounds = rounds;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(String numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public String getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(String numberOfCards) {
		this.numberOfCards = numberOfCards;
	}

	public FlagImage getFlag() {
		return flag;
	}

	public void setFlag(FlagImage flag) {
		this.flag = flag;
	}
	
	public boolean isFromCache()
	{
		return fromCache;
	}
	public void setFromCache(boolean fromCache)
	{
		this.fromCache = fromCache;
	}
}
