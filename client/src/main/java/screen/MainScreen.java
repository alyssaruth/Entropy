package screen;

import achievement.AchievementSetting;
import achievement.AchievementUtilKt;
import bean.AbstractDevScreen;
import game.GameMode;
import game.PlayerAction;
import object.Bid;
import object.BidListCellRenderer;
import object.Player;
import online.screen.EntropyLobby;
import online.screen.TestHarness;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import settings.Setting;
import settings.SettingChangeListener;
import util.*;
import utils.Achievement;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static achievement.AchievementUtilKt.getAchievementsEarned;
import static screen.ScreenCacheKt.IN_GAME_REPLAY;
import static screen.online.PlayOnlineDialogKt.showPlayOnlineDialog;
import static util.ClientGlobals.achievementStore;
import static utils.CoreGlobals.logger;
import static utils.ThreadUtilKt.dumpThreadStacks;

public final class MainScreen extends AbstractDevScreen
							  implements WindowListener, 
							  			 KeyListener, 
							  			 ActionListener,
							  			 Registry
{
	public long startTime = System.currentTimeMillis();
	
	private Timer timerFiveMinutes = null;
	private Timer timerFifteenMinutes = null;
	private Timer timerThirtyMinutes = null;
	private Timer timerSixtyMinutes = null;
	private Timer timerTwoHours = null;

	private boolean bottomAchievementShowing = false;
	private boolean topAchievementShowing = false;

	private boolean isGameToContinue = savedGame.getBoolean(Registry.SAVED_GAME_BOOLEAN_IS_GAME_TO_CONTINUE, false);
	
	private GameMode gameMode = null;
	private ArrayList<Integer> lastTenKeys = new ArrayList<>();

	@Override
	public String getWindowName() {
		return "Entropy";
	}

	public MainScreen()
	{
		super();
		setTitle("Entropy");

		init();

		setFocusable(true);
		addWindowListener(this);
		getContentPane().setLayout(new BorderLayout(0, 0));
		achievementMessageBottom.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		achievementMessageBottom.setBounds(13, 460, 395, 80);
		sidePanel.add(achievementMessageBottom);
		achievementMessageBottom.setLayout(null);
		achievementIconBottom.setBounds(45, 11, 56, 56);
		achievementMessageBottom.add(achievementIconBottom);
		achievementEarnedBottom.setHorizontalAlignment(SwingConstants.CENTER);
		achievementEarnedBottom.setFont(new Font("Tahoma", Font.BOLD, 15));
		achievementEarnedBottom.setBounds(128, 11, 169, 25);
		achievementMessageBottom.add(achievementEarnedBottom);
		achievementTitleBottom.setFont(new Font("Tahoma", Font.BOLD, 12));
		achievementTitleBottom.setHorizontalAlignment(SwingConstants.CENTER);
		achievementTitleBottom.setBounds(128, 43, 169, 20);
		achievementMessageBottom.add(achievementTitleBottom);
		xButtonBottom.setVerticalAlignment(SwingConstants.TOP);
		xButtonBottom.setHorizontalAlignment(SwingConstants.RIGHT);
		xButtonBottom.setFont(new Font("Tahoma", Font.BOLD, 15));
		xButtonBottom.setBounds(368, 5, 16, 18);
		achievementMessageBottom.add(xButtonBottom);
		achievementMessageTop.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		achievementMessageTop.setBounds(13, 373, 395, 80);
		sidePanel.add(achievementMessageTop);
		achievementMessageTop.setLayout(null);
		achievementIconTop.setBounds(45, 11, 56, 56);
		achievementMessageTop.add(achievementIconTop);
		achievementEarnedTop.setHorizontalAlignment(SwingConstants.CENTER);
		achievementEarnedTop.setFont(new Font("Tahoma", Font.BOLD, 15));
		achievementEarnedTop.setBounds(128, 11, 169, 25);
		achievementMessageTop.add(achievementEarnedTop);
		xButtonTop.setFont(new Font("Tahoma", Font.BOLD, 15));
		xButtonTop.setVerticalAlignment(SwingConstants.TOP);
		xButtonTop.setHorizontalAlignment(SwingConstants.RIGHT);
		xButtonTop.setBounds(368, 5, 16, 18);
		achievementMessageTop.add(xButtonTop);
		achievementTitleTop.setHorizontalAlignment(SwingConstants.CENTER);
		achievementTitleTop.setFont(new Font("Tahoma", Font.BOLD, 12));
		achievementTitleTop.setBounds(128, 43, 169, 20);
		achievementMessageTop.add(achievementTitleTop);
		scrollPane.setBounds(115, 72, 194, 144);
		scrollPane.setVisible(false);
		lblBidHistory.setVisible(false);
		btnReplay.setVisible(false);
		getContentPane().add(menuBar, BorderLayout.NORTH);
		menuBar.add(mnFile);
		mntmNewGame.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntmNewGame);
		mnFile.add(mntmContinueGame);
		mnFile.add(mntmPlayOnline);
		mnFile.add(mntmStatistics);
		mnFile.add(mntmClearData);
		mntmContinueGame.setEnabled(isGameToContinue);
		mnFile.add(mntmExit);
		menuBar.add(mnOptions);
		mnOptions.add(mntmPreferences);
		mnOptions.add(mntmViewReplays);
		mnOptions.add(mntmAchievements);
		menuBar.add(mnHelp);
		mnHelp.add(mntmViewHelp);
		mntmViewHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnHelp.add(mntmAbout);
		mnHelp.add(mntmReportBug);
		history.setLocation(0, 119);
		scrollPane.setViewportView(history);
		history.setVisibleRowCount(4);
		scrollPane.setBackground(new Color(0, 0, 0, 0));
		scrollPane.setOpaque(false);
		history.setBackground(new Color(0, 0, 0, 0));
		history.setOpaque(false);
		splitPane.setDividerLocation(622);
		splitPane.setEnabled(false);
		getContentPane().add(splitPane);
		sidePanel.add(scrollPane);
		splitPane.setRightComponent(sidePanel);
		sidePanel.setLayout(null);
		lblBidHistory.setBounds(172, 40, 80, 21);
		sidePanel.add(lblBidHistory);
		lblBidHistory.setHorizontalAlignment(SwingConstants.CENTER);
		lblBidHistory.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnNextRound.setBounds(155, 261, 115, 23);
		sidePanel.add(btnNextRound);
		lblResult.setContentType("text/html");
		lblResult.setBounds(96, 296, 232, 56);
		sidePanel.add(lblResult);
		lblResult.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblResult.setVisible(false);
		lblResult.setOpaque(false);
		lblResult.setBorder(BorderFactory.createEmptyBorder());
		lblResult.setBackground(new Color(0,0,0,0));
		lblResult.setEditable(false);
		lblResult.setFocusable(false);
		btnNextRound.setVisible(false);
		btnReplay.setBounds(116, 215, 192, 23);
		sidePanel.add(btnReplay);
		splitPane.setLeftComponent(leftPanel);
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.add(commandBar, BorderLayout.SOUTH);
		history.setCellRenderer(new BidListCellRenderer());

		addKeyListener(this);
		commandBar.setCheatListener(this);

		//Listeners
		setUpMenuListeners();
		setUpScreenListeners();
	}
	
	//Menu
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnFile = new JMenu("File");
	private final JMenuItem mntmNewGame = new JMenuItem("New Game");
	private final JMenuItem mntmContinueGame = new JMenuItem("Continue Game");
	private final JMenuItem mntmPlayOnline = new JMenuItem("Play Online...");
	private final JMenuItem mntmStatistics = new JMenuItem("Statistics...");
	private final JMenuItem mntmClearData = new JMenuItem("Clear Data...");
	private final JMenuItem mntmExit = new JMenuItem("Exit");
	private final JMenu mnOptions = new JMenu("Tools");
	private final JMenuItem mntmPreferences = new JMenuItem("Preferences...");
	private final JMenuItem mntmViewReplays = new JMenuItem("View Replays...");
	private final JMenuItem mntmAchievements = new JMenuItem("Achievements...");
	private final JMenu mnHelp = new JMenu("Help");
	private final JMenuItem mntmViewHelp = new JMenuItem("View Help...");
	private final JMenuItem mntmAbout = new JMenuItem("About...");
	private final JMenuItem mntmReportBug = new JMenuItem("Send Bug Report");
	private final JMenuItem mntmViewLogs = new JMenuItem("View logs...");
	
	//Screen
	private final  DefaultListModel<PlayerAction> listmodel = new DefaultListModel<>();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JList<PlayerAction> history = new JList<>(listmodel);
	private final JLabel lblBidHistory = new JLabel("Bid History");
	private final JButton btnNextRound = new JButton("Next");
	private final JTextPane lblResult = new JTextPane();
	private final JButton btnReplay = new JButton("Replay");
	private final JPanel achievementMessageBottom = new JPanel();
	private final JLabel achievementIconBottom = new JLabel("New label");
	private final JLabel achievementEarnedBottom = new JLabel("Achievement Earned!");
	private final JLabel achievementTitleBottom = new JLabel("New label");
	private final JLabel xButtonBottom = new JLabel("X");
	private final JPanel achievementMessageTop = new JPanel();
	private final JLabel achievementIconTop = new JLabel("New label");
	private final JLabel achievementEarnedTop = new JLabel("Achievement Earned!");
	private final JLabel achievementTitleTop = new JLabel("New label");
	private final JLabel xButtonTop = new JLabel("X");
	private final BackgroundPanel sidePanel = new BackgroundPanel();
	private final JSplitPane splitPane = new JSplitPane();
	private final BackgroundPanel leftPanel = new BackgroundPanel();
	
	//Non-final as this changes to EntropyScreen/VectropyScreen based on the setting
	public GameScreen gamePanel = null;
	
	private void init()
	{
		setIcon();
		setBackground();
		showBottomAchievementPanel(false);
		showTopAchievementPanel(false);
	}
	
	private void setIcon()
	{
		String imageStr = "entropy";
		
		//Load the four images corresponding to 16px, 32px, 64px and 128px
		ArrayList<Image> images = new ArrayList<>();
		for (int i=16; i<256; i=2*i)
		{
			Image ico = new ImageIcon(getClass().getResource("/icons/" + imageStr + i + ".png")).getImage();
			images.add(ico);
		}
		
		setIconImages(images);
	}
	
	private void setBackground()
	{
		//do nothing, for now...
	}
	
	public void startNewGame()
	{	
		try
		{	
			if (overwriteSavedGame() && quitCurrentGame())
			{
				gameMode = GameMode.valueOf(prefs.get(Registry.PREFERENCES_STRING_GAME_MODE, GameMode.Entropy.name()));
				selectGameScreen(gameMode);
				lblBidHistory.setVisible(true);
				btnReplay.setVisible(true);
				scrollPane.setVisible(true);
				btnReplay.setEnabled(false);
				btnNextRound.setVisible(false);
				lblResult.setVisible(false);
				gamePanel.startNewGame();
			}
		}
		catch (Throwable e)
		{
			Debug.stackTrace(e);
		}
	}
	
	private boolean overwriteSavedGame()
	{
		if (isGameToContinue && savedGame.getBoolean(Registry.SAVED_GAME_BOOLEAN_PLAYER_ENABLED, false))
		{
			try
			{
				int dialogButton = DialogUtilNew.showQuestion("Are you sure you wish to start a new game?"
						+ "\nThis will wipe the currently saved game and count it as a loss in your statistics.", false);

				if (dialogButton == JOptionPane.YES_OPTION)
				{
					isGameToContinue = false;
					try 
					{
						savedGame.clear();
					} 
					catch (BackingStoreException e) 
					{
						Debug.stackTrace(e);
					}

					mntmContinueGame.setEnabled(false);
					AchievementsUtil.updateStreaksForLoss();
					AchievementsUtil.recordGamePlayed(gameMode);
					ReplayFileUtil.saveInGameReplayToFile();
					return true;
				}
				else
				{
					return false;
				}
			}
			catch (Throwable t)
			{
				Debug.stackTrace(t);
				return false;
			}
		}

		return true;
	}
	
	private boolean quitCurrentGame()
	{
		try
		{
			// can't just check playerEnabled, as this only gets set when you press 'Next'. 
			Player player = gamePanel.getPlayer();
			
			int playerNumberOfCards = 0;
			if (player != null)
			{
				playerNumberOfCards = player.getActualNumberOfCards();
			}
			
			if (!gamePanel.gameOver && !gamePanel.firstRound)
			{
				if (playerNumberOfCards != 0)
				{
					return confirmQuit();
				}
				else
				{
					ReplayFileUtil.saveInGameReplayToFile();
					return true;
				}
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
			return false;
		}

		return true;
	}
	
	private boolean confirmQuit() throws Throwable
	{
		int dialogButton = DialogUtilNew.showQuestion("Are you sure you wish to quit the current game?"
										+ "\nThis will count as a loss in your statistics.", false);

		if (dialogButton == JOptionPane.YES_OPTION)
		{
			AchievementsUtil.updateStreaksForLoss();
			AchievementsUtil.recordGamePlayed(gameMode);
			ReplayFileUtil.saveInGameReplayToFile();
			return true;
		}
		else
		{
			return false;
		}
	}

	public void startNewRound() 
	{
		btnReplay.setEnabled(true);
		gamePanel.startNewRound();
	}
	
	public void setResultText(String text)
	{
		lblResult.setVisible(true);
		lblResult.setText("<font face=\"Tahoma\" size=\"5\">" + text + "</font>");
		StyledDocument doc = lblResult.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}
	
	public void hideResult()
	{
		lblResult.setVisible(false);
	}
	
	public String getResultText()
	{
		return lblResult.getText();
	}
	
	public void showNextRoundButton()
	{
		btnNextRound.setVisible(true);
	}
	
	public boolean isNextRoundVisible()
	{
		return btnNextRound.isVisible();
	}
	
	public void fireAppearancePreferencesChange()
	{
		try
		{
			history.repaint();
			gamePanel.fireAppearancePreferencesChange();
			ScreenCache.get(HelpDialog.class).fireAppearancePreferencesChange();
			ScreenCache.getReplayDialogs().forEach(ReplayDialog::fireAppearancePreferencesChange);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}

	private void doStuffOnExit()
	{
		try
		{
			//online stuff
			EntropyLobby entropyLobby = ScreenCache.get(EntropyLobby.class);
			if (entropyLobby.isVisible())
			{
				if (!entropyLobby.confirmExit())
				{
					return;
				}
				else
				{
					entropyLobby.exit(false);
				}
			}
			
			long currentTimePlayed = achievementStore.get(AchievementSetting.TimePlayed);
			long timePlayedThisSession = System.currentTimeMillis() - startTime;
			achievementStore.save(AchievementSetting.TimePlayed, currentTimePlayed + timePlayedThisSession);
			
			if (!gamePanel.gameOver && !gamePanel.firstRound)
			{
				Player player = gamePanel.getPlayer();
				int playerNumberOfCards = player.getActualNumberOfCards();
				
				if (playerNumberOfCards > 0)
				{
					//the player is still alive, so save the game
					saveGame();
				}
				else
				{
					//the player is out but the game isn't over, so save the replay and discard the game
					ReplayFileUtil.saveInGameReplayToFile();
				}
			}
			
			System.exit(0);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void saveGame() throws Throwable
	{
		boolean autosave = prefs.getBoolean(Registry.PREFERENCES_BOOLEAN_AUTOSAVE, false);

		if (autosave)
		{
			gamePanel.saveGame();
		}
		else
		{
			int dialogButton = DialogUtilNew.showQuestion("Do you want to save this game to continue next time?"
					+ "\nAnswering no will count as a loss in your statistics.", false);

			if (dialogButton == JOptionPane.YES_OPTION)
			{
				gamePanel.saveGame();
			}
			else
			{
				AchievementsUtil.setCowardToBeUnlocked();
				AchievementsUtil.updateStreaksForLoss();
				ReplayFileUtil.saveInGameReplayToFile();
			}
		}
	}

	public void continueGame()
	{
		try
		{
			gameMode = GameMode.valueOf(savedGame.get(Registry.SAVED_GAME_STRING_GAME_MODE, null));
			selectGameScreen(gameMode);
			
			lblBidHistory.setVisible(true);
			btnReplay.setVisible(true);
			scrollPane.setVisible(true);
			mntmContinueGame.setEnabled(false);
			gamePanel.continueGame();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
			DialogUtilNew.showError("A serious error occurred retrieving your game.");
		}
	}
	
	private void connectToEntropyOnline()
	{
		EntropyLobby entropyLobby = ScreenCache.get(EntropyLobby.class);
		if (entropyLobby.isVisible())
		{
			entropyLobby.requestFocus();
		}
		else
		{
			showPlayOnlineDialog();
		}
	}
	
	public void dismissCurrentReplay()
	{
		ScreenCache.getReplayDialog(IN_GAME_REPLAY).dispose();
	}
	
	public void enableNewGameOption(boolean enable)
	{
		mntmNewGame.setEnabled(enable);
	}
	
	public void resetStartTime()
	{
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Used to kick the timers off initially, as well as to restart them if the statistics are wiped
	 */
	private void restartTimers()
	{
		restartTimer(Achievement.Sluggish, timerFiveMinutes, 5);
		restartTimer(Achievement.WarmingUp, timerFifteenMinutes, 15);
		restartTimer(Achievement.BreakingASweat, timerThirtyMinutes, 30);
		restartTimer(Achievement.WorldClass, timerSixtyMinutes, 60);
		restartTimer(Achievement.RecordBreaker, timerTwoHours, 120);
	}
	
	private void restartTimer(Achievement achievement, Timer timer, int minutes)
	{
		if (timer != null)
		{
			timer.cancel();
		}

		timer = new Timer("Timer-" + minutes);

		var timePlayed = achievementStore.get(AchievementSetting.TimePlayed);
		TimerTask task = new AchievementsUtil.UnlockAchievementTask(achievement);
		timer.schedule(task, Math.max(60000L * minutes - timePlayed, 0));
	}

	public void showBottomAchievementPanel(boolean visible)
	{
		achievementMessageBottom.setVisible(visible);
		achievementEarnedBottom.setVisible(visible);
		achievementIconBottom.setVisible(visible);
		achievementTitleBottom.setVisible(visible);
		xButtonBottom.setVisible(visible);
		bottomAchievementShowing = visible;
	}

	public void showTopAchievementPanel(boolean visible)
	{
		achievementMessageTop.setVisible(visible);
		achievementEarnedTop.setVisible(visible);
		achievementIconTop.setVisible(visible);
		achievementTitleTop.setVisible(visible);
		xButtonTop.setVisible(visible);
		topAchievementShowing = visible;
	}

	public void showAchievementPopup(String title, ImageIcon icon)
	{
		if (!bottomAchievementShowing)
		{
			showBottomAchievementPanel(true);
			achievementIconBottom.setIcon(icon);
			achievementTitleBottom.setText(title);
		}
		else if (!topAchievementShowing)
		{
			showTopAchievementPanel(true);
			achievementIconTop.setIcon(icon);
			achievementTitleTop.setText(title);
		}
		else
		{
			showBottomAchievementPanel(true);
			achievementIconBottom.setIcon(icon);
			achievementTitleBottom.setText(title);
			topAchievementShowing = false;
		}
	}
	
	public void minimise()
	{
		setState(Frame.ICONIFIED);
	}
	
	public void maximise()
	{
		setState(Frame.NORMAL);
	}
	
	private void selectGameScreen(GameMode gameMode)
	{
		switch (gameMode)
		{
			case Entropy:
				gamePanel = ScreenCache.get(EntropyScreen.class);
				break;
			case Vectropy:
				gamePanel = ScreenCache.get(VectropyScreen.class);
				break;
			default:
				Debug.stackTrace("Unexpected gameMode [" + gameMode + "]");
		}
		
		//rebuild the leftPanel
		leftPanel.removeAll();
		splitPane.setLeftComponent(leftPanel);
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.add(gamePanel, BorderLayout.CENTER);
		leftPanel.add(commandBar, BorderLayout.SOUTH);
		splitPane.setDividerLocation(622);
	}
	
	@Override
	public boolean commandsEnabled()
	{
		return ClientUtil.devMode
		  || rewards.getBoolean(Registry.REWARDS_BOOLEAN_CHEATS, false);
	}
	
	@Override
	public String processCommand(String command)
	{
		String textToShow = "";
		if (ClientUtil.devMode)
		{
			boolean processed = processDevModeCommand(command);
			if (processed)
			{
				return textToShow;
			}
		}

		if (command.equals("health")) {
			ClientGlobals.INSTANCE.getHealthCheckApi().doHealthCheck();
		} else if (command.startsWith("server ")) {
			var serverCommand = command.replace("server ", "");
			ClientGlobals.INSTANCE.getDevApi().doServerCommand(serverCommand);
		} else if (command.equals("keygen")) {
			var key = KeyGeneratorUtil.generateSymmetricKey();
			textToShow = EncryptionUtil.convertSecretKeyToString(key);
		}
		else if (command.equals("simulator"))
		{
			SimulationDialog dialog = ScreenCache.get(SimulationDialog.class);
			dialog.initVariables();
			dialog.setTitle("Simulation Window");
			dialog.setSize(440, 500);
			dialog.setLocationRelativeTo(null);
			dialog.setResizable(false);
			dialog.setModal(true);
			dialog.setVisible(true);
		}
		else if (command.equals("bluescreenofdeath"))
		{
			AchievementsUtil.unlockBlueScreenOfDeath();
		}
		else 
		{
			textToShow = gamePanel.processCommand(command);
		}
		
		return textToShow;
	}
	
	private boolean processDevModeCommand(String command)
	{
		if (command.startsWith("delay "))
		{
			int spaceIndex = command.indexOf(' ');
			int millisDelay = Integer.parseInt(command.substring(spaceIndex+1));
			
			MessageUtil.millisDelay = millisDelay;
			return true;
		}
		else if (command.equals("harness"))
		{
			TestHarness harness = new TestHarness();
			harness.setLocationRelativeTo(this);
			harness.setVisible(true);
		}
		else if (command.equals("stacktrace"))
		{
			Debug.stackTrace(new Throwable());
		}
		else if (command.startsWith("unlock "))
		{
			int spaceIndex = command.indexOf(' ');
			int achievements = Integer.parseInt(command.substring(spaceIndex+1));
			AchievementsUtil.unlockRewards(achievements);
		}
		else if (command.equals("stacks"))
		{
			dumpThreadStacks();
		}
		
		return false;
	}
	
	public void onStart()
	{
		gamePanel = ScreenCache.get(EntropyScreen.class);
		leftPanel.add(gamePanel, BorderLayout.CENTER);
		
		//If we've just updated, show the change log automatically
		if (ClientUtil.justUpdated)
		{
			ChangeLog dialog = new ChangeLog();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
		
		//Trigger a replay conversion if we need to
		ReplayConverter.startReplayConversionIfNecessary();
		
		checkForCoward();
		cleanUpReplayNodes();
		setViewLogsVisibility();
		restartTimers();

		AchievementsUtil.unlockRewards(getAchievementsEarned());
	}
	
	private void cleanUpReplayNodes()
	{
		try
		{
			String[] nodes = Preferences.userRoot().childrenNames();
			int size = nodes.length;
			
			for (int i=0; i<size; i++)
			{
				String node = nodes[i];
				if (node.startsWith(Registry.NODE_ONLINE_REPLAY))
				{
					Preferences replay = Preferences.userRoot().node(node);
					replay.removeNode();
				}
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void setViewLogsVisibility()
	{
		mntmViewLogs.setAccelerator(KeyStroke.getKeyStroke('L', InputEvent.CTRL_DOWN_MASK));
		mnHelp.add(mntmViewLogs);
	}

	public void exitApplication()
	{	
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	private void checkForCoward()
	{
		boolean gotCoward = achievementStore.get(AchievementSetting.WillUnlockCoward);
		if (gotCoward)
		{
			achievementStore.delete(AchievementSetting.WillUnlockCoward);
			AchievementsUtil.unlockCoward();
		}
	}
	
	public DefaultListModel<PlayerAction> getListmodel()
	{
		return listmodel;
	}

	private void setUpMenuListeners()
	{
		mntmViewHelp.addActionListener(this);
		mntmAbout.addActionListener(this);
		mntmReportBug.addActionListener(this);
		mntmViewLogs.addActionListener(this);
		mntmPreferences.addActionListener(this);
		mntmAchievements.addActionListener(this);
		mntmViewReplays.addActionListener(this);
		mntmNewGame.addActionListener(this);
		mntmContinueGame.addActionListener(this);
		mntmPlayOnline.addActionListener(this);
		mntmStatistics.addActionListener(this);
		mntmClearData.addActionListener(this);
		mntmExit.addActionListener(this);
	}

	private void setUpScreenListeners()
	{
		btnNextRound.addActionListener(this);
		btnReplay.addActionListener(this);
		
		xButtonBottom.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				showBottomAchievementPanel(false);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
		xButtonTop.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				showTopAchievementPanel(false);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});

		achievementStore.addChangeListener(new SettingChangeListener() {
			@Override
			public <T> void settingChanged(@NotNull Setting<T> setting, @Nullable T newValue) {
				if (setting.equals(AchievementSetting.TimePlayed)) {
					restartTimers();
				}
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == mntmViewHelp)
		{
			HelpDialog helpDialog = ScreenCache.get(HelpDialog.class);
			helpDialog.initVariables();
			helpDialog.setLocationRelativeTo(null);
			helpDialog.setVisible(true);
		}
		else if (source == mntmAbout)
		{
			AboutDialogEntropy aboutDialog = new AboutDialogEntropy();
			aboutDialog.setLocationRelativeTo(null);
			aboutDialog.setModal(true);
			aboutDialog.setVisible(true);
		}
		else if (source == mntmReportBug)
		{
			BugReportDialog bugReportDialog = new BugReportDialog();
			if (!bugReportDialog.isVisible())
			{
				bugReportDialog.setLocationRelativeTo(null);
				bugReportDialog.setVisible(true);
				bugReportDialog.init();
			}
			else
			{
				bugReportDialog.toFront();
			}
		}
		else if (source == mntmViewLogs)
		{
			ClientGlobals.loggingConsole.setVisible(true);
			ClientGlobals.loggingConsole.toFront();
		}
		else if (source == mntmPreferences)
		{
			PreferencesDialog dialog = new PreferencesDialog();
			dialog.setTitle("Preferences");
			dialog.setSize(440, 520);
			dialog.setLocationRelativeTo(null);
			dialog.setResizable(false);
			dialog.setModal(true);
			dialog.setVisible(true);
		}
		else if (source == mntmAchievements)
		{
			AchievementUtilKt.updateAndUnlockVanity();
			
			AchievementsDialog achievementsDialog = ScreenCache.get(AchievementsDialog.class);
			achievementsDialog.setLocationRelativeTo(null);
			achievementsDialog.init();
			achievementsDialog.setVisible(true);
			achievementsDialog.setResizable(false);
		}
		else if (source == mntmViewReplays)
		{
			int width = prefs.getInt(Registry.PREFERENCES_INT_REPLAY_VIEWER_WIDTH, 875);
			int height = prefs.getInt(Registry.PREFERENCES_INT_REPLAY_VIEWER_HEIGHT, 475);
			
			ReplayInterface replayInterface = ScreenCache.get(ReplayInterface.class);
			replayInterface.setTitle("Replay Viewer");
			replayInterface.setSize(width, height);
			replayInterface.setLocationRelativeTo(null);
			replayInterface.init();
			replayInterface.setVisible(true);
		}
		else if (source == mntmNewGame)
		{
			startNewGame();
		}
		else if (source == mntmContinueGame)
		{
			continueGame();
		}
		else if (source == mntmPlayOnline)
		{
			connectToEntropyOnline();
		}
		else if (source == mntmStatistics)
		{
			AchievementUtilKt.updateAndUnlockVanity();
			
			StatisticsDialog dialog = new StatisticsDialog();
			dialog.setTitle("Statistics");
			dialog.setLocationRelativeTo(null);
			dialog.setResizable(false);
			dialog.setModal(true);
			dialog.setVisible(true);
		}
		else if (source == mntmClearData)
		{
			ClearDataDialog dialog = new ClearDataDialog();
			dialog.setTitle("Clear Data");
			dialog.setSize(220, 220);
			dialog.setLocationRelativeTo(null);
			dialog.setResizable(false);
			dialog.setModal(true);
			dialog.setVisible(true);
		}
		else if (source == mntmExit)
		{
			exitApplication();
		}
		else if (source == btnNextRound)
		{
			btnNextRound.setVisible(false);
			lblResult.setVisible(false);
			startNewRound();
		}
		else if (source == btnReplay)
		{
			ReplayDialog replayDialog = ScreenCache.getReplayDialog(IN_GAME_REPLAY);
			if (replayDialog.isVisible())
			{
				replayDialog.requestFocus();
			}
			else
			{
				replayDialog.setVisible(true);
				replayDialog.initForInGameReplay();
			}
		}
	}

	/**
	 * WindowListener
	 */
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) 
	{
		doStuffOnExit();
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
	public void keyPressed(KeyEvent arg0) 
	{
		int keyCode = arg0.getKeyCode();
		
		synchronized(lastTenKeys)
		{
			lastTenKeys.add(keyCode);
			if (lastTenKeys.size() > 10)
			{
				lastTenKeys.remove(0);
			}
			
			if (AchievementsUtil.hasEnteredKonamiCode(lastTenKeys))
			{
				logger.info("konami", "Entered konami code!");
				AchievementsUtil.unlockKonamiCode();
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
}