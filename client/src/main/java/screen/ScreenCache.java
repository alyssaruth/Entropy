package screen;

import online.screen.ChangePasswordDialog;
import online.screen.ConnectingDialog;
import online.screen.EntropyLobby;
import online.screen.Leaderboard;

public final class ScreenCache 
{
	private static MainScreen mainScreen = null;
	private static EntropyScreen entropyPanel = null;
	private static VectropyScreen vectropyPanel = null;
	private static AchievementsDialog achievementsDialog = null;
	private static ReplayInterface replayInterface = null;
	private static AboutDialogEntropy aboutDialog = null;
	private static HelpDialog helpDialog = null;
	private static ReplayDialog replayDialog = null;
	private static ReplayDialog fileReplayDialog = null;
	private static BugReportDialog bugReportDialog = null;
	private static SimulationDialog simulationDialog = null;
	private static ApiAmendDialog apiAmendDialog = null;
	
	//Dev mode
	private static LoadTesterDialog loadTesterDialog = null;
	
	//Online
	private static EntropyLobby entropyLobby = null;
	private static Leaderboard leaderboard = null;
	private static ConnectingDialog connectingDialog = new ConnectingDialog();
	private static ChangePasswordDialog changePasswordDialog = null;
	
	public static MainScreen getMainScreen()
	{
		if (mainScreen == null)
		{
			mainScreen = new MainScreen();
		}
		
		return mainScreen;
	}
	
	public static AchievementsDialog getAchievementsDialog()
	{
		if (achievementsDialog == null)
		{
			achievementsDialog = new AchievementsDialog();
		}
		return achievementsDialog;
	}
	
	public static ReplayInterface getReplayInterface()
	{
		if (replayInterface == null)
		{
			replayInterface = new ReplayInterface();
		}
		return replayInterface;
	}

	public static AboutDialogEntropy getAboutDialog() 
	{
		if (aboutDialog == null)
		{
			aboutDialog = new AboutDialogEntropy();
		}
		return aboutDialog;
	}

	public static HelpDialog getHelpDialog() 
	{
		if (helpDialog == null)
		{
			helpDialog = new HelpDialog();
		}
		return helpDialog;
	}

	public static EntropyLobby getEntropyLobby() 
	{
		if (entropyLobby == null)
		{
			entropyLobby = new EntropyLobby();
		}
		return entropyLobby;
	}

	public static Leaderboard getLeaderboard() 
	{
		if (leaderboard == null)
		{
			leaderboard = new Leaderboard();
		}
		return leaderboard;
	}
	
	public static ReplayDialog getReplayDialog() 
	{
		if (replayDialog == null)
		{
			replayDialog = new ReplayDialog();
		}
		return replayDialog;
	}
	
	public static ReplayDialog getFileReplayDialog()
	{
		if (fileReplayDialog == null)
		{
			fileReplayDialog = new ReplayDialog();
		}
		return fileReplayDialog;
	}

	public static BugReportDialog getBugReportDialog() 
	{
		if (bugReportDialog == null)
		{
			bugReportDialog = new BugReportDialog();
		}
		return bugReportDialog;
	}
	
	public static SimulationDialog getSimulationDialog()
	{
		if (simulationDialog == null)
		{
			simulationDialog = new SimulationDialog();
		}
		
		return simulationDialog;
	}
	
	public static ApiAmendDialog getApiAmendDialog()
	{
		if (apiAmendDialog == null)
		{
			apiAmendDialog = new ApiAmendDialog();
		}
		
		return apiAmendDialog;
	}

	public static LoadTesterDialog getLoadTesterDialog() 
	{
		if (loadTesterDialog == null)
		{
			loadTesterDialog = new LoadTesterDialog();
		}
		return loadTesterDialog;
	}

	public static EntropyScreen getEntropyPanel() 
	{
		if (entropyPanel == null)
		{
			entropyPanel = new EntropyScreen();
		}
		return entropyPanel;
	}

	public static VectropyScreen getVectropyPanel() 
	{
		if (vectropyPanel == null)
		{
			vectropyPanel = new VectropyScreen();
		}
		return vectropyPanel;
	}
	
	/**
	 * These work slightly differently
	 */
	public static ChangePasswordDialog getChangePasswordDialog() 
	{
		return changePasswordDialog;
	}
	public static void setChangePasswordDialog(ChangePasswordDialog dialog)
	{
		changePasswordDialog = dialog;
	}
	
	public static ConnectingDialog getConnectingDialog()
	{
		return connectingDialog;
	}
	public static void showConnectingDialog()
	{
		connectingDialog.showDialog();
	}
	public static void dismissConnectingDialog()
	{
		connectingDialog.dismissDialog();
	}
}
