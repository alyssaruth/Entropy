package util;

import game.GameSettings;
import object.Player;
import screen.HandPanelMk2;

import javax.swing.*;
import java.net.URL;
import java.util.List;

public class GameUtil 
{
	public static int getWinningPlayer(int playerCards, int opponentOneCards, int opponentTwoCards, int opponentThreeCards) 
	{
		if (playerCards + opponentOneCards + opponentTwoCards == 0)
		{
			return 3;
		}
		else if (playerCards + opponentOneCards + opponentThreeCards == 0)
		{
			return 2;
		}
		else if (playerCards + opponentTwoCards + opponentThreeCards == 0)
		{
			return 1;
		}
		else if (opponentOneCards + opponentTwoCards + opponentThreeCards == 0)
		{
			return 0;
		}
		
		return -1;
	}
	
	public static void showResultDialog(int winningPlayer, HandPanelMk2 handPanel)
	{
		switch (winningPlayer)
		{
			case 0:
				DialogUtilNew.showInfo("You won!");
				return;
			case 1:
				DialogUtilNew.showInfo(handPanel.getOpponentOneName() + " won!");
				return;
			case 2:
				DialogUtilNew.showInfo(handPanel.getOpponentTwoName() + " won!");
				return;
			case 3:
				DialogUtilNew.showInfo(handPanel.getOpponentThreeName() + " won!");
				return;
			default:
				Debug.stackTrace("Unexpected winningPlayer [" + winningPlayer + "]");
		}
	}
	
	public static int getMaxBid(GameSettings settings, int totalNumberOfCards)
	{
		int theoreticalMaxBid = 0;

		int maxWithoutJokers = 17; //(12*1) + (1*2) + (3*1)
		if (settings.getNegativeJacks())
		{
			maxWithoutJokers = 16; //When J not present - (11*1) + (1*2) + (3*1)
		}

		var jokerQuantity = settings.getJokerQuantity();
		var jokerValue = settings.getJokerValue();
		if (jokerQuantity > 0)
		{
			int maxJokerQuantity = Math.min(jokerQuantity, totalNumberOfCards);
			if (totalNumberOfCards > jokerQuantity)
			{
				theoreticalMaxBid = totalNumberOfCards + ((jokerValue-1)*jokerQuantity) + 1;
			}
			else
			{
				theoreticalMaxBid = totalNumberOfCards + ((jokerValue-1)*maxJokerQuantity);
			}

			return Math.min(theoreticalMaxBid, maxWithoutJokers + (jokerValue*jokerQuantity));
		}
		else
		{
			theoreticalMaxBid = totalNumberOfCards + 1;

			return Math.min(theoreticalMaxBid, maxWithoutJokers);
		}
	}
	
	public static int getNextPlayer(int currentPlayer)
	{
		switch (currentPlayer)
		{
		case 0:
			return 2;
		case 1:
			return 3;
		case 2:
			return 1;
		default:
			return 0;
		}
	}
	
	public static void populateHand(Player playerToPopulate, List<String> deck, boolean logging)
	{
		String hand = "";
		
		if (playerToPopulate.isEnabled())
		{
			int playerNumberOfCards = playerToPopulate.getNumberOfCards();
			int playerToBeDealt = playerNumberOfCards;
			List<String> playerHand = playerToPopulate.getHand();
			
			while (playerToBeDealt > 0)
			{
				if (!hand.isEmpty())
				{
					hand += ", ";
				}
				
				String card = deck.remove(0);
				playerHand.add(card);
				hand += card;

				playerToBeDealt--;
			}
		}

		Debug.append("Player " + playerToPopulate + ": [" + hand + "]", logging);
	}
	
	public static ImageIcon getImageForCard(String card, String deck, String jokers, String colours)
	{
		if (card.startsWith("Jo"))
		{
			String path = "/joker_" + jokers + "_" + colours + "/" + card + ".png";
			URL resourceUrl = CardsUtil.class.getResource(path);
			return new ImageIcon(resourceUrl);
		}
		
		//Strip off leading - from negative jack
		if (card.startsWith("-"))
		{
			card = card.substring(1, card.length());
		}
		
		String path = "/deck_" + deck + "_" + colours + "/" + card + ".png";
		URL resourceUrl = CardsUtil.class.getResource(path);
		return new ImageIcon(resourceUrl);
	}
	
	/**
	 * We don't need to pass in the joker directory to this method because jokers are never displayed as faded
	 * Ok, now they can be faded (for revealing purposes)
	 */
	public static ImageIcon getFadedImageForCard(String card, String deck)
	{
		return getFadedImageForCard(card, deck, null);
	}
	public static ImageIcon getFadedImageForCard(String card, String deck, String jokers)
	{
		return getImageForCard(card, deck, jokers, "faded");
	}
}
