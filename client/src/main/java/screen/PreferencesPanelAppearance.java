package screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;

import achievement.Reward;
import bean.ComboBoxItem;
import object.DisabledComboBoxModel;
import preference.PreferenceSetting;
import util.GameUtil;

import static preference.PreferenceSettingKt.*;
import static util.ClientGlobals.preferenceStore;

public class PreferencesPanelAppearance extends AbstractPreferencesPanel
										implements ItemListener
{
	private String deckDesign = "";
	private String jokerDesign = "";
	private String cardBacks = "";
	private String numberOfColours = "";
	private String lookAndFeel = "";
	
	public PreferencesPanelAppearance()
	{
		setPreferredSize(new Dimension(400, 760));
		setLayout(null);
		
		bgDeckDesign.add(rdbtnClassicDesign);
		bgDeckDesign.add(rdbtnMinimalistDesign);
		bgJokerDesign.add(rdbtnClassicJokers);
		bgJokerDesign.add(rdbtnDeveloperJokers);
		deckDesignPanel.setBounds(17, 80, 374, 58);
		add(deckDesignPanel);
		deckDesignPanel.setBorder(new LineBorder(Color.GRAY));
		deckDesignPanel.setLayout(null);
		rdbtnClassicDesign.setBounds(6, 30, 72, 23);
		deckDesignPanel.add(rdbtnClassicDesign);
		rdbtnMinimalistDesign.setBounds(80, 30, 146, 23);
		deckDesignPanel.add(rdbtnMinimalistDesign);
		deckDesignPanel.add(lblCardDesign);
		deckPreviewPanel.setBounds(17, 137, 374, 136);
		add(deckPreviewPanel);
		deckPreviewPanel.setBorder(new LineBorder(Color.GRAY));
		deckPreviewPanel.setLayout(null);
		labelKh.setBounds(187, 20, 72, 96);
		deckPreviewPanel.add(labelKh);
		labelJc.setBounds(43, 20, 72, 96);
		deckPreviewPanel.add(labelJc);
		labelQd.setBounds(115, 20, 72, 96);
		deckPreviewPanel.add(labelQd);
		labelAs.setBounds(259, 20, 72, 96);
		deckPreviewPanel.add(labelAs);
		lblCardDesign.setHorizontalAlignment(SwingConstants.CENTER);
		lblCardDesign.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblCardDesign.setBounds(131, 8, 113, 17);
		lblVisualPreferences.setHorizontalAlignment(SwingConstants.CENTER);
		lblVisualPreferences.setBounds(178, 12, 72, 17);
		add(lblVisualPreferences);
		lblVisualPreferences.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(separator_1);
		jokerDesignPanel.setLayout(null);
		jokerDesignPanel.setBorder(new LineBorder(Color.GRAY));
		jokerDesignPanel.setBounds(17, 288, 374, 58);
		add(jokerDesignPanel);
		rdbtnClassicJokers.setBounds(6, 30, 72, 23);
		jokerDesignPanel.add(rdbtnClassicJokers);
		rdbtnDeveloperJokers.setBounds(80, 30, 102, 23);
		jokerDesignPanel.add(rdbtnDeveloperJokers);
		separator_1.setBounds(0, 38, 429, 2);
		lblJokerDesign.setHorizontalAlignment(SwingConstants.CENTER);
		lblJokerDesign.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblJokerDesign.setBounds(136, 8, 102, 17);
		jokerDesignPanel.add(lblJokerDesign);
		jokerPreviewPanel.setLayout(null);
		jokerPreviewPanel.setBorder(new LineBorder(Color.GRAY));
		jokerPreviewPanel.setBounds(17, 345, 374, 136);
		add(jokerPreviewPanel);
		labelJo2.setBounds(187, 20, 72, 96);
		jokerPreviewPanel.add(labelJo2);
		labelJo0.setBounds(43, 20, 72, 96);
		jokerPreviewPanel.add(labelJo0);
		labelJo1.setBounds(115, 20, 72, 96);
		jokerPreviewPanel.add(labelJo1);
		labelJo3.setBounds(259, 20, 72, 96);
		jokerPreviewPanel.add(labelJo3);
		cbFourColour.setBounds(17, 48, 135, 23);
		add(cbFourColour);
		backDesignPanel.setLayout(null);
		backDesignPanel.setBorder(new LineBorder(Color.GRAY));
		backDesignPanel.setBounds(17, 496, 374, 114);
		add(backDesignPanel);
		lblCardBacks.setHorizontalAlignment(SwingConstants.CENTER);
		lblCardBacks.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblCardBacks.setBounds(127, 8, 102, 17);
		backDesignPanel.add(lblCardBacks);
		labelBack.setBounds(23, 9, 72, 96);
		backDesignPanel.add(labelBack);
		comboBoxBacks.setBounds(121, 46, 190, 22);
		backDesignPanel.add(comboBoxBacks);
		panelLookAndFeel.setLayout(null);
		panelLookAndFeel.setBorder(new LineBorder(Color.GRAY));
		panelLookAndFeel.setBounds(17, 625, 374, 114);
		add(panelLookAndFeel);
		lblLookFeel.setHorizontalAlignment(SwingConstants.CENTER);
		lblLookFeel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblLookFeel.setBounds(127, 8, 102, 17);
		panelLookAndFeel.add(lblLookFeel);
		comboBoxLookAndFeel.setBounds(86, 81, 190, 22);
		panelLookAndFeel.add(comboBoxLookAndFeel);
		DefaultCaret caret = new DefaultCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		txtpnLookAndFeelDisclaimer.setCaret(caret);
		txtpnLookAndFeelDisclaimer.setText("Note: These options are experimental. You will need to restart for this to take effect.");
		txtpnLookAndFeelDisclaimer.setBounds(10, 28, 354, 40);
		txtpnLookAndFeelDisclaimer.setOpaque(false);
		txtpnLookAndFeelDisclaimer.setBorder(BorderFactory.createEmptyBorder());
		txtpnLookAndFeelDisclaimer.setBackground(new Color(0,0,0,0));
		txtpnLookAndFeelDisclaimer.setEditable(false);
		panelLookAndFeel.add(txtpnLookAndFeelDisclaimer);
		
		rdbtnClassicDesign.addActionListener(this);
		rdbtnMinimalistDesign.addActionListener(this);
		rdbtnClassicJokers.addActionListener(this);
		rdbtnDeveloperJokers.addActionListener(this);
		comboBoxBacks.addActionListener(this);
		cbFourColour.addItemListener(this);
	}
	
	private final JLabel lblVisualPreferences = new JLabel("Appearance");
	private final JSeparator separator_1 = new JSeparator();
	private final JPanel deckDesignPanel = new JPanel();
	private final ButtonGroup bgDeckDesign = new ButtonGroup();
	private final JLabel lblCardDesign = new JLabel("Card Design");
	private final JRadioButton rdbtnClassicDesign = new JRadioButton("Classic");
	private final JRadioButton rdbtnMinimalistDesign = new JRadioButton("Minimalist");
	private final JPanel deckPreviewPanel = new JPanel();
	private final JLabel labelJc = new JLabel();
	private final JLabel labelQd = new JLabel();
	private final JLabel labelKh = new JLabel();
	private final JLabel labelAs = new JLabel();
	private final JPanel jokerDesignPanel = new JPanel();
	private final ButtonGroup bgJokerDesign = new ButtonGroup();
	private final JRadioButton rdbtnClassicJokers = new JRadioButton("Classic");
	private final JRadioButton rdbtnDeveloperJokers = new JRadioButton("Developers");
	private final JLabel lblJokerDesign = new JLabel("Joker Design");
	private final JPanel jokerPreviewPanel = new JPanel();
	private final JLabel labelJo0 = new JLabel();
	private final JLabel labelJo1 = new JLabel();
	private final JLabel labelJo2 = new JLabel();
	private final JLabel labelJo3 = new JLabel();
	private final JCheckBox cbFourColour = new JCheckBox("Use 4 colour deck");
	private final JPanel backDesignPanel = new JPanel();
	private final JLabel lblCardBacks = new JLabel("Back Design");
	private final JComboBox<ComboBoxItem<String>> comboBoxBacks = new JComboBox<>();
	private final JComboBox<String> comboBoxLookAndFeel = new JComboBox<>();
	private final JLabel labelBack = new JLabel();
	private final JLabel lblLookFeel = new JLabel("Look & Feel");
	private final JTextPane txtpnLookAndFeelDisclaimer = new JTextPane();
	private final JPanel panelLookAndFeel = new JPanel();
	
	/**
	 * Abstract methods
	 */
	@Override
	public void initVariables()
	{
		getVariablesFromPrefs();
		
		selectRadioButtonsBasedOnDirectories();
		refreshDeckPreview();
		refreshJokerPreview();
		
		selectCardBackBasedOnPreference();
		refreshCardBackPreview();
		setLookAndFeelComboBoxModel();
		
		hideLockedFields();
	}
	
	@Override
	public boolean valid()
	{
		return true;
	}
	
	@Override
	public void savePreferences()
	{
		preferenceStore.save(PreferenceSetting.DeckDesign, deckDesign);
		preferenceStore.save(PreferenceSetting.JokerDesign, jokerDesign);
		preferenceStore.save(PreferenceSetting.NumberOfColours, numberOfColours);
		preferenceStore.save(PreferenceSetting.CardBacks, cardBacks);
		preferenceStore.save(PreferenceSetting.LookAndFeel, (String)comboBoxLookAndFeel.getSelectedItem());
		
		ScreenCache.get(MainScreen.class).fireAppearancePreferencesChange();
	}
	
	private void getVariablesFromPrefs()
	{
		deckDesign = getPreference(PreferenceSetting.DeckDesign);
		jokerDesign = getPreference(PreferenceSetting.JokerDesign);
		numberOfColours = getPreference(PreferenceSetting.NumberOfColours);
		cardBacks = getPreference(PreferenceSetting.CardBacks);
		lookAndFeel = getPreference(PreferenceSetting.LookAndFeel);
	}
	
	private void hideLockedFields()
	{
		toggleLockedComponent(cbFourColour, Reward.FourColours);
		toggleLockedComponent(rdbtnMinimalistDesign, Reward.MinimalistDeck);
		toggleLockedComponent(rdbtnDeveloperJokers, Reward.DeveloperSet);
	}
	
	private void refreshDeckPreview()
	{
		ImageIcon jackClubs = GameUtil.getImageForCard("Jc", deckDesign, jokerDesign, numberOfColours);
		ImageIcon queenDiamonds = GameUtil.getImageForCard("Qd", deckDesign, jokerDesign, numberOfColours);
		ImageIcon kingHearts = GameUtil.getImageForCard("Kh", deckDesign, jokerDesign, numberOfColours);
		ImageIcon aceSpades = GameUtil.getImageForCard("As", deckDesign, jokerDesign, numberOfColours);
		labelJc.setIcon(jackClubs);
		labelQd.setIcon(queenDiamonds);
		labelKh.setIcon(kingHearts);
		labelAs.setIcon(aceSpades);
	}
	
	private void refreshJokerPreview()
	{
		ImageIcon jo0 = GameUtil.getImageForCard("Jo0", deckDesign, jokerDesign, numberOfColours);
		ImageIcon jo1 = GameUtil.getImageForCard("Jo1", deckDesign, jokerDesign, numberOfColours);
		ImageIcon jo2 = GameUtil.getImageForCard("Jo2", deckDesign, jokerDesign, numberOfColours);
		ImageIcon jo3 = GameUtil.getImageForCard("Jo3", deckDesign, jokerDesign, numberOfColours);
		labelJo0.setIcon(jo0);
		labelJo1.setIcon(jo1);
		labelJo2.setIcon(jo2);
		labelJo3.setIcon(jo3);
	}
	
	private void selectCardBackBasedOnPreference()
	{
		Vector<ComboBoxItem<String>> backs = initialiseBacksVector();
		
		ComboBoxModel<ComboBoxItem<String>> model = new DisabledComboBoxModel<>(backs);
		comboBoxBacks.setModel(model);
		
		ComboBoxItem<String> selectedItem = getSelectedItemForCode(backs, cardBacks);
		if (selectedItem == null)
		{
			selectedItem = new ComboBoxItem<>("backBlue", "Blue");
		}
		
		comboBoxBacks.setSelectedItem(selectedItem);
	}
	
	private Vector<ComboBoxItem<String>> initialiseBacksVector()
	{
		Vector<ComboBoxItem<String>> backs = new Vector<>();
		
		backs.addElement(new ComboBoxItem<>("backBlue", "Blue"));
		backs.addElement(new ComboBoxItem<>("backRed", "Red"));
		
		addIfUnlocked(backs, new ComboBoxItem<>("backGreen", "Green"), Reward.FourColours);
		addIfUnlocked(backs, new ComboBoxItem<>("backPurple", "Purple"), Reward.NegativeJacks);
		addIfUnlocked(backs, new ComboBoxItem<>("backOrange", "Orange"), Reward.Blind);
		addIfUnlocked(backs, new ComboBoxItem<>("backLightBlue", "Light Blue"), Reward.MinimalistDeck);
		addIfUnlocked(backs, new ComboBoxItem<>("backPink", "Pink"), Reward.Vectropy);
		addIfUnlocked(backs, new ComboBoxItem<>("backSilver", "Silver"), Reward.CardReveal);
		addIfUnlocked(backs, new ComboBoxItem<>("backGold", "Gold"), Reward.ExtraSuits);
		addIfUnlocked(backs, new ComboBoxItem<>("backMatrix", "Matrix"), Reward.Illegal);
		addIfUnlocked(backs, new ComboBoxItem<>("backCosmic", "Cosmic"), Reward.DeveloperSet);
		addIfUnlocked(backs, new ComboBoxItem<>("backRainbow", "Rainbow"), Reward.Cheats);
		
		return backs;
	}
	private void addIfUnlocked(Vector<ComboBoxItem<String>> backs, ComboBoxItem<String> item, 
	  Reward reward)
	{
		if (reward.isUnlocked())
		{
			backs.addElement(item);
		}
		else
		{
			ComboBoxItem<String> disabledItem = new ComboBoxItem<>("", reward.getThreshold() + " achievements to unlock");
			disabledItem.setEnabled(false);
			backs.addElement(disabledItem);
		}
	}
	
	private void setLookAndFeelComboBoxModel()
	{
		Vector<String> backs = new Vector<>();
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
	    {
	    	String lookAndFeelName = info.getName();
	        backs.add(lookAndFeelName);
	    }
		
		ComboBoxModel<String> model = new DefaultComboBoxModel<>(backs);
		comboBoxLookAndFeel.setModel(model);
		comboBoxLookAndFeel.setSelectedItem(lookAndFeel);
	}
	
	private ComboBoxItem<String> getSelectedItemForCode(Vector<ComboBoxItem<String>> backs, String code)
	{
		int size = backs.size();
		for (int i=0; i<size; i++)
		{
			ComboBoxItem<String> back = backs.get(i);
			String backCode = back.getHiddenData();
			
			if (backCode.equals(code))
			{
				return back;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void refreshCardBackPreview()
	{
		ComboBoxItem<String> selection = (ComboBoxItem<String>)comboBoxBacks.getSelectedItem();
		if (selection == null)
		{
			cardBacks = comboBoxBacks.getItemAt(0).getHiddenData();
		}
		else
		{
			cardBacks = selection.getHiddenData();
		}
		
		ImageIcon back = new ImageIcon(getClass().getResource("/backs/" + cardBacks + ".png"));
		labelBack.setIcon(back);
	}
	
	private void selectRadioButtonsBasedOnDirectories()
	{
		cbFourColour.setSelected(numberOfColours.equals(FOUR_COLOURS));
		
		if (deckDesign.equals(DECK_DESIGN_CLASSIC))
		{
			rdbtnClassicDesign.setSelected(true);
		}
		else if (deckDesign.equals(DECK_DESIGN_ALTERNATE))
		{
			rdbtnMinimalistDesign.setSelected(true);
		}
		
		if (jokerDesign.equals(JOKER_DESIGN_CLASSIC))
		{
			rdbtnClassicJokers.setSelected(true);
		}
		else if (jokerDesign.equals(JOKER_DESIGN_DEVELOPERS))
		{
			rdbtnDeveloperJokers.setSelected(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == rdbtnClassicDesign)
		{
			deckDesign = DECK_DESIGN_CLASSIC;
			refreshDeckPreview();
		}
		else if (source == rdbtnMinimalistDesign)
		{
			deckDesign = DECK_DESIGN_ALTERNATE;
			refreshDeckPreview();
		}
		else if (source == rdbtnClassicJokers)
		{
			jokerDesign = JOKER_DESIGN_CLASSIC;
			refreshJokerPreview();
		}
		else if (source == rdbtnDeveloperJokers)
		{
			jokerDesign = JOKER_DESIGN_DEVELOPERS;
			refreshJokerPreview();
		}
		else if (source == comboBoxBacks)
		{
			refreshCardBackPreview();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		boolean enabled = cbFourColour.isSelected();
		if (enabled)
		{
			numberOfColours = FOUR_COLOURS;
		}
		else
		{
			numberOfColours = TWO_COLOURS;
		}
		
		refreshDeckPreview();
		refreshJokerPreview();
	}
}
