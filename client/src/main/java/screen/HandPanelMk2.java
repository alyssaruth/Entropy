package screen;

import object.CardLabel;
import object.PlayerLabel;
import online.screen.EntropyLobby;
import util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

public class HandPanelMk2 extends TransparentPanel 
						  implements ActionListener,
						  			 MouseListener,
						  			 Registry
{
	private static ImageIcon iconEmptySeat = new ImageIcon(HandPanelMk2.class.getResource("/buttons/emptySeat.png"));
	private static ImageIcon iconEmptySeatDark = new ImageIcon(HandPanelMk2.class.getResource("/buttons/emptySeatDark.png"));
	private static ImageIcon iconEmptySeatDisabled = new ImageIcon(HandPanelMk2.class.getResource("/buttons/emptySeatDisabled.png"));
	
	private String playerName = "Player";
	private String opponentOneName = "Mark";
	private String opponentTwoName = "Dave";
	private String opponentThreeName = "Tom";
	private boolean hasViewedHandThisGame = false;
	private boolean initted = false;
	private boolean observer = false;
	private boolean revealListenerActive = false;
	
	//Need these for standing up/sitting down
	private UUID roomId = null;
	private String roomName = "";
	private String username = "";
	private int players = 2;
	private int playerNumber = 0;
	
	private Timer distractedTimer = null;
	
	private RevealListener listener = null;
	
	public HandPanelMk2(RevealListener listener)
	{
		try
		{
			this.listener = listener;
			
			setBorder(new EmptyBorder(5, 20, 5, 20));
			setLayout(new BorderLayout(0, -80));
			
			add(playerPanel, BorderLayout.SOUTH);
			playerPanel.setLayout(new BorderLayout(0, 0));
			playerPanel.add(panelPlayerCards);
			panelPlayerCards.setLayout(new FlowLayout(FlowLayout.CENTER, -92, 5));
			playerCard1.setPreferredSize(new Dimension(72, 106));
			lblPlayerSeat.setIcon(iconEmptySeat);
			panelPlayerCards.add(lblPlayerSeat);
			panelPlayerCards.add(playerCard5);
			playerCard2.setPreferredSize(new Dimension(72, 106));
			panelPlayerCards.add(playerCard4);
			playerCard3.setPreferredSize(new Dimension(72, 106));
			panelPlayerCards.add(playerCard3);
			playerCard4.setPreferredSize(new Dimension(72, 106));
			panelPlayerCards.add(playerCard2);
			playerCard5.setPreferredSize(new Dimension(72, 106));
			panelPlayerCards.add(playerCard1);
			lblPlayer.setPreferredSize(new Dimension(30, 25));
			playerPanel.add(lblPlayer, BorderLayout.NORTH);
			lblPlayer.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblPlayer.setHorizontalAlignment(SwingConstants.CENTER);
			playerPanel.add(blindPanel, BorderLayout.SOUTH);
			btnViewCards.setPreferredSize(new Dimension(23, 23));
			blindPanel.add(btnViewCards);
			blindPanel.setPreferredSize(new Dimension(25, 30));
			btnViewCards.setIcon(new ImageIcon(EntropyScreen.class.getResource("/buttons/viewCards.png")));
			opponentOnePanel.setPreferredSize(new Dimension(10, 131));
			add(opponentOnePanel, BorderLayout.NORTH);
			opponentOnePanel.setLayout(new BorderLayout(0, 0));
			lblOpponentOne.setPreferredSize(new Dimension(23, 25));
			opponentOnePanel.add(lblOpponentOne, BorderLayout.NORTH);
			lblOpponentOne.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblOpponentOne.setHorizontalAlignment(SwingConstants.CENTER);
			panelOpponentCards.setPreferredSize(new Dimension(10, 106));
			opponentOnePanel.add(panelOpponentCards, BorderLayout.SOUTH);
			panelOpponentCards.setLayout(new FlowLayout(FlowLayout.CENTER, -92, 5));
			opponentCard5.setPreferredSize(new Dimension(72, 96));
			opponentCard4.setPreferredSize(new Dimension(72, 96));
			opponentCard3.setPreferredSize(new Dimension(72, 96));
			opponentCard2.setPreferredSize(new Dimension(72, 96));
			opponentCard1.setPreferredSize(new Dimension(72, 96));
			lblOpponentOneSeat.setIcon(iconEmptySeat);
			lblOpponentOneSeat.setPreferredSize(new Dimension(104, 104));
			panelOpponentCards.add(lblOpponentOneSeat);
			panelOpponentCards.add(opponentCard5);
			panelOpponentCards.add(opponentCard4);
			panelOpponentCards.add(opponentCard3);
			panelOpponentCards.add(opponentCard2);
			panelOpponentCards.add(opponentCard1);
			opponentThreePanel.setPreferredSize(new Dimension(150, 10));
			opponentThreePanel.setBorder(new EmptyBorder(0, 10, 0, 20));
			
			add(opponentThreePanel, BorderLayout.EAST);
			opponentThreePanel.setLayout(new BorderLayout(0, 0));
			lblOpponentThree.setPreferredSize(new Dimension(60, 25));
			opponentThreePanel.add(lblOpponentThree, BorderLayout.NORTH);
			lblOpponentThree.setHorizontalAlignment(SwingConstants.CENTER);
			lblOpponentThree.setFont(new Font("Tahoma", Font.PLAIN, 15));
			panelOpponentThreeCards.setPreferredSize(new Dimension(76, 150));
			opponentThreePanel.add(panelOpponentThreeCards, BorderLayout.CENTER);
			opponentThreeCard4.setBounds(24, 60, 76, 114);
			opponentThreeCard4.setPreferredSize(new Dimension(72, 96));
			opponentThreeCard3.setBounds(24, 40, 76, 114);
			opponentThreeCard3.setPreferredSize(new Dimension(72, 96));
			opponentThreeCard2.setBounds(24, 20, 76, 114);
			opponentThreeCard2.setPreferredSize(new Dimension(72, 96));
			panelOpponentThreeCards.setLayout(null);
			opponentThreeCard5.setBounds(24, 80, 76, 114);
			opponentThreeCard5.setPreferredSize(new Dimension(72, 96));
			lblOpponentThreeSeat.setIcon(iconEmptySeat);
			lblOpponentThreeSeat.setHorizontalAlignment(SwingConstants.CENTER);
			lblOpponentThreeSeat.setSize(104, 104);
			lblOpponentThreeSeat.setLocation(8, 65);
			panelOpponentThreeCards.add(lblOpponentThreeSeat);
			panelOpponentThreeCards.add(opponentThreeCard5);
			panelOpponentThreeCards.add(opponentThreeCard4);
			panelOpponentThreeCards.add(opponentThreeCard3);
			panelOpponentThreeCards.add(opponentThreeCard2);
			opponentThreeCard1.setBounds(24, 0, 76, 114);
			opponentThreeCard1.setPreferredSize(new Dimension(72, 96));
			panelOpponentThreeCards.add(opponentThreeCard1);
			opponentTwoPanel.setPreferredSize(new Dimension(150, 10));
			opponentTwoPanel.setBorder(new EmptyBorder(0, 20, 0, 10));
			
			add(opponentTwoPanel, BorderLayout.WEST);
			opponentTwoPanel.setLayout(new BorderLayout(0, 0));
			lblOpponentTwo.setPreferredSize(new Dimension(46, 25));
			opponentTwoPanel.add(lblOpponentTwo, BorderLayout.NORTH);
			lblOpponentTwo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblOpponentTwo.setHorizontalAlignment(SwingConstants.CENTER);
			opponentTwoPanel.add(panelOpponentTwoCards, BorderLayout.CENTER);
			opponentTwoCard1.setBounds(24, 0, 76, 114);
			opponentTwoCard1.setPreferredSize(new Dimension(72, 96));
			panelOpponentTwoCards.setLayout(null);
			opponentTwoCard5.setBounds(24, 80, 76, 114);
			opponentTwoCard5.setPreferredSize(new Dimension(72, 96));
			lblOpponentTwoSeat.setIcon(iconEmptySeat);
			lblOpponentTwoSeat.setHorizontalAlignment(SwingConstants.CENTER);
			lblOpponentTwoSeat.setBounds(8, 65, 104, 104);
			panelOpponentTwoCards.add(lblOpponentTwoSeat);
			panelOpponentTwoCards.add(opponentTwoCard5);
			opponentTwoCard4.setBounds(24, 60, 76, 114);
			opponentTwoCard4.setPreferredSize(new Dimension(72, 96));
			panelOpponentTwoCards.add(opponentTwoCard4);
			opponentTwoCard3.setBounds(24, 40, 76, 114);
			opponentTwoCard3.setPreferredSize(new Dimension(72, 96));
			panelOpponentTwoCards.add(opponentTwoCard3);
			opponentTwoCard2.setBounds(24, 20, 76, 114);
			opponentTwoCard2.setPreferredSize(new Dimension(72, 96));
			panelOpponentTwoCards.add(opponentTwoCard2);
			panelOpponentTwoCards.add(opponentTwoCard1);
			
			add(scrollPane, BorderLayout.CENTER);
			scrollPane.setVisible(false);
			lblOpponentTwo.setVisible(false);
			lblOpponentThree.setVisible(false);
			lblOpponentOne.setVisible(false);
			btnViewCards.setVisible(false);
			lblPlayerSeat.setVisible(false);
			lblOpponentOneSeat.setVisible(false);
			lblOpponentTwoSeat.setVisible(false);
			lblOpponentThreeSeat.setVisible(false);
			
			btnViewCards.addActionListener(this);
			lblPlayerSeat.addMouseListener(this);
			lblOpponentOneSeat.addMouseListener(this);
			lblOpponentTwoSeat.addMouseListener(this);
			lblOpponentThreeSeat.addMouseListener(this);
			lblPlayer.setVisible(false);
			
			playerCard1.addMouseListener(this);
			playerCard2.addMouseListener(this);
			playerCard3.addMouseListener(this);
			playerCard4.addMouseListener(this);
			playerCard5.addMouseListener(this);
			
			distractedTimer = new Timer("Timer-Distracted");

			lblPlayer.setName("PlayerOneLabel");
			lblOpponentOne.setName("PlayerTwoLabel");
			lblOpponentTwo.setName("PlayerThreeLabel");
			lblOpponentThree.setName("PlayerFourLabel");
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private final TransparentPanel opponentOnePanel = new TransparentPanel();
	private final PlayerLabel lblOpponentOne = new PlayerLabel("Mark");
	private final JPanel panelOpponentCards = new JPanel();
	private final CardLabel opponentCard5 = new CardLabel();
	private final CardLabel opponentCard4 = new CardLabel();
	private final CardLabel opponentCard3 = new CardLabel();
	private final CardLabel opponentCard2 = new CardLabel();
	private final CardLabel opponentCard1 = new CardLabel();
	private final TransparentPanel opponentThreePanel = new TransparentPanel();
	private final PlayerLabel lblOpponentThree = new PlayerLabel("New label");
	private final JPanel panelOpponentThreeCards = new JPanel();
	private final CardLabel opponentThreeCard1 = new CardLabel();
	private final CardLabel opponentThreeCard2 = new CardLabel();
	private final CardLabel opponentThreeCard3 = new CardLabel();
	private final CardLabel opponentThreeCard4 = new CardLabel();
	private final CardLabel opponentThreeCard5 = new CardLabel();
	private final TransparentPanel opponentTwoPanel = new TransparentPanel();
	private final PlayerLabel lblOpponentTwo = new PlayerLabel("New label");
	private final JPanel panelOpponentTwoCards = new JPanel();
	private final CardLabel opponentTwoCard1 = new CardLabel();
	private final CardLabel opponentTwoCard2 = new CardLabel();
	private final CardLabel opponentTwoCard3 = new CardLabel();
	private final CardLabel opponentTwoCard4 = new CardLabel();
	private final CardLabel opponentTwoCard5 = new CardLabel();
	private final TransparentPanel playerPanel = new TransparentPanel();
	private final PlayerLabel lblPlayer = new PlayerLabel("Player");
	private final JPanel panelPlayerCards = new JPanel();
	private final CardLabel playerCard5 = new CardLabel();
	private final CardLabel playerCard4 = new CardLabel();
	private final CardLabel playerCard3 = new CardLabel();
	private final CardLabel playerCard2 = new CardLabel();
	private final CardLabel playerCard1 = new CardLabel();
	private final JPanel blindPanel = new JPanel();
	private final CardLabel[] opponentThreeCards = {opponentThreeCard1, opponentThreeCard2, opponentThreeCard3, opponentThreeCard4, opponentThreeCard5};
	private final CardLabel[] opponentTwoCards = {opponentTwoCard1, opponentTwoCard2, opponentTwoCard3, opponentTwoCard4, opponentTwoCard5};
	private final CardLabel[] opponentOneCards = {opponentCard1, opponentCard2, opponentCard3, opponentCard4, opponentCard5};
	private final CardLabel[] playerCards = {playerCard5, playerCard4, playerCard3, playerCard2, playerCard1};
	private final JButton btnViewCards = new JButton("");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JLabel lblPlayerSeat = new JLabel("");
	private final JLabel lblOpponentOneSeat = new JLabel("");
	private final JLabel lblOpponentTwoSeat = new JLabel("");
	private final JLabel lblOpponentThreeSeat = new JLabel("");
	
	public void displayHandsInGame(List<String> playerHand, List<String> opponentOneHand,
								   List<String> opponentTwoHand, List<String> opponentThreeHand)
	{
		initialiseCardLabels(playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand);
	}
	
	public void displayHandsOnline(ConcurrentHashMap<Integer, List<String>> hmHandByPlayerNumber)
	{
		List<String> playerHand = hmHandByPlayerNumber.get(0);
		List<String> opponentOneHand = hmHandByPlayerNumber.get(1);
		List<String> opponentTwoHand = hmHandByPlayerNumber.get(2);
		List<String> opponentThreeHand = hmHandByPlayerNumber.get(3);
		
		initialiseCardLabels(playerHand, opponentOneHand, opponentTwoHand, opponentThreeHand);
	}
	
	private void initialiseCardLabels(List<String> playerHand, List<String> opponentOneHand, List<String> opponentTwoHand,
	  List<String> opponentThreeHand)
	{
		resetPlayerCardPositions();
		
		boolean playerFaceUp = !observer && !btnViewCards.isVisible();
		initialiseCardLabelsForPlayer(playerHand, playerCards, playerFaceUp);
		initialiseCardLabelsForPlayer(opponentOneHand, opponentOneCards, false);
		initialiseCardLabelsForPlayer(opponentTwoHand, opponentTwoCards, false);
		initialiseCardLabelsForPlayer(opponentThreeHand, opponentThreeCards, false);
	}
	
	private void resetPlayerCardPositions()
	{
		for (int i=0; i<playerCards.length; i++)
		{
			//Reset the position of each card
			playerCards[i].setBorder(new EmptyBorder(10, 0, 0, 0));
		}
	}
	
	private void initialiseCardLabelsForPlayer(List<String> hand, CardLabel[] labels, boolean faceUp)
	{
		int size = 0;
		if (hand != null)
		{
			size = hand.size();
		}
		
		for (int i=0; i<labels.length; i++)
		{
			labels[i].setFaceUp(faceUp);
			labels[i].setFaded(false);
			
			if (i<size)
			{
				String card = hand.get(i);
				labels[i].setCard(card);
				labels[i].setVisible(true);
			}
			else
			{
				labels[i].setCard("");
				labels[i].setVisible(false);
			}
		}
		
		refreshIcons(labels);
	}

	private void makePlaceholderHand(CardLabel[] labels, int handSize) {
		for (int i=0; i<handSize; i++) {
			labels[i].setFaceUp(false);
			labels[i].setFaded(false);
			labels[i].setCard(null);
			labels[i].setVisible(true);
		}

		refreshIcons(labels);
	}
	
	private void refreshIcons()
	{
		refreshIcons(playerCards);
		refreshIcons(opponentOneCards);
		refreshIcons(opponentTwoCards);
		refreshIcons(opponentThreeCards);
	}
	
	private void refreshIcons(CardLabel[] cards)
	{
		for (int i=0; i<cards.length; i++)
		{
			cards[i].refreshIcon();
		}
	}
	
	public void revealCard(String card)
	{
		revealIfPresent(card, playerCards);
		revealIfPresent(card, opponentOneCards);
		revealIfPresent(card, opponentTwoCards);
		revealIfPresent(card, opponentThreeCards);
		
		refreshIcons();
	}
	
	private void revealIfPresent(String card, CardLabel[] handLabels)
	{
		int size = handLabels.length;
		for (int i=0; i<size; i++)
		{
			CardLabel cardLabel = handLabels[i];
			String handCard = cardLabel.getCard();
			if (handCard.equals(card))
			{
				cardLabel.setFaceUp(true);
				cardLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
			}
		}
	}
	
	public void displayAndHighlightHands(int suitCode)
	{
		displayAndHighlightHand(suitCode, playerCards);
		displayAndHighlightHand(suitCode, opponentOneCards);
		displayAndHighlightHand(suitCode, opponentTwoCards);
		displayAndHighlightHand(suitCode, opponentThreeCards);
		
		refreshIcons();
	}
	
	private void displayAndHighlightHand(int suitCode, CardLabel[] cards)
	{
		for (int i=0; i<cards.length; i++)
		{
			CardLabel cardLabel = cards[i];
			String cardId = cardLabel.getCard();
			boolean isRelevant = CardsUtil.isRelevant(cardId, suitCode);
			
			cardLabel.setFaded(!isRelevant);
			cardLabel.setFaceUp(true);
		}
	}
	
	public void initialisePlayer(int playerNumber, String name, String colour, int startingNumberOfCards)
	{
		PlayerLabel label = getPlayerLabelForPlayerNumber(playerNumber);
		label.setText(name);
		label.setColour(colour);
		label.setVisible(true);
		
		switch (playerNumber)
		{
		case 0:
			playerName = name;
			makePlaceholderHand(playerCards, startingNumberOfCards);
			break;
		case 1:
			opponentOneName = name;
			makePlaceholderHand(opponentOneCards, startingNumberOfCards);
			break;
		case 2:
			opponentTwoName = name;
			makePlaceholderHand(opponentTwoCards, startingNumberOfCards);
			break;
		case 3:
			opponentThreeName = name;
			makePlaceholderHand(opponentThreeCards, startingNumberOfCards);
			break;
		default:
			Debug.stackTrace("Unexpected playerNumber [" + playerNumber + "]");
		}
	}
	
	public void saveLabels(Preferences replay)
	{
		replay.put(REPLAY_STRING_PLAYER_NAME, playerName);
		replay.put(REPLAY_STRING_OPPONENT_ONE_NAME, opponentOneName);
		replay.put(REPLAY_STRING_OPPONENT_TWO_NAME, opponentTwoName);
		replay.put(REPLAY_STRING_OPPONENT_THREE_NAME, opponentThreeName);
		
		saveColour(lblPlayer, REPLAY_STRING_PLAYER_COLOUR, replay);
		saveColour(lblOpponentOne, REPLAY_STRING_OPPONENT_ONE_COLOUR, replay);
		saveColour(lblOpponentTwo, REPLAY_STRING_OPPONENT_TWO_COLOUR, replay);
		saveColour(lblOpponentThree, REPLAY_STRING_OPPONENT_THREE_COLOUR, replay);
	}
	
	private void saveColour(PlayerLabel label, String node, Preferences replay)
	{
		String colour = label.getColour();
		if (!colour.equals("gray"))
		{
			replay.put(node, colour);
		}
	}
	
	public void clear()
	{
		removePlayer(0);
		removePlayer(1);
		removePlayer(2);
		removePlayer(3);
	}
	
	public void activateEmptySeats()
	{
		ImageIcon icon = iconEmptySeatDisabled;
		if (observer)
		{
			icon = iconEmptySeat;
		}
		
		lblPlayerSeat.setVisible(false);
		lblOpponentOneSeat.setVisible(false);
		lblOpponentTwoSeat.setVisible(false);
		lblOpponentThreeSeat.setVisible(false);
		
		if (players == 2)
		{
			activateEmptySeat(lblPlayer, lblPlayerSeat, icon);
			activateEmptySeat(lblOpponentOne, lblOpponentOneSeat, icon);
		}
		else if (players == 3)
		{
			//Ugh, how do I get myself into these messes
			if (playerNumber == 0 || playerNumber == -1)
			{
				activateEmptySeat(lblPlayer, lblPlayerSeat, icon);
				activateEmptySeat(lblOpponentOne, lblOpponentOneSeat, icon);
				activateEmptySeat(lblOpponentTwo, lblOpponentTwoSeat, icon);
			}
			else if (playerNumber == 1)
			{
				activateEmptySeat(lblPlayer, lblPlayerSeat, icon);
				activateEmptySeat(lblOpponentOne, lblOpponentOneSeat, icon);
				activateEmptySeat(lblOpponentThree, lblOpponentThreeSeat, icon);
			}
			else if (playerNumber == 2)
			{
				activateEmptySeat(lblPlayer, lblPlayerSeat, icon);
				activateEmptySeat(lblOpponentTwo, lblOpponentTwoSeat, icon);
				activateEmptySeat(lblOpponentThree, lblOpponentThreeSeat, icon);
			}
		}
		else if (players == 4)
		{
			activateEmptySeat(lblPlayer, lblPlayerSeat, icon);
			activateEmptySeat(lblOpponentOne, lblOpponentOneSeat, icon);
			activateEmptySeat(lblOpponentTwo, lblOpponentTwoSeat, icon);
			activateEmptySeat(lblOpponentThree, lblOpponentThreeSeat, icon);
		}
	}
	
	private void activateEmptySeat(JLabel lblPlayer, JLabel lblSeat, ImageIcon icon)
	{
		lblSeat.setVisible(!lblPlayer.isVisible());
		lblSeat.setIcon(icon);
	}
	
	public void removeFormerPlayers()
	{
		String colourZero = lblPlayer.getColour();
		if (colourZero.equals("gray"))
		{
			removePlayer(0);
		}
		
		String colourOne = lblOpponentOne.getColour();
		if (colourOne.equals("gray"))
		{
			removePlayer(1);
		}
		
		String colourTwo = lblOpponentTwo.getColour();
		if (colourTwo.equals("gray"))
		{
			removePlayer(2);
		}
		
		String colourThree = lblOpponentThree.getColour();
		if (colourThree.equals("gray"))
		{
			removePlayer(3);
		}
		
		activateEmptySeats();
	}
	
	public void removePlayer(int adjustedNo)
	{
		selectPlayerInAwtThread(adjustedNo, false);
		
		switch (adjustedNo)
		{
		case 0:
			hideCards(playerCards);
			lblPlayer.setVisible(false);
			break;
		case 1:
			hideCards(opponentOneCards);
			lblOpponentOne.setVisible(false);
			break;
		case 2:
			hideCards(opponentTwoCards);
			lblOpponentTwo.setVisible(false);
			break;
		case 3:
			hideCards(opponentThreeCards);
			lblOpponentThree.setVisible(false);
			break;
		default:
			Debug.stackTrace("Unexpected adjustedNo [" + adjustedNo + "]");
		}
	}
	
	private void hideCards(CardLabel[] cards)
	{
		for (int i=0; i<cards.length; i++)
		{
			cards[i].setVisible(false);
		}
	}
	
	public boolean playerIsSelected(int playerNumber)
	{
		PlayerLabel label = getPlayerLabelForPlayerNumber(playerNumber);
		Font font = label.getFont();
		return font.isBold();
	}
	
	public void playerLeft(int playerNumber)
	{
		selectPlayerInAwtThread(playerNumber, false);
		
		PlayerLabel label = getPlayerLabelForPlayerNumber(playerNumber);
		label.setColour("gray");
	}
	
	public void setViewCardsVisibility(boolean visible)
	{
		btnViewCards.setVisible(visible && !observer);
	}
	
	public boolean isPlayingBlind()
	{
		return btnViewCards.isVisible();
	}
	
	public void hideLabelForPlayer(int opponent)
	{
		PlayerLabel label = getPlayerLabelForPlayerNumber(opponent);
		label.setVisible(false);
	}
	
	public void displayLabels(boolean player, boolean opponentOne, boolean opponentTwo, boolean opponentThree)
	{
		lblPlayer.setColour("red");
		lblPlayer.setText(playerName);
		lblPlayer.setVisible(player);
		lblOpponentOne.setColour("blue");
		lblOpponentOne.setText(opponentOneName);
		lblOpponentOne.setVisible(opponentOne);
		lblOpponentTwo.setColour("green");
		lblOpponentTwo.setText(opponentTwoName);
		lblOpponentTwo.setVisible(opponentTwo);
		lblOpponentThree.setColour("purple");
		lblOpponentThree.setText(opponentThreeName);
		lblOpponentThree.setVisible(opponentThree);
	}
	
	public void selectPlayerInAwtThread(final int playerNumber, final boolean selected)
	{
		Runnable fontChangeRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				selectPlayer(playerNumber, selected);
			}
		};
		
		if (SwingUtilities.isEventDispatchThread())
		{
			//We're in the EDT, so fine to just run the function
			fontChangeRunnable.run();
			return;
		}
		
		try
		{
			SwingUtilities.invokeAndWait(fontChangeRunnable);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void selectPlayer(int playerNumber, boolean selected)
	{
		selectSpecificPlayer(playerNumber, selected);
		
		if (selected)
		{
			deselectOtherPlayers(playerNumber);
		}
	}
	
	private void selectSpecificPlayer(int playerNumber, boolean selected)
	{
		Font font = new Font("Tahoma", Font.PLAIN, 15);
		if (selected)
		{
			font = new Font("Tahoma", Font.BOLD, 18);
		}
		
		PlayerLabel label = getPlayerLabelForPlayerNumber(playerNumber);
		label.setFont(font);
		
		if (playerNumber == 0)
		{
			if (selected)
			{
				distractedTimer.schedule(new WaitedTooLong(), 1000*60*3); //3 mins
			}
			else
			{
				cancelTimer();
			}
		}
		else if (selected)
		{
			cancelTimer();
		}
	}
	
	private void deselectOtherPlayers(int playerNumber)
	{
		for (int i=0; i<4; i++)
		{
			if (i != playerNumber)
			{
				selectSpecificPlayer(i, false);
			}
		}
	}
	
	public void resetPlayers(int startingNumberOfCards)
	{
		resetPlayerCardPositions();
		
		resetPlayer(0, playerName, startingNumberOfCards);
		resetPlayer(1, opponentOneName, startingNumberOfCards);
		resetPlayer(2, opponentTwoName, startingNumberOfCards);
		resetPlayer(3, opponentThreeName, startingNumberOfCards);
	}
	
	private void resetPlayer(int playerNumber, String name, int startingNumberOfCards)
	{
		PlayerLabel label = getPlayerLabelForPlayerNumber(playerNumber);
		label.setText(name);
		if (label.isVisible())
		{
			initialisePlayer(playerNumber, name, label.getColour(), startingNumberOfCards);
		}
	}
	
	public void assignAsteriskToStartingPlayer(int personToStart)
	{
		lblPlayer.setText(playerName);
		lblOpponentOne.setText(opponentOneName);
		lblOpponentTwo.setText(opponentTwoName);
		lblOpponentThree.setText(opponentThreeName);
		
		PlayerLabel label = getPlayerLabelForPlayerNumber(personToStart);
		String text = label.getPlayerName() + "*";
		label.setText(text);
	}
	
	public void fireAppearancePreferencesChange()
	{
		if (initted)
		{
			refreshIcons();
		}
	}
	
	public void initPlayerNames()
	{
		playerName = prefs.get(PREFERENCES_STRING_PLAYER_NAME, "Player");
		opponentOneName = prefs.get(PREFERENCES_STRING_OPPONENT_ONE_NAME, "Mark");
		opponentTwoName = prefs.get(PREFERENCES_STRING_OPPONENT_TWO_NAME, "Dave");
		opponentThreeName = prefs.get(PREFERENCES_STRING_OPPONENT_THREE_NAME, "Tom");
	}
	
	public void loadPlayerNames(Preferences savedGame)
	{
		playerName = savedGame.get(SAVED_GAME_STRING_PLAYER_NAME, "player");
		opponentOneName = savedGame.get(SAVED_GAME_STRING_OPPONENT_ONE_NAME, "opponent 1");
		opponentTwoName = savedGame.get(SAVED_GAME_STRING_OPPONENT_TWO_NAME, "opponent 2");
		opponentThreeName = savedGame.get(SAVED_GAME_STRING_OPPONENT_THREE_NAME, "opponent 3");
	}
	
	public void setPlayerNumber(int playerNumber)
	{
		this.playerNumber = playerNumber;
	}
	public void setPlayers(int players)
	{
		this.players = players;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public void setRoomId(UUID roomId) { this.roomId = roomId; }
	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}
	public String getPlayerName()
	{
		return playerName;
	}
	public String getOpponentOneName()
	{
		return opponentOneName;
	}
	public String getOpponentTwoName()
	{
		return opponentTwoName;
	}
	public String getOpponentThreeName()
	{
		return opponentThreeName;
	}
	public boolean getHasViewedHandThisGame()
	{
		return hasViewedHandThisGame;
	}
	public void setHasViewedHandThisGame(boolean hasViewedHandThisGame)
	{
		this.hasViewedHandThisGame = hasViewedHandThisGame;
	}
	public void setObserver(boolean observer)
	{
		this.observer = observer;
	}
	public void setInitted(boolean initted)
	{
		this.initted = initted;
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			AchievementsUtil.unlockSecondThoughts(roomName);
			
			for (int i=0; i <playerCards.length; i++)
			{
				playerCards[i].setFaceUp(true);
			}
			
			refreshIcons(playerCards);
			btnViewCards.setVisible(false);
			hasViewedHandThisGame = true;
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	public void cancelTimer()
	{
		if (distractedTimer != null)
		{
			distractedTimer.cancel();
			distractedTimer = new Timer("Timer-Distracted");
		}
	}
	
	public void activateRevealListener()
	{
		revealListenerActive = true;
		togglePlayerCardsForSelection(true);
	}
	public boolean isRevealListenerActive()
	{
		return revealListenerActive;
	}
	
	private void togglePlayerCardsForSelection(boolean selecting)
	{
		for (int i=0; i<playerCards.length; i++)
		{
			CardLabel cardLabel = playerCards[i];
			if (!isRevealed(cardLabel))
			{
				cardLabel.setFaded(selecting);
				cardLabel.refreshIcon();
			}
		}
	}
	
	private static class WaitedTooLong extends TimerTask
	{
		@Override
		public void run() 
		{
			AchievementsUtil.unlockDistracted();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		JLabel source = (JLabel)arg0.getSource();
		if (isSeatLabel(source))
		{
			seatClicked(source);
		}
		else if (revealListenerActive)
		{
			CardLabel cardLabel = (CardLabel)source;
			playerCardClicked(cardLabel);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		JLabel source = (JLabel)arg0.getSource();
		if (isSeatLabel(source))
		{
			setSeatIcon(source, iconEmptySeatDark);
		}
		else if (revealListenerActive)
		{
			CardLabel card = (CardLabel)source;
			setCardIcon(card, false);
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		JLabel source = (JLabel)arg0.getSource();
		if (isSeatLabel(source))
		{
			setSeatIcon(source, iconEmptySeat);
		}
		else if (revealListenerActive)
		{
			CardLabel card = (CardLabel)source;
			setCardIcon(card, true);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	private boolean isSeatLabel(JLabel source)
	{
		return source == lblPlayerSeat
		  || source == lblOpponentOneSeat
		  || source == lblOpponentTwoSeat
		  || source == lblOpponentThreeSeat;
	}
	
	private void setSeatIcon(JLabel source, ImageIcon newIcon)
	{
		ImageIcon icon = (ImageIcon)source.getIcon();
		if (icon.equals(iconEmptySeatDisabled))
		{
			return;
		}
		
		source.setIcon(newIcon);
	}
	
	private void seatClicked(JLabel source)
	{
		ImageIcon icon = (ImageIcon)source.getIcon();
		if (icon.equals(iconEmptySeatDisabled))
		{
			return;
		}
		
		int playerNumber = 0;
		if (source == lblOpponentOneSeat)
		{
			playerNumber = 1;
		}
		else if (source == lblOpponentTwoSeat)
		{
			playerNumber = 2;
		}
		else if (source == lblOpponentThreeSeat)
		{
			playerNumber = 3;
		}


		var room = ScreenCache.get(EntropyLobby.class).getGameRoomForId(roomId);
		ClientGlobals.roomApi.sitDown(room, playerNumber);
	}
	
	private void setCardIcon(CardLabel cardLabel, boolean faded)
	{
		int index = getIndexOfPlayerCard(cardLabel);
		if (index > -1
		  && !isRevealed(cardLabel))
		{
			cardLabel.setFaded(faded);
			cardLabel.refreshIcon();
		}
	}
	
	private void playerCardClicked(CardLabel source)
	{
		if (isRevealed(source))
		{
			return;
		}
		
		//Which card was clicked on?
		int index = getIndexOfPlayerCard(source);
		if (index == -1)
		{
			Debug.stackTrace("Processing mouse click for card not in player's hand: " + source);
			return;
		}
		
		revealListenerActive = false;
		
		//Get the card and set the icon (need to do this in case we're playing blind!)
		source.setFaceUp(true);
		source.setFaded(false);
		source.refreshIcon();
		
		//Move the card up. Do this by setting the border to 0, 0, 0, 0
		source.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		//We're no longer selecting, so set the cards back to normal
		togglePlayerCardsForSelection(false);
		
		//Finally, call back into game screen to notify it that something's happened
		listener.cardRevealed(source.getCard());
	}
	
	private boolean isRevealed(JLabel cardLabel)
	{
		EmptyBorder border = (EmptyBorder)cardLabel.getBorder();
		int top = border.getBorderInsets().top;
		return top == 0;
	}
	
	private int getIndexOfPlayerCard(JLabel cardLabel)
	{
		for (int i=0; i<5; i++)
		{
			if (cardLabel == playerCards[i])
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private PlayerLabel getPlayerLabelForPlayerNumber(int playerNumber)
	{
		if (playerNumber == 0)
		{
			return lblPlayer;
		}
		else if (playerNumber == 1)
		{
			return lblOpponentOne;
		}
		else if (playerNumber == 2)
		{
			return lblOpponentTwo;
		}
		else
		{
			return lblOpponentThree;
		}
	}
}