package online.screen;

import game.GameMode;
import game.GameSettings;
import http.dto.RoomSummary;
import object.*;
import online.util.XmlBuilderClient;
import org.w3c.dom.Document;
import screen.*;
import util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

import static utils.ColourUtilKt.getColourForPlayerNumber;
import static utils.CoreGlobals.logger;

/**
 * This is an actual room as seen by the player
 */
public abstract class GameRoom extends JFrame
					  		   implements WindowListener,
					  			          ActionListener,
					  			          BidListener,
					  			          RevealListener,
					  			          Registry
{
	public static final int MAX_NUMBER_OF_PLAYERS = 4;
	private static final int ADJUSTED_PLAYER_NUMBER_ME = 0;


	private final UUID id;
	private final String roomName;
	private final int players;
	private final GameSettings settings;
	
	private String gameId = "";
	private String username = null;
	public boolean observer = false;
	public int playerNumber = -1;
	public int playerNumberLocal = -1;
	public int totalNumberOfCards = 0;
	private boolean gameInProgress = false;
	private int roundNumber = 1;
	private boolean playBlind = false;
	private boolean hasActedBlindThisGame = false;
	public boolean earnedPsychic = false;
	public boolean hasOverbid = false;
	private boolean seenRoundStart = false;
	
	public Bid lastBid = null;
	
	private ConcurrentHashMap<Integer, Player> hmPlayerByAdjustedPlayerNumber = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer, List<String>> hmHandByAdjustedPlayerNumber = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer, Bid> hmBidByPlayerNumber = new ConcurrentHashMap<>();
	private int personToStartLocal = -1;
	private int personToStart = -1;
	public int lastPlayerToAct = 0;
	private OnlineChatPanel chatPanel = null;
	public Preferences replay = null;
	public ReplayDialog replayDialog = new ReplayDialog();
	public BidPanel bidPanel = null;
	
	public GameRoom(UUID id, String roomName, GameSettings settings, int players)
	{
		this.id = id;
		this.roomName = roomName;
		this.settings = settings;
		this.players = players;

		bgPanel.setLayout(new BorderLayout(0, 0));
		splitPane.setOpaque(false);
		setContentPane(bgPanel);
		
		chatPanel = new OnlineChatPanel(roomName, false);
		
		setIcon();
		setLocationRelativeTo(ScreenCache.get(EntropyLobby.class));
		int width = getWidth();
		splitPane.setDividerLocation(width - 350);
		splitPane.setResizeWeight(1.0);
		splitPane.setEnabled(false);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(leftPane);
		leftPane.setLayout(new BorderLayout(0, 0));
		splitPane.setRightComponent(rightPane);
		rightPane.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab("Chat", null, chatPanel, null);
		chatPanel.setPreferredSize(new Dimension(300, 200));
		handPanel.setOpaque(false);
		leftPane.add(handPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		rightPane.add(rightCenter, BorderLayout.CENTER);
		rightCenter.setLayout(new BorderLayout(0, 0));
		rightCenter.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblResult.setPreferredSize(new Dimension(0, 30));
		rightCenter.add(panelInformation, BorderLayout.SOUTH);
		panelInformation.setLayout(new BorderLayout(0, 0));
		panelInformation.add(lblResult);
		lblResult.setContentType("text/html");
		lblResult.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblResult.setEditable(false);
		lblResult.setFocusable(false);
		lblResult.setOpaque(false);
		lblResult.setBorder(BorderFactory.createEmptyBorder());
		lblResult.setBackground(new Color(0,0,0,0));
		panelInformation.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_1.add(btnStandUp);
		panel_1.add(chckbxStandUpAfter);
		rightCenter.add(panelHistory, BorderLayout.CENTER);
		panelHistory.setLayout(new BorderLayout(0, 0));
		panelHistory.add(scrollPane);
		scrollPane.setViewportView(bidBox);
		lblBidHistory.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblBidHistory.setHorizontalAlignment(SwingConstants.CENTER);
		panelHistory.add(lblBidHistory, BorderLayout.NORTH);
		panelHistory.add(btnReplay, BorderLayout.SOUTH);
		lblGameInformation.setVerticalAlignment(SwingConstants.TOP);
		lblGameInformation.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblGameInformation.setHorizontalAlignment(SwingConstants.CENTER);
		lblGameInformation.setPreferredSize(new Dimension(450, 25));
		leftPaneSouth.setLayout(new BorderLayout(0, 0));
		leftPaneSouth.add(lblGameInformation, BorderLayout.NORTH);
		leftPane.add(leftPaneSouth, BorderLayout.SOUTH);
		lblGameInformation.setForeground(Color.getHSBColor(0, (float)0.5, 1));
		lblGameInformation.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		chckbxStandUpAfter.setEnabled(false);
		panelInformation.add(tabbedPane, BorderLayout.SOUTH);
		tabbedPane.addTab("Info", null, panelInfo, null);
		panelInfo.setLayout(new BorderLayout(0, 0));
		panelInfo.add(textPaneInfo);
		textPaneInfo.setOpaque(true);
		textPaneInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		textPaneInfo.setBackground(Color.WHITE);
		textPaneInfo.setEditable(false);
		bidBox.setCellRenderer(new BidListCellRenderer());
		
		btnReplay.addActionListener(this);
		btnStandUp.addActionListener(this);
		addWindowListener(this);
	}
	
	private final BackgroundPanel bgPanel = new BackgroundPanel();
	private final JSplitPane splitPane = new JSplitPane();
	private final TransparentPanel leftPane = new TransparentPanel();
	public final JPanel leftPaneSouth = new JPanel();
	private final TransparentPanel rightPane = new TransparentPanel();
	public final HandPanelMk2 handPanel = new HandPanelMk2(this);
	private final JScrollPane scrollPane = new JScrollPane();
	private final DefaultListModel<Bid> listmodel = new DefaultListModel<>();
	private final JList<Bid> bidBox = new JList<>(listmodel);
	private final TransparentPanel rightCenter = new TransparentPanel();
	private final JTextPane lblResult = new JTextPane();
	private final JPanel panelInformation = new JPanel();
	private final PulsingTextLabel lblGameInformation = new PulsingTextLabel("Waiting for players...");
	private final JPanel panelHistory = new JPanel();
	private final JLabel lblBidHistory = new JLabel("Bid History");
	private final JButton btnReplay = new JButton("Replay");
	private final JPanel panel_1 = new JPanel();
	private final JButton btnStandUp = new JButton("Stand Up");
	private final JCheckBox chckbxStandUpAfter = new JCheckBox("Stand up after this game");
	private final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
	private final TransparentPanel panelInfo = new TransparentPanel();
	private final JTextPane textPaneInfo = new JTextPane();
	
	public static GameRoom factoryCreate(RoomSummary room)
	{
		GameRoom ret = null;

		var settings = room.getGameSettings();
		
		String roomName = room.getName();
		int capacity = room.getCapacity();
		GameMode mode = settings.getMode();
		
		if (mode == GameMode.Entropy)
		{
			ret = new EntropyRoom(room.getId(), roomName, settings, capacity);
		}
		else if (mode == GameMode.Vectropy)
		{
			ret = new VectropyRoom(room.getId(), roomName, settings, capacity);
		}

		return ret;
	}
	
	/**
	 * Abstract methods
	 */
	public abstract void updateScreenForChallengeOrIllegal();
	public abstract void resetBids();
	public abstract void doSpecificResetGameVariables();
	public abstract void saveModeSpecificVariablesForReplay();
	public abstract void updatePerfectBidVariables(Bid bid);
	public abstract void updateAchievementVariables(Bid bid);
	public abstract void unlockEndOfGameAchievements();
	
	private void setIcon()
	{
		boolean unlockedExtraSuits = rewards.getBoolean(REWARDS_BOOLEAN_EXTRA_SUITS, false);
		
		ArrayList<String> suits = new ArrayList<>();
		suits.add("club");
		suits.add("diamond");
		suits.add("heart");
		suits.add("spade");
		
		if (unlockedExtraSuits)
		{
			suits.add("moon");
			suits.add("star");
		}
		
		//Pick a suit at random
		int size = suits.size();
		Random iconChooser = new Random();
		int index = iconChooser.nextInt(size);
		String suit = suits.get(index);
		
		//Load the four images corresponding to 16px, 32px, 64px and 128px
		ArrayList<Image> images = new ArrayList<>();
		for (int i=16; i<256; i=2*i)
		{
			Image ico = new ImageIcon(getClass().getResource("/icons/" + suit + i + ".png")).getImage();
			images.add(ico);
		}
		
		setIconImages(images);
	}
	
	public void adjustSize()
	{
		boolean vectropy = this instanceof VectropyRoom;
		
		if (vectropy 
		  && settings.getIncludeMoons()
		  && settings.getIncludeStars())
		{
			setMinimumSize(new Dimension(970, 570));
			setSize(970, 570);
		}
		else if (vectropy && (settings.getIncludeMoons() || settings.getIncludeStars()))
		{
			setMinimumSize(new Dimension(925, 570));
			setSize(925, 570);
		}
		else
		{
			setMinimumSize(new Dimension(880, 570));
			setSize(880, 570);
		}
	}
	
	public void init(boolean hotswap)
	{
		if (observer)
		{
			initObserver(hotswap);
		}
		else
		{
			//Always hotswapping if joining as a player
			initPlayer();
		}
		
		resetBids();
	}
	
	public void initPlayer()
	{
		adjustSize();

		resetVariables(true);
		
		btnStandUp.setVisible(true);
		chckbxStandUpAfter.setVisible(true);
		chckbxStandUpAfter.setEnabled(false);
		
		leftPaneSouth.add(bidPanel, BorderLayout.CENTER);
		
		initBidPanel();
		
		bidPanel.fireAppearancePreferencesChange();
		enableBidPanel(false);
		btnStandUp.setEnabled(true);
		
		//Add self straight away so there's no delay
		addOrUpdatePlayer(playerNumber, username);
	}
	
	public void initObserver(boolean hotswap)
	{
		setUsername(username);
		setObserver(true);
		setPlayerNumber(-1);
		setMinimumSize(new Dimension(800, 440));
		setSize(840, 440);
		
		resetVariables(hotswap);
		btnStandUp.setVisible(false);
		chckbxStandUpAfter.setVisible(false);
		
		leftPaneSouth.remove(bidPanel);
		
		Document observerRequest = XmlBuilderClient.factoryObserverRequest(roomName, username);
		MessageUtil.sendMessage(observerRequest, 0);
	}
	
	private void resetVariables(boolean hotswap)
	{
		int width = getWidth();
		splitPane.setDividerLocation(width - 350);
		
		gameId = "";
		btnReplay.setEnabled(false);
		replay = Preferences.userRoot().node(NODE_ONLINE_REPLAY + roomName + username);
		initHandPanel();
		
		if (!hotswap)
		{
			chatPanel.init();
		}
		
		hmPlayerByAdjustedPlayerNumber = new ConcurrentHashMap<>();
		hmHandByAdjustedPlayerNumber = new ConcurrentHashMap<>();
		hmBidByPlayerNumber = new ConcurrentHashMap<>();
		totalNumberOfCards = players * 5;
		waitingForPlayers();
		gameInProgress = false;
		listmodel.clear();
		lblResult.setText("");
		seenRoundStart = false;
		
		setInfoText();
	}
	
	private void setInfoText()
	{
		String text = "";
		if (settings.getJokerQuantity() > 0)
		{
			text += "The deck includes " + settings.getJokerQuantity() + " jokers worth " + settings.getJokerValue() + " of every suit.";
		}
		else
		{
			text += "The deck contains no jokers.";
		}
		
		if (settings.getCardReveal())
		{
			text += "\n\nPlayers reveal cards as the game progresses.";
		}
		
		if (settings.getNegativeJacks())
		{
			text += "\n\nJacks are worth -1.";
		}
		
		textPaneInfo.setText(text);
	}
	
	private void resetGameVariables()
	{
		resetBids();
		initBidPanel();
		
		earnedPsychic = false;
		
		doSpecificResetGameVariables();
	}
	
	private void initBidPanel()
	{
		int maxBid = GameUtil.getMaxBid(settings, totalNumberOfCards);
		bidPanel.init(maxBid, totalNumberOfCards, true, settings.getIncludeMoons(), settings.getIncludeStars(), settings.getIllegalAllowed());
	}
	
	public void initHandPanel()
	{
		handPanel.setViewCardsVisibility(false);
		handPanel.setHasViewedHandThisGame(false);
		handPanel.setObserver(observer);
		handPanel.clear();
		handPanel.setUsername(username);
		handPanel.setPlayers(players);
		handPanel.setPlayerNumber(playerNumber);
		handPanel.setRoomId(id);
		handPanel.setRoomName(roomName);
		handPanel.activateEmptySeats();
	}
	
	public void enableBidPanel(boolean enable)
	{
		btnStandUp.setEnabled(enable);
		bidPanel.enableBidPanel(enable);
		
		//Don't enable the challenge/illegal options unless there's been an actual bid
		if (!bidHistoryContainsBid())
		{
			bidPanel.enableChallenge(false);
		}
	}
	
	public void synchronisePlayers(Map<Integer, String> serverHmPlayerByPlayerNumber,
								   Map<Integer, String> serverHmFormerPlayerByPlayerNumber)
	{
		//Add any players the server has that we don't
		addPlayersFromServer(serverHmPlayerByPlayerNumber, true);
		
		//Add players who used to be active but aren't anymore - this hm will only be present when sending
		//to a new observer
		addPlayersFromServer(serverHmFormerPlayerByPlayerNumber, false);
		
		//Remove players who we have as active but the server doesn't
		processPlayersLeaving(serverHmPlayerByPlayerNumber);
		
		//Initialise a game if we've become full
		int currentSize = hmPlayerByAdjustedPlayerNumber.size();
		if (currentSize == players
		  && !gameInProgress)
		{
			if (observer)
			{
				Document observerRequest = XmlBuilderClient.factoryObserverRequest(roomName, username);
				MessageUtil.sendMessage(observerRequest, 0);
			}
			else
			{
				sendGameInitialiseRequest();
			}
		}
	}
	
	private void addPlayersFromServer(Map<Integer, String> hmPlayerByPlayerNumber, boolean active)
	{
		Iterator<Integer> it = hmPlayerByPlayerNumber.keySet().iterator();
		for (; it.hasNext();)
		{
			int playerNumber = it.next();
			String username = hmPlayerByPlayerNumber.get(playerNumber);
			addOrUpdatePlayer(playerNumber, username);
			
			if (!active)
			{
				removePlayer(adjustForMe(playerNumber), true);
			}
		}
	}
	
	private void processPlayersLeaving(Map<Integer, String> serverHmPlayerByPlayerNumber)
	{
		for (int i=0; i<MAX_NUMBER_OF_PLAYERS; i++)
		{
			Player playerInHm = hmPlayerByAdjustedPlayerNumber.get(i);
			if (playerInHm == null)
			{
				continue;
			}
			
			//We never want to remove ourselves, but may end up trying to if a PlayerNotification gets back to us
			//AFTER we've sent a CloseRoomRequest/JoinRoomRequest. In this case, just ignore it.	
			String usernameInHm = playerInHm.getName();
			if (!observer
			  && usernameInHm.equals(username))
			{
				continue;
			}
			
			if (!serverHmPlayerByPlayerNumber.containsValue(usernameInHm))
			{
				removePlayer(i, false);
				handPanel.activateEmptySeats();
			}
		}
	}
	
	public void addOrUpdatePlayer(int playerNumber, String username)
	{
		int adjustedNo = adjustForMe(playerNumber);
		
		Player currentPlayer = hmPlayerByAdjustedPlayerNumber.get(adjustedNo);
		if (currentPlayer == null)
		{
			String colour = getColourForPlayerNumber(playerNumber);
			Player newPlayer = new Player(playerNumber, colour);
			newPlayer.setName(username);
			newPlayer.setEnabled(true);
			hmPlayerByAdjustedPlayerNumber.put(adjustedNo, newPlayer);
			
			handPanel.initialisePlayer(adjustedNo, username, colour, 5);
			handPanel.activateEmptySeats();
		}
		else
		{
			String currentUsername = currentPlayer.getName();
			if (!currentUsername.equals(username))
			{
				String details = "Adding player " + username + " (" + playerNumber + "), but already have " + currentPlayer;
				Debug.stackTrace(details);
			}
		}
	}
	
	private void removePlayer(int playerNumberAdjusted, boolean formerPlayer)
	{
		Player removedPlayer = hmPlayerByAdjustedPlayerNumber.remove(playerNumberAdjusted);
		removedPlayer.setEnabled(false);
		
		//If there isn't a game in progress and we're not dealing with a former player then we just want
		//to do the removal
		if (!gameInProgress
		  && !formerPlayer)
		{
			handPanel.removePlayer(playerNumberAdjusted);
			return;
		}
		
		//Move on to the next player if we're in actual play
		if (!formerPlayer)
		{
			LeftBid bid = new LeftBid();
			bid.setPlayer(removedPlayer);
			addBidToBidBox(playerNumberAdjusted, bid);
			
			boolean wasPlayersTurn = handPanel.playerIsSelected(playerNumberAdjusted);
			if (wasPlayersTurn)
			{
				selectNextPlayer(playerNumberAdjusted);
			}
		}
		
		//Finally, deselect the player and grey them out
		handPanel.playerLeft(playerNumberAdjusted);
	}
	
	/*private boolean hasPlayerLeft(int playerNumberAdjusted)
	{
		Player player = hmPlayerByAdjustedPlayerNumber.get(playerNumberAdjusted);
		return player == null;
	}*/
	
	public void selectNextPlayer(int previousPlayerAdjusted)
	{
		int nextPlayerAdjusted = GameUtil.getNextPlayer(previousPlayerAdjusted);
		if (nextPlayerAdjusted == playerNumberLocal
		  && playerIsEnabled(playerNumberLocal))
		{
			requestFocus();
			enableBidPanel(true);
			
			handPanel.selectPlayerInAwtThread(playerNumberLocal, true);
		}
		else if (playerIsEnabled(nextPlayerAdjusted))
		{
			handPanel.selectPlayerInAwtThread(nextPlayerAdjusted, true);
		}
		else
		{
			selectNextPlayer(nextPlayerAdjusted);
		}
	}
	
	private boolean playerIsEnabled(int adjustedPlayerNo)
	{
		Player player = hmPlayerByAdjustedPlayerNumber.get(adjustedPlayerNo);
		return player != null
		  && player.isEnabled();
	}
	
	private void processChallengeOrIllegal()
	{
		handPanel.setViewCardsVisibility(false);
		
		updateScreenForChallengeOrIllegal();
		
		if (getSeenRoundStart() && observer)
		{
			AchievementsUtil.unlockRailbird();
		}
	}
	
	private void addBidToBidBox(int playerNumberAdjusted, Bid bid)
	{
		boolean blind = handPanel.isPlayingBlind() 
					 && playerNumberAdjusted == playerNumberLocal 
					 && !(bid instanceof LeftBid);
		
		hasActedBlindThisGame |= blind;
		bid.setBlind(blind);
		
		listmodel.add(0, bid);
	}
	
	public void showResult(String result)
	{
		lblResult.setText("<font face=\"Tahoma\" size=\"5\">" + result + "</font>");
		
		StyledDocument doc = lblResult.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}
	
	/*
	 * This is ugly as SHIT
	 */
	public int adjustForMe(int i)
	{
		switch (playerNumber)
		{
		case 1:
			if (i == 0 || i == 2)
			{
				return i + 1;
			}
			else
			{
				return i - 1;
			}
		case 2:
			if (i == 0)
			{
				return 3;
			}
			else if (i == 1)
			{
				return 2;
			}
			else if (i == 2)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		case 3:
			if (i == 0)
			{
				return 2;
			}
			else if (i == 1)
			{
				return 3;
			}
			else if (i == 2)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		default:
			//no adjustment necessary
			return i;
		}
	}
	
	private void sendGameInitialiseRequest()
	{
		sendGameInitialiseRequest(0);
	}
	private void sendGameInitialiseRequest(int delay)
	{
		if (isVisible())
		{
			Document initialiseRequest = XmlBuilderClient.factoryNewGameRequest(roomName, gameId, username);
			MessageUtil.sendMessage(initialiseRequest, delay);
			
			AchievementsUtil.updateAndUnlockSocial(hmPlayerByAdjustedPlayerNumber);
		}
	}
	
	public void setHand(int playerNumber, List<String> hand)
	{
		int playerNumberAdjusted = adjustForMe(playerNumber);
		hmHandByAdjustedPlayerNumber.put(playerNumberAdjusted, hand);
	}
	
	public void startGame(int personToStart)
	{
		//Dispose the replay window in case it is open from a previous game
		replayDialog.dispose();
		
		try
		{
			replay.clear();
		}
		catch (Throwable t)
		{
			logger.error("replayClearError", "Error clearing replay", t);
		}
		
		adjustPlayersBasedOnHands();
		playBlind = prefs.getBoolean(PREFERENCES_BOOLEAN_PLAY_BLIND, false);
		hasActedBlindThisGame = false;
		handPanel.setHasViewedHandThisGame(false);
		handPanel.setViewCardsVisibility(playBlind && playerIsEnabled(playerNumberLocal));
		
		chckbxStandUpAfter.setEnabled(true);
		btnReplay.setEnabled(false);
		gameInProgress = true;
		roundNumber = 1;
		lblGameInformation.setVisible(false);
		totalNumberOfCards = players * 5;
		personToStartLocal = adjustForMe(personToStart);
		listmodel.clear();
		lblResult.setText("");
		handPanel.assignAsteriskToStartingPlayer(personToStartLocal);
		handPanel.selectPlayerInAwtThread(personToStartLocal, true);
		handPanel.displayHandsOnline(hmHandByAdjustedPlayerNumber);
		handPanel.setInitted(true);
		hasOverbid = false;
		
		if (personToStartLocal == playerNumberLocal)
		{
			requestFocus();
		}
		
		resetGameVariables();
		if (personToStartLocal == playerNumberLocal)
		{
			enableBidPanel(true);
			bidPanel.enableChallenge(false);
		}
		else
		{
			enableBidPanel(false);
		}
	}
	
	public void startObserving(int personToStart, int lastPlayerToAct)
	{
		try
		{
			replay.clear();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
		
		adjustPlayersBasedOnHands();
		btnReplay.setEnabled(false);
		gameInProgress = true;
		lblGameInformation.setVisible(false);
		personToStartLocal = personToStart;
		listmodel.clear();
		lblResult.setText("");
		handPanel.assignAsteriskToStartingPlayer(personToStartLocal);
		handPanel.selectPlayerInAwtThread(personToStartLocal, true);
		handPanel.displayHandsOnline(hmHandByAdjustedPlayerNumber);
		handPanel.setInitted(true);
		
		Bid lastBid = hmBidByPlayerNumber.get(lastPlayerToAct);
		if (lastBid != null)
		{
			handleBid(lastPlayerToAct, lastBid);
		}
	}
	
	public void clearHands()
	{
		hmHandByAdjustedPlayerNumber.clear();
	}
	
	public void handleBid(int playerNumber, Bid bid)
	{	
		int playerNumberAdjusted = adjustForMe(playerNumber);
		Player player = hmPlayerByAdjustedPlayerNumber.get(playerNumberAdjusted);
		if (player != null)
		{
			bid.setPlayer(player);
		}
		
		addBidToBidBox(playerNumberAdjusted, bid);
		handPanel.selectPlayerInAwtThread(playerNumberAdjusted, false);
		
		if (bid.isChallenge()
		  || bid.isIllegal())
		{
			processChallengeOrIllegal();
			return;
		}
		
		lastPlayerToAct = playerNumber;
		lastBid = bid;
		hmBidByPlayerNumber.put(playerNumber, bid);
		
		if (playerNumber != this.playerNumber
		  && !observer)
		{
			bidPanel.adjust(bid);
		}
		
		if (settings.getCardReveal())
		{
			String card = bid.getCardToReveal();
			if (!card.isEmpty())
			{
				handPanel.revealCard(card);
				player.addRevealedCard(card);
				
				ArrayList<String> revealedCards = getOpponentsRevealedCards();
				AchievementsUtil.unlockOmniscient(revealedCards);
			}
		}
		
		selectNextPlayer(playerNumberAdjusted);
	}
	
	private ArrayList<String> getOpponentsRevealedCards()
	{
		ArrayList<String> ret = new ArrayList<>();
		
		for (int i=1; i<MAX_NUMBER_OF_PLAYERS; i++)
		{
			Player player = hmPlayerByAdjustedPlayerNumber.get(i);
			if (player != null
			  && player.isEnabled())
			{
				ret.addAll(player.getRevealedCards());
			}
		}
		
		return ret;
	}
	
	public void startRound(int proposedPersonToStart)
	{
		if (observer)
		{
			seenRoundStart = true;
		}
		
		roundNumber++;
		btnReplay.setEnabled(true);
		adjustPlayersBasedOnHands();
		totalNumberOfCards = getTotalFromHands();
		
		listmodel.clear();
		lblResult.setText("");
		
		personToStart = adjustPersonToStart(proposedPersonToStart);
		personToStartLocal = adjustForMe(personToStart);
		
		handPanel.assignAsteriskToStartingPlayer(personToStartLocal);
		handPanel.selectPlayerInAwtThread(personToStartLocal, true);
		handPanel.setViewCardsVisibility(playBlind && playerIsEnabled(playerNumberLocal));
		handPanel.displayHandsOnline(hmHandByAdjustedPlayerNumber);
		handPanel.setInitted(true);
		
		resetBids();
		
		if (!gameInProgress)
		{
			return;
		}
		
		initBidPanel();
		
		boolean playerTurn = personToStartLocal == playerNumberLocal;
		enableBidPanel(playerTurn);
		if (playerTurn)
		{
			requestFocus();
		}
	}
	
	public void processEndOfGameFromServer(int winningPlayer)
	{
		int winningPlayerAdjusted = adjustForMe(winningPlayer);
		processEndOfGame(winningPlayerAdjusted);
	}
	private void processEndOfGame(int winningPlayerAdjusted)
	{
		gameInProgress = false;
		unlockAchievementsAndSaveReplay(winningPlayerAdjusted);
		
		//Now stand up if the option was selected
		if (chckbxStandUpAfter.isSelected())
		{
			standUp();
			return;
		}
		
		handPanel.resetPlayers(5);
		clearScreenAfterGameEnd();
		
		int currentSize = hmPlayerByAdjustedPlayerNumber.size();
		if (currentSize == players)
		{
			sendGameInitialiseRequest();
		}
	}
	
	private void clearScreenAfterGameEnd()
	{
		enableBidPanel(false);
		btnStandUp.setEnabled(true);
		chckbxStandUpAfter.setEnabled(false);
		waitingForPlayers();
		handPanel.removeFormerPlayers();
		handPanel.selectPlayerInAwtThread(0, false);
		listmodel.clear();
		lblResult.setText("");
	}
	
	private void unlockAchievementsAndSaveReplay(int winningPlayerAdjusted)
	{
		if (roundNumber == 1
		  || observer)
		{
			return;
		}
		
		saveRoundForReplay();
		
		if (winningPlayerAdjusted == ADJUSTED_PLAYER_NUMBER_ME)
		{
			unlockEndOfGameAchievements();
			
			int numberOfRounds = replay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0);
			AchievementsUtil.unlockPerfectGameAchievements(numberOfRounds, players);
			AchievementsUtil.unlockFullBlindGameAchievements(players, playBlind, handPanel.getHasViewedHandThisGame(), settings.getCardReveal());
			AchievementsUtil.unlockPrecision(hasOverbid, numberOfRounds);

			replay.putInt(REPLAY_INT_PLAYER_WON, 1);
		}
		else
		{
			replay.putInt(REPLAY_INT_PLAYER_WON, -1);
		}
		
		replay.putInt(REPLAY_INT_GAME_COMPLETE, 1);
		ReplayFileUtil.saveOnlineReplayToFile(roomName, username);
	}
	
	private int adjustPersonToStart(int proposedPersonToStart)
	{
		int adjustedPersonToStart = adjustForMe(proposedPersonToStart);
		Player playerToStart = hmPlayerByAdjustedPlayerNumber.get(adjustedPersonToStart);
		if (playerToStart.isEnabled())
		{
			return proposedPersonToStart;
		}
		else
		{
			return adjustPersonToStart(GameUtil.getNextPlayer(proposedPersonToStart));
		}
	}
	
	/**
	 * Handles:
	 *  - Enabling/disabling players based on whether they have cards or not
	 *  - Updating the numberOfCards on the Player
	 */
	private void adjustPlayersBasedOnHands()
	{
		Iterator<Map.Entry<Integer, Player>> it = hmPlayerByAdjustedPlayerNumber.entrySet().iterator();
		for (; it.hasNext();)
		{
			Map.Entry<Integer, Player> entry = it.next();
			int playerNumberAdjusted = entry.getKey();
			List<String> hand = hmHandByAdjustedPlayerNumber.get(playerNumberAdjusted);
			
			Player player = entry.getValue();
			boolean enabled = hand != null;
			player.setEnabled(enabled);
			
			if (enabled)
			{
				player.setNumberOfCards(hand.size());
				player.resetHand();
			}
			else if (playerNumberAdjusted == playerNumberLocal)
			{
				AchievementsUtil.updateStreaksForLoss();
			}
		}
	}
	
	private int getTotalFromHands()
	{
		int total = 0;
		
		for (int i=0; i<MAX_NUMBER_OF_PLAYERS; i++)
		{
			List<String> hand = hmHandByAdjustedPlayerNumber.get(i);
			if (hand != null)
			{
				total += hand.size();
			}
		}
		
		return total;
	}
	
	public void setObserver(boolean observer)
	{
		this.observer = observer;
	}
	public void setUsername(String username)
	{
		this.username = username;
		setTitle("Room: " + roomName + " (" + username + ")");
	}
	public void setPlayerNumber(int playerNumber)
	{
		this.playerNumber = playerNumber;
		if (playerNumber > -1)
		{
			playerNumberLocal = ADJUSTED_PLAYER_NUMBER_ME;
		}
		else
		{
			playerNumberLocal = -1;
		}
	}
	public int getRoundNumber()
	{
		return roundNumber;
	}
	public void setRoundNumber(int roundNumber)
	{
		this.roundNumber = roundNumber;
	}
	public String getGameId()
	{
		return gameId;
	}
	public void setGameId(String gameId)
	{
		this.gameId = gameId;
	}
	public DefaultListModel<Bid> getListmodel()
	{
		return listmodel;
	}

	public void saveRoundForReplay()
	{
		try
		{
			if (!replay.nodeExists(""))
			{
				//We've removed the node, so don't bother
				return;
			}
			
			int roundsSoFar = replay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0) + 1;
			replay.putInt(REPLAY_INT_ROUNDS_SO_FAR, roundsSoFar);
			
			//save the listmodel
			int historySize = listmodel.size();
			replay.putInt(roundsSoFar + REPLAY_INT_HISTORY_SIZE, historySize);
			for (int i=0; i<historySize; i++)
			{
				Bid bid = listmodel.get(i);
				replay.put(roundsSoFar + REPLAY_STRING_LISTMODEL + i, bid.toXmlString());
			}
			
			saveHands(roundsSoFar);
			replay.putBoolean(REPLAY_BOOLEAN_PLAY_BLIND, playBlind);
			replay.putBoolean(REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME, hasActedBlindThisGame);
			replay.putBoolean(REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME, handPanel.getHasViewedHandThisGame());
			
			//save non-round-dependent stuff
			handPanel.saveLabels(replay);
			replay.putInt(REPLAY_INT_JOKER_VALUE, settings.getJokerValue());
			replay.putBoolean(REPLAY_BOOLEAN_INCLUDE_MOONS, settings.getIncludeMoons());
			replay.putBoolean(REPLAY_BOOLEAN_INCLUDE_STARS, settings.getIncludeStars());
			replay.put(REPLAY_STRING_ROOM_NAME, getUnindexedRoomName(roomName));
			
			//save who is enabled
			replay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_PLAYER_ENABLED, playerIsEnabled(0));
			replay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_OPPONENT_ONE_ENABLED, playerIsEnabled(1));
			replay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_OPPONENT_TWO_ENABLED, playerIsEnabled(2));
			replay.putBoolean(roundsSoFar + REPLAY_BOOLEAN_OPPONENT_THREE_ENABLED, playerIsEnabled(3));
			
			replay.putInt(roundsSoFar + REPLAY_INT_PERSON_TO_START, personToStartLocal);
			
			chatPanel.saveRecentChat(replay, roundsSoFar);
			
			saveModeSpecificVariablesForReplay();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void saveHands(int roundNumber)
	{
		int playerNumberOfCards = 0;
		int opponentOneNumberOfCards = 0;
		int opponentTwoNumberOfCards = 0;
		int opponentThreeNumberOfCards = 0;
		
		List<String> playerHand = hmHandByAdjustedPlayerNumber.get(ADJUSTED_PLAYER_NUMBER_ME);
		if (playerHand != null)
		{
			playerNumberOfCards = playerHand.size();
		}
		
		List<String> opponentOneHand = hmHandByAdjustedPlayerNumber.get(1);
		if (opponentOneHand != null)
		{
			opponentOneNumberOfCards = opponentOneHand.size();
		}
		
		List<String> opponentTwoHand = hmHandByAdjustedPlayerNumber.get(2);
		if (opponentTwoHand != null)
		{
			opponentTwoNumberOfCards = opponentTwoHand.size();
		}
		
		List<String> opponentThreeHand = hmHandByAdjustedPlayerNumber.get(3);
		if (opponentThreeHand != null)
		{
			opponentThreeNumberOfCards = opponentThreeHand.size();
		}
		
		//save the player hands
		replay.putInt(roundNumber + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, playerNumberOfCards);
		replay.putInt(roundNumber + REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS, opponentOneNumberOfCards);
		replay.putInt(roundNumber + REPLAY_INT_OPPONENT_TWO_NUMBER_OF_CARDS, opponentTwoNumberOfCards);
		replay.putInt(roundNumber + REPLAY_INT_OPPONENT_THREE_NUMBER_OF_CARDS, opponentThreeNumberOfCards);

		for (int i=0; i<playerNumberOfCards; i++)
		{
			replay.put(roundNumber + REPLAY_STRING_PLAYER_HAND + i, playerHand.get(i));
		}
		for (int i=0; i<opponentOneNumberOfCards; i++)
		{
			replay.put(roundNumber + REPLAY_STRING_OPPONENT_ONE_HAND + i, opponentOneHand.get(i));
		}
		for (int i=0; i<opponentTwoNumberOfCards; i++)
		{
			replay.put(roundNumber + REPLAY_STRING_OPPONENT_TWO_HAND + i, opponentTwoHand.get(i));
		}
		for (int i=0; i<opponentThreeNumberOfCards; i++)
		{
			replay.put(roundNumber + REPLAY_STRING_OPPONENT_THREE_HAND + i, opponentThreeHand.get(i));
		}
	}
	
	public void deleteReplayNode()
	{
		try
		{
			replay.removeNode();
		}
		catch (Throwable t)
		{
			logger.error("preferenceError", "Error deleting replay node", t);
		}
	}
	
	public String getUsername()
	{
		return username;
	}
	public boolean getSeenRoundStart()
	{
		return seenRoundStart;
	}
	public OnlineChatPanel getChatPanel()
	{
		return chatPanel;
	}
	
	public void fireAppearancePreferencesChange()
	{
		bidBox.repaint();
		handPanel.fireAppearancePreferencesChange();
		replayDialog.fireAppearancePreferencesChange();
		bidPanel.fireAppearancePreferencesChange();
	}
	
	private static String getUnindexedRoomName(String roomName)
	{
		int index = roomName.indexOf(' ');
		return roomName.substring(0, index);
	}
	
	public void startClosingWindow(boolean showWarning)
	{
		int option = JOptionPane.YES_OPTION;
		if (showWarning 
		  && gameInProgress 
		  && roundNumber > 1
		  && !observer 
		  && playerIsEnabled(playerNumberLocal))
		{
			option = DialogUtil.showQuestion("There is a game in progress. Are you sure you want to leave?", false);
			if (option == JOptionPane.YES_OPTION)
			{
				AchievementsUtil.unlockCoward();
			}
		}
		
		if (option == JOptionPane.YES_OPTION)
		{
			handPanel.cancelTimer();
			ClientGlobals.roomApi.leaveRoom(this);
		}
	}
	
	public void closeWindow()
	{
		if (gameInProgress
		  && roundNumber > 1
		  && !observer)
		{
			saveRoundForReplay();
			ReplayFileUtil.saveOnlineReplayToFile(roomName, username);
		}
		
		deleteReplayNode();
		dispose();
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) 
	{
		startClosingWindow(true);
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		JButton source = (JButton)arg0.getSource();
		if (source == btnReplay)
		{
			viewReplay();
		}
		else if (source == btnStandUp)
		{
			standUp();
		}
	}
	
	private void viewReplay()
	{
		if (replayDialog.isVisible())
		{
			replayDialog.requestFocus();
		}
		else
		{
			replayDialog.setVisible(true);
			replayDialog.initForRoomReplay(this);
		}
	}
	
	private void standUp()
	{
		if (gameInProgress 
		  && roundNumber > 1
		  && !observer 
		  && playerIsEnabled(playerNumberLocal))
		{
			int option = DialogUtil.showQuestion("There is a game in progress. Are you sure you want to leave?", false);
			if (option == JOptionPane.NO_OPTION)
			{
				return;
			}
			
			AchievementsUtil.unlockCoward();
		}

		ClientGlobals.roomApi.standUp(this);
	}
	
	public void waitingForPlayers()
	{
		handPanel.setViewCardsVisibility(false);
		
		lblGameInformation.startPulsing();
		lblGameInformation.setText("Waiting for players...");
		lblGameInformation.setVisible(true);
	}
	public void waitingForCountdown(long timeRemaining)
	{
		//Just to make sure
		enableBidPanel(false);
		btnStandUp.setEnabled(true);
		
		lblGameInformation.setVisible(true);
		lblGameInformation.stopPulsing();
		
		//round to nearest seconds
		double timeSeconds = timeRemaining / (double)1000;
		timeRemaining = Math.round(timeSeconds);
		
		if (timeRemaining >= 1)
		{
			lblGameInformation.setText("New game in " + timeRemaining + "s");
			sendGameInitialiseRequest(500);
		}
		else
		{
			sendGameInitialiseRequest(100);
		}
	}
	
	public boolean bidHistoryContainsBid()
	{
		int size = listmodel.size();
		for (int i=0; i<size; i++)
		{
			Bid bid = listmodel.get(i);
			if (bid instanceof LeftBid)
			{
				continue;
			}
			
			//We've found something other than "X left", so there's been a bid
			return true;
		}
		
		return false;
	}
	
	@Override
	public void requestFocus() 
	{
		boolean popUpRoom = prefs.getBoolean(PREFERENCES_BOOLEAN_POP_UP_ROOMS, true);
		
		if (popUpRoom)
		{
			Component focusedComponent = getFocusOwner();
			super.requestFocus();
			
			//Re-focus where we were within the window, e.g. in the chat panel
			if (focusedComponent != null
			  && focusedComponent != this)
			{
				focusedComponent.requestFocus();
			}
		}
	}
	
	/**
	 * BidListener
	 */
	@Override
	public void bidMade(Bid bid)
	{
		//1. Set the player on the bid
		Player player = hmPlayerByAdjustedPlayerNumber.get(0);
		bid.setPlayer(player);
		lastBid = bid;
		
		//2. Disable the bid panel
		enableBidPanel(false);
		
		//3. If we're revealing cards, wait for one to be chosen
		if (settings.getCardReveal()
		  && player.hasMoreCardsToReveal())
		{
			handPanel.activateRevealListener();
		}
		else
		{
			processPlayerBid();
		}
	}
	
	private void processPlayerBid()
	{
		//4. Fire off a message to the server
		Document bidXml = XmlBuilderClient.factoryBidXml(roomName, getUsername(), getGameId(),
				getRoundNumber(), lastBid, -1);
		
		MessageUtil.sendMessage(bidXml, 0);
		
		//5. Unlock achievements, including specific perfect bid ones
		updateAchievementVariables(lastBid);
		if (lastBid.isPerfect(hmHandByAdjustedPlayerNumber, settings)
		  && lastBid.isOverAchievementThreshold())
		{
			updatePerfectBidVariables(lastBid);
			
			if (handPanel.isPlayingBlind())
			{
				earnedPsychic = true;
			}
		}
		
		boolean overBid = lastBid.isOverbid(hmHandByAdjustedPlayerNumber, settings.getJokerValue());
		if (overBid)
		{
			hasOverbid = true;
		}
	}
	
	@Override
	public void cardRevealed(String card)
	{
		lastBid.setCardToReveal(card);
		processPlayerBid();
	}
	
	@Override
	public void challengeMade() 
	{
		enableBidPanel(false);
		
		Bid bid = new ChallengeBid();
		Player player = hmPlayerByAdjustedPlayerNumber.get(0);
		bid.setPlayer(player);
		
		Document challenge = XmlBuilderClient.factoryBidXml(roomName, getUsername(), getGameId(), 
																  getRoundNumber(), bid, lastPlayerToAct);
		MessageUtil.sendMessage(challenge, 0);
	}
	
	@Override
	public void illegalCalled() 
	{
		enableBidPanel(false);
		
		Bid bid = new IllegalBid();
		Player player = hmPlayerByAdjustedPlayerNumber.get(0);
		bid.setPlayer(player);
		
		Document illegal = XmlBuilderClient.factoryBidXml(roomName, getUsername(), getGameId(),
															  getRoundNumber(), bid, lastPlayerToAct);
		MessageUtil.sendMessage(illegal, 0);
	}

	public String getRoomName() { return roomName; }
	public UUID getId() { return id; }
	public int getJokerValue()
	{
		return settings.getJokerValue();
	}
	public boolean getIncludeMoons()
	{
		return settings.getIncludeMoons();
	}
	public boolean getIncludeStars()
	{
		return settings.getIncludeStars();
	}
}
