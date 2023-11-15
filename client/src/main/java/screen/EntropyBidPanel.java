package screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import object.Bid;
import object.EntropyBid;
import util.CardsUtil;
import util.Debug;
import util.EntropyColour;
import util.Registry;

public class EntropyBidPanel extends BidPanel
							 implements ActionListener,
							 			ChangeListener,
							 			Registry
{
	private String suitSelected = CardsUtil.getSuitSymbolForCode(0);
	
	private int bidSuitCode = CardsUtil.SUIT_CLUBS;
	private int lastBidSuitCode = 0;
	private int lastBidAmount = 0;
	private boolean illegalAllowed = false;
	private boolean includeMoons = false;
	private boolean includeStars = false;
	private boolean online = false;
	
	public EntropyBidPanel() 
	{
		setPreferredSize(new Dimension(550, 150));
		bidGroup.add(btnClubs);
		bidGroup.add(btnDiamonds);
		bidGroup.add(btnHearts);
		bidGroup.add(btnMoons);
		bidGroup.add(btnSpades);
		bidGroup.add(btnStars);
		TransparentPanel panelSlider = new TransparentPanel();
		FlowLayout flowLayout = (FlowLayout) panelSlider.getLayout();
		flowLayout.setHgap(20);
		panelSlider.setPreferredSize(new Dimension(300, 65));
		setLayout(new BorderLayout(0, 0));
		add(panelSlider, BorderLayout.NORTH);
		
		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(60, 45));
		panelSlider.add(panel_2);
		
		TransparentPanel panel_1 = new TransparentPanel();
		panelSlider.add(panel_1);
		panel_1.setPreferredSize(new Dimension(340, 35));
		panel_1.setLayout(null);
		bidSlider.setBounds(10, 5, 250, 31);
		panel_1.add(bidSlider);
		bidSlider.setMinorTickSpacing(1);
		bidSlider.setPaintTicks(true);
		bidSlider.setToolTipText("");
		bidSlider.setMinimum(1);
		bidAmountDisplay.setOpaque(false);
		bidAmountDisplay.setBorder(BorderFactory.createEmptyBorder());
		bidAmountDisplay.setBackground(new Color(0,0,0,0));
		bidAmountDisplay.setLocation(280, 0);
		bidAmountDisplay.setSize(new Dimension(50, 30));
		panel_1.add(bidAmountDisplay);
		bidAmountDisplay.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
		bidAmountDisplay.setHorizontalAlignment(SwingConstants.CENTER);
		bidAmountDisplay.setColumns(1);
		bidAmountDisplay.setEditable(false);
		bidAmountDisplay.setBorder(new LineBorder(Color.BLACK,0));
		bidAmountDisplay.setFocusable(false);
		totalCardsPanel.setPreferredSize(new Dimension(60, 45));
		panelSlider.add(totalCardsPanel);
		totalCardsPanel.add(smallCardIcon);
		smallCardIcon.setIcon(new ImageIcon(EntropyScreen.class.getResource("/backs/backSmall.png")));
		totalCardsPanel.add(totalCardsLabel);
		FlowLayout fl_panelBidChallenge = (FlowLayout) panelBidChallenge.getLayout();
		fl_panelBidChallenge.setHgap(12);
		JPanel panelSuitButtons = new JPanel();
		add(panelSuitButtons, BorderLayout.CENTER);
		btnClubs.setSelectedIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/clubButtonSelected.png")));
		btnClubs.setIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/clubButton.png")));
		btnClubs.setPreferredSize(new Dimension(40, 40));
		panelSuitButtons.add(btnClubs);
		btnDiamonds.setSelectedIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/diamondButtonSelected.png")));
		btnDiamonds.setIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/diamondButton.png")));
		btnDiamonds.setPreferredSize(new Dimension(40, 40));
		panelSuitButtons.add(btnDiamonds);
		btnHearts.setSelectedIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/heartButtonSelected.png")));
		btnHearts.setIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/heartButton.png")));
		btnHearts.setPreferredSize(new Dimension(40, 40));
		panelSuitButtons.add(btnHearts);
		btnMoons.setSelectedIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/moonButtonSelected.png")));
		btnMoons.setIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/moonButton.png")));
		btnMoons.setPreferredSize(new Dimension(40, 40));
		panelSuitButtons.add(btnMoons);
		btnSpades.setSelectedIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/spadeButtonSelected.png")));
		btnSpades.setIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/spadeButton.png")));
		btnSpades.setPreferredSize(new Dimension(40, 40));
		panelSuitButtons.add(btnSpades);
		btnStars.setIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/starButton.png")));
		btnStars.setSelectedIcon(new ImageIcon(EntropyBidPanel.class.getResource("/buttons/starButtonSelected.png")));
		btnStars.setPreferredSize(new Dimension(40, 40));
		panelSuitButtons.add(btnStars);
		panelBidChallenge.add(btnBid);
		panelBidChallenge.add(btnChallenge);
		add(panelBidChallenge, BorderLayout.SOUTH);
		
		panelBidChallenge.add(btnIllegal);
		
		btnBid.addActionListener(this);
		btnChallenge.addActionListener(this);
		btnIllegal.addActionListener(this);
		btnClubs.addActionListener(this);
		btnDiamonds.addActionListener(this);
		btnHearts.addActionListener(this);
		btnMoons.addActionListener(this);
		btnSpades.addActionListener(this);
		btnStars.addActionListener(this);
		
		bidSlider.addChangeListener(this);
	}
	
	private final JButton btnBid = new JButton("Bid");
	private final JButton btnChallenge = new JButton("Challenge!");
	private final JToggleButton btnClubs = new JToggleButton("");
	private final JToggleButton btnDiamonds = new JToggleButton("");
	private final JToggleButton btnHearts = new JToggleButton("");
	private final JToggleButton btnMoons = new JToggleButton("");
	private final JToggleButton btnSpades = new JToggleButton("");
	private final JToggleButton btnStars = new JToggleButton("");
	private final JToggleButton[] bidButtons = { btnClubs, btnDiamonds, btnHearts, btnMoons, btnSpades, btnStars };
	private final ButtonGroup bidGroup = new ButtonGroup();
	private final JSlider bidSlider = new JSlider();
	private final JTextField bidAmountDisplay = new JTextField();
	private final JPanel totalCardsPanel = new JPanel();
	private final JLabel smallCardIcon = new JLabel("");
	private final JLabel totalCardsLabel = new JLabel("");
	private final JPanel panelBidChallenge = new JPanel();
	private final JButton btnIllegal = new JButton("Illegal!");
	
	@Override
	public void init(int maxBid, int totalNumberOfCards, boolean online, boolean includeMoons, boolean includeStars, boolean illegalAllowed)
	{
		this.online = online;
		this.includeMoons = includeMoons;
		this.includeStars = includeStars;
		this.illegalAllowed = illegalAllowed;
		this.maxBid = maxBid;
		
		setIllegalButtonState();
		btnMoons.setVisible(includeMoons);
		btnStars.setVisible(includeStars);
		
		bidSuitCode = CardsUtil.SUIT_CLUBS;
		lastBidAmount = 0;
		lastBidSuitCode = CardsUtil.SUIT_CLUBS;
		
		bidSlider.setMaximum(maxBid);
		bidSlider.setMinimum(1);
		bidSlider.setValue(1);

		btnClubs.setSelected(true);
		suitSelected = CardsUtil.getSuitSymbolForCode(0);
		bidAmountDisplay.setText("1 " + suitSelected);
		setBidAmountDisplayColour();
		setBidButtonColours();
		
		totalCardsLabel.setText("x " + totalNumberOfCards);
		String back = prefs.get(PREFERENCES_STRING_CARD_BACKS, Registry.BACK_CODE_CLASSIC_BLUE);
		smallCardIcon.setIcon(new ImageIcon(EntropyScreen.class.getResource("/backs/" + back + "Small.png")));
	}
	
	private void setIllegalButtonState()
	{
		if (!online)
		{
			illegalAllowed |= rewards.getBoolean(REWARDS_BOOLEAN_ILLEGAL, false);
		}
		
		btnIllegal.setVisible(illegalAllowed);
	}
	
	@Override
	public void showBidPanel(boolean visible)
	{
		totalCardsPanel.setVisible(visible);
		smallCardIcon.setVisible(visible);
		totalCardsLabel.setVisible(visible);
		btnSpades.setVisible(visible);
		btnHearts.setVisible(visible);
		btnDiamonds.setVisible(visible);
		btnClubs.setVisible(visible);
		btnMoons.setVisible(visible && includeMoons);
		btnStars.setVisible(visible && includeStars);
		btnBid.setVisible(visible);
		btnChallenge.setVisible(visible);
		btnIllegal.setVisible(visible && illegalAllowed);
		bidSlider.setVisible(visible);
		bidAmountDisplay.setVisible(visible);
	}
	
	@Override
	public void enableBidPanel(boolean enable)
	{
		if (enable)
		{
			Debug.appendBanner("Player", getLogging());
		}
		
		btnBid.setEnabled(enable);
		btnChallenge.setEnabled(enable);
		btnIllegal.setEnabled(enable);
		btnClubs.setEnabled(enable);
		btnDiamonds.setEnabled(enable);
		btnHearts.setEnabled(enable);
		btnMoons.setEnabled(enable);
		btnSpades.setEnabled(enable);
		btnStars.setEnabled(enable);
		bidSlider.setEnabled(enable);
	}
	
	@Override
	public void enableChallenge(boolean enable)
	{
		btnChallenge.setEnabled(enable);
		btnIllegal.setEnabled(enable);
	}
	
	@Override
	public void adjust(Bid bid)
	{
		EntropyBid entropyBid = (EntropyBid)bid;
		int lastBidAmount = entropyBid.getBidAmount();
		if (lastBidAmount == 0)
		{
			return;
		}
		
		this.lastBidSuitCode = entropyBid.getBidSuitCode();
		this.lastBidAmount = entropyBid.getBidAmount();
		
		if (lastBidSuitCode == CardsUtil.SUIT_HEARTS && !includeMoons)
		{
			bidSlider.setMinimum(lastBidAmount);
			bidButtons[(4)].setSelected(true);
			bidSuitCode = CardsUtil.SUIT_SPADES;
		}
		else if ((lastBidSuitCode == CardsUtil.SUIT_SPADES && !includeStars)
		  || lastBidSuitCode == CardsUtil.SUIT_STARS)
		{
			bidSlider.setMinimum(lastBidAmount + 1);
			btnClubs.setSelected(true);
			bidSuitCode = CardsUtil.SUIT_CLUBS;
		}
		else
		{
			bidSlider.setMinimum(lastBidAmount);
			bidButtons[(lastBidSuitCode + 1)].setSelected(true);
			bidSuitCode = lastBidSuitCode + 1;
		}
		
		suitSelected = CardsUtil.getSuitSymbolForCode(bidSuitCode);
		
		String spaceStr = bidSuitCode == CardsUtil.SUIT_MOONS ? "":" ";
		bidAmountDisplay.setText(bidSlider.getValue() + spaceStr + suitSelected);
		
		setBidAmountDisplayColour();
	}
	
	@Override
	public void fireAppearancePreferencesChange()
	{
		String back = prefs.get(PREFERENCES_STRING_CARD_BACKS, Registry.BACK_CODE_CLASSIC_BLUE);
		smallCardIcon.setIcon(new ImageIcon(EntropyScreen.class.getResource("/backs/" + back + "Small.png")));
		
		setBidAmountDisplayColour();
		setBidButtonColours();
	}
	
	private void setBidAmountDisplayColour()
	{
		String numberOfColoursStr = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
		boolean fourColours = (numberOfColoursStr.equals(Registry.FOUR_COLOURS));
		
		if (bidSuitCode == CardsUtil.SUIT_HEARTS)
		{
			bidAmountDisplay.setForeground(Color.red);
		}
		else if (bidSuitCode == CardsUtil.SUIT_SPADES)
		{
			bidAmountDisplay.setForeground(Color.black);
		}
		else if (bidSuitCode == CardsUtil.SUIT_STARS)
		{
			bidAmountDisplay.setForeground(EntropyColour.COLOUR_SUIT_GOLD);
		}
		else if (bidSuitCode == CardsUtil.SUIT_CLUBS)
		{
			if (fourColours)
			{
				bidAmountDisplay.setForeground(EntropyColour.COLOUR_SUIT_GREEN);
			}
			else
			{
				bidAmountDisplay.setForeground(Color.black);
			}
		}
		else if (bidSuitCode == CardsUtil.SUIT_DIAMONDS)
		{
			if (fourColours)
			{
				bidAmountDisplay.setForeground(Color.blue);
			}
			else
			{
				bidAmountDisplay.setForeground(Color.red);
			}
		}
		else if (bidSuitCode == CardsUtil.SUIT_MOONS)
		{
			if (fourColours)
			{
				bidAmountDisplay.setForeground(EntropyColour.COLOUR_SUIT_PURPLE);
			}
			else
			{
				bidAmountDisplay.setForeground(EntropyColour.COLOUR_SUIT_GOLD);
			}
		}
	}
	
	private void setBidButtonColours()
	{
		String numberOfColoursStr = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
		boolean fourColours = (numberOfColoursStr.equals(Registry.FOUR_COLOURS));
		
		if (fourColours)
		{
			btnClubs.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubButtonGreenSelected.png")));
			btnClubs.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubButtonGreen.png")));
			
			btnDiamonds.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondButtonBlueSelected.png")));
			btnDiamonds.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondButtonBlue.png")));
			
			btnMoons.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonButtonPurpleSelected.png")));
			btnMoons.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonButtonPurple.png")));
		}
		else
		{
			btnClubs.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubButtonSelected.png")));
			btnClubs.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/clubButton.png")));
			
			btnDiamonds.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondButtonSelected.png")));
			btnDiamonds.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/diamondButton.png")));
			
			btnMoons.setSelectedIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonButtonSelected.png")));
			btnMoons.setIcon(new ImageIcon(ReplayDialog.class.getResource("/buttons/moonButton.png")));
		}
	}
	
	@Override
	public void saveState(Preferences savedGame)
	{
		savedGame.putInt(SAVED_GAME_INT_BID_SUIT_CODE, bidSuitCode);
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_CHALLENGE_ENABLED, btnChallenge.isEnabled());
		savedGame.putBoolean(SAVED_GAME_BOOLEAN_ILLEGAL_ENABLED, btnIllegal.isEnabled());
		savedGame.put(SAVED_GAME_STRING_TOTAL_CARDS_LABEL, totalCardsLabel.getText());
	}
	
	@Override
	public void loadState(Preferences savedGame)
	{
		includeMoons = savedGame.getBoolean(SAVED_GAME_BOOLEAN_INCLUDE_MOONS, false);
		includeStars = savedGame.getBoolean(SAVED_GAME_BOOLEAN_INCLUDE_STARS, false);
		maxBid = savedGame.getInt(SAVED_GAME_INT_MAX_BID, 0);
		init(maxBid, -1, false, includeMoons, includeStars, false);
		
		setBidAmountDisplayColour();
		setBidButtonColours();

		suitSelected = CardsUtil.getSuitSymbolForCode(bidSuitCode);
		bidAmountDisplay.setText(bidSlider.getValue() + " " + suitSelected);
		
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
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		
		if (source == btnBid)
		{
			if (listener != null)
			{
				EntropyBid bid = new EntropyBid(bidSuitCode, bidSlider.getValue());
				listener.bidMade(bid);
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
		else if (source == btnClubs)
		{
			actionPerformedBidButton(CardsUtil.SUIT_CLUBS);
		}
		else if (source == btnDiamonds)
		{
			actionPerformedBidButton(CardsUtil.SUIT_DIAMONDS);
		}
		else if (source == btnHearts)
		{
			actionPerformedBidButton(CardsUtil.SUIT_HEARTS);
		}
		else if (source == btnMoons)
		{
			actionPerformedBidButton(CardsUtil.SUIT_MOONS);
		}
		else if (source == btnSpades)
		{
			actionPerformedBidButton(CardsUtil.SUIT_SPADES);
		}
		else if (source == btnStars)
		{
			actionPerformedBidButton(CardsUtil.SUIT_STARS);
		}
	}
	
	private void actionPerformedBidButton(int suitCode)
	{
		bidSuitCode = suitCode;
		if (lastBidSuitCode < suitCode)
		{
			bidSlider.setMinimum(Math.max(lastBidAmount, 1));
		}
		else
		{
			bidSlider.setMinimum(lastBidAmount + 1);
		}
		suitSelected = CardsUtil.getSuitSymbolForCode(suitCode);
		
		String spaceStr = suitCode == CardsUtil.SUIT_MOONS ? "":" ";
		bidAmountDisplay.setText(bidSlider.getValue() + spaceStr + suitSelected);
		setBidAmountDisplayColour();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) 
	{
		String spaceStr = bidSuitCode == CardsUtil.SUIT_MOONS ? "":" ";
		bidAmountDisplay.setText(bidSlider.getValue() + spaceStr + suitSelected);
	}
}
