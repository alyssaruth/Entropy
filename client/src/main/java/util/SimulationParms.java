package util;

import game.GameMode;

/**
 * All the parameters that can be specified for running an Entropy/Vectropy simulation
 */
public class SimulationParms
{
	//Mode
	private GameMode gameMode = null;
	
	//Opponents
	private boolean opponentTwoEnabled = false;
	private boolean opponentThreeEnabled = false;
	private String opponentZeroStrategy = null;
	private String opponentOneStrategy = null;
	private String opponentTwoStrategy = null;
	private String opponentThreeStrategy = null;
	
	//Gameplay
	private int numberOfCards = -1;
	private int jokerValue = -1;
	private int jokerQuantity = -1;
	private boolean includeMoons = false;
	private boolean includeStars = false;
	private boolean negativeJacks = false;
	private boolean cardReveal = false;
	
	//Advanced
	private boolean logging = false;
	private boolean randomiseOrder = false;
	private boolean forceStart = false;
	
	/**
	 * Helpers
	 */
	public StrategyParms getStrategyParms()
	{
		StrategyParms parms = new StrategyParms();
		parms.setGameMode(gameMode);
		parms.setIncludeMoons(includeMoons);
		parms.setIncludeStars(includeStars);
		parms.setNegativeJacks(negativeJacks);
		parms.setCardReveal(cardReveal);
		parms.setJokerQuantity(jokerQuantity);
		parms.setJokerValue(jokerValue);
		
		parms.setLogging(logging);
		
		return parms;
	}
	
	/**
	 * Gets / sets
	 */
	public GameMode getGameMode()
	{
		return gameMode;
	}
	public void setGameMode(GameMode gameMode)
	{
		this.gameMode = gameMode;
	}
	public boolean getOpponentTwoEnabled()
	{
		return opponentTwoEnabled;
	}
	public void setOpponentTwoEnabled(boolean opponentTwoEnabled)
	{
		this.opponentTwoEnabled = opponentTwoEnabled;
	}
	public boolean getOpponentThreeEnabled()
	{
		return opponentThreeEnabled;
	}
	public void setOpponentThreeEnabled(boolean opponentThreeEnabled)
	{
		this.opponentThreeEnabled = opponentThreeEnabled;
	}
	public String getOpponentZeroStrategy()
	{
		return opponentZeroStrategy;
	}
	public void setOpponentZeroStrategy(String opponentZeroStrategy)
	{
		this.opponentZeroStrategy = opponentZeroStrategy;
	}
	public String getOpponentOneStrategy()
	{
		return opponentOneStrategy;
	}
	public void setOpponentOneStrategy(String opponentOneStrategy)
	{
		this.opponentOneStrategy = opponentOneStrategy;
	}
	public String getOpponentTwoStrategy()
	{
		return opponentTwoStrategy;
	}
	public void setOpponentTwoStrategy(String opponentTwoStrategy)
	{
		this.opponentTwoStrategy = opponentTwoStrategy;
	}
	public String getOpponentThreeStrategy()
	{
		return opponentThreeStrategy;
	}
	public void setOpponentThreeStrategy(String opponentThreeStrategy)
	{
		this.opponentThreeStrategy = opponentThreeStrategy;
	}
	public int getNumberOfCards()
	{
		return numberOfCards;
	}
	public void setNumberOfCards(int numberOfCards)
	{
		this.numberOfCards = numberOfCards;
	}
	public int getJokerValue()
	{
		return jokerValue;
	}
	public void setJokerValue(int jokerValue)
	{
		this.jokerValue = jokerValue;
	}
	public int getJokerQuantity()
	{
		return jokerQuantity;
	}
	public void setJokerQuantity(int jokerQuantity)
	{
		this.jokerQuantity = jokerQuantity;
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
	public boolean getCardReveal()
	{
		return cardReveal;
	}
	public void setCardReveal(boolean cardReveal)
	{
		this.cardReveal = cardReveal;
	}
	public boolean getLogging()
	{
		return logging;
	}
	public void setLogging(boolean logging)
	{
		this.logging = logging;
	}
	public boolean getRandomiseOrder()
	{
		return randomiseOrder;
	}
	public void setRandomiseOrder(boolean randomiseOrder)
	{
		this.randomiseOrder = randomiseOrder;
	}
	public boolean getForceStart()
	{
		return forceStart;
	}
	public void setForceStart(boolean forceStart)
	{
		this.forceStart = forceStart;
	}
}
