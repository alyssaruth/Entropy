package help;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;

import util.Debug;
import util.EntropyColour;

@SuppressWarnings("serial")
public class RulesEntropyBidding extends HelpPanel
{
	private String panelName = "RulesEntropyBidding";
	private JTextPane title = new JTextPane();
	private JTextPane paneOne = new JTextPane();
	private JTextPane subtitle = new JTextPane();
	private JTextPane paneTwo = new JTextPane();
	private JTextPane[] textFields = {title, paneOne, subtitle, paneTwo};
	
	public RulesEntropyBidding()
	{
		try
		{
			setBackground(Color.WHITE);
			setPanelName(panelName);
			setTextFields(textFields);
			addMouseListeners("bidding");
			setNodeName("Bidding");
			setLayout(null);
			paneOne.setFont(new Font("SansSerif", Font.PLAIN, 14));
			paneOne.setContentType("text/html");
			paneOne.setText("<html>A round starts with the first person to play bidding. At the start of a new game, the round is started by a player chosen at random. In each subsequent round the loser of the previous round starts, unless losing the last round caused them to go out. In this case, the person to the left of the losing player starts the next round.\r\n<br><br>\r\nEach bid must be a suit and a number, for example \"2 Spades\". Each subsequent bid must be higher than the last, including adhering to the suit <u><font color=\"blue\">order</font></u>. Bidding continues clockwise round the players. At any point a player may opt to <u><font color=\"blue\">challenge</font></u> the current bid rather than making a higher bid of their own.</html>");
			paneOne.setBounds(21, 54, 429, 220);
			paneOne.setEditable(false);
			add(paneOne);
			paneTwo.setFont(new Font("SansSerif", Font.PLAIN, 14));
			paneTwo.setContentType("text/html");
			paneTwo.setText("A bid is higher than another if the number is higher or if the number is equal and the suit is higher. For example, \"3 Hearts\" is higher than \"2 Hearts\", as 3 is greater than 2. \"3 Hearts\" is lower than \"3 Spades\" because spades is a higher suit than hearts. ");
			paneTwo.setBounds(21, 313, 429, 100);
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
			subtitle.setBounds(21, 284, 159, 30);
			subtitle.setEditable(false);
			add(subtitle);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
}