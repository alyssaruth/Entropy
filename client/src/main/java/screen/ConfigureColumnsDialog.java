package screen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import preference.PreferenceSetting;
import util.Debug;
import util.Registry;

import static preference.PreferenceSettingKt.getPreference;
import static util.ClientGlobals.preferenceStore;

public class ConfigureColumnsDialog extends JDialog
{
	private boolean showGameMode = false;
	private boolean showRounds = false;
	private boolean showPlayers = false;
	private boolean showCards = false;
	private boolean showRoomName = false;
	private boolean okPressed = false;
	
	public ConfigureColumnsDialog() 
	{
		setSize(220, 250);
		setTitle("Configure Columns");
		setResizable(false);
		getContentPane().setLayout(null);
		chckbxGameMode.setBounds(18, 20, 159, 23);
		getContentPane().add(chckbxGameMode);
		chckbxRounds.setBounds(18, 78, 204, 23);
		getContentPane().add(chckbxRounds);
		chckbxPlayers.setBounds(18, 107, 159, 23);
		getContentPane().add(chckbxPlayers);
		chckbxCards.setBounds(18, 136, 159, 23);
		getContentPane().add(chckbxCards);
		JPanel panel = new JPanel();
		panel.setBounds(0, 174, 222, 33);
		getContentPane().add(panel);
		panel.add(btnOk);
		panel.add(btnCancel);
		chckbxRoomName.setBounds(18, 49, 204, 23);
		getContentPane().add(chckbxRoomName);
		initVariables();
		setUpListeners();
	}
	
	private JCheckBox chckbxGameMode = new JCheckBox("Game Mode");
	private JCheckBox chckbxRoomName = new JCheckBox("Room Name");
	private JCheckBox chckbxRounds = new JCheckBox("Number of Rounds");
	private JCheckBox chckbxPlayers = new JCheckBox("Number of Players");
	private JCheckBox chckbxCards = new JCheckBox("Number of Cards");
	private JButton btnOk = new JButton("Ok");
	private JButton btnCancel = new JButton("Cancel");
	
	private void initVariables()
	{
		showGameMode = getPreference(PreferenceSetting.IncludeGameModeColumn);
		showRounds = getPreference(PreferenceSetting.IncludeRoundsColumn);
		showPlayers = getPreference(PreferenceSetting.IncludePlayersColumn);
		showCards = getPreference(PreferenceSetting.IncludeCardsColumn);
		showRoomName = getPreference(PreferenceSetting.IncludeRoomNameColumn);
		
		chckbxGameMode.setSelected(showGameMode);
		chckbxRounds.setSelected(showRounds);
		chckbxPlayers.setSelected(showPlayers);
		chckbxCards.setSelected(showCards);
		chckbxRoomName.setSelected(showRoomName);
	}
	
	private void saveSettings()
	{
		preferenceStore.save(PreferenceSetting.IncludeGameModeColumn, chckbxGameMode.isSelected());
		preferenceStore.save(PreferenceSetting.IncludeRoomNameColumn, chckbxRoomName.isSelected());
		preferenceStore.save(PreferenceSetting.IncludeRoundsColumn, chckbxRounds.isSelected());
		preferenceStore.save(PreferenceSetting.IncludePlayersColumn, chckbxPlayers.isSelected());
		preferenceStore.save(PreferenceSetting.IncludeCardsColumn, chckbxCards.isSelected());
		
		dispose();
	}
	
	private void setUpListeners()
	{
		btnOk.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				okPressed = true;
				saveSettings();
			}
		});
		btnCancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				dispose();
			}
		});
	}
	
	public static boolean configureColumns()
	{
		ConfigureColumnsDialog dialog = new ConfigureColumnsDialog();
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		return dialog.okPressed;
	}
}