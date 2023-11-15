package screen;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import object.FlagImage;
import util.Debug;
import util.DialogUtil;
import util.Registry;
import util.ReplayFileUtil;

import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ReplayFilterPanel extends JPanel
							   implements Registry
{
	private static final String FILTER_MODE_EQUAL_TO = "is equal to";
	private static final String FILTER_MODE_GREATER_THAN = "is greater than";
	private static final String FILTER_MODE_LESS_THAN = "is less than";
	
	private static final String GAME_MODE_ENTROPY = "Entropy";
	private static final String GAME_MODE_VECTROPY = "Vectropy";
	
	private final String[] filterModes = {FILTER_MODE_EQUAL_TO, FILTER_MODE_GREATER_THAN, FILTER_MODE_LESS_THAN};
	
	public ReplayFilterPanel()
	{
		try
		{
			setLayout(new FormLayout(new ColumnSpec[] {
					ColumnSpec.decode("10px"),
					ColumnSpec.decode("78px"),
					ColumnSpec.decode("340px:grow"),},
				new RowSpec[] {
					FormSpecs.NARROW_LINE_GAP_ROWSPEC,
					RowSpec.decode("56px"),
					RowSpec.decode("51px"),
					RowSpec.decode("61px"),
					RowSpec.decode("48px"),
					RowSpec.decode("48px"),
					FormSpecs.DEFAULT_ROWSPEC,}));
			
						JLabel lblFilters = new JLabel("Filters:");
						add(lblFilters, "2, 2, center, fill");
			JPanel panel_8 = new JPanel();
			panel_8.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			add(panel_8, "3, 2, fill, fill");
			panel_8.setLayout(new GridLayout(0, 3, 0, 0));
			panel_8.add(radioComplete);
			panel_8.add(radioIncomplete);
			radioBoth.setSelected(true);
			panel_8.add(radioBoth);
			cbWins.setSelected(true);
			panel_8.add(cbWins);
			cbLosses.setSelected(true);
			panel_8.add(cbLosses);
			cbUnknown.setSelected(true);
			panel_8.add(cbUnknown);
			ButtonGroup completenessGroup = new ButtonGroup();
			completenessGroup.add(radioComplete);
			completenessGroup.add(radioIncomplete);
			completenessGroup.add(radioBoth);
			add(cbFilterByFlag, "2, 4, left, fill");
			add(flagPanel, "3, 4, fill, fill");
			flagPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			flagPanel.setBounds(97, 142, 305, 61);
			flagPanel.setLayout(new GridLayout(0, 6, 0, 0));
			JPanel perfectPanel = new JPanel();
			flagPanel.add(perfectPanel);
			perfectPanel.add(cbPerfect);
			perfectPanel.add(perfectFlag);
			perfectFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.PERFECT_FLAG));
			JPanel blindPanel = new JPanel();
			flagPanel.add(blindPanel);
			blindPanel.add(cbFullyBlind);
			blindPanel.add(blindFlag);
			blindFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.BLIND_FLAG));
			JPanel partiallyBlindPanel = new JPanel();
			flagPanel.add(partiallyBlindPanel);
			partiallyBlindPanel.add(cbPartiallyBlind);
			partiallyBlindFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.PARTIALLY_BLIND_FLAG));
			partiallyBlindPanel.add(partiallyBlindFlag);
			JPanel handicap4Panel = new JPanel();
			flagPanel.add(handicap4Panel);
			handicap4Panel.add(cbHandicap4);
			handicapFourFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.HANDICAP_FOUR_FLAG));
			handicap4Panel.add(handicapFourFlag);
			JPanel handicap3Panel = new JPanel();
			flagPanel.add(handicap3Panel);
			handicap3Panel.add(cbHandicap3);
			handicapThreeFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.HANDICAP_THREE_FLAG));
			handicap3Panel.add(handicapThreeFlag);
			JPanel handicap2Panel = new JPanel();
			flagPanel.add(handicap2Panel);
			handicap2Panel.add(cbHandicap2);
			handicapTwoFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.HANDICAP_TWO_FLAG));
			handicap2Panel.add(handicapTwoFlag);
			JPanel handicap1Panel = new JPanel();
			flagPanel.add(handicap1Panel);
			handicap1Panel.add(cbHandicap1);
			handicapOneFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.HANDICAP_ONE_FLAG));
			handicap1Panel.add(handicapOneFlag);
			flagPanel.add(onlinePanel);
			onlinePanel.add(cbOnline);
			onlineFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.ONLINE_FLAG));
			onlinePanel.add(onlineFlag);
			flagPanel.add(moonPanel);
			cbMoon.setEnabled(false);
			moonPanel.add(cbMoon);
			moonFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.MOON_FLAG));
			moonFlag.setEnabled(false);
			moonPanel.add(moonFlag);
			flagPanel.add(starPanel);
			cbStar.setEnabled(false);
			starPanel.add(cbStar);
			starFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.STAR_FLAG));
			starFlag.setEnabled(false);
			starPanel.add(starFlag);
			flagPanel.add(moonAndStarPanel);
			cbMoonAndStar.setEnabled(false);
			moonAndStarPanel.add(cbMoonAndStar);
			moonAndStarFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.MOON_AND_STAR_FLAG));
			moonAndStarFlag.setEnabled(false);
			moonAndStarPanel.add(moonAndStarFlag);
			flagPanel.add(noFlagPanel);
			noFlagPanel.add(cbBlank);
			noFlagFlag.setIcon(FlagImage.getImageIconForFlagName(ReplayFileUtil.NO_FLAG));
			noFlagPanel.add(noFlagFlag);
			cbFilterByRounds.setHorizontalAlignment(SwingConstants.CENTER);
			add(cbFilterByRounds, "2, 5, left, fill");
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			add(panel, "3, 5, left, center");
			roundFilterMode.setPreferredSize(new Dimension(120, 20));
			panel.add(roundFilterMode);
			panel.add(roundFilterSpinner);
			roundFilterSpinner.setModel(new SpinnerNumberModel(1, 1, 19, 1));
			roundFilterMode.setEnabled(false);
			roundFilterSpinner.setEnabled(false);
			cbFilterByGameMode.setHorizontalAlignment(SwingConstants.CENTER);
			add(cbFilterByGameMode, "2, 6, left, fill");
			panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			chckbxEntropy.setEnabled(false);
			chckbxVectropy.setEnabled(false);
			add(panel_1, "3, 6, left, center");
			panel_1.setLayout(new GridLayout(0, 4, 0, 0));
			panel_1.add(chckbxEntropy);
			panel_1.add(chckbxVectropy);

			enableFlagBoxes(false);
			enableFlags(false);
			
			setUpListeners();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}

	private final JRadioButton radioComplete = new JRadioButton("Complete");
	private final JRadioButton radioIncomplete = new JRadioButton("Incomplete");
	private final JRadioButton radioBoth = new JRadioButton("Both");
	private final JCheckBox cbWins = new JCheckBox("Wins");
	private final JCheckBox cbLosses = new JCheckBox("Losses");
	private final JCheckBox cbUnknown = new JCheckBox("Unknown");
	private final JCheckBox cbFilterByFlag = new JCheckBox("Flag");
	private final JCheckBox cbHandicap4 = new JCheckBox("");
	private final JCheckBox cbHandicap3 = new JCheckBox("");
	private final JCheckBox cbHandicap2 = new JCheckBox("");
	private final JCheckBox cbHandicap1 = new JCheckBox("");
	private final JCheckBox cbPerfect = new JCheckBox("");
	private final JCheckBox cbFullyBlind = new JCheckBox("");
	private final JCheckBox cbPartiallyBlind = new JCheckBox("");
	private final JCheckBox cbBlank = new JCheckBox("");
	private final JPanel flagPanel = new JPanel();
	private final JLabel handicapOneFlag = new JLabel("");
	private final JLabel handicapTwoFlag = new JLabel("");
	private final JLabel handicapThreeFlag = new JLabel("");
	private final JLabel handicapFourFlag = new JLabel("");
	private final JLabel perfectFlag = new JLabel("");
	private final JLabel blindFlag = new JLabel("");
	private final JLabel partiallyBlindFlag = new JLabel("");
	private final JLabel noFlagFlag = new JLabel("");
	private final JCheckBox cbFilterByRounds = new JCheckBox("Rounds");
	private final JPanel panel = new JPanel();
	private final ComboBoxModel<String> comboModel = new DefaultComboBoxModel<>(filterModes);
	private final JComboBox<String> roundFilterMode = new JComboBox<>(comboModel);
	private final JSpinner roundFilterSpinner = new JSpinner();
	private final JCheckBox cbFilterByGameMode = new JCheckBox("Mode");
	private final JPanel panel_1 = new JPanel();
	private final JCheckBox chckbxEntropy = new JCheckBox("Entropy");
	private final JCheckBox chckbxVectropy = new JCheckBox("Vectropy");
	private final JPanel onlinePanel = new JPanel();
	private final JCheckBox cbOnline = new JCheckBox("");
	private final JLabel onlineFlag = new JLabel("");
	private final JPanel moonPanel = new JPanel();
	private final JCheckBox cbMoon = new JCheckBox("");
	private final JLabel moonFlag = new JLabel("");
	private final JPanel starPanel = new JPanel();
	private final JCheckBox cbStar = new JCheckBox("");
	private final JLabel starFlag = new JLabel("");
	private final JPanel moonAndStarPanel = new JPanel();
	private final JCheckBox cbMoonAndStar = new JCheckBox("");
	private final JLabel moonAndStarFlag = new JLabel("");
	private final JPanel noFlagPanel = new JPanel();
	
	public void setMoonAndStarVisibility()
	{
		boolean unlockedExtraSuits = rewards.getBoolean(REWARDS_BOOLEAN_EXTRA_SUITS, false);
		GridLayout layout = (GridLayout)flagPanel.getLayout();
		flagPanel.remove(moonPanel);
		flagPanel.remove(starPanel);
		flagPanel.remove(moonAndStarPanel);
		
		if (unlockedExtraSuits)
		{
			layout.setColumns(6);
			flagPanel.remove(noFlagPanel);
			flagPanel.add(moonPanel);
			flagPanel.add(starPanel);
			flagPanel.add(moonAndStarPanel);
			flagPanel.add(noFlagPanel);
		}
		else
		{
			layout.setColumns(5);
		}
	}
	
	private void setUpListeners()
	{
		radioComplete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if (radioComplete.isSelected())
				{
					cbUnknown.setEnabled(false);
					cbWins.setEnabled(true);
					cbLosses.setEnabled(true);
				}
			}
		});
		radioIncomplete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if (radioIncomplete.isSelected())
				{
					cbUnknown.setEnabled(true);
					cbWins.setEnabled(false);
					cbLosses.setEnabled(true);
				}
			}
		});
		radioBoth.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if (radioBoth.isSelected())
				{
					cbUnknown.setEnabled(true);
					cbWins.setEnabled(true);
					cbLosses.setEnabled(true);
				}
			}
		});
		
		cbFilterByFlag.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				boolean enabled = cbFilterByFlag.isSelected();
				enableFlags(enabled);
				enableFlagBoxes(enabled);
			}
		});
		
		cbFilterByRounds.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				boolean enabled = cbFilterByRounds.isSelected();
				roundFilterMode.setEnabled(enabled);
				roundFilterSpinner.setEnabled(enabled);
			}
		});
		
		cbFilterByGameMode.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				boolean enabled = cbFilterByGameMode.isSelected();
				chckbxEntropy.setEnabled(enabled);
				chckbxVectropy.setEnabled(enabled);
			}
		});
	}
	
	private void enableFlags(boolean enabled)
	{
		handicapOneFlag.setEnabled(enabled);
		handicapTwoFlag.setEnabled(enabled);
		handicapThreeFlag.setEnabled(enabled);
		handicapFourFlag.setEnabled(enabled);
		perfectFlag.setEnabled(enabled);
		blindFlag.setEnabled(enabled);
		partiallyBlindFlag.setEnabled(enabled);
		noFlagFlag.setEnabled(enabled);
		onlineFlag.setEnabled(enabled);
		moonFlag.setEnabled(enabled);
		starFlag.setEnabled(enabled);
		moonAndStarFlag.setEnabled(enabled);
	}
	
	private void enableFlagBoxes(boolean enabled)
	{
		cbHandicap4.setEnabled(enabled);
		cbHandicap3.setEnabled(enabled);
		cbHandicap2.setEnabled(enabled);
		cbHandicap1.setEnabled(enabled);
		cbPerfect.setEnabled(enabled);
		cbFullyBlind.setEnabled(enabled);
		cbPartiallyBlind.setEnabled(enabled);
		cbBlank.setEnabled(enabled);
		cbOnline.setEnabled(enabled);
		cbMoon.setEnabled(enabled);
		cbStar.setEnabled(enabled);
		cbMoonAndStar.setEnabled(enabled);
	}
	
	public boolean getShowComplete()
	{
		return radioComplete.isSelected() || radioBoth.isSelected();
	}
	
	public boolean getShowIncomplete()
	{
		return radioIncomplete.isSelected() || radioBoth.isSelected();
	}
	
	public boolean getShowWins()
	{
		return cbWins.isSelected();
	}
	
	public boolean getShowLosses()
	{
		return cbLosses.isSelected();
	}
	
	public boolean getShowUnknown()
	{
		return cbUnknown.isSelected();
	}
	
	public boolean getFilterByFlag()
	{
		return cbFilterByFlag.isSelected();
	}
	
	public boolean getFilterByRounds()
	{
		return cbFilterByRounds.isSelected();
	}
	
	public boolean getFilterByGameMode()
	{
		return cbFilterByGameMode.isSelected();
	}
	
	public boolean includesFlag(FlagImage fi)
	{
		if (fi.getIcon() == null)
		{
			return cbBlank.isSelected();
		}
		
		ArrayList<String> imageNames = fi.getImageNames();
		for (int i=0; i<imageNames.size(); i++)
		{
			String imageName = imageNames.get(i);
			if (boxIsTickedForImageName(imageName))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean includesGameMode(String mode)
	{
		if (mode.equals(GAME_MODE_ENTROPY))
		{
			return chckbxEntropy.isSelected();
		}
		
		if (mode.equals(GAME_MODE_VECTROPY))
		{
			return chckbxVectropy.isSelected();
		}
		
		Debug.stackTrace("Filtering out unexpected game mode: " + mode);
		return false;
	}
	
	public boolean includesRoundNumber(String roundsStr)
	{
		int rounds = Integer.parseInt(roundsStr);
		
		String selection = (String)roundFilterMode.getSelectedItem();
		int numberSpecified = (int)roundFilterSpinner.getValue();
		
		if (selection.equals(FILTER_MODE_EQUAL_TO))
		{
			return rounds == numberSpecified;
		}
		else if (selection.equals(FILTER_MODE_GREATER_THAN))
		{
			return rounds > numberSpecified;
		}
		else if (selection.equals(FILTER_MODE_LESS_THAN))
		{
			return rounds < numberSpecified;
		}
		else
		{
			Debug.stackTrace("Unexpected comboBox index for round number filtering. Index: " + selection);
			return true;
		}
	}
	
	private boolean boxIsTickedForImageName(String flagName)
	{
		boolean isTicked = false;
		
		if (flagName.equals(ReplayFileUtil.BLIND_FLAG))
		{
			isTicked = cbFullyBlind.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.PARTIALLY_BLIND_FLAG))
		{
			isTicked = cbPartiallyBlind.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.PERFECT_FLAG))
		{
			isTicked = cbPerfect.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.HANDICAP_FOUR_FLAG))
		{
			isTicked = cbHandicap4.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.HANDICAP_THREE_FLAG))
		{
			isTicked = cbHandicap3.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.HANDICAP_TWO_FLAG))
		{
			isTicked = cbHandicap2.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.HANDICAP_ONE_FLAG))
		{
			isTicked = cbHandicap1.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.ONLINE_FLAG))
		{
			isTicked = cbOnline.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.MOON_FLAG))
		{
			isTicked = cbMoon.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.STAR_FLAG))
		{
			isTicked = cbStar.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.MOON_AND_STAR_FLAG))
		{
			isTicked = (cbMoon.isSelected() && cbStar.isSelected()) || cbMoonAndStar.isSelected();
		}
		else if (flagName.equals(ReplayFileUtil.CHEAT_FLAG))
		{
			isTicked = false;
		}
		else
		{
			Debug.stackTrace("Unexpected flag name: " + flagName);
		}
		
		return isTicked;
	}
	
	public boolean atLeastOneFlagSelected()
	{
		return cbFullyBlind.isSelected() || cbPartiallyBlind.isSelected() || cbPerfect.isSelected() || cbHandicap4.isSelected()
				|| cbHandicap3.isSelected() || cbHandicap2.isSelected() || cbHandicap1.isSelected() || cbBlank.isSelected()
				|| cbOnline.isSelected() || cbMoon.isSelected() || cbStar.isSelected() || cbMoonAndStar.isSelected();
	}
	
	public boolean atLeastOneModeSelected()
	{
		return chckbxEntropy.isSelected() || chckbxVectropy.isSelected();
	}
	
	public boolean valid()
	{
		if (getFilterByFlag() && !atLeastOneFlagSelected())
		{
			DialogUtil.showError("You must select at least one flag option to filter by.");
			return false;
		}
		
		if (getFilterByGameMode() && !atLeastOneModeSelected())
		{
			DialogUtil.showError("You must select at least one game mode to filter by.");
			return false;
		}
			
		return true;
	}
}