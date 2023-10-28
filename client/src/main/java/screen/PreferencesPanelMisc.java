package screen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import util.Debug;
import util.DialogUtil;
import util.Registry;
import util.ReplayFileUtil;

public class PreferencesPanelMisc extends AbstractPreferencesPanel
								  implements ItemListener
{
	private static final int GAME_SPEED_SLOW = 1500;
	private static final int GAME_SPEED_MEDIUM = 1000;
	private static final int GAME_SPEED_FAST = 500;
	
	private boolean autosave = false;
	private int gameSpeed = GAME_SPEED_MEDIUM;
	private boolean saveReplays = false;
	private String replayDirectory = "";
	private int replayDefault = OPEN_ON_FIRST_ROUND;
	private boolean autoStartNextRound = false;
	private int autoStartSeconds = 2; //2 seconds
	private boolean popUpRoomsOnline = true;
	private boolean checkForUpdates = true;
	
	public PreferencesPanelMisc()
	{
		setLayout(null);
		
		bgReplayRound.add(rdbtnFirstRound);
		bgReplayRound.add(rdbtnLastRound);
		bgSpeed.add(rdbtnSlow);
		bgSpeed.add(rdbtnMedium);
		bgSpeed.add(rdbtnFast);
		chckbxSaveReplays.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxSaveReplays.setBounds(20, 67, 91, 22);
		add(chckbxSaveReplays);
		lblOtherOptions.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblOtherOptions.setBounds(171, 12, 86, 17);
		add(lblOtherOptions);
		separator_3.setBounds(0, 38, 429, 2);
		add(separator_3);
		chosenDirectory.setBounds(116, 67, 180, 22);
		chosenDirectory.setEditable(false);
		add(chosenDirectory);
		chosenDirectory.setColumns(10);
		btnSelectDirectory.setBounds(296, 67, 20, 22);
		add(btnSelectDirectory);
		chckbxAutosave.setBounds(20, 180, 127, 22);
		add(chckbxAutosave);
		chckbxAutosave.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblReplayViewerDefaults.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblReplayViewerDefaults.setBounds(20, 112, 127, 14);
		add(lblReplayViewerDefaults);
		rdbtnFirstRound.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnFirstRound.setBounds(144, 109, 91, 20);
		add(rdbtnFirstRound);
		rdbtnLastRound.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnLastRound.setBounds(237, 109, 91, 20);
		add(rdbtnLastRound);
		lblGameSpeed.setBounds(20, 148, 86, 14);
		add(lblGameSpeed);
		lblGameSpeed.setHorizontalAlignment(SwingConstants.LEFT);
		lblGameSpeed.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnSlow.setBounds(144, 144, 60, 20);
		add(rdbtnSlow);
		rdbtnSlow.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnMedium.setBounds(206, 144, 70, 20);
		add(rdbtnMedium);
		rdbtnMedium.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnFast.setBounds(274, 144, 54, 20);
		add(rdbtnFast);
		rdbtnFast.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxAutoStartNextRound.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxAutoStartNextRound.setBounds(20, 205, 208, 23);
		add(chckbxAutoStartNextRound);
		chckbxPopUpRooms.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxPopUpRooms.setBounds(20, 230, 208, 23);
		add(chckbxPopUpRooms);
		spinnerAutoStartSeconds.setBounds(228, 205, 38, 22);
		add(spinnerAutoStartSeconds);
		lblSeconds.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSeconds.setBounds(275, 206, 55, 20);
		add(lblSeconds);
		chckbxCheckForUpdates.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxCheckForUpdates.setBounds(20, 256, 237, 23);
		add(chckbxCheckForUpdates);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		rdbtnFast.addActionListener(this);
		rdbtnMedium.addActionListener(this);
		rdbtnSlow.addActionListener(this);
		btnSelectDirectory.addActionListener(this);
		chckbxSaveReplays.addItemListener(this);
		chckbxAutoStartNextRound.addItemListener(this);
	}
	
	private final JLabel lblOtherOptions = new JLabel("Other Options");
	private final JSeparator separator_3 = new JSeparator();
	private final JFileChooser fc = new JFileChooser();
	private final JTextField chosenDirectory = new JTextField();
	private final JCheckBox chckbxSaveReplays = new JCheckBox("Save replays");
	private final JButton btnSelectDirectory = new JButton("...");
	private final JCheckBox chckbxAutosave = new JCheckBox("Autosave on exit");
	private final ButtonGroup bgSpeed = new ButtonGroup();
	private final JRadioButton rdbtnSlow = new JRadioButton("Slow");
	private final JRadioButton rdbtnMedium = new JRadioButton("Medium");
	private final JRadioButton rdbtnFast = new JRadioButton("Fast");
	private final JLabel lblGameSpeed = new JLabel("Game speed");
	private final JCheckBox chckbxAutoStartNextRound = new JCheckBox("Automatically start next round after");
	private final JCheckBox chckbxPopUpRooms = new JCheckBox("Pop up online rooms on my turn");
	private final JSpinner spinnerAutoStartSeconds = new JSpinner();
	private final JLabel lblSeconds = new JLabel("seconds");
	private final JLabel lblReplayViewerDefaults = new JLabel("Replay viewer default");
	private final ButtonGroup bgReplayRound = new ButtonGroup();
	private final JRadioButton rdbtnFirstRound = new JRadioButton("First Round");
	private final JRadioButton rdbtnLastRound = new JRadioButton("Last Round");
	private final JCheckBox chckbxCheckForUpdates = new JCheckBox("Automatically check for updates");
	
	/**
	 * Abstract methods
	 */
	@Override
	public void initVariables()
	{
		getVariablesFromPrefs();
		
		spinnerAutoStartSeconds.setModel(new SpinnerNumberModel(autoStartSeconds, 1, 5, 1));
		chosenDirectory.setText(replayDirectory + "\\Replays");
		chckbxAutosave.setSelected(autosave);
		rdbtnSlow.setSelected(gameSpeed == GAME_SPEED_SLOW);
		rdbtnMedium.setSelected(gameSpeed == GAME_SPEED_MEDIUM);
		rdbtnFast.setSelected(gameSpeed == GAME_SPEED_FAST);
		chckbxSaveReplays.setSelected(saveReplays);
		chosenDirectory.setText(replayDirectory + "\\Replays");
		chosenDirectory.setEnabled(saveReplays);
		btnSelectDirectory.setEnabled(saveReplays);
		chckbxAutoStartNextRound.setSelected(autoStartNextRound);
		chckbxPopUpRooms.setSelected(popUpRoomsOnline);
		chckbxCheckForUpdates.setSelected(checkForUpdates);
		spinnerAutoStartSeconds.setEnabled(autoStartNextRound);
		lblSeconds.setEnabled(autoStartNextRound);
		spinnerAutoStartSeconds.setValue(autoStartSeconds);
		rdbtnFirstRound.setSelected(replayDefault == Registry.OPEN_ON_FIRST_ROUND);
		rdbtnLastRound.setSelected(replayDefault == Registry.OPEN_ON_LAST_ROUND);
	}
	
	@Override
	public boolean valid()
	{
		return confirmChangeOfDirectory();
	}
	
	@Override
	public void savePreferences()
	{
		autosave = chckbxAutosave.isSelected();
		replayDefault = rdbtnFirstRound.isSelected() ? Registry.OPEN_ON_FIRST_ROUND:Registry.OPEN_ON_LAST_ROUND;
		autoStartSeconds = (int)spinnerAutoStartSeconds.getValue();
		popUpRoomsOnline = chckbxPopUpRooms.isSelected();
		checkForUpdates = chckbxCheckForUpdates.isSelected();
		
		prefs.putBoolean(PREFERENCES_BOOLEAN_SAVE_REPLAYS, saveReplays);
		prefs.putBoolean(PREFERENCES_BOOLEAN_AUTOSAVE, autosave);
		prefs.put(PREFERENCES_STRING_REPLAY_DIRECTORY, replayDirectory);
		prefs.putInt(PREFERENCES_INT_REPLAY_DEFAULT, replayDefault);
		prefs.putBoolean(PREFERENCES_BOOLEAN_AUTO_START_NEXT_ROUND, autoStartNextRound);
		prefs.putInt(PREFERENCES_INT_AUTO_START_SECONDS, autoStartSeconds);
		prefs.putBoolean(PREFERENCES_BOOLEAN_POP_UP_ROOMS, popUpRoomsOnline);
		prefs.putBoolean(PREFERENCES_BOOLEAN_CHECK_FOR_UPDATES, checkForUpdates);
		prefs.putInt(PREFERENCES_INT_GAME_SPEED, gameSpeed);
	}
	
	private void getVariablesFromPrefs()
	{
		autosave = prefs.getBoolean(PREFERENCES_BOOLEAN_AUTOSAVE, false);
		gameSpeed = prefs.getInt(PREFERENCES_INT_GAME_SPEED, GAME_SPEED_MEDIUM);
		saveReplays = prefs.getBoolean(PREFERENCES_BOOLEAN_SAVE_REPLAYS, false);
		replayDirectory = prefs.get(PREFERENCES_STRING_REPLAY_DIRECTORY, System.getProperty("user.dir"));
		replayDefault = prefs.getInt(PREFERENCES_INT_REPLAY_DEFAULT, Registry.OPEN_ON_LAST_ROUND);
		autoStartNextRound = prefs.getBoolean(PREFERENCES_BOOLEAN_AUTO_START_NEXT_ROUND, false);
		autoStartSeconds = prefs.getInt(PREFERENCES_INT_AUTO_START_SECONDS, 2);
		popUpRoomsOnline = prefs.getBoolean(PREFERENCES_BOOLEAN_POP_UP_ROOMS, true);
		checkForUpdates = prefs.getBoolean(PREFERENCES_BOOLEAN_CHECK_FOR_UPDATES, true);
	}
	
	private boolean confirmChangeOfDirectory()
	{
		String originalReplayDirectory = prefs.get(PREFERENCES_STRING_REPLAY_DIRECTORY, System.getProperty("user.dir"));
		
		File[] myExistingFiles = new File(originalReplayDirectory + "//Replays//" + ReplayFileUtil.FOLDER_PERSONAL_REPLAYS).listFiles();
		int myExistingFilesLength = 0;
		if (myExistingFiles != null)
		{
			myExistingFilesLength = myExistingFiles.length;
		}
		
		File[] importedExistingFiles = new File(originalReplayDirectory + "//Replays//"+ ReplayFileUtil.FOLDER_IMPORTED_REPLAYS).listFiles();
		int importedExistingFilesLength = 0;
		if (importedExistingFiles != null)
		{
			importedExistingFilesLength = importedExistingFiles.length;
		}
		
		if (!replayDirectory.equals(originalReplayDirectory) && (myExistingFilesLength != 0 || importedExistingFilesLength != 0))
		{
			int choice = DialogUtil.showQuestion("You have changed your replay directory but there are still files in the old one. "
											   + "\nWould you like to move these files to the new location?", true);
			
			if (choice == JOptionPane.YES_OPTION)
			{
				ReplayFileUtil.moveReplays(myExistingFiles, importedExistingFiles, originalReplayDirectory, replayDirectory);
			}
			else if (choice == JOptionPane.CANCEL_OPTION)
			{
				return false;
			}
			else
			{
				DialogUtil.showInfo("Existing replay files were left in the old directory and will have to be deleted or moved manually.");
			}
		}
		
		return true;
	}
	
	private void selectReplayDirectory()
	{
		try
		{
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				File file = fc.getSelectedFile();

				replayDirectory = file.getPath();
				chosenDirectory.setText(replayDirectory + "\\Replays");
				Debug.append("Selected " + file.getPath() + " as replay directory.", true);
			} 
			else 
			{
				Debug.append("Directory selection cancelled by user.", true);
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == rdbtnFast)
		{
			gameSpeed = GAME_SPEED_FAST;
		}
		else if (source == rdbtnMedium)
		{
			gameSpeed = GAME_SPEED_MEDIUM;
		}
		else if (source == rdbtnSlow)
		{
			gameSpeed = GAME_SPEED_SLOW;
		}
		else if (source == btnSelectDirectory)
		{
			selectReplayDirectory();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == chckbxSaveReplays)
		{
			saveReplays = chckbxSaveReplays.isSelected();
			chosenDirectory.setEnabled(saveReplays);
			btnSelectDirectory.setEnabled(saveReplays);
		}
		else if (source == chckbxAutoStartNextRound)
		{
			autoStartNextRound = chckbxAutoStartNextRound.isSelected();
			lblSeconds.setEnabled(autoStartNextRound);
			spinnerAutoStartSeconds.setEnabled(autoStartNextRound);
		}
	}
}