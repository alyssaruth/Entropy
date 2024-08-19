package help;

import util.EntropyColour;

import javax.swing.*;
import java.awt.*;

public class RulesVectropyChallenging extends HelpPanel
{
	private final JTextPane title = new JTextPane();
	private final JTextPane paneOne = new JTextPane();

	public RulesVectropyChallenging() {
		setBackground(Color.WHITE);
		setNodeName("Challenging");
		setLayout(null);
		paneOne.setFont(new Font("SansSerif", Font.PLAIN, 14));
		paneOne.setContentType("text/html");
		paneOne.setText("<html>Any player whose turn it is to bid can choose to challenge instead. This means that the player does not believe that there are as many cards of one or more of the suits bid by the previous player. When a player challenges, all cards are revealed on the table and counted. The challenge is then evaluated.\r\n<br><br>\r\nIf a challenge is successful, the player who made the last bid loses the round, whereas if it is unsuccessful the player who challenged is the one to lose. Whether or not a challenge is successful is determined by how many of the four suits were actually present \u2013 it is successful if there were fewer of <b>any</b> of the suits bid, and unsuccessful otherwise.\r\n<br><br>\r\nFor example, in a game with 3 clubs, 4 diamonds, 2 hearts and 2 spades, challenging a bid of (3, 2, 1, 0) would be unsuccessful as there were at least as many cards as bid in all four suits. A bid of (3, 3, 0, 3), however, contains more spades than are present, so challenging this would be successful.</html>");
		paneOne.setBounds(21, 54, 429, 353);
		add(paneOne);
		title.setText("Challenging");
		title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		title.setFont(new Font("Tahoma", Font.BOLD, 18));
		title.setEditable(false);
		title.setBounds(21, 25, 216, 30);
		add(title);

		finaliseComponents();
	}

	@Override
	public String getPanelName() {
		return "RulesVectropyChallenging";
	}

	@Override
	protected String[] searchTermsToExclude() {
		return new String[]{"challeng"};
	}
}