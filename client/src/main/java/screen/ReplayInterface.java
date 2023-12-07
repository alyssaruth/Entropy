package screen;

import bean.FileUploadListener;
import bean.FileUploader;
import object.ReplayTable;
import util.AchievementsUtil;
import util.DialogUtil;
import util.Registry;
import util.ReplayFileUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import static utils.InjectedThings.logger;

public class ReplayInterface extends JFrame
							 implements ListSelectionListener, 
							 	        WindowListener,
							 	        ActionListener,
							 	        Registry,
							 	        FileUploadListener
{
	private static final int INIT_MILLIS_STACK_TRACE_THRESHOLD = 5 * 1000; //10s
	
	public ReplayInterface() 
	{
		setIconImage(new ImageIcon(AchievementsDialog.class.getResource("/icons/replay.png")).getImage());
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(tabbedPane);
		personalTab = new JPanel();
		tabbedPane.addTab(ReplayFileUtil.FOLDER_PERSONAL_REPLAYS, null, personalTab, null);
		importedTab = new JPanel();
		tabbedPane.addTab(ReplayFileUtil.FOLDER_IMPORTED_REPLAYS, null, importedTab, null);
		noMyReplays.setVerticalAlignment(SwingConstants.TOP);
		noMyReplays.setHorizontalAlignment(SwingConstants.CENTER);
		noMyReplays.setFont(new Font("Tahoma", Font.ITALIC, 12));
		noMyReplays.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		noImportedReplays.setVerticalAlignment(SwingConstants.TOP);
		noImportedReplays.setHorizontalAlignment(SwingConstants.CENTER);
		noImportedReplays.setFont(new Font("Tahoma", Font.ITALIC, 12));
		noImportedReplays.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		myCount.setPreferredSize(new Dimension(46, 20));
		myCount.setFont(new Font("Tahoma", Font.ITALIC, 12));
		myCount.setHorizontalAlignment(SwingConstants.CENTER);
		personalTab.setLayout(new BorderLayout(0, 0));
		personalTab.add(personalFilterPanel, BorderLayout.WEST);
		personalTablePanel = new JPanel();
		personalTab.add(personalTablePanel, BorderLayout.CENTER);
		personalTablePanel.setLayout(new BorderLayout(0, 0));
		personalTablePanel.add(personalReplaysScrollPane, BorderLayout.CENTER);
		personalTablePanel.add(exportPanel, BorderLayout.NORTH);
		personalTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 10));
		personalReplaysScrollPane.setViewportView(personalReplaysTable);
		personalReplaysScrollPane.setVisible(false);
		personalReplaysTable.setRowSelectionAllowed(true);
		personalReplaysTable.setShowGrid(false);
		personalReplaysTable.getTableHeader().setReorderingAllowed(false);
		personalReplaysTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		personalReplaysTable.setFillsViewportHeight(true);
		importedCount.setPreferredSize(new Dimension(46, 20));
		importedCount.setHorizontalAlignment(SwingConstants.CENTER);
		importedCount.setFont(new Font("Tahoma", Font.ITALIC, 12));
		importedTab.setLayout(new BorderLayout(0, 0));
		importedTab.add(importedFilterPanel, BorderLayout.WEST);
		importedFilterPanel.setBorder(BorderFactory.createEmptyBorder(27, 0, 0, 5));
		personalFilterPanel.setBorder(BorderFactory.createEmptyBorder(27, 0, 0, 5));
		importedTab.add(importedTablePanel);
		importedTablePanel.setLayout(new BorderLayout(0, 0));
		importedTablePanel.add(importedReplaysScrollPane, BorderLayout.CENTER);
		importedTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 10));
		importedReplaysScrollPane.setViewportView(importedReplaysTable);
		importedReplaysScrollPane.setVisible(false);
		importedReplaysTable.setRowSelectionAllowed(true);
		importedReplaysTable.setShowGrid(false);
		importedReplaysTable.getTableHeader().setReorderingAllowed(false);
		importedReplaysTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		importedReplaysTable.setFillsViewportHeight(true);
		panel_3.setLayout(new BorderLayout(0, 0));
		panel_3.add(fileUploader, BorderLayout.CENTER);
		panel = new JPanel();
		personalFilterPanel.add(panel, "2, 7, 2, 1, fill, center");
		btnConfigurePersonalColumns.setPreferredSize(new Dimension(155, 20));
		panel.add(btnConfigurePersonalColumns);
		btnRefreshPersonal.setPreferredSize(new Dimension(80, 20));
		panel.add(btnRefreshPersonal);
		personalTablePanel.add(myCount, BorderLayout.SOUTH);
		importedTablePanel.add(importedCount, BorderLayout.SOUTH);
		importedTablePanel.add(panel_3, BorderLayout.NORTH);
		panel_3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
		exportPanel.setLayout(new BorderLayout(0, 0));
		btnExport.setPreferredSize(new Dimension(80, 20));
		exportPanel.add(btnExport, BorderLayout.EAST);
		exportPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lblFilename.setHorizontalAlignment(SwingConstants.CENTER);
		lblFilename.setText("");
		lblFilename.setOpaque(false);
		lblFilename.setBackground(new Color(0,0,0,0));
		lblFilename.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		ListSelectionModel plsm = personalReplaysTable.getSelectionModel();
		plsm.addListSelectionListener(this);
		lblFilename.setEditable(false);
		exportPanel.add(lblFilename, BorderLayout.CENTER);
		importedFilterPanel.add(panel_1, "2, 7, 2, 1, fill, center");
		btnConfigureImportedColumns.setPreferredSize(new Dimension(155, 20));
		panel_1.add(btnConfigureImportedColumns);
		btnRefreshImported.setPreferredSize(new Dimension(80, 20));
		panel_1.add(btnRefreshImported);
		FileFilter[] filters = fc.getChoosableFileFilters();
		fc.removeChoosableFileFilter(filters[0]);
		fc.addChoosableFileFilter(new EntSaveFilter());

		btnExport.addActionListener(this);
		btnRefreshPersonal.addActionListener(this);
		btnRefreshImported.addActionListener(this);
		btnConfigurePersonalColumns.addActionListener(this);
		btnConfigureImportedColumns.addActionListener(this);
		fileUploader.addFileUploadListener(this);
		addWindowListener(this);
	}
	
	private final JFileChooser fc = new JFileChooser();
	private final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
	private final JScrollPane personalReplaysScrollPane = new JScrollPane();
	private final JScrollPane importedReplaysScrollPane = new JScrollPane();
	private final JLabel noMyReplays = new JLabel("There are no replays to show.");
	private final JLabel noImportedReplays = new JLabel("There are no replays to show.");
	private final ReplayFilterPanel personalFilterPanel = new ReplayFilterPanel();
	private final ReplayFilterPanel importedFilterPanel = new ReplayFilterPanel();
	private final JButton btnExport = new JButton("Export...");
	private final JButton btnRefreshPersonal = new JButton("Refresh");
	private final JButton btnRefreshImported = new JButton("Refresh");
	private final JLabel myCount = new JLabel("New label");
	private final JLabel importedCount = new JLabel("New label");
	private final ReplayTable personalReplaysTable = new ReplayTable(ReplayFileUtil.FOLDER_PERSONAL_REPLAYS, personalFilterPanel, myCount);
	private final ReplayTable importedReplaysTable = new ReplayTable(ReplayFileUtil.FOLDER_IMPORTED_REPLAYS, importedFilterPanel, importedCount);
	private JPanel personalTab;
    private JPanel panel;
    private JPanel personalTablePanel;
    private final JPanel importedTablePanel = new JPanel();
    private final JPanel panel_3 = new JPanel();
    private final FileUploader fileUploader = new FileUploader(new EntSaveFilter(), "Import");
    private JPanel importedTab;
    private final JPanel panel_1 = new JPanel();
    private final JPanel exportPanel = new JPanel();
    private final JTextField lblFilename = new JTextField("filename");
    private final JButton btnConfigurePersonalColumns = new JButton("Configure Columns...");
    private final JButton btnConfigureImportedColumns = new JButton("Configure Columns...");
	
	public void init()
	{
		logger.info("replayInit", "Initialising replay interface...");
		long startTime = System.currentTimeMillis();

		personalFilterPanel.setMoonAndStarVisibility();
		importedFilterPanel.setMoonAndStarVisibility();

		boolean expectLongerDuration = personalReplaysTable.init();
		expectLongerDuration |= importedReplaysTable.init();

		setScrollpaneViewport(personalReplaysScrollPane, personalReplaysTable, noMyReplays);
		setScrollpaneViewport(importedReplaysScrollPane, importedReplaysTable, noImportedReplays);

		long endTime = System.currentTimeMillis();
		long initDuration = endTime - startTime;
		if (initDuration > INIT_MILLIS_STACK_TRACE_THRESHOLD
		  && !expectLongerDuration)
		{
			logger.error("slowReplayInit", "Took longer than " + INIT_MILLIS_STACK_TRACE_THRESHOLD + " millis to init ReplayInterface");
		}
	}
	
	private void setScrollpaneViewport(JScrollPane scrollPane, ReplayTable table, JLabel replacementLabel)
	{
		scrollPane.setVisible(true);
		
		int rowCount = table.getRowCount();
		if (rowCount > 0)
		{
			scrollPane.setViewportView(table);
		}
		else
		{
			if (!prefs.getBoolean(PREFERENCES_BOOLEAN_SAVE_REPLAYS, false)
			  && table.getFolder().equals(ReplayFileUtil.FOLDER_PERSONAL_REPLAYS))
			{
				replacementLabel.setText("Saving replays is currently disabled.");
			}
			else
			{
				replacementLabel.setText("There are no replays to show.");
			}
			
			scrollPane.setViewportView(replacementLabel);
		}
	}
	
	private void importReplay(File file)
	{
		String filePath = file.getPath();
		if (ReplayFileUtil.successfullyFilledRegistryFromFile(filePath, tempReplayStore))
		{
			String filename = ReplayFileUtil.saveImportedReplay();
			importedReplaysTable.replayAdded(filename);
			importedReplaysScrollPane.setViewportView(importedReplaysTable);
		}
		else
		{
			DialogUtil.showError("The file specified was not in the correct format and could not be imported.");
		}
	}
	
	private void exportReplay()
	{
		String selectedFilename = personalReplaysTable.getSelectedFilename();
		if (selectedFilename.isEmpty())
		{
			DialogUtil.showError("You must select a replay to export.");
			return;
		}

		logger.info("exportReplay", "Exporting replay " + selectedFilename);

		int returnVal = fc.showSaveDialog(ReplayInterface.this);
		if (returnVal != JFileChooser.APPROVE_OPTION)
		{
			logger.info("exportReplay", "User cancelled save");
			return;
		}

		File newFile = fc.getSelectedFile();
		String filePath = newFile.getPath();

		filePath = adjustFileExtensionIfNecessary(filePath);

		String directory = ReplayFileUtil.getDirectoryFromPreferences();
		String fullPath = directory + "//Replays//" + ReplayFileUtil.FOLDER_PERSONAL_REPLAYS + "//" + selectedFilename;

		if (ReplayFileUtil.successfullyFilledRegistryFromFile(fullPath, tempReplayStore))
		{
			ReplayFileUtil.exportReplay(filePath);
			AchievementsUtil.unlockAchievement(ACHIEVEMENTS_BOOLEAN_LOOK_AT_ME);
		}
		else
		{
			DialogUtil.showError("The file specified was not in the correct format and could not be exported.");
		}
	}
	
	private String adjustFileExtensionIfNecessary(String filePath)
	{
		if (!filePath.toLowerCase().endsWith(".ent"))
		{
			int index = filePath.indexOf('.');
			if (index == -1)
			{
				return filePath + ".ent";
			}
			else
			{
				filePath = filePath.substring(0, index);
				return adjustFileExtensionIfNecessary(filePath);
			}
		}
		else
		{
			return filePath;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == btnExport)
		{
			exportReplay();
		}
		else if (source == btnConfigurePersonalColumns
		  || source == btnConfigureImportedColumns)
		{
			if (ConfigureColumnsDialog.configureColumns())
			{
				personalReplaysTable.refreshTable();
				importedReplaysTable.refreshTable();
			}
		}
		else if (source == btnRefreshImported)
		{
			importedReplaysTable.refreshTable();
		}
		else if (source == btnRefreshPersonal)
		{
			personalReplaysTable.refreshTable();
		}
	}
	
	@Override
	public void fileUploaded(File file)
	{
		importReplay(file);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		String selectedFilename = personalReplaysTable.getSelectedFilename();
		lblFilename.setText(selectedFilename);
	}

	@Override
	public void windowClosing(WindowEvent e) 
	{
		Dimension dim = this.getSize();
		prefs.putInt(PREFERENCES_INT_REPLAY_VIEWER_HEIGHT, dim.height);
		prefs.putInt(PREFERENCES_INT_REPLAY_VIEWER_WIDTH, dim.width);
	}

	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	
	private static class EntSaveFilter extends FileFilter
	{ 
		@Override
		public boolean accept(File f)
		{
			if (f.isDirectory())
			{
				return true;
			}

			String s = f.getName();

			return s.endsWith(".ent");
		}

		@Override
		public String getDescription() 
		{
			return "*.ent, *.ENT";
		}
	}
}