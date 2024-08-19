package help;

import util.EntropyColour;

import javax.swing.*;
import java.awt.*;

public class ToolsReplayViewer extends HelpPanel
{
	private final JTextPane title = new JTextPane();
	private final JLabel lblReplayTable = new JLabel("");
	private final JTextPane txtpnIntroduction = new JTextPane();
	private final JTextPane txtpnSavingReplays1 = new JTextPane();
	private final JTextPane titleSavingReplays = new JTextPane();
	private final JTextPane txtpnSavingReplays2 = new JTextPane();
	private final JLabel lblSaveReplaysOption = new JLabel("");
	private final JTextPane titleUsingTheReplayViewer = new JTextPane();
	private final JTextPane txtpnUsingTheReplayViewer1 = new JTextPane();
	private final JTextPane txtpnUsingTheReplayViewer2 = new JTextPane();
	private final JTextPane titleFilteringAndOrdering = new JTextPane();
	private final JTextPane txtpnFilteringAndOrdering1 = new JTextPane();
	private final JTextPane txtpnFilteringAndOrdering2 = new JTextPane();
	private final JTextPane titleImportingExporting = new JTextPane();
	private final JTextPane txtpnImportingExporting = new JTextPane();
	private final JPanel panel_2 = new JPanel();

	public ToolsReplayViewer() {
		setBackground(Color.WHITE);
		setNodeName("Replay Viewer");
		setLayout(null);
		title.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		title.setFont(new Font("Tahoma", Font.BOLD, 18));
		title.setText("The Replay Viewer");
		title.setBounds(21, 25, 188, 30);
		add(title);
		txtpnIntroduction.setContentType("text/html");
		txtpnIntroduction.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnIntroduction.setText("The Replay Viewer provides a useful interface for you to watch back all your past games of Entropy. Replays are shown in a colour-coded table, with filter options to make it easy to locate a specific game.");
		txtpnIntroduction.setBounds(21, 54, 429, 66);
		add(txtpnIntroduction);
		txtpnSavingReplays1.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnSavingReplays1.setContentType("text/html");
		txtpnSavingReplays1.setText("To use the Replay Viewer, you will first need to turn on the setting to save replays. You will find this under the 'Miscellaneous' tab in Preferences:");
		txtpnSavingReplays1.setBounds(21, 157, 423, 66);
		add(txtpnSavingReplays1);
		titleSavingReplays.setText("Saving Replays");
		titleSavingReplays.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		titleSavingReplays.setFont(new Font("Tahoma", Font.BOLD, 18));
		titleSavingReplays.setBounds(21, 128, 188, 30);
		add(titleSavingReplays);
		txtpnSavingReplays2.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnSavingReplays2.setContentType("text/html");
		txtpnSavingReplays2.setText("You will need to tick this option and choose a place for the replays to be saved. This will create a new folder called 'Replays', with sub-folders 'Personal' (for your own replays) and 'Imported' (see Importing & Exporting Replays, lower down).\r\n<br><br>\r\nNote: If you change your mind about this location at any point, you can change it without losing any data. You will be given the option to move all of the replay files over to the new location so all of your existing replays will be preserved.");
		txtpnSavingReplays2.setBounds(21, 291, 423, 177);
		add(txtpnSavingReplays2);
		lblSaveReplaysOption.setIcon(new ImageIcon(ToolsReplayViewer.class.getResource("/help/replayViewerSettings.png")));
		lblSaveReplaysOption.setBounds(62, 234, 309, 38);
		add(lblSaveReplaysOption);
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(11, 480, 494, 461);
		add(panel);
		panel.setLayout(null);
		titleUsingTheReplayViewer.setText("Using the Replay Viewer");
		titleUsingTheReplayViewer.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		titleUsingTheReplayViewer.setFont(new Font("Tahoma", Font.BOLD, 18));
		titleUsingTheReplayViewer.setBounds(10, 11, 262, 30);
		panel.add(titleUsingTheReplayViewer);
		txtpnUsingTheReplayViewer1.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnUsingTheReplayViewer1.setContentType("text/html");
		txtpnUsingTheReplayViewer1.setText("Once you have saved some replays, you will see a table like the following:");
		txtpnUsingTheReplayViewer1.setBounds(10, 40, 423, 44);
		panel.add(txtpnUsingTheReplayViewer1);
		lblReplayTable.setBounds(10, 95, 428, 264);
		panel.add(lblReplayTable);
		lblReplayTable.setIcon(new ImageIcon(ToolsReplayViewer.class.getResource("/help/replayViewerTable.png")));
		txtpnUsingTheReplayViewer2.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnUsingTheReplayViewer2.setContentType("text/html");
		txtpnUsingTheReplayViewer2.setText("Double-clicking on a row will show that replay. You can also select a row and press the enter key.\r\n<br>You can also delete one or more replays by selecting the row(s) and pressing the delete key.");
		txtpnUsingTheReplayViewer2.setBounds(10, 370, 423, 84);
		panel.add(txtpnUsingTheReplayViewer2);
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(11, 940, 494, 554);
		add(panel_1);
		titleFilteringAndOrdering.setText("Filtering and Sorting Replays");
		titleFilteringAndOrdering.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		titleFilteringAndOrdering.setFont(new Font("Tahoma", Font.BOLD, 18));
		titleFilteringAndOrdering.setBounds(10, 11, 280, 30);
		panel_1.add(titleFilteringAndOrdering);
		txtpnFilteringAndOrdering1.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnFilteringAndOrdering1.setContentType("text/html");
		txtpnFilteringAndOrdering1.setText("Many filter options are available on the left hand side to help you narrow down the selection (you need to click refresh for them to take effect). The first set of options is as follows:");
		txtpnFilteringAndOrdering1.setBounds(10, 40, 423, 63);
		panel_1.add(txtpnFilteringAndOrdering1);
		txtpnFilteringAndOrdering2.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnFilteringAndOrdering2.setContentType("text/html");
		txtpnFilteringAndOrdering2.setText("A 'complete' game is one where a winner was determined, and an 'incomplete' game is one which was ended prematurely. Complete games show as opaque in the table, whereas incomplete games will have a washed out effect.\r\n<br><br>\r\nWins are displayed in green, losses are displayed in red. An unknown result occurs when a game is ended before the player has been knocked out. These are displayed in grey. The remaining filter options should be self-explanatory. \r\n<br><br>\r\nTo sort the table, click on the title of the column you want to sort by:");
		txtpnFilteringAndOrdering2.setBounds(10, 173, 423, 220);
		panel_1.add(txtpnFilteringAndOrdering2);
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(ToolsReplayViewer.class.getResource("/help/replayViewerFilters.png")));
		label.setBounds(50, 114, 338, 48);
		panel_1.add(label);
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(ToolsReplayViewer.class.getResource("/help/replayViewerOrdering.png")));
		lblNewLabel.setBounds(15, 400, 412, 143);
		panel_1.add(lblNewLabel);
		panel_2.setLayout(null);
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(11, 1500, 442, 317);
		add(panel_2);
		titleImportingExporting.setText("Importing & Exporting Replays");
		titleImportingExporting.setForeground(EntropyColour.COLOUR_HELP_TITLE);
		titleImportingExporting.setFont(new Font("Tahoma", Font.BOLD, 18));
		titleImportingExporting.setBounds(10, 11, 299, 30);
		panel_2.add(titleImportingExporting);
		txtpnImportingExporting.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtpnImportingExporting.setContentType("text/html");
		txtpnImportingExporting.setText("Replays are divided into 'Personal' and 'Imported' categories. The Personal category corresponds to the replays you have recorded yourself, whilst the Imported category provides you with a way to view the replays of other people. \r\n<br><br>\r\nExporting: If you have a replay you want to show to someone else, you will first need to export it. To do this, select the row in your replay table and click the 'Export' button. This will allow you to choose a name for the file and save it somewhere on your PC.\r\n<br><br>\r\nImporting: Once you have a file to import, go to the 'Imported' tab of the Replay Viewer, find the file and click 'Import'. You shouldn't try to move it manually into the 'Imported' folder as this will skip the conversion process and so the file won't be readable.");
		txtpnImportingExporting.setBounds(10, 40, 423, 280);
		panel_2.add(txtpnImportingExporting);

		finaliseComponents();
	}

	@Override
	public String getPanelName() {
		return "ToolsReplayViewer";
	}
}