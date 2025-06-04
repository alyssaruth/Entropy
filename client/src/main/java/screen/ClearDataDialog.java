package screen;

import achievement.AchievementSetting;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import static util.ClientGlobals.achievementStore;

public class ClearDataDialog extends JDialog
							 implements ActionListener,
							 			Registry
{
	private boolean clearOnlyStatistics = false;
	private boolean clearAchievementsAndStatistics = false;
	private boolean clearMyReplays = false;
	private boolean clearImportedReplays = false;
	
	public ClearDataDialog() 
	{
		getContentPane().setLayout(null);
		chckbxOnlyStatistics.setBounds(18, 20, 159, 23);
		getContentPane().add(chckbxOnlyStatistics);
		chckbxAchievementsAndStatistics.setBounds(18, 49, 204, 23);
		getContentPane().add(chckbxAchievementsAndStatistics);
		chckbxMyReplays.setBounds(18, 78, 159, 23);
		getContentPane().add(chckbxMyReplays);
		chckbxImportedReplays.setBounds(18, 107, 159, 23);
		getContentPane().add(chckbxImportedReplays);
		JPanel panel = new JPanel();
		panel.setBounds(0, 145, 222, 33);
		getContentPane().add(panel);
		panel.add(btnOk);
		panel.add(btnCancel);
		initVariables();

		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
		chckbxOnlyStatistics.addActionListener(this);
		chckbxAchievementsAndStatistics.addActionListener(this);
	}
	
	private void initVariables()
	{
		clearOnlyStatistics = false;
		clearAchievementsAndStatistics = false;
		clearMyReplays = false;
		clearImportedReplays = false;
		
		chckbxOnlyStatistics.setSelected(false);
		chckbxAchievementsAndStatistics.setSelected(false);
		chckbxMyReplays.setSelected(false);
		chckbxImportedReplays.setSelected(false);
	}
	
	private JCheckBox chckbxOnlyStatistics = new JCheckBox("Statistics Only");
	private JCheckBox chckbxAchievementsAndStatistics = new JCheckBox("Achievements and Statistics");
	private JCheckBox chckbxMyReplays = new JCheckBox("My Replays");
	private JCheckBox chckbxImportedReplays = new JCheckBox("Imported Replays");
	private JButton btnOk = new JButton("Ok");
	private JButton btnCancel = new JButton("Cancel");
	
	private boolean valid()
	{
		clearOnlyStatistics = chckbxOnlyStatistics.isSelected();
		clearAchievementsAndStatistics = chckbxAchievementsAndStatistics.isSelected();
		clearMyReplays = chckbxMyReplays.isSelected();
		clearImportedReplays = chckbxImportedReplays.isSelected();
		
		if (!clearOnlyStatistics && !clearAchievementsAndStatistics && !clearMyReplays && !clearImportedReplays)
		{
			DialogUtilNew.showError("You must select something to clear.");
			return false;
		}
		
		return true;
	}
	
	private void clearData()
	{
		if (clearOnlyStatistics)
		{
			int choice = DialogUtilNew.showQuestion("You have selected to reset your statistics. Whilst you will not lose earned achievements,\n"
											   + " any progress you may have made towards locked achievements will be lost. \n\nProceed?", false);
			
			if (choice == JOptionPane.YES_OPTION)
			{
				removeStatisticsVariablesFromNode();
				DialogUtilNew.showInfo("Statistics were reset successfully.");
			}
			else
			{
				return;
			}
			
		}
		
		if (clearAchievementsAndStatistics)
		{
			int choice = DialogUtilNew.showQuestion("Clearing your Achievements will also clear any rewards you have unlocked.\n\nProceed?", false);

			if (choice == JOptionPane.NO_OPTION)
			{
				return;
			}

			achievementStore.clear();
			RegistryUtil.clearNode(rewards);
			RegistryUtil.clearNode(savedGame);
			resetPreferences();
			DialogUtilNew.showInfo("Achievements and statistics were reset successfully.");
			ScreenCache.get(AchievementsDialog.class).refresh(true);
			ScreenCache.get(MainScreen.class).showTopAchievementPanel(false);
			ScreenCache.get(MainScreen.class).showBottomAchievementPanel(false);
		}
		
		if (clearMyReplays)
		{
			String result = ReplayFileUtil.deleteReplays(ReplayFileUtil.FOLDER_PERSONAL_REPLAYS);

			if (result.equals("nullReplayFiles"))
			{
				DialogUtilNew.showError("A serious error occurred deleting the personal replays.");
			}
			else if (result.equals("noReplays"))
			{
				DialogUtilNew.showInfo("There were no personal replays to delete.");
			}
			else if (!result.isEmpty())
			{
				DialogUtilNew.showError("Replay deletion failed for the following personal replays:\n" + result);
			}
			else 
			{
				DialogUtilNew.showInfo("Personal replays were deleted successfully.");
			}
		}
		
		if (clearImportedReplays)
		{
			String result = ReplayFileUtil.deleteReplays(ReplayFileUtil.FOLDER_IMPORTED_REPLAYS);

			if (result.equals("nullReplayFiles"))
			{
				DialogUtilNew.showError("A serious error occurred deleting the imported replays.");
			}
			else if (result.equals("noReplays"))
			{
				DialogUtilNew.showInfo("There were no imported replays to delete.");
			}
			else if (!result.isEmpty())
			{
				DialogUtilNew.showError("Replay deletion failed for the following imported replays:\n" + result);
			}
			else 
			{
				DialogUtilNew.showInfo("Imported replays were deleted successfully.");
			}
		}
		
		closeDialog();
	}
	
	private void removeStatisticsVariablesFromNode()
	{
		ScreenCache.get(MainScreen.class).resetStartTime();
		achievementStore.delete(AchievementSetting.TimePlayed);
		achievementStore.delete(AchievementSetting.BestStreak);
		achievementStore.delete(AchievementSetting.CurrentStreak);
		achievementStore.delete(AchievementSetting.EntropyGamesPlayed);
		achievementStore.delete(AchievementSetting.VectropyGamesPlayed);
		achievementStore.delete(AchievementSetting.EntropyGamesWon);
		achievementStore.delete(AchievementSetting.VectropyGamesWon);
	}
	
	private void resetPreferences()
	{
		prefs.remove(PREFERENCES_BOOLEAN_PLAY_BLIND);
		prefs.remove(PREFERENCES_BOOLEAN_PLAY_WITH_HANDICAP);
		prefs.remove(PREFERENCES_XML_API_SETTINGS);
		prefs.remove(PREFERENCES_STRING_GAME_MODE);
		prefs.remove(PREFERENCES_INT_HANDICAP_AMOUNT);
		prefs.remove(PREFERENCES_INT_JOKER_QUANTITY);
		prefs.remove(PREFERENCES_INT_JOKER_VALUE);
		prefs.remove(PREFERENCES_STRING_CARD_BACKS);
		prefs.remove(PREFERENCES_STRING_LOOK_AND_FEEL);
		prefs.remove(PREFERENCES_STRING_DECK_DIRECTORY);
		prefs.remove(PREFERENCES_STRING_JOKER_DIRECTORY);
		prefs.remove(PREFERENCES_STRING_NUMBER_OF_COLOURS);
	}
	
	private void closeDialog()
	{
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == btnOk)
		{
			if (valid())
			{
				clearData();
			}
		}
		else if (source == btnCancel)
		{
			closeDialog();
		}
		else if (source == chckbxOnlyStatistics)
		{
			chckbxAchievementsAndStatistics.setEnabled(!chckbxOnlyStatistics.isSelected());
		}
		else if (source == chckbxAchievementsAndStatistics)
		{
			chckbxOnlyStatistics.setEnabled(!chckbxAchievementsAndStatistics.isSelected());
		}
	}
}