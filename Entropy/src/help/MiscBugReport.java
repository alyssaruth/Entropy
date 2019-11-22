package help;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;

import util.Debug;
import util.EntropyColour;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class MiscBugReport extends HelpPanel
{
	private String panelName = "MiscBugReport";
	private JTextPane title = new JTextPane();
	private JTextPane paneIntro = new JTextPane();
	private JTextPane[] textFields = {title, paneIntro};
	
	public MiscBugReport()
	{
		try
		{
			setBackground(Color.WHITE);
			setPanelName(panelName);
			setTextFields(textFields);
			addMouseListeners("");
			setNodeName("Bug Report");
			setLayout(null);
			title.setText("Bug Report");
			title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
			title.setFont(new Font("Tahoma", Font.BOLD, 18));
			title.setEditable(false);
			title.setBounds(21, 25, 192, 30);
			add(title);
			paneIntro.setFont(new Font("SansSerif", Font.PLAIN, 14));
			paneIntro.setContentType("text/html");
			paneIntro.setText("<html>You can report any issues you may find through the 'Bug Report' feature, located under the 'Help' menu. Please include as much information as you can about what you were doing when you encountered the problem. \r\n<br><br>\r\nSending the bug report will also send logs that will help me to investigate, however these are lost when you exit the application so please send the bug report before doing so. In more severe circumstances the logs will get emailed automatically, so if you see the following error message you don't need to send a bug report yourself:</html>");
			paneIntro.setBounds(21, 408, 429, 190);
			add(paneIntro);
			
			JLabel lblNewLabel = new JLabel("");
			lblNewLabel.setIcon(new ImageIcon(MiscBugReport.class.getResource("/help/bugReport.png")));
			lblNewLabel.setBounds(70, 66, 320, 321);
			add(lblNewLabel);
			
			JLabel label = new JLabel("");
			label.setIcon(new ImageIcon(MiscBugReport.class.getResource("/help/bugError.png")));
			label.setBounds(35, 610, 379, 105);
			add(label);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
}