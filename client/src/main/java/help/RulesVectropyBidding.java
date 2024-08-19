package help;

import screen.ScreenCache;
import util.EntropyColour;
import util.Registry;

import javax.swing.*;
import java.awt.*;

public class RulesVectropyBidding extends HelpPanel
								  implements Registry
{
	private final JTextPane title = new JTextPane();
	private final JTextPane paneOne = new JTextPane();
	private final JTextPane subtitle = new JTextPane();
	private final JTextPane paneTwo = new JTextPane();
	
	private String clubsColour = "black";
	private String diamondsColour = "red";

	public RulesVectropyBidding() {
		setBackground(Color.WHITE);
		setNodeName("Bidding");
		setLayout(null);
		paneOne.setFont(new Font("SansSerif", Font.PLAIN, 14));
		paneOne.setContentType("text/html");
		paneOne.setBounds(21, 54, 429, 230);
		paneOne.setEditable(false);
		add(paneOne);
		paneTwo.setFont(new Font("SansSerif", Font.PLAIN, 14));
		paneTwo.setContentType("text/html");
		paneTwo.setText("<html>A bid is higher than another if the sum of its elements is higher, with the added restriction that each bid must include at least as many of each individual suit as the one before it. For example, if faced with a bid of (0, 0, 0, 2):\r\n\r\n<ul style=\"margin-left:10px; padding:0px\">\r\n<li style=\"margin-bottom: 6px;\"> (1, 0, 0, 2) is a valid higher bid because it includes (0, 0, 0, 2).</li>\r\n<li style=\"margin-bottom:6px;\"> (5, 5, 5, 0) is a higher bid, but this is <b>not</b> legal because it contains fewer spades than the bid before it.</li></ul></html>");
		paneTwo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		paneTwo.setBounds(21, 320, 429, 156);
		paneTwo.setEditable(false);
		add(paneTwo);
		title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		title.setFont(new Font("Tahoma", Font.BOLD, 18));
		title.setText("Bidding");
		title.setBounds(21, 25, 159, 30);
		title.setEditable(false);
		add(title);
		subtitle.setText("Bid Hierarchy");
		subtitle.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		subtitle.setFont(new Font("Tahoma", Font.BOLD, 18));
		subtitle.setBounds(21, 290, 159, 30);
		subtitle.setEditable(false);
		add(subtitle);

		finaliseComponents();
	}

	@Override
	public String getPanelName() {
		return "RulesVectropyBidding";
	}

	@Override
	protected String[] searchTermsToExclude() {
		return new String[]{"bidding"};
	}

	@Override
	public void fireAppearancePreferencesChange()
	{
		boolean fourColours = ScreenCache.getHelpDialog().fourColours;
		clubsColour = fourColours?"green":"black";
		diamondsColour = fourColours?"blue":"red";
	}
	
	@Override
	public void refresh() 
	{
		setPaneOneText();
	}
	
	private void setPaneOneText()
	{
		boolean extraSuits = rewards.getBoolean(REWARDS_BOOLEAN_EXTRA_SUITS, false);
		
		String text = "<html>A round starts with the first person to play bidding. At the start of a new game, ";
		text += "the round is started by a player chosen at random. In each subsequent round the loser of the previous ";
		text += "round starts, unless losing the last round caused them to go out. In this case, the person to the left ";
		text += "of the losing player starts the next round.\r\n<br><br>\r\nEach bid is an ordered vector of four numbers. ";
		text += "These numbers represent the amount that is being bid for each suit from lowest to highest - ";
		text += "(<font color=\"" + clubsColour + "\">\u2663</font>,  <font color=\"" + diamondsColour + "\">\u2666</font>, ";
		text += "<font color=\"red\">\u2665</font>, \u2660). ";
		
		if (extraSuits)
		{
			text += "This vector naturally extends if additional suits are in play. ";
		}
		
		text += "Each subsequent bid must be higher than the last. Bidding continues clockwise round the players. ";
		text += "At any point a player may opt to <u><font color=\"blue\">challenge</font></u> the current bid rather than making a higher bid of their own.</html>\r\n\r\n\r\n";
		
		paneOne.setText(text);
	}
}