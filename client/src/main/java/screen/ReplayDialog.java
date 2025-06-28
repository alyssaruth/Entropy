package screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import game.Suit;
import object.Bid;
import object.BidListCellRenderer;
import http.dto.OnlineMessage;
import object.PlayerLabel;
import online.screen.GameRoom;
import online.screen.OnlineChatPanel;
import util.*;

import static game.CardsUtilKt.countSuit;
import static game.CardsUtilKt.isCardRelevant;
import static game.RenderingUtilKt.getVectropyResult;
import static utils.CoreGlobals.logger;

public class ReplayDialog extends JFrame
						  implements ActionListener,
						  			 Registry
{	
	public static final int RECENT_CHAT_MESSAGES_TO_SHOW = 10;
	
	private String deckDirectory = Registry.DECK_DIRECTORY_CLASSIC;
	private String jokerDirectory = Registry.JOKER_DIRECTORY_CLASSIC;
	private String numberOfColours = "";
	
	private int totalRounds = 0;
	private int roundNumber = 0;
	
	private int playerNumberOfCards = 5;
	private int opponentOneNumberOfCards = 5;
	private int opponentTwoNumberOfCards = 5;
	private int opponentThreeNumberOfCards = 5;
	
	private Suit lastBidSuit = null;
	
	private boolean playerEnabled = true;
	private boolean opponentOneEnabled = true;
	private boolean opponentTwoEnabled = true;
	private boolean opponentThreeEnabled = true;
	
	private List<String> opponentOneHand = new ArrayList<>();
	private List<String> playerHand = new ArrayList<>();
	private List<String> opponentTwoHand = new ArrayList<>();
	private List<String> opponentThreeHand = new ArrayList<>();
	
	private int jokerValue = 2;
	private boolean includeMoons = false;
	private boolean includeStars = false;
	
	private String playerName = "";
	private String opponentOneName = "";
	private String opponentTwoName = "";
	private String opponentThreeName = "";
	
	private String replayType = "";
	
	private int mode = -1;
	
	private Preferences replay = inGameReplay;

	public ReplayDialog()
	{
		try
		{
			setSize(960, 480);
			setLocationRelativeTo(null);
			setResizable(false);
			setIconImage(new ImageIcon(AchievementsDialog.class.getResource("/icons/replay.png")).getImage());
			getContentPane().setBackground(new Color(169, 169, 169));
			getContentPane().setLayout(null);
			handsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, SystemColor.scrollbar, new Color(105, 105, 105)));
			handsPanel.setBackground(new Color(192, 192, 192));
			handsPanel.setBounds(50, 11, 489, 365);
			getContentPane().add(handsPanel);
			handsPanel.setLayout(null);
			lblPlayer.setBounds(162, 206, 163, 23);
			handsPanel.add(lblPlayer);
			lblPlayer.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblPlayer.setHorizontalAlignment(SwingConstants.CENTER);
			lblOpponentOne.setBounds(153, 10, 181, 23);
			handsPanel.add(lblOpponentOne);
			lblOpponentOne.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblOpponentOne.setHorizontalAlignment(SwingConstants.CENTER);
			lblOpponentTwo.setBounds(-1, 37, 129, 23);
			handsPanel.add(lblOpponentTwo);
			lblOpponentTwo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblOpponentTwo.setHorizontalAlignment(SwingConstants.CENTER);
			lblOpponentThree.setBounds(358, 37, 129, 23);
			handsPanel.add(lblOpponentThree);
			lblOpponentThree.setHorizontalAlignment(SwingConstants.CENTER);
			lblOpponentThree.setFont(new Font("Tahoma", Font.PLAIN, 15));
			panelOpponentTwoCards.setBounds(6, 67, 115, 232);
			handsPanel.add(panelOpponentTwoCards);
			panelOpponentTwoCards.setLayout(null);
			opponentTwoCard5.setBounds(21, 117, 72, 96);
			panelOpponentTwoCards.add(opponentTwoCard5);
			opponentTwoCard4.setBounds(21, 92, 72, 96);
			panelOpponentTwoCards.add(opponentTwoCard4);
			opponentTwoCard3.setBounds(21, 67, 72, 96);
			panelOpponentTwoCards.add(opponentTwoCard3);
			opponentTwoCard2.setBounds(21, 42, 72, 96);
			panelOpponentTwoCards.add(opponentTwoCard2);
			opponentTwoCard1.setBounds(21, 17, 72, 96);
			panelOpponentTwoCards.add(opponentTwoCard1);
			panelOpponentThreeCards.setBounds(365, 67, 115, 232);
			handsPanel.add(panelOpponentThreeCards);
			panelOpponentThreeCards.setLayout(null);
			opponentThreeCard5.setBounds(21, 117, 72, 96);
			panelOpponentThreeCards.add(opponentThreeCard5);
			opponentThreeCard4.setBounds(21, 92, 72, 96);
			panelOpponentThreeCards.add(opponentThreeCard4);
			opponentThreeCard3.setBounds(21, 67, 72, 96);
			panelOpponentThreeCards.add(opponentThreeCard3);
			opponentThreeCard2.setBounds(21, 42, 72, 96);
			panelOpponentThreeCards.add(opponentThreeCard2);
			opponentThreeCard1.setBounds(21, 17, 72, 96);
			panelOpponentThreeCards.add(opponentThreeCard1);
			panelOpponentCards.setBounds(127, 40, 232, 110);
			handsPanel.add(panelOpponentCards);
			panelOpponentCards.setLayout(new FlowLayout(FlowLayout.CENTER, -92, 5));
			opponentCard5.setPreferredSize(new Dimension(72, 96));
			panelOpponentCards.add(opponentCard5);
			opponentCard4.setPreferredSize(new Dimension(72, 96));
			panelOpponentCards.add(opponentCard4);
			opponentCard3.setPreferredSize(new Dimension(72, 96));
			panelOpponentCards.add(opponentCard3);
			opponentCard2.setPreferredSize(new Dimension(72, 96));
			panelOpponentCards.add(opponentCard2);
			opponentCard1.setPreferredSize(new Dimension(72, 96));
			panelOpponentCards.add(opponentCard1);
			panelPlayerCards.setBounds(127, 235, 232, 110);
			handsPanel.add(panelPlayerCards);
			panelPlayerCards.setLayout(new FlowLayout(FlowLayout.CENTER, -92, 5));
			playerCard1.setPreferredSize(new Dimension(72, 96));
			panelPlayerCards.add(playerCard1);
			playerCard2.setPreferredSize(new Dimension(72, 96));
			panelPlayerCards.add(playerCard2);
			playerCard3.setPreferredSize(new Dimension(72, 96));
			panelPlayerCards.add(playerCard3);
			playerCard4.setPreferredSize(new Dimension(72, 96));
			panelPlayerCards.add(playerCard4);
			playerCard5.setPreferredSize(new Dimension(72, 96));
			panelPlayerCards.add(playerCard5);
			lblOpponentOne.setVisible(false);
			lblPlayer.setVisible(false);
			separator.setBackground(SystemColor.scrollbar);
			separator.setOrientation(1);
			separator.setForeground(SystemColor.controlDkShadow);
			separator.setBounds(589, 0, 10, 452);
			getContentPane().add(separator);
			lblBidHistory.setHorizontalAlignment(SwingConstants.CENTER);
			lblBidHistory.setFont(new Font("Tahoma", Font.PLAIN, 17));
			lblBidHistory.setBounds(723, 15, 97, 23);
			getContentPane().add(lblBidHistory);
			scrollPane.setBounds(674, 42, 194, 144);
			getContentPane().add(scrollPane);
			history.setBackground(SystemColor.control);
			history.setLocation(0, 119);
			scrollPane.setViewportView(history);
			history.setVisibleRowCount(4);
			lblResult.setContentType("text/html");
			lblResult.setEditable(false);
			lblResult.setFocusable(false);
			lblResult.setFont(new Font("Tahoma", Font.PLAIN, 16));
			lblResult.setBounds(655, 200, 232, 46);
			lblResult.setOpaque(false);
			lblResult.setBorder(BorderFactory.createEmptyBorder());
			lblResult.setBackground(new Color(0,0,0,0));
			getContentPane().add(lblResult);
			nextRound.setBounds(303, 387, 49, 48);
			getContentPane().add(nextRound);
			nextRound.setToolTipText("Next Round");
			previousRound.setBounds(244, 387, 49, 48);
			getContentPane().add(previousRound);
			previousRound.setToolTipText("Previous Round");
			setFilterIcons();
			clubFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubFilterGreen.png")));
			clubFilter.setBackground(new Color(216, 191, 216));
			clubFilter.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			clubFilter.setBounds(628, 275, 40, 40);
			clubFilter.setMargin(new Insets(0, 0, 0, 0));
			getContentPane().add(clubFilter);
			diamondFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondFilterBlue.png")));
			diamondFilter.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			diamondFilter.setBounds(668, 275, 40, 40);
			diamondFilter.setMargin(new Insets(0, 0, 0, 0));
			getContentPane().add(diamondFilter);
			heartFilter.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			heartFilter.setBounds(708, 275, 40, 40);
			heartFilter.setMargin(new Insets(0, 0, 0, 0));
			getContentPane().add(heartFilter);
			spadeFilter.setBackground(new Color(216, 191, 216));
			spadeFilter.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			spadeFilter.setBounds(788, 275, 40, 40);
			spadeFilter.setMargin(new Insets(0, 0, 0, 0));
			getContentPane().add(spadeFilter);
			noFilter.setBackground(new Color(216, 191, 216));
			noFilter.setFont(new Font("Times New Roman", Font.BOLD, 24));
			noFilter.setBounds(868, 275, 40, 40);
			noFilter.setMargin(new Insets(0, 0, 0, 0));
			getContentPane().add(noFilter);
			lblFilter.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblFilter.setHorizontalAlignment(SwingConstants.CENTER);
			lblFilter.setBounds(741, 245, 60, 22);
			getContentPane().add(lblFilter);
			filterGroup.add(noFilter);
			filterGroup.add(clubFilter);
			filterGroup.add(diamondFilter);
			filterGroup.add(heartFilter);
			filterGroup.add(spadeFilter);
			filterGroup.add(moonFilter);
			filterGroup.add(starFilter);
			firstRound.setToolTipText("Previous Round");
			firstRound.setBounds(185, 387, 49, 48);
			getContentPane().add(firstRound);
			firstRound.setToolTipText("First Round");
			lastRound.setToolTipText("Previous Round");
			lastRound.setBounds(362, 387, 49, 48);
			getContentPane().add(lastRound);
			lastRound.setToolTipText("Last Round");
			chatPanel.setBounds(598, 340, 327, 100);
			chatPanel.setBackground(SystemColor.control);
			getContentPane().add(chatPanel);
			moonFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonFilterPurple.png")));
			moonFilter.setBounds(748, 275, 40, 40);
			getContentPane().add(moonFilter);
			starFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/starFilterSelected.png")));
			starFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/starFilter.png")));
			starFilter.setBounds(828, 275, 40, 40);
			getContentPane().add(starFilter);
			history.setCellRenderer(new BidListCellRenderer());

			initialiseListeners();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private final JSeparator separator = new JSeparator();
	private final Panel panelOpponentCards = new Panel();
	private final JLabel opponentCard5 = new JLabel();
	private final JLabel opponentCard4 = new JLabel();
	private final JLabel opponentCard3 = new JLabel();
	private final JLabel opponentCard2 = new JLabel();
	private final JLabel opponentCard1 = new JLabel();
	private final Panel panelOpponentThreeCards = new Panel();
	private final JLabel opponentThreeCard1 = new JLabel();
	private final JLabel opponentThreeCard2 = new JLabel();
	private final JLabel opponentThreeCard3 = new JLabel();
	private final JLabel opponentThreeCard4 = new JLabel();
	private final JLabel opponentThreeCard5 = new JLabel();
	private final Panel panelOpponentTwoCards = new Panel();
	private final JLabel opponentTwoCard1 = new JLabel();
	private final JLabel opponentTwoCard2 = new JLabel();
	private final JLabel opponentTwoCard3 = new JLabel();
	private final JLabel opponentTwoCard4 = new JLabel();
	private final JLabel opponentTwoCard5 = new JLabel();
	private final Panel panelPlayerCards = new Panel();
	private final JLabel playerCard5 = new JLabel();
	private final JLabel playerCard4 = new JLabel();
	private final JLabel playerCard3 = new JLabel();
	private final JLabel playerCard2 = new JLabel();
	private final JLabel playerCard1 = new JLabel();
	private final JLabel[] opponentThreeCards = {opponentThreeCard1, opponentThreeCard2, opponentThreeCard3, opponentThreeCard4, opponentThreeCard5};
	private final JLabel[] opponentTwoCards = {opponentTwoCard1, opponentTwoCard2, opponentTwoCard3, opponentTwoCard4, opponentTwoCard5};
	private final JLabel[] opponentOneCards = {opponentCard1, opponentCard2, opponentCard3, opponentCard4, opponentCard5};
	private final JLabel[] playerCards = {playerCard5, playerCard4, playerCard3, playerCard2, playerCard1};
	private final  DefaultListModel<Bid> listmodel = new DefaultListModel<>();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JList<Bid> history = new JList<>(listmodel);
	private final JLabel lblBidHistory = new JLabel("Bid History");
	private final PlayerLabel lblOpponentOne = new PlayerLabel("Mark");
	private final PlayerLabel lblPlayer = new PlayerLabel("Player");
	private final PlayerLabel lblOpponentTwo = new PlayerLabel("New label");
	private final PlayerLabel lblOpponentThree = new PlayerLabel("New label");
	private final JTextPane lblResult = new JTextPane();
	private final JPanel handsPanel = new JPanel();
	private final JButton nextRound = new JButton(new ImageIcon(ReplayDialog.class.getResource("/buttons/btnright.png")));
	private final JButton previousRound = new JButton(new ImageIcon(ReplayDialog.class.getResource("/buttons/btnleft.png")));
	private final JButton firstRound = new JButton(new ImageIcon(ReplayDialog.class.getResource("/buttons/btnStart.png")));
	private final JButton lastRound = new JButton(new ImageIcon(ReplayDialog.class.getResource("/buttons/btnEnd.png")));
	private final JLabel lblFilter = new JLabel("Filter:");
	private final JToggleButton clubFilter = new JToggleButton("");
	private final JToggleButton diamondFilter = new JToggleButton("");
	private final JToggleButton heartFilter = new JToggleButton("");
	private final JToggleButton moonFilter = new JToggleButton("");
	private final JToggleButton spadeFilter = new JToggleButton("");
	private final JToggleButton starFilter = new JToggleButton("");
	private final JToggleButton noFilter = new JToggleButton("");
	private final ButtonGroup filterGroup = new ButtonGroup();
	private final OnlineChatPanel chatPanel = new OnlineChatPanel(null, true);

	public void initForInGameReplay()
	{
		Debug.append("Opened in-game replay", true);
		replay = inGameReplay;
		replayType = "In-Game Replay";
		initVariables();
		roundNumber = totalRounds;
		init();
	}
	public void initForRoomReplay(GameRoom room)
	{
		if (room == null)
		{
			Debug.stackTrace("Got NULL room trying to load replay");
			return;
		}
		
		String username = room.getUsername();
		String roomName = room.getRoomName();

		Debug.append("Opened online replay for room " + roomName, true);
		replay = Preferences.userRoot().node(NODE_ONLINE_REPLAY + roomName + username);
		replayType = "Replay for " + roomName;
		initVariables();
		roundNumber = totalRounds;
		init();
	}
	
	public void initForFileReplay(String filename, String folder)
	{
		String directory = ReplayFileUtil.getDirectoryFromPreferences();
		String fullPath = directory + "//Replays//" + folder + "//" + filename;

		if (ReplayFileUtil.successfullyFilledRegistryFromFile(fullPath, fileReplay))
		{
			Debug.append("Successfully opened file replay for file " + filename, true);
			replay = fileReplay;
			
			replayType = "File Replay";
			String roomName = replay.get(REPLAY_STRING_ROOM_NAME, "");
			if (!roomName.isEmpty())
			{
				replayType += " for " + roomName;
			}
			
			initVariables();
			roundNumber = getStartOrEndRoundNumber();
			init();
		}
		else
		{
			Debug.append("Unable to read file " + filename, true);
			DialogUtil.showError("Could not load replay - replay file is invalid.");
		}
	}
	
	private void init()
	{
		try
		{
			setTitle(replayType + " (Round " + roundNumber + "/" + totalRounds + ")");
			
			setSizeAndChatVisibility();
			toggleRoundNavigationButtons();
			setRoundIndependentVariables();
			getEnabledPlayers();
			setPlayerNames();
			resetHandDisplay();
			populateHands();
			var lastBidSuitName = replay.get(roundNumber + REPLAY_STRING_LAST_BID_SUIT_NAME, null);
			if (lastBidSuitName != null) {
				lastBidSuit = Suit.valueOf(lastBidSuitName);
			}
			displayHands();
			setFilterIcons();
			highlightHands(lastBidSuit);
			populateBidHistory();
			showResult(lastBidSuit);
			selectMatchingFilter();
			populateChat();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void initVariables()
	{
		mode = replay.getInt(REPLAY_INT_GAME_MODE, -1);
		totalRounds = replay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0);
		
		String playerColour = replay.get(REPLAY_STRING_PLAYER_COLOUR, "red");
		lblPlayer.setColour(playerColour);
		String opponentOneColour = replay.get(REPLAY_STRING_OPPONENT_ONE_COLOUR, "blue");
		lblOpponentOne.setColour(opponentOneColour);
		String opponentTwoColour = replay.get(REPLAY_STRING_OPPONENT_TWO_COLOUR, "green");
		lblOpponentTwo.setColour(opponentTwoColour);
		String opponentThreeColour = replay.get(REPLAY_STRING_OPPONENT_THREE_COLOUR, "purple");
		lblOpponentThree.setColour(opponentThreeColour);
		
		includeMoons = replay.getBoolean(REPLAY_BOOLEAN_INCLUDE_MOONS, false);
		includeStars = replay.getBoolean(REPLAY_BOOLEAN_INCLUDE_STARS, false);
		
		deckDirectory = prefs.get(PREFERENCES_STRING_DECK_DIRECTORY, Registry.DECK_DIRECTORY_CLASSIC);
		jokerDirectory = prefs.get(PREFERENCES_STRING_JOKER_DIRECTORY, Registry.JOKER_DIRECTORY_CLASSIC);
		numberOfColours = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
	}
	
	private int getStartOrEndRoundNumber()
	{
		int openWhere = prefs.getInt(PREFERENCES_INT_REPLAY_DEFAULT, Registry.OPEN_ON_LAST_ROUND);

		if (openWhere == Registry.OPEN_ON_LAST_ROUND)
		{
			return totalRounds;
		}
		else
		{
			return 1;
		}
	}
	
	private void setSizeAndChatVisibility()
	{
		moonFilter.setVisible(includeMoons);
		starFilter.setVisible(includeStars);
		boolean online = ReplayFileUtil.isOnline(mode);
		chatPanel.setVisible(online);
		
		if (online)
		{
			setSize(936, 480);
		}
		else if (includeStars && includeMoons)
		{
			setSize(920, 480);
		}
		else if (includeStars || includeMoons)
		{
			setSize(880, 480);
		}
		else
		{
			setSize(840, 480);
		}
		
		int xAdjustment = (getWidth() - 840)/2;
		int bidHistoryHeight = online? 15:38;
		int scrollPaneHeight = online? 42:76;
		int lblResultHeight = online? 200:250;
		int lblFilterHeight = online? 245:360;
		
		lblBidHistory.setBounds(663 + xAdjustment, bidHistoryHeight, 97, 23);
		scrollPane.setBounds(614 + xAdjustment, scrollPaneHeight, 194, 144);
		lblResult.setBounds(595 + xAdjustment, lblResultHeight, 232, 46);
		lblFilter.setBounds(681 + xAdjustment, lblFilterHeight, 60, 22);
		
		int filterHeight = lblFilterHeight + 30;
		int filterStartX = 608 + xAdjustment;
		if (includeMoons)
		{
			filterStartX -= 20;
		}
		if (includeStars)
		{
			filterStartX -= 20;
		}
		
		clubFilter.setBounds(filterStartX, filterHeight, 40, 40);
		diamondFilter.setBounds(filterStartX + 40, filterHeight, 40, 40);
		heartFilter.setBounds(filterStartX + 80, filterHeight, 40, 40);
		if (includeMoons)
		{
			moonFilter.setBounds(filterStartX + 120, filterHeight, 40, 40);
			filterStartX += 40;
		}
		
		spadeFilter.setBounds(filterStartX + 120, filterHeight, 40, 40);
		if (includeStars)
		{
			starFilter.setBounds(filterStartX + 160, filterHeight, 40, 40);
			filterStartX += 40;
		}
		
		noFilter.setBounds(filterStartX + 160, filterHeight, 40, 40);
	}
	
	private void setRoundIndependentVariables()
	{
		jokerValue = replay.getInt(REPLAY_INT_JOKER_VALUE, 0);
		playerName = replay.get(REPLAY_STRING_PLAYER_NAME, "Player");
		opponentOneName = replay.get(REPLAY_STRING_OPPONENT_ONE_NAME, "Opponent 1");
		opponentTwoName = replay.get(REPLAY_STRING_OPPONENT_TWO_NAME, "Opponent 2");
		opponentThreeName = replay.get(REPLAY_STRING_OPPONENT_THREE_NAME, "Opponent 3");
	}
	
	private void getEnabledPlayers()
	{
		playerEnabled = replay.getBoolean(roundNumber + REPLAY_BOOLEAN_PLAYER_ENABLED, true);
		opponentOneEnabled = replay.getBoolean(roundNumber + REPLAY_BOOLEAN_OPPONENT_ONE_ENABLED, true);
		opponentTwoEnabled = replay.getBoolean(roundNumber + REPLAY_BOOLEAN_OPPONENT_TWO_ENABLED, true);
		opponentThreeEnabled = replay.getBoolean(roundNumber + REPLAY_BOOLEAN_OPPONENT_THREE_ENABLED, true);
	}
	
	private void setPlayerNames()
	{
		int personToStart = replay.getInt(roundNumber + REPLAY_INT_PERSON_TO_START, 0);
		String playerStar = personToStart == 0 ? "*":"";
		String opponentOneStar = personToStart == 1 ? "*":"";
		String opponentTwoStar = personToStart == 2 ? "*":"";
		String opponentThreeStar = personToStart == 3 ? "*":"";
		
		lblPlayer.setText(playerName + playerStar);
		lblOpponentOne.setText(opponentOneName + opponentOneStar);
		lblOpponentTwo.setText(opponentTwoName + opponentTwoStar);
		lblOpponentThree.setText(opponentThreeName + opponentThreeStar);
		
		setLabelVisibility(lblPlayer, playerName, playerEnabled, REPLAY_STRING_PLAYER_COLOUR, "red");
		setLabelVisibility(lblOpponentOne, opponentOneName, opponentOneEnabled, REPLAY_STRING_OPPONENT_ONE_COLOUR, "blue");
		setLabelVisibility(lblOpponentTwo, opponentTwoName, opponentTwoEnabled, REPLAY_STRING_OPPONENT_TWO_COLOUR, "green");
		setLabelVisibility(lblOpponentThree, opponentThreeName, opponentThreeEnabled, REPLAY_STRING_OPPONENT_THREE_COLOUR, "purple");
	}
	
	private void setLabelVisibility(PlayerLabel label, String name, boolean enabled, String colourNode, String defaultColour)
	{
		if (enabled)
		{
			label.setVisible(true);
			String colour = replay.get(colourNode, defaultColour);
			label.setColour(colour);
		}
		else
		{
			if (playerLeftThisRound(name))
			{
				label.setVisible(true);
				label.setColour("gray");
			}
			else
			{
				label.setVisible(false);
			}
			
		}
	}
	
	private boolean playerLeftThisRound(String name)
	{
		int historySize = replay.getInt(roundNumber + REPLAY_INT_HISTORY_SIZE, 0);
		for (int i = 0; i < historySize; i++)
		{
			String modelItem = replay.get(roundNumber + REPLAY_STRING_LISTMODEL + i, "");
			if (modelItem.contains(name + " left"))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void populateHands()
	{
		//get the hands
		playerNumberOfCards = replay.getInt(roundNumber + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, 0);
		opponentOneNumberOfCards = replay.getInt(roundNumber + REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS, 0);
		opponentTwoNumberOfCards = replay.getInt(roundNumber + REPLAY_INT_OPPONENT_TWO_NUMBER_OF_CARDS, 0);
		opponentThreeNumberOfCards = replay.getInt(roundNumber + REPLAY_INT_OPPONENT_THREE_NUMBER_OF_CARDS, 0);
		opponentThreeHand = new ArrayList<>();
		opponentTwoHand = new ArrayList<>();
		opponentOneHand = new ArrayList<>();
		playerHand = new ArrayList<>();

		for (int i = 0; i < playerNumberOfCards; i++)
		{
			playerHand.add(replay.get(roundNumber + REPLAY_STRING_PLAYER_HAND + i, ""));
		}
		for (int i = 0; i < opponentOneNumberOfCards; i++)
		{
			opponentOneHand.add(replay.get(roundNumber + REPLAY_STRING_OPPONENT_ONE_HAND + i, ""));
		}
		for (int i = 0; i < opponentTwoNumberOfCards; i++)
		{
			opponentTwoHand.add(replay.get(roundNumber + REPLAY_STRING_OPPONENT_TWO_HAND + i, ""));
		}
		for (int i = 0; i < opponentThreeNumberOfCards; i++)
		{
			opponentThreeHand.add(replay.get(roundNumber + REPLAY_STRING_OPPONENT_THREE_HAND + i, ""));
		}
	}
	
	private void resetHandDisplay()
	{
		for (int i = 0; i < 5; i++)
		{
			playerCards[i].setIcon(null);
			playerCards[i].setVisible(true);
			opponentOneCards[i].setIcon(null);
			opponentOneCards[i].setVisible(true);
			opponentTwoCards[i].setIcon(null);
			opponentTwoCards[i].setVisible(true);
			opponentThreeCards[i].setIcon(null);
			opponentThreeCards[i].setVisible(true);
		}
	}
	
	private void displayHands()
	{
		deckDirectory = prefs.get(PREFERENCES_STRING_DECK_DIRECTORY, Registry.DECK_DIRECTORY_CLASSIC);
		numberOfColours = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
		
		displayHand(playerCards, playerHand, playerNumberOfCards);
		displayHand(opponentOneCards, opponentOneHand, opponentOneNumberOfCards);
		displayHand(opponentTwoCards, opponentTwoHand, opponentTwoNumberOfCards);
		displayHand(opponentThreeCards, opponentThreeHand, opponentThreeNumberOfCards);
	}
	
	private void displayHand(JLabel[] cardLabels, List<String> hand, int handSize)
	{
		for (int i = 0; i < 5; i++)
		{
			if (i < handSize)
			{
				ImageIcon card = GameUtil.getImageForCard(hand.get(i), deckDirectory, jokerDirectory, numberOfColours);
				cardLabels[i].setIcon(card);
			}
			else
			{
				cardLabels[i].setVisible(false);
			}
		}
	}
	
	private void highlightHands(Suit suit)
	{
		highlightHand(suit, playerHand, playerCards);
		highlightHand(suit, opponentOneHand, opponentOneCards);
		highlightHand(suit, opponentTwoHand, opponentTwoCards);
		highlightHand(suit, opponentThreeHand, opponentThreeCards);
	}
	
	private void highlightHand(Suit suit, List<String> hand, JLabel[] cards)
	{
		int size = hand.size();
		
		for (int i=0; i<size; i++)
		{
			boolean isRelevant = isCardRelevant(hand.get(i), suit);
			if (!isRelevant)
			{
				ImageIcon fadedIcon = GameUtil.getFadedImageForCard(hand.get(i), deckDirectory);
				cards[i].setIcon(fadedIcon);
			}
			else
			{
				ImageIcon normalIcon = GameUtil.getImageForCard(hand.get(i), deckDirectory, jokerDirectory, numberOfColours);
				cards[i].setIcon(normalIcon);
			}
		}
	}
	
	private void populateBidHistory()
	{
		listmodel.clear();
		int historySize = replay.getInt(roundNumber + REPLAY_INT_HISTORY_SIZE, 0);
		for (int i = 0; i < historySize; i++)
		{
			String modelItem = replay.get(roundNumber + REPLAY_STRING_LISTMODEL + i, "");
			Bid bid = Bid.factoryFromXmlString(modelItem, includeMoons, includeStars);
			listmodel.addElement(bid);
		}
	}
	
	private void showResult(Suit suit)
	{
		switch (mode)
		{
		case ReplayConstants.GAME_MODE_ENTROPY:
		case ReplayConstants.GAME_MODE_ENTROPY_ONLINE:
			showEntropyResult(suit);
			break;
		case ReplayConstants.GAME_MODE_VECTROPY:
		case ReplayConstants.GAME_MODE_VECTROPY_ONLINE:
			showVectropyResult(suit);
			break;
		default:
			Debug.stackTrace("Invalid mode showing replay result: " + mode);
			return;
		}
	}
	
	private void showEntropyResult(Suit suit)
	{
		int total = countSuit(suit, getConcatenatedHands(), jokerValue);
		
		String suitStr = suit.getDescription(total);
		if (total == 1)
		{
			setResultText("There was " + total + " " + suitStr);
		}
		else
		{
			setResultText("There were " + total + " " + suitStr);
		}
	}
	
	private void showVectropyResult(Suit suitCode)
	{
		String result = getVectropyResult(getConcatenatedHands(), jokerValue, suitCode, includeMoons, includeStars);
		setResultText("Result: " + result);
	}
	
	private void setResultText(String text)
	{
		lblResult.setText("<font face=\"Tahoma\" size=\"5\">" + text + "</font>");
		StyledDocument doc = lblResult.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}
	
	private void selectMatchingFilter()
	{
		if (lastBidSuit == null) {
			noFilter.setSelected(true);
			return;
		}

		switch (lastBidSuit)
		{
			case Clubs:
				clubFilter.setSelected(true);
				break;
			case Diamonds:
				diamondFilter.setSelected(true);
				break;
			case Hearts:
				heartFilter.setSelected(true);
				break;
			case Moons:
				moonFilter.setSelected(true);
				break;
			case Spades:
				spadeFilter.setSelected(true);
				break;
			case Stars:
				starFilter.setSelected(true);
				break;
			default:
				logger.error("replay.error", "Invalid case for replay filter: " + lastBidSuit);
		}
				
	}
	
	private void populateChat()
	{
		if (chatPanel.isVisible())
		{
			chatPanel.clear();
			for (int i=RECENT_CHAT_MESSAGES_TO_SHOW-1; i>=0; i--)
			{
				String colour = replay.get(roundNumber + REPLAY_STRING_CHAT_COLOUR + i, "");
				String username = replay.get(roundNumber + REPLAY_STRING_CHAT_USERNAME + i, "");
				String text = replay.get(roundNumber + REPLAY_STRING_CHAT_CONTENT + i, "");
				
				if (!text.isEmpty())
				{
					OnlineMessage message = new OnlineMessage(colour, text, username);
					chatPanel.updateChatBox(message);
				}
			}
			
			chatPanel.scrollToBottom();
		}
	}
	
	private void toggleRoundNavigationButtons()
	{
		nextRound.setEnabled(totalRounds > roundNumber);
		lastRound.setEnabled(totalRounds > roundNumber);
		previousRound.setEnabled(roundNumber > 1);
		firstRound.setEnabled(roundNumber > 1);
	}
	
	private void initNewRound()
	{
		try
		{
			setTitle(replayType + " (Round " + roundNumber + "/" + totalRounds + ")");
			toggleRoundNavigationButtons();
			getEnabledPlayers();
			setPlayerNames();
			resetHandDisplay();
			populateHands();
			var lastBidSuitName = replay.get(roundNumber + REPLAY_STRING_LAST_BID_SUIT_NAME, null);
			if (lastBidSuitName != null) {
				lastBidSuit = Suit.valueOf(lastBidSuitName);
			}
			displayHands();
			highlightHands(lastBidSuit);
			populateBidHistory();
			showResult(lastBidSuit);
			selectMatchingFilter();
			populateChat();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void showFirstHand()
	{
		roundNumber = 1;
		initNewRound();
	}
	
	private void showLastHand()
	{
		roundNumber = totalRounds;
		initNewRound();
	}

	private void showNextHand()
	{
		roundNumber++;
		initNewRound();
	}

	private void showPreviousHand()
	{
		roundNumber--;
		initNewRound();
	}
	
	public void fireAppearancePreferencesChange()
	{
		try
		{
			deckDirectory = prefs.get(PREFERENCES_STRING_DECK_DIRECTORY, Registry.DECK_DIRECTORY_CLASSIC);
			jokerDirectory = prefs.get(PREFERENCES_STRING_JOKER_DIRECTORY, Registry.JOKER_DIRECTORY_CLASSIC);
			numberOfColours = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
			
			if (isVisible())
			{
				history.repaint();
				
				Suit suitFiltered = getFilterSuitFromFilters();
				setFilterIcons();
				displayHands();
				highlightHands(suitFiltered);
				
				//if suitFiltered = -1, then show the result for lastBidSuitCode
				if (suitFiltered == null)
				{
					showResult(lastBidSuit);
				}
				else
				{
					showResult(suitFiltered);
				}
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private Suit getFilterSuitFromFilters()
	{
		if (clubFilter.isSelected())
		{
			return Suit.Clubs;
		}
		else if (diamondFilter.isSelected())
		{
			return Suit.Diamonds;
		}
		else if (heartFilter.isSelected())
		{
			return Suit.Hearts;
		}
		else if (spadeFilter.isSelected())
		{
			return Suit.Spades;
		}
		else if (moonFilter.isSelected()) {
			return Suit.Moons;
		}
		else if (starFilter.isSelected()) {
			return Suit.Stars;
		}
		
		return null;
	}
	
	private void setFilterIcons()
	{
		numberOfColours = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
		boolean fourColours = (numberOfColours.equals(Registry.FOUR_COLOURS));
		
		heartFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/heartFilterSelected.png")));
		heartFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/heartFilter.png")));
		spadeFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/spadeFilterSelected.png")));
		spadeFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/spadeFilter.png")));
		starFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/starFilter.png")));
		starFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/starFilterSelected.png")));
		noFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/noFilterSelected.png")));
		noFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/noFilter.png")));
		
		if (fourColours)
		{
			clubFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubFilterGreenSelected.png")));
			clubFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubFilterGreen.png")));
			
			diamondFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondFilterBlueSelected.png")));
			diamondFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondFilterBlue.png")));
			
			moonFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonFilterPurpleSelected.png")));
			moonFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonFilterPurple.png")));
		}
		else
		{
			clubFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubFilterSelected.png")));
			clubFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubFilter.png")));
			
			diamondFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondFilterSelected.png")));
			diamondFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondFilter.png")));
			
			moonFilter.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonFilterSelected.png")));
			moonFilter.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonFilter.png")));
		}
	}
	
	public void roundAdded()
	{	
		if (isVisible())
		{
			totalRounds = replay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0);
			setTitle(replayType + " (Round " + roundNumber + "/" + totalRounds + ")");
			nextRound.setEnabled(true);
			lastRound.setEnabled(true);
		}
	}

	private List<String> getConcatenatedHands()
	{
		var result = new ArrayList<String>();
		result.addAll(playerHand);
		result.addAll(opponentOneHand);
		result.addAll(opponentTwoHand);
		result.addAll(opponentThreeHand);
		return result;
	}
	
	private void initialiseListeners()
	{
		firstRound.addActionListener(this);
		lastRound.addActionListener(this);
		nextRound.addActionListener(this);
		previousRound.addActionListener(this);
		clubFilter.addActionListener(this);
		diamondFilter.addActionListener(this);
		heartFilter.addActionListener(this);
		moonFilter.addActionListener(this);
		spadeFilter.addActionListener(this);
		starFilter.addActionListener(this);
		noFilter.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		try
		{
			Component source = (Component)arg0.getSource();
			if (source == firstRound)
			{
				showFirstHand();
			}
			else if (source == lastRound)
			{
				showLastHand();
			}
			else if (source == nextRound)
			{
				showNextHand();
			}
			else if (source == previousRound)
			{
				showPreviousHand();
			}
			else if (source == clubFilter)
			{
				highlightHands(Suit.Clubs);
				showResult(Suit.Clubs);
			}
			else if (source == diamondFilter)
			{
				highlightHands(Suit.Diamonds);
				showResult(Suit.Diamonds);
			}
			else if (source == heartFilter)
			{
				highlightHands(Suit.Hearts);
				showResult(Suit.Hearts);
			}
			else if (source == moonFilter)
			{
				highlightHands(Suit.Moons);
				showResult(Suit.Moons);
			}
			else if (source == spadeFilter)
			{
				highlightHands(Suit.Spades);
				showResult(Suit.Spades);
			}
			else if (source == starFilter)
			{
				highlightHands(Suit.Stars);
				showResult(Suit.Stars);
			}
			else if (source == noFilter)
			{
				highlightHands(null);
				showResult(lastBidSuit);
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
}