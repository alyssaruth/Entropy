package util;

import java.util.List;
import java.util.Random;

import object.Bid;
import object.Player;
import screen.ScreenCache;
import screen.SimulationDialog;

public final class GameSimulator 
{
	private SimulationParms parms = null;
	private int personToStart = 0;
	
	private Bid lastBid = null;
	
	private Player opponentZero = new Player(0, "");
	private Player opponentOne = new Player(1, "");
	private Player opponentTwo = new Player(2, "");
	private Player opponentThree = new Player(3, "");
	
	private int[] playOrder = {2,3,1,0}; //standard play order
	
	/**
	 * Simulation methods
	 */
	public void startNewGame(int number)
	{
		Debug.appendBanner("Game " + number, parms.getLogging());

		initVariablesForSimulationNewGame();
		initVariablesForSimulationNewRound();
		setRandomPersonToStart();
		
		Player player = getPlayer(personToStart);
		processOpponentTurn(player);
	}

	private void startNewRound()
	{
		Debug.appendBanner("New Round", parms.getLogging());
		log("opponentZeroNumberOfCards = " + opponentZero.getNumberOfCards());
		log("opponentOneNumberOfCards = " + opponentOne.getNumberOfCards());
		log("opponentTwoNumberOfCards = " + opponentTwo.getNumberOfCards());
		log("opponentThreeNumberOfCards = " + opponentThree.getNumberOfCards());

		knockOutPlayers();
		boolean gameOver = checkForResult();

		if (!gameOver)
		{
			initVariablesForSimulationNewRound();

			Player playerToStart = getPlayer(personToStart);
			processOpponentTurn(playerToStart);
		}
	}

	private void populateHands(List<String> deck)
	{
		log("Dealing hands...");

		GameUtil.populateHand(opponentZero, deck, parms.getLogging());
		GameUtil.populateHand(opponentOne, deck, parms.getLogging());
		GameUtil.populateHand(opponentTwo, deck, parms.getLogging());
		GameUtil.populateHand(opponentThree, deck, parms.getLogging());
	}

	private void setRandomPersonToStart()
	{
		Random random = new Random();
		if (opponentTwo.isEnabled() 
		  && opponentThree.isEnabled())
		{
			personToStart = random.nextInt(4);
		}
		else if (opponentTwo.isEnabled() 
		  && !opponentThree.isEnabled())
		{
			personToStart = random.nextInt(3);
		}
		else if (!opponentTwo.isEnabled() 
		  && opponentThree.isEnabled())
		{
			personToStart = random.nextInt(3);
			if (personToStart == 2)
			{
				personToStart++;
			}
		}
		else
		{
			personToStart = random.nextInt(2);
		}
		
		//If we're forcing player zero to start, then set this
		if (parms.getForceStart())
		{
			personToStart = 0;
		}
	}

	private void knockOutPlayers()
	{
		log("Knocking out players...");
		
		knockOut(opponentZero);
		knockOut(opponentOne);
		knockOut(opponentTwo);
		knockOut(opponentThree);
	}
	
	private void knockOut(Player player)
	{
		if (player.getNumberOfCards() == 0
		  && player.isEnabled())
		{
			log("Opponent " + player + " disabled");
			player.setEnabled(false);
		}
	}

	private boolean checkForResult()
	{
		int opponentZeroNumberOfCards = opponentZero.getNumberOfCards();
		int opponentOneNumberOfCards = opponentOne.getNumberOfCards();
		int opponentTwoNumberOfCards = opponentTwo.getNumberOfCards();
		int opponentThreeNumberOfCards = opponentThree.getNumberOfCards();
		
		if (opponentZeroNumberOfCards == 0 
		  && opponentOneNumberOfCards == 0 
		  && opponentTwoNumberOfCards == 0)
		{
			recordWin(opponentThree);
			return true;
		}
		else if (opponentZeroNumberOfCards == 0 
		  && opponentOneNumberOfCards == 0 
		  && opponentThreeNumberOfCards == 0)
		{
			recordWin(opponentTwo);
			return true;
		}
		else if (opponentZeroNumberOfCards == 0 
		  && opponentThreeNumberOfCards == 0 
		  && opponentTwoNumberOfCards == 0)
		{
			recordWin(opponentOne);
			return true;
		}
		else if (opponentOneNumberOfCards == 0 
		  && opponentTwoNumberOfCards == 0 
		  && opponentThreeNumberOfCards == 0)
		{
			recordWin(opponentZero);
			return true;
		}
		return false;
	}
	
	private void recordWin(Player winner)
	{
		SimulationDialog dialog = ScreenCache.get(SimulationDialog.class);
		if (winner.getNumberOfCards() == parms.getNumberOfCards())
		{
			dialog.recordWin(winner.getPlayerNumber(), true);
		}
		else
		{
			dialog.recordWin(winner.getPlayerNumber(), false);
		}
	}
	
	private void processOpponentTurn(Player opponent)
	{	
		if (!opponent.isEnabled())
		{
			int nextPlayerNumber = playOrder[opponent.getPlayerNumber()];
			processOpponentTurn(getPlayer(nextPlayerNumber));
			return;
		}
		
		Debug.appendBanner("Opponent " + opponent, parms.getLogging());
		if (lastBid != null)
		{
			ScreenCache.get(SimulationDialog.class).recordOpportunityToChallenge(opponent.getPlayerNumber());
		}
		
		StrategyParms stratParms = getStrategyParms(opponent);
		Bid bid = CpuStrategies.processOpponentTurn(stratParms, opponent);
		if (bid == null)
		{
			//Abort the simulation, something's gone wrong
			throw new SimulationException("Simulation error");
		}
		
		bid.setPlayer(opponent);

		if (bid.isChallenge())
		{
			processChallenge(opponent);
		}
		else
		{
			lastBid = bid;
			int nextPlayerNumber = playOrder[opponent.getPlayerNumber()];
			processOpponentTurn(getPlayer(nextPlayerNumber));
		}
	}

