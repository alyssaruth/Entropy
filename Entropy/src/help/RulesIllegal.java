package help;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;

import util.Debug;
import util.EntropyColour;

@SuppressWarnings("serial")
public class RulesIllegal extends HelpPanel
{
	private String panelName = "RulesIllegal";
	private JTextPane title = new JTextPane();
	private JTextPane paneOne = new JTextPane();
	private JTextPane[] textFields = {title, paneOne};
	
	public RulesIllegal()
	{
		try
		{
			setBackground(Color.WHITE);
			setPanelName(panelName);
			setTextFields(textFields);
			addMouseListeners("bid");
			setNodeName("Illegal!");
			setLayout(null);
			paneOne.setFont(new Font("SansSerif", Font.PLAIN, 14));
			paneOne.setContentType("text/html");
			paneOne.setText("<html>The 'Illegal' option provides an alternative to bidding higher or challenging when facing a bid. If a player declares 'Illegal', they claim that the bid they were faced with was <font color=\"blue\"><u>perfect</u></font>. If they are right the opponent loses a card for the next round, else they lose a card - regardless of whether the bid they were faced with was an overbid or an underbid.</html>");
			paneOne.setBounds(21, 54, 429, 220);
			paneOne.setEditable(false);
			add(paneOne);
			title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
			title.setFont(new Font("Tahoma", Font.BOLD, 18));
			title.setText("Illegal!");
			title.setBounds(21, 25, 159, 30);
			title.setEditable(false);
			add(title);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
}