package screen;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import game.GameMode;
import util.Debug;

public class PreferencesPanelGameplay extends AbstractPreferencesPanel
									  implements ChangeListener,
									  			 ItemListener
{
	private GameMode gameMode = GameMode.Entropy;
	private boolean includeJokers = false;
	private int numberOfCards = 5;
	private int jokerQuantity = 2;
	private int jokerValue = 2;
	private boolean playBlind = false;
	private boolean playWithHandicap = false;
	private int handicapAmount = 1;
	private boolean includeStars = false;
	private boolean includeMoons = false;
	private boolean negativeJacks = false;
	private boolean cardReveal = false;
	
	public PreferencesPanelGameplay()
	{
		setLayout(null);
		separatorTitle.setBounds(0, 38, 429, 2);
		add(separatorTitle);
		lblGameplay.setBounds(184, 12, 60, 17);
		add(lblGameplay);
		lblGameplay.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblWarning.setBounds(65, 395, 304, 14);
		add(lblWarning);
		lblWarning.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panelGameMode.setBorder(new TitledBorder(null, "Mode", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelGameMode.setBounds(20, 51, 165, 73);
		add(panelGameMode);
		panelGameMode.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 10));
		panelGameMode.add(rdbtnEntropy);
		rdbtnEntropy.setFont(new Font("Tahoma", Font.PLAIN, 11));
		bgMode.add(rdbtnEntropy);
		panelGameMode.add(rdbtnVectropy);
		rdbtnVectropy.setFont(new Font("Tahoma", Font.PLAIN, 11));
		bgMode.add(rdbtnVectropy);
		panelDeck.setBorder(new TitledBorder(null, "Deck", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelDeck.setBounds(20, 253, 393, 131);
		add(panelDeck);
		panelDeck.setLayout(new GridLayout(0, 1, 0, 0));
		FlowLayout fl_panelJokers = (FlowLayout) panelJokers.getLayout();
		fl_panelJokers.setAlignment(FlowLayout.LEFT);
		panelJokers.setBorder(new EmptyBorder(0, -5, 0, 0));
		panelDeck.add(panelJokers);
		panelJokers.add(cbJokers);
		cbJokers.setFont(new Font("Tahoma", Font.PLAIN, 11));
		jokerQuantitySpinner.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelJokers.add(jokerQuantitySpinner);
		lblWorth.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelJokers.add(lblWorth);
		jokerValueSpinner.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelJokers.add(jokerValueSpinner);
		cbJokers.addItemListener(this);
		panelDeck.add(cbNegativeJacks);
		cbNegativeJacks.setSelected(false);
		cbNegativeJacks.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelDeck.add(cbIncludeMoons);
		cbIncludeMoons.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelDeck.add(cbIncludeStars);
		cbIncludeStars.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGameplay.setBorder(new TitledBorder(null, "Gameplay", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelGameplay.setBounds(20, 135, 393, 107);
		add(panelGameplay);
		panelGameplay.setLayout(new GridLayout(0, 1, 0, 0));
		handicapPanel.setBorder(new EmptyBorder(0, -5, 0, 0));
		FlowLayout flowLayout_1 = (FlowLayout) handicapPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEADING);
		panelGameplay.add(handicapPanel);
		handicapPanel.add(cbHandicap);
		cbHandicap.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cbHandicap.setToolTipText("Start with fewer cards than your opponents.");
		handicapAmountSpinner.setFont(new Font("Tahoma", Font.PLAIN, 11));
		handicapPanel.add(handicapAmountSpinner);
		handicapAmountSpinner.setToolTipText("Start with this many cards less than your opponents.");
		cbHandicap.addItemListener(this);
		panelGameplay.add(cbPlayBlind);
		cbPlayBlind.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cbPlayBlind.setToolTipText("Your hand will be face-down each round until you choose to view it manually.");
		cbPlayersRevealCards.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGameplay.add(cbPlayersRevealCards);
		panelStartingCards.setBorder(new TitledBorder(null, "Starting cards", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelStartingCards.setBounds(195, 51, 218, 73);
		add(panelStartingCards);
		numberOfCardsSlider.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelStartingCards.add(numberOfCardsSlider);
		numberOfCardsSlider.setMinorTickSpacing(1);
		numberOfCardsSlider.setPaintTicks(true);
		numberOfCardsSlider.setPaintLabels(true);
		numberOfCardsSlider.setMajorTickSpacing(4);
		numberOfCardsSlider.setToolTipText("");
		numberOfCardsSlider.setMinimum(1);
		numberOfCardsSlider.setMaximum(5);
		numberOfCardsSlider.addChangeListener(this);
		handicapAmountSpinner.addChangeListener(this);
		
		rdbtnEntropy.addActionListener(this);
		rdbtnVectropy.addActionListener(this);
	}
	
	private final JLabel lblGameplay = new JLabel("Gameplay");
	private final JSeparator separatorTitle = new JSeparator();
	private final JPanel panelGameMode = new JPanel();
	private final ButtonGroup bgMode = new ButtonGroup();
	private final JRadioButton rdbtnEntropy = new JRadioButton("Entropy");
	private final JRadioButton rdbtnVectropy = new JRadioButton("Vectropy");
	private final JPanel panelStartingCards = new JPanel();
	private final JSlider numberOfCardsSlider = new JSlider();
	private final JPanel panelGameplay = new JPanel();
	private final JPanel handicapPanel = new JPanel();
	private final JCheckBox cbHandicap = new JCheckBox("Handicap:");
	private final JSpinner handicapAmountSpinner = new JSpinner();
	private final JCheckBox cbPlayBlind = new JCheckBox("Play Blind");
	private final JCheckBox cbPlayersRevealCards = new JCheckBox("Players reveal cards");
	private final JPanel panelDeck = new JPanel();
	private final JPanel panelJokers = new JPanel();
	private final JCheckBox cbJokers = new JCheckBox("Jokers");
	private final JSpinner jokerQuantitySpinner = new JSpinner();
	private final JLabel lblWorth = new JLabel("worth");
	private final JSpinner jokerValueSpinner = new JSpinner();
	private final JCheckBox cbNegativeJacks = new JCheckBox("Jacks worth -1");
	private final JCheckBox cbIncludeMoons = new JCheckBox("Include Moons");
	private final JCheckBox cbIncludeStars = new JCheckBox("Include Stars");
	private final JLabel lblWarning = new JLabel("Note: Changes will not take effect until you start a new game.");
	
	@Override
	public void initVariables()
	{
		getVariablesFromPreferences();

		cbJokers.setSelected(includeJokers);
		cbNegativeJacks.setSelected(negativeJacks);
		cbPlayersRevealCards.setSelected(cardReveal);
		jokerQuantitySpinner.setModel(new SpinnerNumberModel(jokerQuantity, 1, 4, 1));
		jokerValueSpinner.setModel(new SpinnerNumberModel(jokerValue, 2, 4, 1));
		numberOfCardsSlider.setValue(numberOfCards);
		jokerQuantitySpinner.setValue(jokerQuantity);
		jokerValueSpinner.setValue(jokerValue);
		cbPlayBlind.setSelected(playBlind);
		cbHandicap.setSelected(playWithHandicap);
		jokerQuantitySpinner.setEnabled(includeJokers);
		lblWorth.setEnabled(includeJokers);
		jokerValueSpinner.setEnabled(includeJokers);
		handicapAmountSpinner.setEnabled(playWithHandicap);
		
		adjustHandicapSpinner();
		
		cbIncludeStars.setSelected(includeStars);
		cbIncludeMoons.setSelected(includeMoons);
		
		rdbtnEntropy.setSelected(gameMode == GameMode.Entropy);
		rdbtnVectropy.setSelected(gameMode == GameMode.Vectropy);
		
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
		numberOfCards = numberOfCardsSlider.getValue();
		negativeJacks = cbNegativeJacks.isSelected();
		cardReveal = cbPlayersRevealCards.isSelected();
		jokerQuantity = includeJokers ? (int) jokerQuantitySpinner.getValue() : 0;
		jokerValue = (int) jokerValueSpinner.getValue();
		handicapAmount = (int) handicapAmountSpinner.getValue();
		playBlind = cbPlayBlind.isSelected();
		includeStars = cbIncludeStars.isSelected();
		includeMoons = cbIncludeMoons.isSelected();

		prefs.putInt(SHARED_INT_NUMBER_OF_CARDS, numberOfCards);
		prefs.putBoolean(SHARED_BOOLEAN_NEGATIVE_JACKS, negativeJacks);
		prefs.putInt(SHARED_INT_JOKER_QUANTITY, jokerQuantity);
		prefs.putInt(SHARED_INT_JOKER_VALUE, jokerValue);
		prefs.putBoolean(PREFERENCES_BOOLEAN_PLAY_WITH_HANDICAP, playWithHandicap);
		prefs.putInt(PREFERENCES_INT_HANDICAP_AMOUNT, handicapAmount);
		prefs.putBoolean(PREFERENCES_BOOLEAN_PLAY_BLIND, playBlind);
		prefs.putBoolean(SHARED_BOOLEAN_INCLUDE_STARS, includeStars);
		prefs.putBoolean(SHARED_BOOLEAN_INCLUDE_MOONS, includeMoons);
		prefs.putBoolean(SHARED_BOOLEAN_CARD_REVEAL, cardReveal);
		prefs.put(PREFERENCES_STRING_GAME_MODE, gameMode.name());
	}
	
	private void getVariablesFromPreferences()
	{
		numberOfCards = prefs.getInt(SHARED_INT_NUMBER_OF_CARDS, 5);
		jokerQuantity = prefs.getInt(SHARED_INT_JOKER_QUANTITY, 2);
		includeJokers = jokerQuantity > 0;
		jokerValue = prefs.getInt(SHARED_INT_JOKER_VALUE, 2);
		playWithHandicap = prefs.getBoolean(PREFERENCES_BOOLEAN_PLAY_WITH_HANDICAP, false);
		handicapAmount = Math.max(prefs.getInt(PREFERENCES_INT_HANDICAP_AMOUNT, 1), 1);
		playBlind = prefs.getBoolean(PREFERENCES_BOOLEAN_PLAY_BLIND, false);
		includeStars = prefs.getBoolean(SHARED_BOOLEAN_INCLUDE_STARS, false);
		includeMoons = prefs.getBoolean(SHARED_BOOLEAN_INCLUDE_MOONS, false);
		negativeJacks = prefs.getBoolean(SHARED_BOOLEAN_NEGATIVE_JACKS, false);
		cardReveal = prefs.getBoolean(SHARED_BOOLEAN_CARD_REVEAL, false);
		gameMode = GameMode.valueOf(prefs.get(PREFERENCES_STRING_GAME_MODE, GameMode.Entropy.name()));
	}
	
	private void adjustHandicapSpinner()
	{
		if (numberOfCards == 1)
		{
			cbHandicap.setSelected(false);
			cbHandicap.setEnabled(false);
			handicapAmountSpinner.setEnabled(false);
		}
		else
		{
			cbHandicap.setEnabled(true);
			handicapAmountSpinner.setEnabled(cbHandicap.isSelected());
			
			int newMax = numberOfCards - 1;
			if (handicapAmount > newMax)
			{
				handicapAmount = newMax;
			}
			
			handicapAmountSpinner.setModel(new SpinnerNumberModel(handicapAmount, 1, newMax, 1));
		}
	}
	
	private void hideLockedFields()
	{
		toggleLockedComponent(cbNegativeJacks, REWARDS_BOOLEAN_NEGATIVE_JACKS);
		toggleLockedComponent(cbPlayBlind, REWARDS_BOOLEAN_BLIND);
		toggleLockedComponent(rdbtnVectropy, REWARDS_BOOLEAN_VECTROPY);
		toggleLockedComponent(cbPlayersRevealCards, REWARDS_BOOLEAN_CARD_REVEAL);
		toggleLockedComponent(cbIncludeMoons, REWARDS_BOOLEAN_EXTRA_SUITS);
		toggleLockedComponent(cbIncludeStars, REWARDS_BOOLEAN_EXTRA_SUITS);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		GameMode originalGameMode = gameMode;
		
		Object source = arg0.getSource();
		if (source == rdbtnEntropy)
		{
			gameMode = GameMode.Entropy;
		}
		else if (source == rdbtnVectropy)
		{
			gameMode = GameMode.Vectropy;
		}
		
		if (originalGameMode != gameMode)
		{
			parent.gameModeChanged(gameMode);
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == numberOfCardsSlider)
		{
			if (!numberOfCardsSlider.getValueIsAdjusting())
			{
				numberOfCards = numberOfCardsSlider.getValue();
				adjustHandicapSpinner();
			}
		}
		else if (source == handicapAmountSpinner)
		{
			handicapAmount = (int)handicapAmountSpinner.getValue();
		}
		else
		{
			Debug.stackTrace("Unexpected stateChanged: [" + source + "]");
		}
		
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == cbJokers)
		{
			includeJokers = cbJokers.isSelected();
			lblWorth.setEnabled(includeJokers);
			jokerQuantitySpinner.setEnabled(includeJokers);
			jokerValueSpinner.setEnabled(includeJokers);
		}
		else if (source == cbHandicap)
		{
			playWithHandicap = cbHandicap.isSelected();
			handicapAmountSpinner.setEnabled(playWithHandicap);
		}
		else
		{
			Debug.stackTrace("Unexpected itemStateChanged: [" + source + "]");
		}
	}
}