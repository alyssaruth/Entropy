package screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import object.Bid;
import object.VectropyBid;
import util.CardsUtil;
import util.Debug;
import util.EntropyColour;
import util.Registry;
import util.VectropyUtil;

public class VectropyBidPanel extends BidPanel
							  implements ActionListener,
							             ChangeListener,
							             Registry
{
	private VectropyBid lastBid = VectropyUtil.getEmptyBid(false, false);
	private boolean illegalAllowed = false;
	private boolean includeMoons = false;
	private boolean includeStars = false;
	private boolean online = false;
	
	public VectropyBidPanel()
	{
		setLayout(new BorderLayout(0, 0));
		updateSpinnerColours();
		
		setPreferredSize(new Dimension(540, 150));
		bidChallengePanel.setPreferredSize(new Dimension(10, 55));
		bidChallengePanel.setAlignmentY(Component.TOP_ALIGNMENT);
		add(bidChallengePanel, BorderLayout.SOUTH);
		bidChallengePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 5));
		bidChallengePanel.add(btnBid);
		bidChallengePanel.add(btnChallenge);
		bidChallengePanel.add(btnIllegal);
		bidChallengePanel.setOpaque(false);
		FlowLayout fl_topPanel = (FlowLayout) topPanel.getLayout();
		fl_topPanel.setHgap(30);
		
		add(topPanel, BorderLayout.CENTER);
		leftPaddingPanel.setPreferredSize(new Dimension(60, 45));
		
		topPanel.add(leftPaddingPanel);
		
		JPanel suitsPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) suitsPanel.getLayout();
		flowLayout.setHgap(15);
		topPanel.add(suitsPanel);
		
		JPanel clubsPanel = new JPanel();
		clubsPanel.setPreferredSize(new Dimension(50, 70));
		suitsPanel.add(clubsPanel);
		clubsPanel.setLayout(new GridLayout(0, 1, 5, 0));
		clubsPanel.add(clubLabel);
		clubLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
		clubLabel.setOpaque(false);
		clubLabel.setBorder(BorderFactory.createEmptyBorder());
		clubLabel.setBackground(new Color(0,0,0,0));
		clubLabel.setHorizontalAlignment(SwingConstants.CENTER);
		clubLabel.setColumns(10);
		clubLabel.setFocusable(false);
		clubLabel.setEditable(false);
		clubsPanel.add(clubSpinner);
		clubSpinner.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JPanel diamondsPanel = new JPanel();
		diamondsPanel.setPreferredSize(new Dimension(50, 70));
		suitsPanel.add(diamondsPanel);
		diamondsPanel.setLayout(new GridLayout(0, 1, 5, 0));
		diamondsPanel.add(diamondLabel);
		diamondLabel.setHorizontalAlignment(SwingConstants.CENTER);
		diamondLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
		diamondLabel.setColumns(10);
		diamondLabel.setOpaque(false);
		diamondLabel.setBorder(BorderFactory.createEmptyBorder());
		diamondLabel.setBackground(new Color(0,0,0,0));
		diamondLabel.setFocusable(false);
		diamondLabel.setEditable(false);
		diamondsPanel.add(diamondSpinner);
		diamondSpinner.setFont(new Font("Tahoma", Font.PLAIN, 16));
		heartsPanel.setPreferredSize(new Dimension(50, 70));
		
		suitsPanel.add(heartsPanel);
		heartsPanel.setLayout(new GridLayout(0, 1, 5, 0));
		heartsPanel.add(heartLabel);
		heartLabel.setHorizontalAlignment(SwingConstants.CENTER);
		heartLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
		heartLabel.setColumns(10);
		heartLabel.setOpaque(false);
		heartLabel.setBorder(BorderFactory.createEmptyBorder());
		heartLabel.setBackground(new Color(0,0,0,0));
		heartLabel.setForeground(Color.RED);
		heartLabel.setFocusable(false);
		heartLabel.setEditable(false);
		heartsPanel.add(heartSpinner);
		heartSpinner.setFont(new Font("Tahoma", Font.PLAIN, 16));
		moonsPanel.setPreferredSize(new Dimension(50, 70));
		
		suitsPanel.add(moonsPanel);
		moonsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		moonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		moonLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
		moonLabel.setFocusable(false);
		moonLabel.setEditable(false);
		moonLabel.setColumns(10);
		moonLabel.setOpaque(false);
		moonLabel.setBorder(BorderFactory.createEmptyBorder());
		moonLabel.setBackground(new Color(0,0,0,0));
		
		moonsPanel.add(moonLabel);
		moonSpinner.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		moonsPanel.add(moonSpinner);
		spadesPanel.setPreferredSize(new Dimension(50, 70));
		
		suitsPanel.add(spadesPanel);
		spadesPanel.setLayout(new GridLayout(0, 1, 0, 0));
		spadesPanel.add(spadeLabel);
		spadeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		spadeLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
		spadeLabel.setColumns(10);
		spadeLabel.setOpaque(false);
		spadeLabel.setBorder(BorderFactory.createEmptyBorder());
		spadeLabel.setBackground(new Color(0,0,0,0));
		spadeLabel.setFocusable(false);
		spadeLabel.setEditable(false);
		spadesPanel.add(spadeSpinner);
		spadeSpinner.setFont(new Font("Tahoma", Font.PLAIN, 16));
		starsPanel.setPreferredSize(new Dimension(50, 70));
		
		suitsPanel.add(starsPanel);
		starsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		starLabel.setHorizontalAlignment(SwingConstants.CENTER);
		starLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
		starLabel.setFocusable(false);
		starLabel.setEditable(false);
		starLabel.setColumns(10);
		starLabel.setOpaque(false);
		starLabel.setBorder(BorderFactory.createEmptyBorder());
		starLabel.setBackground(new Color(0,0,0,0));
		starLabel.setForeground(EntropyColour.COLOUR_SUIT_GOLD);
		
		starsPanel.add(starLabel);
		starSpinner.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		starsPanel.add(starSpinner);
		totalCardsPanel.setPreferredSize(new Dimension(60, 45));
		topPanel.add(totalCardsPanel);
		totalCardsPanel.add(smallCardIcon);
		totalCardsPanel.add(totalCardsLabel);
		
		starSpinner.addChangeListener(this);
		spadeSpinner.addChangeListener(this);
		moonSpinner.addChangeListener(this);
		heartSpinner.addChangeListener(this);
		diamondSpinner.addChangeListener(this);
		clubSpinner.addChangeListener(this);
		
		btnBid.addActionListener(this);
		btnChallenge.addActionListener(this);
		btnIllegal.addActionListener(this);
	}
	
	private final JButton btnBid = new JButton("Bid");
	private final JButton btnChallenge = new JButton("Challenge!");
	private final JButton btnIllegal = new JButton("Illegal!");
	private final JPanel bidChallengePanel = new JPanel();
	private final JPanel totalCardsPanel = new JPanel();
	private final JLabel smallCardIcon = new JLabel("");
	private final JLabel totalCardsLabel = new JLabel("");
	private final JSpinner clubSpinner = new JSpinner();
	private final JSpinner diamondSpinner = new JSpinner();
	private final JSpinner heartSpinner = new JSpinner();
	private final JSpinner spadeSpinner = new JSpinner();
	private final JTextField clubLabel = new JTextField(CardsUtil.getSuitSymbolForCode(0));
	private final JTextField diamondLabel = new JTextField(CardsUtil.getSuitSymbolForCode(1));
	private final JTextField heartLabel = new JTextField(CardsUtil.getSuitSymbolForCode(2));
	private final JTextField spadeLabel = new JTextField(CardsUtil.getSuitSymbolForCode(4));
	private final JPanel heartsPanel = new JPanel();
	private final JPanel spadesPanel = new JPanel();
	private final JPanel topPanel = new JPanel();
	private final JPanel leftPaddingPanel = new JPanel();
	private final JPanel moonsPanel = new JPanel();
	private final JTextField moonLabel = new JTextField(CardsUtil.getSuitSymbolForCode(3));
	private final JSpinner moonSpinner = new JSpinner();
	private final JPanel starsPanel = new JPanel();
	private final JTextField starLabel = new JTextField(CardsUtil.getSuitSymbolForCode(5));
	private final JSpinner starSpinner = new JSpinner();
	
	@Override
	public void init(int maxBid, int totalNumberOfCards, boolean online, boolean includeMoons, boolean includeStars, boolean illegalAllowed)
	{
		this.online = online;
		this.includeMoons = includeMoons;
		this.includeStars = includeStars;
		this.illegalAllowed = illegalAllowed;
		this.maxBid = maxBid;
		
		setIllegalButtonState();
		moonsPanel.setVisible(includeMoons);
		starsPanel.setVisible(includeStars);
		
		totalCardsLabel.setText("x " + totalNumberOfCards);
		String back = prefs.get(PREFERENCES_STRING_CARD_BACKS, Registry.BACK_CODE_CLASSIC_BLUE);
		smallCardIcon.setIcon(new ImageIcon(EntropyScreen.class.getResource("/backs/" + back + "Small.png")));
		
		lastBid = VectropyUtil.getEmptyBid(includeMoons, includeStars);
		adjust(lastBid);
	}
	
	@Override
	public void adjust(Bid bid)
	{
		VectropyBid lastBid = (VectropyBid)bid;
		this.lastBid = lastBid;
		
		int clubs = lastBid.getClubs();
		int diamonds = lastBid.getDiamonds();
		int hearts = lastBid.getHearts();
		int moons = lastBid.getMoons();
		int spades = lastBid.getSpades();
		int stars = lastBid.getStars();
		
		clubSpinner.setModel(new SpinnerNumberModel(clubs, clubs, maxBid, 1));
		diamondSpinner.setModel(new SpinnerNumberModel(diamonds, diamonds, maxBid, 1));
		heartSpinner.setModel(new SpinnerNumberModel(hearts, hearts, maxBid, 1));
		moonSpinner.setModel(new SpinnerNumberModel(moons, moons, maxBid, 1));
		spadeSpinner.setModel(new SpinnerNumberModel(spades, spades, maxBid, 1));
		starSpinner.setModel(new SpinnerNumberModel(stars, stars, maxBid, 1));
	}
	
	private void setIllegalButtonState()
	{
		if (!online)
		{
			illegalAllowed |= rewards.getBoolean(REWARDS_BOOLEAN_ILLEGAL, false);
		}
		
		btnIllegal.setVisible(illegalAllowed);
	}
	
	private void updateSpinnerColours()
	{
		String numberOfColoursStr = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
		boolean fourColours = (numberOfColoursStr.equals(Registry.FOUR_COLOURS));
		
		if (fourColours)
		{
			clubLabel.setForeground(EntropyColour.COLOUR_SUIT_GREEN);
			diamondLabel.setForeground(Color.BLUE);
			moonLabel.setForeground(EntropyColour.COLOUR_SUIT_PURPLE);
		}
		else
		{
			clubLabel.setForeground(Color.BLACK);
			diamondLabel.setForeground(Color.RED);
			moonLabel.setForeground(EntropyColour.COLOUR_SUIT_GOLD);
		}
	}
	
	private void setBidButtonState()
	{
		VectropyBid currentSelection = getBidFromSpinners();
		boolean enabled = currentSelection.higherThan(lastBid);
		btnBid.setEnabled(enabled);
	}
	
	private VectropyBid getBidFromSpinners()
	{
		int clubs = (int)clubSpinner.getValue();
		int diamonds = (int)diamondSpinner.getValue();
		int hearts = (int)heartSpinner.getValue();
		
		int moons = 0;
		if (includeMoons)
		{
			moons = (int)moonSpinner.getValue();
		}
		
		int spades = (int)spadeSpinner.getValue();
		
		int stars = 0;
		if (includeStars)
		{
			stars = (int)starSpinner.getValue();
		}
		
		return new VectropyBid(clubs, diamonds, hearts, moons, spades, stars, includeMoons, includeStars);
	}
	
	@Override
	public void fireAppearancePreferencesChange()
	{
		String back = prefs.get(PREFERENCES_STRING_CARD_BACKS, Registry.BACK_CODE_CLASSIC_BLUE);
		smallCardIcon.setIcon(new ImageIcon(EntropyScreen.class.getResource("/backs/" + back + "Small.png")));
		
		updateSpinnerColours();
	}
	
	@Override
	public void saveState(Preferences savedGame)
	{
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_CHALLENGE_ENABLED, btnChallenge.isEnabled());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_ILLEGAL_ENABLED, btnIllegal.isEnabled());
		savedGame.put(SAVED_GAME_STRING_TOTAL_CARDS_LABEL, totalCardsLabel.getText());
	}
	
	@Override
	public void loadState(Preferences savedGame)
	{
		includeMoons = savedGame.getBoolean(SHARED_BOOLEAN_INCLUDE_MOONS, false);
		includeStars = savedGame.getBoolean(SHARED_BOOLEAN_INCLUDE_STARS, false);
		maxBid = savedGame.getInt(SAVED_GAME_INT_MAX_BID, 0);
		init(maxBid, -1, false, includeMoons, includeStars, false);
		
		setIllegalButtonState();
		
		boolean challengeEnabled = savedGame.getBoolean(SAVED_GAME_BOOLEAN_CHALLENGE_ENABLED, true);
		btnChallenge.setEnabled(challengeEnabled);
		boolean illegalEnabled = savedGame.getBoolean(SAVED_GAME_BOOLEAN_ILLEGAL_ENABLED, true);
		btnIllegal.setEnabled(illegalEnabled);

		String label = savedGame.get(SAVED_GAME_STRING_TOTAL_CARDS_LABEL, "0");
		totalCardsLabel.setText(label);
		
		String back = prefs.get(PREFERENCES_STRING_CARD_BACKS, Registry.BACK_CODE_CLASSIC_BLUE);
		smallCardIcon.setIcon(new ImageIcon(EntropyScreen.class.getResource("/backs/" + back + "Small.png")));
	}
	
	@Override
	public void showBidPanel(boolean visible)
	{
		totalCardsPanel.setVisible(visible);
		smallCardIcon.setVisible(visible);
		totalCardsLabel.setVisible(visible);
		btnBid.setVisible(visible);
		btnChallenge.setVisible(visible);
		btnIllegal.setVisible(visible && illegalAllowed);
		clubSpinner.setVisible(visible);
		diamondSpinner.setVisible(visible);
		heartSpinner.setVisible(visible);
		spadeSpinner.setVisible(visible);
		clubLabel.setVisible(visible);
		diamondLabel.setVisible(visible);
		heartLabel.setVisible(visible);
		spadeLabel.setVisible(visible);
		starLabel.setVisible(visible);
		
		moonsPanel.setVisible(visible && includeMoons);
		starsPanel.setVisible(visible && includeStars);
	}
	
	@Override
	public void enableBidPanel(boolean enabled)
	{
		if (enabled)
		{
			Debug.appendBanner("Player", getLogging());
		}
		
		btnBid.setEnabled(false);
		btnChallenge.setEnabled(enabled);
		btnIllegal.setEnabled(enabled);
		clubSpinner.setEnabled(enabled);
		diamondSpinner.setEnabled(enabled);
		heartSpinner.setEnabled(enabled);
		moonSpinner.setEnabled(enabled && includeMoons);
		spadeSpinner.setEnabled(enabled);
		starSpinner.setEnabled(enabled && includeStars);
	}
	
	@Override
	public void enableChallenge(boolean enabled)
	{
		btnChallenge.setEnabled(enabled);
		btnIllegal.setEnabled(enabled);
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) 
	{
		setBidButtonState();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		
		if (source == btnBid)
		{
			if (listener != null)
			{
				listener.bidMade(getBidFromSpinners());
			}
		}
		else if (source == btnChallenge)
		{
			if (listener != null)
			{
				listener.challengeMade();
			}
		}
		else if (source == btnIllegal)
		{
			if (listener != null)
			{
				listener.illegalCalled();
			}
		}
	}

}
