package help;

import util.EntropyColour;

import javax.swing.*;
import java.awt.*;

public class MiscClearingSaveData extends HelpPanel
{
	private final JTextPane title = new JTextPane();
	private final JTextPane paneIntro = new JTextPane();
	private final JTextPane paneOne = new JTextPane();

	public MiscClearingSaveData() {
		setBackground(Color.WHITE);
		setNodeName("Clearing Data");
		setLayout(null);
		paneOne.setFont(new Font("SansSerif", Font.PLAIN, 14));
		paneOne.setContentType("text/html");
		paneOne.setText("<html>\r\n<b>- Statistics Only: </b>This will clear anything that appears under your statistics such as time played and total games won. Clearing this will preserve any achievements you have already earned, but any progress made towards locked achievements (e.g. those for amount of time played) will be lost.\r\n<br><br>\r\n<b>- Achievements and Statistics: </b> This will again clear your statistics, but will this time also remove any achievements you have already earned.\r\n<br><br>\r\n<b>- My Replays / Imported Replays: </b> These options will attempt to delete the replay files in your Personal / Imported folders. Note that it is not possible to recover these once they have been deleted, so be certain that you want to do this before proceeding.\r\n</html>");
		paneOne.setBounds(21, 360, 429, 279);
		add(paneOne);
		title.setText("Clearing Saved Data");
		title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		title.setFont(new Font("Tahoma", Font.BOLD, 18));
		title.setEditable(false);
		title.setBounds(21, 25, 192, 30);
		add(title);
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(MiscClearingSaveData.class.getResource("/help/clearData.png")));
		lblNewLabel.setBounds(120, 114, 230, 230);
		add(lblNewLabel);
		paneIntro.setFont(new Font("SansSerif", Font.PLAIN, 14));
		paneIntro.setContentType("text/html");
		paneIntro.setText("<html>Options to clear saved data can be found under File > Clear Data. This will bring up the following dialog: </html>");
		paneIntro.setBounds(21, 54, 429, 50);
		add(paneIntro);

		finaliseComponents();
	}

	@Override
	public String getPanelName() {
		return "MiscClearingSaveData";
	}
}