package object;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import screen.ReplayDialog;
import screen.ReplayFilterPanel;
import screen.ScreenCache;
import util.DialogUtil;
import util.EntropyColour;
import util.FileUtil;
import util.Registry;
import util.ReplayConverter;
import util.ReplayFileUtil;
import util.ReplayRowWrapper;
import util.TableUtil;

import static utils.CoreGlobals.logger;

public class ReplayTable extends JTable
						 implements MouseListener,
						 			Registry
{
	private static final int INDEX_OF_DATETIME_COLUMN = 0;
	private static final int INDEX_OF_NAME_COLUMN = INDEX_OF_DATETIME_COLUMN + 1;
	private static final int INDEX_OF_MODE_COLUMN = INDEX_OF_NAME_COLUMN + 1;
	private static final int INDEX_OF_ROOM_NAME_COLUMN = INDEX_OF_MODE_COLUMN + 1;
	private static final int INDEX_OF_ROUNDS_COLUMN = INDEX_OF_ROOM_NAME_COLUMN + 1;
	private static final int INDEX_OF_PLAYERS_COLUMN = INDEX_OF_ROUNDS_COLUMN + 1;
	private static final int INDEX_OF_CARDS_COLUMN = INDEX_OF_PLAYERS_COLUMN + 1;
	private static final int INDEX_OF_FLAGS_COLUMN = INDEX_OF_CARDS_COLUMN + 1;	
	private static final int INDEX_OF_FILENAME_COLUMN = INDEX_OF_FLAGS_COLUMN + 1;
	private static final int INDEX_OF_COMPLETED_COLUMN = INDEX_OF_FILENAME_COLUMN + 1;
	private static final int INDEX_OF_WON_COLUMN = INDEX_OF_COMPLETED_COLUMN + 1;
	
	private static final String FILE_NAME_INDEX = "Index.txt";
	
	//Cache of filename -> RowWrapper
	private HashMap<String, ReplayRowWrapper> rowWrapperByFileName = new HashMap<>();
	
	private String folder = null;
	private ReplayFilterPanel filterPanel = null;
	private JLabel countLabel = null;
	private DefaultTableModel model = new TableUtil.DefaultModel();
	private ArrayList<String> replayFilenames = new ArrayList<>();
	private ArrayList<String> replayFilenamesFiltered = new ArrayList<>();
	private ArrayList<File> corruptFiles = new ArrayList<>();
	
	public ReplayTable(String folder, ReplayFilterPanel filterPanel, JLabel countLabel)
	{
		super();
		this.folder = folder;
		this.filterPanel = filterPanel;
		this.countLabel = countLabel;
		
		setModel(model);
		addMouseListener(this);
		addListeners();
	}
	
	private final ReplayTableCentreRenderer centreRenderer = new ReplayTableCentreRenderer();
	
	public boolean init()
	{
		boolean expectLongerDuration = getIndexFileIfExists() == null;
		
		fillReplayList();
		initialiseCacheFromIndexFile();
		refreshTable(true);
		writeOutIndexFileFromCache();
		
		return expectLongerDuration 
		  || !corruptFiles.isEmpty();
	}
	public void refreshTable()
	{
		if (filterPanel.valid())
		{
			refreshTable(false);
		}
	}
	private void refreshTable(boolean init)
	{
		resetTableModel();
		populateFilteredList(init);
		fillTableModelFromFilteredList();
		
		countLabel.setText("Showing " + replayFilenamesFiltered.size() + " of " + replayFilenames.size() + " replay(s)");
	}
	
	private void resetTableModel()
	{
		model = new TableUtil.DefaultModel();
		setModel(model);
		
		model.addColumn("Date & Time");
		model.addColumn("Name");
		model.addColumn("Mode");
		model.addColumn("Room");
		model.addColumn("Rounds");
		model.addColumn("Players");
		model.addColumn("Cards");
		model.addColumn("Flags");
		model.addColumn("!filename");
		model.addColumn("!complete");
		model.addColumn("!won");
		
		getColumnModel().getColumn(INDEX_OF_DATETIME_COLUMN).setCellRenderer(centreRenderer);
		getColumnModel().getColumn(INDEX_OF_NAME_COLUMN).setCellRenderer(centreRenderer);
		getColumnModel().getColumn(INDEX_OF_MODE_COLUMN).setCellRenderer(centreRenderer);
		getColumnModel().getColumn(INDEX_OF_ROOM_NAME_COLUMN).setCellRenderer(centreRenderer);
		getColumnModel().getColumn(INDEX_OF_ROUNDS_COLUMN).setCellRenderer(centreRenderer);
		getColumnModel().getColumn(INDEX_OF_PLAYERS_COLUMN).setCellRenderer(centreRenderer);
		getColumnModel().getColumn(INDEX_OF_CARDS_COLUMN).setCellRenderer(centreRenderer);
		getColumnModel().getColumn(INDEX_OF_FLAGS_COLUMN).setCellRenderer(TableUtil.FLAG_RENDERER);

		getColumnModel().getColumn(INDEX_OF_DATETIME_COLUMN).setPreferredWidth(150);
		getColumnModel().getColumn(INDEX_OF_NAME_COLUMN).setPreferredWidth(100);
		getColumnModel().getColumn(INDEX_OF_PLAYERS_COLUMN).setPreferredWidth(50);
		getColumnModel().getColumn(INDEX_OF_CARDS_COLUMN).setPreferredWidth(50);
		getColumnModel().getColumn(INDEX_OF_FLAGS_COLUMN).setPreferredWidth(100);
		
		setRowHeight(25);
		
		setComparators();
	    stripOutHiddenAndRemovedColumns();
	}
	
	private void setComparators()
	{
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
	    
	    sorter.setComparator(INDEX_OF_DATETIME_COLUMN, TableUtil.DATE_COMPARATOR);
	    sorter.setComparator(INDEX_OF_FLAGS_COLUMN, TableUtil.FLAG_COMPARATOR);
	    sorter.setComparator(INDEX_OF_ROUNDS_COLUMN, TableUtil.INT_COMPARATOR);
	    
	    setRowSorter(sorter);
	}
	
	private void stripOutHiddenAndRemovedColumns()
	{
		//do this in REVERSE order to avoid IOOBE
		removeColumn(getColumnModel().getColumn(INDEX_OF_WON_COLUMN));
	    removeColumn(getColumnModel().getColumn(INDEX_OF_COMPLETED_COLUMN));
	    removeColumn(getColumnModel().getColumn(INDEX_OF_FILENAME_COLUMN));

	    boolean showGameMode = prefs.getBoolean(PREFERENCES_BOOLEAN_INCLUDE_GAME_MODE_COLUMN, true);
		boolean showRounds = prefs.getBoolean(PREFERENCES_BOOLEAN_INCLUDE_ROUNDS_COLUMN, false);
		boolean showPlayers = prefs.getBoolean(PREFERENCES_BOOLEAN_INCLUDE_PLAYERS_COLUMN, true);
		boolean showCards = prefs.getBoolean(PREFERENCES_BOOLEAN_INCLUDE_CARDS_COLUMN, false);
		boolean showRoomName = prefs.getBoolean(PREFERENCES_BOOLEAN_INCLUDE_ROOM_NAME_COLUMN, false);
	    
		if (!showCards)
		{
			removeColumn(getColumnModel().getColumn(INDEX_OF_CARDS_COLUMN));
		}
		if (!showPlayers)
		{
			removeColumn(getColumnModel().getColumn(INDEX_OF_PLAYERS_COLUMN));
		}
	    if (!showRounds)
	    {
	    	removeColumn(getColumnModel().getColumn(INDEX_OF_ROUNDS_COLUMN));
	    }
	    if (!showRoomName)
	    {
	    	removeColumn(getColumnModel().getColumn(INDEX_OF_ROOM_NAME_COLUMN));
	    }
	    if (!showGameMode)
	    {
	    	removeColumn(getColumnModel().getColumn(INDEX_OF_MODE_COLUMN));
	    }
	}
	
	private void fillReplayList()
	{
		logger.info("initReplayList", "Filling " + folder + " replay list");

		String replayDirectory = ReplayFileUtil.getDirectoryFromPreferences();
		String directoryStr = replayDirectory + "//Replays//" + folder;
		
		//if the 'Replays' directory doesn't exist, just return.
		File directory = new File(directoryStr);
		if (!directory.isDirectory())
		{
			logger.info("initReplayList", "Directory " + directory + " does not exist.");
			return;
		}
        
		//Reset the array lists
		replayFilenames = new ArrayList<>();
		corruptFiles = new ArrayList<>();
		String corruptReplayMessage = "";
		
    	File[] directoryFiles = directory.listFiles();
    	for (File file : directoryFiles)
    	{
    		String fileName = file.getName();
    		if (ReplayFileUtil.filenameIsValid(fileName))
    		{
    			replayFilenames.add(fileName);
    		}
    		else if (!fileName.equals(FILE_NAME_INDEX))
    		{
    			corruptReplayMessage += "\n" + file.getName();
    			corruptFiles.add(file);
    		}
    	}

		logger.info("initReplayList", "Finished filling " + folder + " replay list [size: " + replayFilenames.size() + "]");
        if (!corruptReplayMessage.isEmpty() )
		{
			promptToDeleteCorruptReplays(corruptReplayMessage);
		}
	}
	
	private void promptToDeleteCorruptReplays(String corruptFilesDesc)
	{
		int option = DialogUtil.showQuestion("The following files are corrupt and will not appear in the '" 
										    + folder + " Replays' table: \n" + corruptFilesDesc
											+ "\n\nWould you like to remove these files?", false);

		if (option == JOptionPane.YES_OPTION)
		{
			deleteCorruptReplaysAndShowResult(corruptFiles);
		}
	}
	
	private void deleteCorruptReplaysAndShowResult(ArrayList<File> filesToDelete)
	{
		String result = ReplayFileUtil.deleteCorruptReplays(filesToDelete);

		if (!result.isEmpty())
		{
			String failureMsg = "Replay deletion failed for the following " + folder.toLowerCase() + " replays:\n" + result;
			DialogUtil.showError(failureMsg);
		}
		else 
		{
			DialogUtil.showInfo(folder + " replays were deleted successfully.");
		}
	}
	
	private void initialiseCacheFromIndexFile()
	{
		if (!rowWrapperByFileName.isEmpty())
		{
			logger.info("replayListIndex", "Not reading index file as rows are already cached.");
			return;
		}
		
		File indexFile = getIndexFileIfExists();
		if (indexFile == null)
		{
			logger.info("replayListIndex", "No index file for " + folder + " replays");
			return;
		}
		
		String contents = FileUtil.getBase64DecodedFileContentsAsString(indexFile);
		if (contents == null)
		{
			return;
		}
		
		String[] lines = contents.split("\n");
		logger.info("replayListIndex", "Found " + lines.length + " lines in index file.");
		for (int i=0; i<lines.length; i++)
		{
			String rowWrapperStr = lines[i];
			ReplayRowWrapper wrapper = new ReplayRowWrapper(rowWrapperStr);
			String filename = wrapper.getFilename();
			if (!replayFilenames.contains(filename))
			{

				logger.info("replayListIndex", "Not caching " + filename + " as file no longer exists.");
				continue;
			}
			
			rowWrapperByFileName.put(filename, wrapper);
		}
	}
	
	private File getIndexFileIfExists()
	{
		String replayDirectory = ReplayFileUtil.getDirectoryFromPreferences();
		String directoryStr = replayDirectory + "//Replays//" + folder + "//" + FILE_NAME_INDEX;
		
		File indexFile = new File(directoryStr);
		if (indexFile.exists())
		{
			return indexFile;
		}
		
		return null;
	}
	
	private void writeOutIndexFileFromCache()
	{
		String replayDirectory = ReplayFileUtil.getDirectoryFromPreferences() + "//Replays//" + folder;
		if (rowWrapperByFileName.isEmpty())
		{
			return;
		}
		
		File directory = new File(replayDirectory);
		if (!directory.isDirectory())
		{
			logger.info("replayListIndex", "Directory " + directory + " does not exist, not writing out index file.");
			return;
		}
		
		Path path = Paths.get(replayDirectory + "//" + FILE_NAME_INDEX);
		
		String text = getIndexTextFromCache();
		FileUtil.encodeAndSaveToFile(path, text);
	}
	
	private String getIndexTextFromCache()
	{
		String ret = "";
		Iterator<Map.Entry<String, ReplayRowWrapper>> it = rowWrapperByFileName.entrySet().iterator();
		for (; it.hasNext(); )
		{
			Map.Entry<String, ReplayRowWrapper> entry = it.next();
			ReplayRowWrapper wrapper = entry.getValue();
			
			ret += wrapper.toIndexStr();
			ret += "\n";
		}
		
		return ret;
	}
	
	private void populateFilteredList(boolean init)
	{
		replayFilenamesFiltered.clear();

		int fullSize = replayFilenames.size();
		if (init) {
			logger.info("populateFilteredList", "Populating filtered list for " + this + " - total files: " + fullSize);
		}
		
		int cachedCount = 0;
		for (int i=0; i<fullSize; i++)
		{
			String name = replayFilenames.get(i);
			ReplayRowWrapper wrapper = getRowWrapperFromCache(name, folder);
			if (wrapper.isFromCache())
			{
				cachedCount++;
			}
			
			if (includeRowBasedOnFilters(wrapper))
			{
				replayFilenamesFiltered.add(name);
			}
		}

		if (init) {
			logger.info("populateFilteredList", "Finished populating filtered list for " + this + " - files found in cache: " + cachedCount);
		}
	}
	
	private boolean includeRowBasedOnFilters(ReplayRowWrapper wrapper)
	{
		String filename = wrapper.getFilename();
		String mode = wrapper.getGameMode();
		String rounds = wrapper.getRounds();
		String gameComplete = wrapper.getGameComplete();
		String playerWon = wrapper.getPlayerWon();
		String date = ReplayFileUtil.getFormattedDateFromFileName(filename);
		FlagImage image = wrapper.getFlag();
		
		if (filterPanel.getFilterByFlag() && !filterPanel.includesFlag(image))
		{
			return false;
		}
		
		if (filterPanel.getFilterByGameMode() && !filterPanel.includesGameMode(mode))
		{
			return false;
		}
		
		if (filterPanel.getFilterByRounds() && !filterPanel.includesRoundNumber(rounds))
		{
			return false;
		}
		
		boolean wins = filterPanel.getShowWins();
		boolean unknown = filterPanel.getShowUnknown();
		boolean losses = filterPanel.getShowLosses();
		boolean incomplete = filterPanel.getShowIncomplete();
		boolean complete = filterPanel.getShowComplete();
		
		if (playerWon.equals("1") && !wins)
		{
			return false;
		}
		
		if (playerWon.equals("0") && !unknown)
		{
			return false;
		}
		
		if (playerWon.equals("-1") && !losses)
		{
			return false;
		}
		
		if (gameComplete.equals("0") && !incomplete)
		{
			return false;
		}
		
		if (gameComplete.equals("1") && !complete)
		{
			return false;
		}
		
		return true;
	}
	
	private void fillTableModelFromFilteredList()
	{
		//Clear the table down
		int rowCount = model.getRowCount();
		for (int i=0; i<rowCount; i++)
		{
			model.removeRow(0);
		}
		
		int size = replayFilenamesFiltered.size();
		for (int i=0; i<size; i++)
		{
			//hidden stuff
			String replayFileName = replayFilenamesFiltered.get(i);
			ReplayRowWrapper rowWrapper = getRowWrapperFromCache(replayFileName, folder);
			
			Object[] row = rowWrapper.getAsRow();
			model.addRow(row);
		}
	}
	
	private ReplayRowWrapper getRowWrapperFromCache(String filename, String folder)
	{
		ReplayRowWrapper wrapper = rowWrapperByFileName.get(filename);
		if (wrapper != null)
		{
			wrapper.setFromCache(true);
			return wrapper;
		}
		
		ReplayRowWrapper newWrapper = ReplayFileUtil.factoryRowWrapper(filename, folder);
		
		//cache for next time
		rowWrapperByFileName.put(filename, newWrapper);
		return newWrapper;
	}
	
	private void addListeners()
	{
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		getActionMap().put("Enter", new AbstractAction() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) 
			{
				showReplay();
			}
		});
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete");
		getActionMap().put("Delete", new AbstractAction() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) 
			{
				deleteReplays();
			}
		});
	}
	
	private void deleteReplays()
	{
		int[] viewRows = getSelectedRows();
		if (viewRows.length == 0)
		{
			//pressed delete without anything selected, so do nothing
			return;
		}
		
		//From the selected rows, get the list of filenames to delete
		ArrayList<String> replaysToDelete = new ArrayList<>();
		for (int i=0; i<viewRows.length; i++)
		{
			int internalRow = convertRowIndexToModel(viewRows[i]);
			String filename = (String)model.getValueAt(internalRow, INDEX_OF_FILENAME_COLUMN);
			replaysToDelete.add(filename);
		}
		
		//Confirm the deletion
		int size = replaysToDelete.size();
		if (!confirmDeletion(size))
		{
			return;
		}
		
		//Loop through and delete the replays, keeping track of whether any fail
		boolean success = true;
		String directory = ReplayFileUtil.getDirectoryFromPreferences();
		for (int i=0; i<size; i++)
		{
			String filename = replaysToDelete.get(i);
			String replayPath = directory + "//Replays//" + folder + "//" + filename;
			success &= deleteWithCatch(replayPath, filename);
		}
		
		//Show an error on failure
		if (!success)
		{
			DialogUtil.showError("Deletion failed for one or more replays.");
		}
		
		//Refresh the table as one or more replays may have been deleted
		refreshTable();
	}
	
	private boolean confirmDeletion(int size)
	{
		String q = "Are you sure you want to delete this replay?";
		if (size > 1)
		{
			q = "Are you sure you want to delete these " + size + " replays?";
		}
		
		int choice = DialogUtil.showQuestion(q, false);
		return choice == JOptionPane.YES_OPTION;
	}
	
	private boolean deleteWithCatch(String replayPath, String filename)
	{
		try
		{
			logger.info("deleteReplay", "Deleting " + replayPath);
			Files.delete(Paths.get(replayPath));
			
			replayFilenames.remove(filename);
			replayFilenamesFiltered.remove(filename);
		}
		catch (IOException x)
		{
			logger.info("deleteReplay", "Caught " + x + " deleting file " + replayPath);
			return false;
		}
		
		return true;
	}
	
	public void replayAdded(String replayFilename)
	{
		replayFilenames.add(replayFilename);
		refreshTable();
	}
	
	public String getSelectedFilename()
	{
		int viewRow = getSelectedRow();
		if (viewRow == -1)
		{
			return "";
		}
		
		int internalRow = convertRowIndexToModel(viewRow);
		return (String)model.getValueAt(internalRow, INDEX_OF_FILENAME_COLUMN);
	}
	
	public String getFolder()
	{
		return folder;
	}
	
	public void showReplay()
	{
		int viewRow = getSelectedRow();
		if (viewRow == -1)
		{
			return;
		}

		int internalRow = convertRowIndexToModel(viewRow);

		String filename = (String)model.getValueAt(internalRow, INDEX_OF_FILENAME_COLUMN);
		logger.info("showReplay", "Opening file " + filename);
		if (!validateReplayContents(filename))
		{
			return;
		}

		ReplayDialog replayDialog = ScreenCache.getFileReplayDialog();
		replayDialog.setLocationRelativeTo(null);
		replayDialog.setResizable(false);
		replayDialog.initForFileReplay(filename, folder);
		replayDialog.setVisible(true);
	}
	
	private boolean validateReplayContents(String filename)
	{
		String replayDirectory = ReplayFileUtil.getDirectoryFromPreferences();
		String filePath = replayDirectory + "//Replays//" + folder + "//" + filename;
		int replayVersion = ReplayConverter.getReplayVersion(filePath);
		if (replayVersion == -1)
		{
			String question = "The file you selected is corrupt and cannot be displayed. Would you like to delete it?";
			dealWithCorruptReplay(filePath, filename, question);
			return false;
		}
		else if (replayVersion < ReplayConverter.REPLAY_VERSION)
		{
			return convertReplayToLatestVersion(filePath, filename, replayVersion);
		}
		else
		{
			return true;
		}
	}
	
	private void dealWithCorruptReplay(String filePath, String filename, String question)
	{
		int option = DialogUtil.showQuestion(question, false);
		if (option == JOptionPane.YES_OPTION)
		{
			boolean success = deleteWithCatch(filePath, filename);
			if (!success)
			{
				DialogUtil.showError("Failed to delete replay.");
			}
			else
			{
				refreshTable();
			}
		}
	}
	
	private boolean convertReplayToLatestVersion(String filePath, String filename, int replayVersion)
	{
		String message = "The file you selected is in an out of date format - it will be updated now.";
		DialogUtil.showInfo(message);
		boolean success = ReplayConverter.convertReplay(filePath, replayVersion);
		if (success)
		{
			logger.info("replayConverted", "Successfully converted replay");
			return true;
		}
		
		dealWithCorruptReplay(filePath, filename, "The file could not be converted, would you like to delete it?");
		return false;
	}
	
	public Color getColorForRow(int row)
	{
		String playerWon = (String)model.getValueAt(row, INDEX_OF_WON_COLUMN);
		String complete = (String)model.getValueAt(row, INDEX_OF_COMPLETED_COLUMN);
		
		if (playerWon.equals("1"))
		{
			return EntropyColour.COLOUR_REPLAY_VICTORY;
		}
		else if (playerWon.equals("0"))
		{
			return EntropyColour.COLOUR_REPLAY_UNKNOWN_OUTCOME;
		}
		else if (playerWon.equals("-1") && complete.equals("0"))
		{
			return EntropyColour.COLOUR_REPLAY_UNFINISHED_AND_LOST;
		}
		else
		{
			return Color.red;
		}
	}
	
	@Override
	public String toString()
	{
		return "Replay Table [" + folder + "]";
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		if (arg0.getClickCount() == 2)
		{
			showReplay();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0){}
	@Override
	public void mouseExited(MouseEvent arg0){}
	@Override
	public void mousePressed(MouseEvent arg0){}
	@Override
	public void mouseReleased(MouseEvent arg0){}
}
