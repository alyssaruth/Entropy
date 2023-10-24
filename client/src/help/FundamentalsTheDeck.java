package help;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import screen.ScreenCache;
import util.CardsUtil;
import util.Debug;
import util.EntropyColour;
import util.Registry;

@SuppressWarnings("serial")
public class FundamentalsTheDeck extends HelpPanel
								 implements Registry
{
	private String clubString = "clubs (<font color = \"black\">\u2663</font>)";
	private String diamondString = "diamonds (<font color=\"red\">\u2666</font>)";
	private String moonString = null;
	
	private String panelName = "FundamentalsTheDeck";
	private JTextPane title = new JTextPane();
	private JTextPane paneOne = new JTextPane();
	private JTextPane[] textFields = {title, paneOne};
	
	private final JLabel clubLabel = new JLabel("\u2663");
	private final JLabel diamondLabel = new JLabel("\u2666");
	private final JLabel heartLabel = new JLabel("\u2665");
	private final JLabel moonLabel = new JLabel(CardsUtil.MOONS_SYMBOL);
	private final JLabel spadeLabel = new JLabel("\u2660");
	private final JLabel starLabel = new JLabel(CardsUtil.STARS_SYMBOL);
	private final JLabel label_3 = new JLabel("<");
	private final JLabel label_4 = new JLabel("<");
	private final JLabel label_5 = new JLabel("<");
	private final JLabel rightmostLabel = new JLabel("<");
	private final JLabel leftmostLabel = new JLabel("<");
	
	public FundamentalsTheDeck()
	{
		try
		{
			setBackground(Color.WHITE);
			setPanelName(panelName);
			setTextFields(textFields);
			addMouseListeners("bidding");
			setNodeName("The Deck");
			setLayout(null);
			paneOne.setFont(new Font("SansSerif", Font.PLAIN, 14));
			paneOne.setContentType("text/html");
			paneOne.setBounds(21, 54, 429, 310);
			add(paneOne);
			title.setText("The Deck");
			title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
			title.setFont(new Font("Tahoma", Font.BOLD, 18));
			title.setEditable(false);
			title.setBounds(21, 25, 165, 30);
			add(title);
			clubLabel.setHorizontalAlignment(SwingConstants.CENTER);
			clubLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
			clubLabel.setBounds(80, 360, 65, 60);
			add(clubLabel);
			label_4.setHorizontalAlignment(SwingConstants.CENTER);
			label_4.setFont(new Font("Arial", Font.PLAIN, 40));
			label_4.setBounds(120, 360, 65, 60);
			add(label_4);
			diamondLabel.setHorizontalAlignment(SwingConstants.CENTER);
			diamondLabel.setForeground(Color.RED);
			diamondLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
			diamondLabel.setBounds(160, 360, 65, 60);
			add(diamondLabel);
			label_3.setHorizontalAlignment(SwingConstants.CENTER);
			label_3.setFont(new Font("Arial", Font.PLAIN, 40));
			label_3.setBounds(200, 360, 65, 60);
			add(label_3);
			heartLabel.setHorizontalAlignment(SwingConstants.CENTER);
			heartLabel.setForeground(Color.RED);
			heartLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
			heartLabel.setBounds(240, 360, 65, 60);
			add(heartLabel);
			moonLabel.setHorizontalAlignment(SwingConstants.CENTER);
			moonLabel.setForeground(EntropyColour.COLOUR_SUIT_PURPLE);
			moonLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 32));
			moonLabel.setBounds(240, 360, 65, 60);
			add(moonLabel);
			label_5.setHorizontalAlignment(SwingConstants.CENTER);
			label_5.setFont(new Font("Arial", Font.PLAIN, 40));
			label_5.setBounds(280, 360, 65, 60);
			add(label_5);
			spadeLabel.setHorizontalAlignment(SwingConstants.CENTER);
			spadeLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
			spadeLabel.setBounds(320, 360, 65, 60);
			add(spadeLabel);
			rightmostLabel.setHorizontalAlignment(SwingConstants.CENTER);
			rightmostLabel.setFont(new Font("Arial", Font.PLAIN, 40));
			rightmostLabel.setBounds(360, 360, 65, 60);
			add(rightmostLabel);
			leftmostLabel.setHorizontalAlignment(SwingConstants.CENTER);
			leftmostLabel.setFont(new Font("Arial", Font.PLAIN, 40));
			leftmostLabel.setBounds(40, 360, 65, 60);
			add(leftmostLabel);
			starLabel.setHorizontalAlignment(SwingConstants.CENTER);
			starLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
			starLabel.setBounds(400, 360, 65, 60);
			starLabel.setForeground(EntropyColour.COLOUR_SUIT_GOLD);
			add(starLabel);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void setPaneOneText(boolean moonsAndStars)
	{
		String paneOneText = "<html>For the standard game, a normal deck of 52 cards is used. This deck is made up of four suits: "
						+ clubString + ", " + diamondString + ", hearts (<font color=\"red\">\u2665</font>) and spades (\u2660), "
						+ "each of 13 cards. ";
		
		if (moonsAndStars)
		{
			paneOneText += "Two optional suits, " + moonString + " and stars ";
			paneOneText += "(<font color=\"CC9900\">" + CardsUtil.STARS_SYMBOL + "</font>) can also be added to the deck.";
		}
		
		paneOneText += "The 13 cards in each suit are the 13 ranks of cards: ace (A), two (2), three (3), four (4), "
					+ "five (5), six (6), seven (7), eight (8), nine (9), ten (T), jack (J), queen (Q) and king (K). "
					+ "\r\n<br><br>\r\nWhen playing Entropy and its variants, the focus is on the suit as that\u2019s what is used during the bidding. "
					+ "Each card is worth one of its own suit, irrespective of rank. For example, the five of hearts (5<font color=\"red\">\u2665</font>) "
					+ "is worth one heart. However, aces are special cards. Not only are they worth one of their own suit, but they are also worth an extra one of all the suits. "
					+ "This means that the ace of spades (A\u2660) is worth one club, one diamond, one heart and two spades (one for being a spade and one for being an ace)."
					+ "\r\n<br><br>\r\nTo define whether one bid is higher than another, the suits are also ordered as follows:</html>\r\n";
		
		paneOne.setText(paneOneText);
	}
	
	@Override
	public void fireAppearancePreferencesChange()
	{
		boolean fourColours = ScreenCache.getHelpDialog().fourColours;
		String clubsColour = fourColours?"green":"black";
		String diamondsColour = fourColours?"blue":"red";
		String moonsColour = fourColours?"purple":"CC9900";
		
		clubString = "clubs (<font color = \"" + clubsColour + "\">\u2663</font>)";
		diamondString = "diamonds (<font color=\"" + diamondsColour + "\">\u2666</font>)";
		moonString = "moons (<font face=\"Segoe UI Symbol\" color=\"" + moonsColour + "\">" + CardsUtil.MOONS_SYMBOL + "</font>)";
		
		boolean moonsAndStars = rewards.getBoolean(REWARDS_BOOLEAN_EXTRA_SUITS, false);
		setPaneOneText(moonsAndStars);
		
		if (fourColours)
		{
			clubLabel.setForeground(new Color(0, 128, 0));
			diamondLabel.setForeground(Color.BLUE);
			moonLabel.setForeground(EntropyColour.COLOUR_SUIT_PURPLE);
		}
		else
		{
			clubLabel.setForeground(Color.black);
			diamondLabel.setForeground(new Color(255, 0, 0));
			moonLabel.setForeground(EntropyColour.COLOUR_SUIT_GOLD);
		}
	}
	
	private void refreshSuitRankingVisibility()
	{
		boolean moonsAndStars = rewards.getBoolean(REWARDS_BOOLEAN_EXTRA_SUITS, false);
		
		if (moonsAndStars)
		{
			moonLabel.setVisible(true);
			starLabel.setVisible(true);
			rightmostLabel.setVisible(true);
			leftmostLabel.setVisible(true);
			clubLabel.setBounds(0, 360, 65, 60);
			diamondLabel.setBounds(80, 360, 65, 60);
			heartLabel.setBounds(160, 360, 65, 60);
		}
		else
		{
			moonLabel.setVisible(false);
			starLabel.setVisible(false);
			rightmostLabel.setVisible(false);
			leftmostLabel.setVisible(false);
			clubLabel.setBounds(80, 360, 65, 60);
			diamondLabel.setBounds(160, 360, 65, 60);
			heartLabel.setBounds(240, 360, 65, 60);
		}
	}
	
	@Override
	public void refresh() 
	{
		fireAppearancePreferencesChange();
		refreshSuitRankingVisibility();
	}
}