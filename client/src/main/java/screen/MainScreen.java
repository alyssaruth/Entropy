package screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import object.Bid;
import object.BidListCellRenderer;
import object.Player;
import online.screen.EntropyLobby;
import online.screen.TestHarness;
import online.util.XmlBuilderDesktop;

import org.w3c.dom.Document;

import util.AbstractClient;
import util.AchievementsUtil;
import util.CoreRegistry;
import util.Debug;
import util.DialogUtil;
import util.GameConstants;
import util.MessageUtil;
import util.OnlineConstants;
import util.Registry;
import util.ReplayConverter;
import util.ReplayFileUtil;
import bean.AbstractDevScreen;

import static utils.ThreadUtilKt.dumpThreadStacks;

public final class MainScreen extends AbstractDevScreen
							  implements WindowListener, 
							  			 KeyListener, 
							  			 ActionListener,
							  			 Registry
{
	public double startTime = System.currentTimeMillis();
	
	private Timer timerFiveMinutes = null;
	private Timer timerFifteenMinutes = null;
	private Timer timerThirtyMinutes = null;
	private Timer timerSixtyMinutes = null;
	private Timer timerTwoHours = null;

	private boolean bottomAchievementShowing = false;
	private boolean topAchievementShowing = false;

	private boolean isGameToContinue = savedGame.getBoolean(Registry.SAVED_GAME_BOOLEAN_IS_GAME_TO_CONTINUE, false);
	
	private int gameMode = -1;
	private ArrayList<Integer> lastTenKeys = new ArrayList<>();

	public MainScreen()
	{
		super();
		setTitle("Entropy");
		
		try
		{
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
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}

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
	private final  DefaultListModel<Bid> listmodel = new DefaultListModel<>();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JList<Bid> history = new JList<>(listmodel);
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
				gameMode = prefs.getInt(Registry.PREFERENCES_INT_GAME_MODE, GameConstants.GAME_MODE_ENTROPY);
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
				int dialogButton = DialogUtil.showQuestion("Are you sure you wish to start a new game?"
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
		int dialogButton = DialogUtil.showQuestion("Are you sure you wish to quit the current game?"
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
			ScreenCache.getHelpDialog().fireAppearancePreferencesChange();
			ScreenCache.getReplayDialog().fireAppearancePreferencesChange();
			ScreenCache.getFileReplayDialog().fireAppearancePreferencesChange();
			ScreenCache.getEntropyLobby().fireAppearancePreferencesChange();
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
			EntropyLobby entropyLobby = ScreenCache.getEntropyLobby();
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
			
			double currentTimePlayed = achievements.getDouble(Registry.STATISTICS_DOUBLE_TIME_PLAYED, 0);
			double timePlayedThisSession = System.currentTimeMillis() - startTime;
			achievements.putDouble(Registry.STATISTICS_DOUBLE_TIME_PLAYED, currentTimePlayed + timePlayedThisSession);
			
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
			int dialogButton = DialogUtil.showQuestion("Do you want to save this game to continue next time?"
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
			gameMode = savedGame.getInt(Registry.SAVED_GAME_INT_GAME_MODE, -1);
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
			DialogUtil.showError("A serious error occurred retrieving your game.");
		}
	}
	
	private void connectToEntropyOnline()
	{
		EntropyLobby entropyLobby = ScreenCache.getEntropyLobby();
		if (entropyLobby.isVisible())
		{
			entropyLobby.requestFocus();
		}
		else
		{
			ScreenCache.showLoginDialog();
		}
	}
	
	public void dismissCurrentReplay()
	{
		ScreenCache.getReplayDialog().dispose();
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
		restartTimer(ACHIEVEMENTS_BOOLEAN_FIVE_MINUTES, timerFiveMinutes, 5);
		restartTimer(ACHIEVEMENTS_BOOLEAN_FIFTEEN_MINUTES, timerFifteenMinutes, 15);
		restartTimer(ACHIEVEMENTS_BOOLEAN_THIRTY_MINUTES, timerThirtyMinutes, 30);
		restartTimer(ACHIEVEMENTS_BOOLEAN_SIXTY_MINUTES, timerSixtyMinutes, 60);
		restartTimer(ACHIEVEMENTS_BOOLEAN_TWO_HOURS, timerTwoHours, 120);
	}
	
	private void restartTimer(String registryLocation, Timer timer, int minutes)
	{
		if (!achievements.getBoolean(registryLocation, false))
		{
			if (timer != null)
			{
				timer.cancel();
			}
			
			timer = new Timer("Timer-" + minutes);
			
			TimerTask task = new AchievementsUtil.UnlockAchievementTask(registryLocation);
			timer.schedule(task, (long) Math.max(60000*minutes - achievements.getDouble(Registry.STATISTICS_DOUBLE_TIME_PLAYED, 0), 0));
		}
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
		AchievementsDialog achievementsDialog = ScreenCache.getAchievementsDialog();
		achievementsDialog.refresh(false);
		int achievementsEarned = achievementsDialog.getAchievementsEarned();
		
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
		
		AchievementsUtil.unlockRewards(achievementsEarned);
		
		//Send an update to the Server if we're connected
		EntropyLobby entropyLobby = ScreenCache.getEntropyLobby();
		if (entropyLobby.isVisible())
		{
			String username = entropyLobby.getUsername();
			Document achievementsUpdate = XmlBuilderDesktop.factoryAchievementsUpdate(username, title, achievementsEarned);
			MessageUtil.sendMessage(achievementsUpdate, 500);
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
	
	private void selectGameScreen(int gameMode)
	{
		switch (gameMode)
		{
			case GameConstants.GAME_MODE_ENTROPY:
				gamePanel = ScreenCache.getEntropyPanel();
				break;
			case GameConstants.GAME_MODE_VECTROPY:
				gamePanel = ScreenCache.getVectropyPanel();
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
		return AbstractClient.devMode
		  || rewards.getBoolean(Registry.REWARDS_BOOLEAN_CHEATS, false);
	}
	
	@Override
	public String processCommand(String command)
	{
		String textToShow = "";
		if (AbstractClient.devMode)
		{
			boolean processed = processDevModeCommand(command);
			if (processed)
			{
				return textToShow;
			}
		}
		
		if (command.equals("sendlogs"))
		{
			Debug.sendContentsAsEmailInSeparateThread("Manual logs (" + OnlineConstants.ENTROPY_VERSION_NUMBER + ")", true, null);
		}
		else if (command.equals("emailsoff"))
		{
			Debug.append("Emails disabled");
			instance.putBoolean(CoreRegistry.INSTANCE_BOOLEAN_ENABLE_EMAILS, false);
		}
		else if (command.equals("emailson"))
		{
			Debug.append("Emails enabled");
			instance.putBoolean(CoreRegistry.INSTANCE_BOOLEAN_ENABLE_EMAILS, true);
		}
		else if (command.equals("simulator"))
		{
			SimulationDialog dialog = ScreenCache.getSimulationDialog();
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
		if (command.startsWith("loadtest"))
		{
			LoadTesterDialog loadTesterDialog = ScreenCache.getLoadTesterDialog();
			loadTesterDialog.setLocationRelativeTo(null);
			loadTesterDialog.init();
			loadTesterDialog.setVisible(true);
		}
		else if (command.startsWith("delay "))
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
		gamePanel = ScreenCache.getEntropyPanel();
		leftPanel.add(gamePanel, BorderLayout.CENTER);
		
		//If we've just updated, show the change log automatically
		if (AbstractClient.justUpdated)
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
		
		int achievementsEarned = ScreenCache.getAchievementsDialog().getAchievementsEarned();
		AchievementsUtil.unlockRewards(achievementsEarned);
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
					Debug.appendWithoutDate("Removed replay node " + node);
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
		if (AbstractClient.devMode)
		{
			mntmViewLogs.setAccelerator(KeyStroke.getKeyStroke('L', InputEvent.CTRL_DOWN_MASK));
			mnHelp.add(mntmViewLogs);
		}
	}

	public void exitApplication()
	{	
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	private void checkForCoward()
	{
		boolean gotCoward = achievements.getBoolean(Registry.ACHIEVEMENTS_BOOLEAN_WILL_UNLOCK_COWARD, false);
		if (gotCoward)
		{
			achievements.remove(Registry.ACHIEVEMENTS_BOOLEAN_WILL_UNLOCK_COWARD);
			AchievementsUtil.unlockCoward();
		}
	}
	
	public DefaultListModel<Bid> getListmodel()
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
		
		achievements.addPreferenceChangeListener(new PreferenceChangeListener()
		{
			@Override
			public void preferenceChange(PreferenceChangeEvent arg0) 
			{
				String keyChanged = arg0.getKey();
				if (keyChanged.equals(Registry.STATISTICS_DOUBLE_TIME_PLAYED))
				{
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
			HelpDialog helpDialog = ScreenCache.getHelpDialog();
			helpDialog.initVariables();
			helpDialog.setLocationRelativeTo(null);
			helpDialog.setVisible(true);
		}
		else if (source == mntmAbout)
		{
			AboutDialogEntropy aboutDialog = ScreenCache.getAboutDialog();
			aboutDialog.setLocationRelativeTo(null);
			aboutDialog.setModal(true);
			aboutDialog.setVisible(true);
		}
		else if (source == mntmReportBug)
		{
			BugReportDialog bugReportDialog = ScreenCache.getBugReportDialog();
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
			DebugConsole loggingDialog = ScreenCache.getDebugConsole();
			loggingDialog.setTitle("Console");
			loggingDialog.setSize(1000, 600);
			loggingDialog.setLocationRelativeTo(null);
			loggingDialog.setVisible(true);
			loggingDialog.toFront();
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
			AchievementsUtil.updateAndUnlockVanity();
			
			AchievementsDialog achievementsDialog = ScreenCache.getAchievementsDialog();
			achievementsDialog.setLocationRelativeTo(null);
			achievementsDialog.init();
			achievementsDialog.setVisible(true);
			achievementsDialog.setResizable(false);
		}
		else if (source == mntmViewReplays)
		{
			int width = prefs.getInt(Registry.PREFERENCES_INT_REPLAY_VIEWER_WIDTH, 875);
			int height = prefs.getInt(Registry.PREFERENCES_INT_REPLAY_VIEWER_HEIGHT, 475);
			
			ReplayInterface replayInterface = ScreenCache.getReplayInterface();
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
			AchievementsUtil.updateAndUnlockVanity();
			
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
			ReplayDialog replayDialog = ScreenCache.getReplayDialog();
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
				Debug.append("Entered konami code!");
				AchievementsUtil.unlockKonamiCode();
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
}