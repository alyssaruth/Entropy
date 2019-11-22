package help;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import util.Debug;
import util.EntropyColour;
import util.Registry;

public class ToolsGameplaySettings extends HelpPanel
								   implements Registry
{
	private String panelName = "ToolsGameplaySettings";
	private JTextPane title = new JTextPane();
	private final JLabel bulletThree = new JLabel("");
	private final JLabel bulletFour = new JLabel("");
	private final JLabel playBlindImage = new JLabel("");
	private final JTextPane txtpnOneTitle = new JTextPane();
	private final JTextPane txtpnOneExplanation = new JTextPane();
	private final JTextPane txtpnTwoTitle = new JTextPane();
	private final JTextPane txtpnTwoExplanation = new JTextPane();
	private final JTextPane txtpnThreeTitle = new JTextPane();
	private final JTextPane txtpnThreeExplanation = new JTextPane();
	private final JTextPane txtpnTickbox = new JTextPane();
	private final JTextPane txtpnFourTitle = new JTextPane();
	private final JTextPane txtpnIfThisIs = new JTextPane();
	private final JTextPane txtpnHandicap_1 = new JTextPane();
	private final JTextPane txtpnWithThisTicked_1 = new JTextPane();
	private JTextPane[] textFields = {title, txtpnOneTitle, txtpnOneExplanation, txtpnTwoTitle, txtpnTwoExplanation, txtpnThreeTitle, txtpnThreeExplanation,
									  txtpnTickbox, txtpnFourTitle, txtpnIfThisIs, txtpnHandicap_1, txtpnWithThisTicked_1};
	
	private final JPanel blindPanel = new JPanel();
	private final JPanel panel = new JPanel();
	private final JLabel label = new JLabel("");
	
	
	public ToolsGameplaySettings()
	{
		try
		{
			setBackground(Color.WHITE);
			setPanelName(panelName);
			setTextFields(textFields);
			addMouseListeners("");
			setNodeName("Gameplay Settings");
			setLayout(null);
			title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
			title.setFont(new Font("Tahoma", Font.BOLD, 18));
			title.setText("Preferences: Gameplay");
			title.setBounds(21, 25, 259, 30);
			add(title);
			JLabel bulletOne = new JLabel("");
			bulletOne.setIcon(new ImageIcon(ToolsGameplaySettings.class.getResource("/help/numberOne.png")));
			bulletOne.setBounds(24, 78, 25, 25);
			add(bulletOne);
			JLabel bulletTwo = new JLabel("");
			bulletTwo.setIcon(new ImageIcon(ToolsGameplaySettings.class.getResource("/help/numberTwo.png")));
			bulletTwo.setBounds(24, 130, 25, 25);
			add(bulletTwo);
			txtpnOneTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtpnOneTitle.setText("Starting Cards");
			txtpnOneTitle.setBounds(54, 132, 118, 20);
			add(txtpnOneTitle);
			txtpnOneExplanation.setFont(new Font("Tahoma", Font.PLAIN, 12));
			txtpnOneExplanation.setText("A simple slider to set how many cards are dealt to each player at the start of a new game.");
			txtpnOneExplanation.setBounds(54, 156, 390, 36);
			add(txtpnOneExplanation);
			txtpnTwoTitle.setText("Game Mode");
			txtpnTwoTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtpnTwoTitle.setBounds(54, 80, 118, 20);
			add(txtpnTwoTitle);
			txtpnTwoExplanation.setText("Toggle between the different game modes you have unlocked.");
			txtpnTwoExplanation.setFont(new Font("Tahoma", Font.PLAIN, 12));
			txtpnTwoExplanation.setBounds(54, 104, 390, 25);
			add(txtpnTwoExplanation);
			JPanel jokerPanel = new JPanel();
			jokerPanel.setBackground(Color.WHITE);
			jokerPanel.setBounds(21, 206, 423, 128);
			add(jokerPanel);
			jokerPanel.setLayout(null);
			bulletThree.setBounds(3, 4, 25, 25);
			jokerPanel.add(bulletThree);
			bulletThree.setIcon(new ImageIcon(ToolsGameplaySettings.class.getResource("/help/numberThree.png")));
			txtpnThreeTitle.setBounds(33, 6, 118, 20);
			jokerPanel.add(txtpnThreeTitle);
			txtpnThreeTitle.setText("Joker Settings");
			txtpnThreeTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtpnThreeExplanation.setBounds(33, 30, 390, 19);
			jokerPanel.add(txtpnThreeExplanation);
			txtpnThreeExplanation.setText("Settings to vary how many jokers are included in the deck:");
			txtpnThreeExplanation.setFont(new Font("Tahoma", Font.PLAIN, 12));
			txtpnTickbox.setContentType("text/html");
			txtpnTickbox.setText("<html><font face=\"Tahoma\" size=\"3\"><b>- Tickbox:</b> Whether jokers are included or not. <br>\r\n<b>- Quantity:</b> How many jokers to add, between 1 and 4. <br>\r\n<b>- Value:</b> The worth of each joker, between 2 and 4. Jokers will be worth this many of every suit. </font></html>");
			txtpnTickbox.setBounds(33, 60, 380, 68);
			jokerPanel.add(txtpnTickbox);
			panel.setBounds(21, 342, 423, 104);
			add(panel);
			panel.setLayout(null);
			panel.setBackground(Color.WHITE);
			label.setIcon(new ImageIcon(ToolsGameplaySettings.class.getResource("/help/numberFour.png")));
			label.setBounds(3, 4, 25, 25);
			panel.add(label);
			txtpnHandicap_1.setText("Handicap");
			txtpnHandicap_1.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtpnHandicap_1.setBounds(33, 6, 118, 20);
			panel.add(txtpnHandicap_1);
			txtpnWithThisTicked_1.setText("With this ticked, you will be dealt less cards than your opponents at the start of a new game. The number represents how many cards less you will be dealt - so the higher the number, the larger your disadvantage will be. ");
			txtpnWithThisTicked_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
			txtpnWithThisTicked_1.setBounds(33, 30, 390, 66);
			panel.add(txtpnWithThisTicked_1);
			blindPanel.setLayout(null);
			blindPanel.setBackground(Color.WHITE);
			blindPanel.setBounds(21, 460, 423, 296);
			add(blindPanel);
			bulletFour.setBounds(3, 4, 25, 25);
			blindPanel.add(bulletFour);
			bulletFour.setIcon(new ImageIcon(ToolsGameplaySettings.class.getResource("/help/numberFive.png")));
			txtpnFourTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtpnFourTitle.setBounds(33, 6, 217, 20);
			blindPanel.add(txtpnFourTitle);
			txtpnIfThisIs.setOpaque(false);
			txtpnIfThisIs.setFont(new Font("Tahoma", Font.PLAIN, 12));
			txtpnIfThisIs.setBounds(33, 37, 380, 66);
			blindPanel.add(txtpnIfThisIs);
			playBlindImage.setIcon(new ImageIcon(ToolsGameplaySettings.class.getResource("/help/playingBlind_backBlue.png")));
			playBlindImage.setBounds(128, 114, 199, 157);
			blindPanel.add(playBlindImage);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	@Override
	public void refresh()
	{
		boolean unlockedBlind = rewards.getBoolean(REWARDS_BOOLEAN_BLIND, false);
		blindPanel.setVisible(unlockedBlind);
		
		if (unlockedBlind)
		{
			setPreferredSize(new Dimension(455, 750));
			txtpnFourTitle.setText("Blind Play");
			txtpnIfThisIs.setText("If this is ticked, your cards will be dealt face-down at the start of every round. \r\nYou will still be able to view your cards at any time by pressing the eye symbol below them:");
		}
		else
		{
			setPreferredSize(new Dimension(455, 450));
			txtpnFourTitle.setText("");
			txtpnIfThisIs.setText("");
		}
	}
}