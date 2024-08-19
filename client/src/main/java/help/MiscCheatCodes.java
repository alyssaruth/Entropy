package help;

import util.EntropyColour;

import javax.swing.*;
import java.awt.*;

public class MiscCheatCodes extends HelpPanel
{
	private final JTextPane title = new JTextPane();
	private final JTextPane paneIntro = new JTextPane();
	private final JTextPane paneOne = new JTextPane();

	private final String cheatsText = "<html><b>- showmethecards: </b>Turn all hands face-up!<br><br>"
									 + "<b>- maxbids: </b> Lists the amount of each suit present in the current game.<br><br>"
					 				 + "<b>- perfectbid: </b> Gives you the <u><font color=\"blue\">perfect</u></font> bid for the current round. Returns the same value as maxbids if playing in Vectropy mode.<br><br>"
									 + "<b>- rainingjokers: </b> Randomly turns 1-5 of the cards in play into jokers.<br><br>"
									 + "<b>- bluescreenofdeath: </b> Unlocks the hidden 'Blue Screen of Death' achievement (pfft, like you've not already got it).<br><br>"
									 + "<b>- simulator: </b> Opens the Entropy Simulator. This was used during development to pit strategies against one another so that we could test enhancements and rank them.</html>";

	public MiscCheatCodes() {
		setBackground(Color.WHITE);
		setNodeName("Cheat Codes");
		setLayout(null);
		paneOne.setFont(new Font("SansSerif", Font.PLAIN, 14));
		paneOne.setContentType("text/html");
		paneOne.setText(cheatsText);
		paneOne.setBounds(21, 156, 429, 317);
		add(paneOne);
		title.setText("Cheat Codes");
		title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		title.setFont(new Font("Tahoma", Font.BOLD, 18));
		title.setEditable(false);
		title.setBounds(21, 25, 192, 30);
		add(title);
		paneIntro.setFont(new Font("SansSerif", Font.PLAIN, 14));
		paneIntro.setContentType("text/html");
		paneIntro.setText("<html>Congratulations on reaching 50 achievements! Here are some cheats you can enter whilst playing the game, which I originally created to make testing easier. Whilst in the main window, press CTRL+; to bring up the command bar, then enter any of the following commands:</html>");
		paneIntro.setBounds(21, 54, 429, 91);
		add(paneIntro);

		finaliseComponents();
	}

	@Override
	public String getPanelName() {
		return "MiscCheatCodes";
	}
}