	private void processChallenge(Player challenger)
	{
		log("Challenged");

		SimulationDialog dialog = ScreenCache.get(SimulationDialog.class);
		if (!lastBid.isOverbid(opponentZero.getHand(), opponentOne.getHand(), opponentTwo.getHand(), 
				opponentThree.getHand(), parms.getJokerValue()))
		{
			log("not overbid");
			dialog.recordChallenge(challenger.getPlayerNumber(), false);
			
			challenger.setCardsToSubtract(1);
			challenger.doSubtraction();
			personToStart = challenger.getPlayerNumber();
		}
		else
		{
			log("overbid");
			dialog.recordChallenge(challenger.getPlayerNumber(), true);
			
			Player bidder = lastBid.getPlayer();
			bidder.setCardsToSubtract(1);
			bidder.doSubtraction();
			personToStart = bidder.getPlayerNumber();
		}
		
		startNewRound();
	}
	
	private void initVariablesForSimulationNewGame()
	{
		opponentZero.setEnabled(true);
		opponentOne.setEnabled(true);
		boolean opponentTwoEnabled = parms.getOpponentTwoEnabled();
		opponentTwo.setEnabled(opponentTwoEnabled);
		boolean opponentThreeEnabled = parms.getOpponentThreeEnabled();
		opponentThree.setEnabled(opponentThreeEnabled);
		int opponentTwoCoeff = opponentTwoEnabled? 1:0;
		int opponentThreeCoeff = opponentThreeEnabled? 1:0;
		
		int numberOfCards = parms.getNumberOfCards();
		opponentZero.setNumberOfCards(numberOfCards);
		opponentOne.setNumberOfCards(numberOfCards);
		opponentTwo.setNumberOfCards(opponentTwoCoeff * numberOfCards);
		opponentThree.setNumberOfCards(opponentThreeCoeff * numberOfCards);
		
		opponentZero.setStrategy(parms.getOpponentZeroStrategy());
		opponentOne.setStrategy(parms.getOpponentOneStrategy());
		opponentTwo.setStrategy(parms.getOpponentTwoStrategy());
		opponentThree.setStrategy(parms.getOpponentThreeStrategy());

		setPlayOrder(parms.getRandomiseOrder());
	}
	
	private void setPlayOrder(boolean randomiseOrder)
	{
		if (randomiseOrder)
		{
			Random dice = new Random();
			int orderNo = dice.nextInt(6);

			if (orderNo == 0)
			{
				playOrder[0] = 1;
				playOrder[1] = 2;
				playOrder[2] = 3;
				playOrder[3] = 0;
			}
			else if (orderNo == 1)
			{
				playOrder[0] = 1;
				playOrder[1] = 3;
				playOrder[2] = 0;
				playOrder[3] = 2;
			}
			else if (orderNo == 2)
			{
				playOrder[0] = 2;
				playOrder[1] = 0;
				playOrder[2] = 3;
				playOrder[3] = 1;
			}
			else if (orderNo == 3)
			{
				playOrder[0] = 2;
				playOrder[1] = 3;
				playOrder[2] = 1;
				playOrder[3] = 0;
			}
			else if (orderNo == 4)
			{
				playOrder[0] = 3;
				playOrder[1] = 0;
				playOrder[2] = 1;
				playOrder[3] = 2;
			}
			else if (orderNo == 5)
			{
				playOrder[0] = 3;
				playOrder[1] = 2;
				playOrder[2] = 0;
				playOrder[3] = 1;
			}
		}
		else
		{
			playOrder[0] = 2;
			playOrder[1] = 3;
			playOrder[2] = 1;
			playOrder[3] = 0;
		}
	}

	private void initVariablesForSimulationNewRound()
	{
		lastBid = null;
		
		opponentZero.resetHand();
		opponentOne.resetHand();
		opponentTwo.resetHand();
		opponentThree.resetHand();
		
		List<String> deck = CardsUtil.createAndShuffleDeck(parms.getIncludeJokers(), 
		  parms.getJokerQuantity(), parms.getIncludeMoons(), parms.getIncludeStars(), parms.getNegativeJacks());
				
		populateHands(deck);
	}
	
	private void log(String text)
	{
		Debug.append(text, parms.getLogging());
	}
	
	private StrategyParms getStrategyParms(Player opponent)
	{
		StrategyParms stratParms = parms.getStrategyParms();
		stratParms.setLastBid(lastBid);
		stratParms.setOpponentOneCards(opponentOne.getNumberOfCards());
		stratParms.setOpponentTwoCards(opponentTwo.getNumberOfCards());
		stratParms.setOpponentThreeCards(opponentThree.getNumberOfCards());
		stratParms.setPlayerCards(opponentZero.getNumberOfCards());
		
		//Set the cards revealed
		stratParms.appendCardsOnShowFromOpponent(opponent, opponentZero);
		stratParms.appendCardsOnShowFromOpponent(opponent, opponentOne);
		stratParms.appendCardsOnShowFromOpponent(opponent, opponentTwo);
		stratParms.appendCardsOnShowFromOpponent(opponent, opponentThree);
		
		return stratParms;
	}
	
	private Player getPlayer(int playerNumber)
	{
		if (playerNumber == 0)
		{
			return opponentZero;
		}
		else if (playerNumber == 1)
		{
			return opponentOne;
		}
		else if (playerNumber == 2)
		{
			return opponentTwo;
		}
		
		return opponentThree;
	}
	
	public void setSimulationParms(SimulationParms parms)
	{
		this.parms = parms;
	}
}