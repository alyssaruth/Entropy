package online.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import online.util.XmlBuilderClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import screen.ScreenCache;
import util.DateUtil;
import util.Debug;
import util.EntropyColour;
import util.MessageUtil;
import util.TableUtil;
import util.XmlUtil;

public class Leaderboard extends JFrame
						 implements ActionListener
{
	private static final Font TABLE_FONT = new Font("Arial",Font.BOLD,15);
	private static final String START = "<html>";
	private static final String END = "</html>";
	private static final int MIN_REFRESH_MILLIS = 1000;
	
	private long lastRefreshMillis = 0;
	
	public Leaderboard() 
	{
		setResizable(false);
		setTitle("Leaderboard");
		setSize(590, 500);

		getContentPane().add(panelRefresh, BorderLayout.NORTH);
		panelRefresh.add(btnRefresh);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		JPanel panelEntropy = new JPanel();
		panelEntropy.setBackground(new Color(152, 251, 152));
		tabbedPane.addTab("Entropy", null, panelEntropy, null);
		panelEntropy.setLayout(new BorderLayout(0, 0));
		scrollPaneEntropy.setBackground(new Color(152, 251, 152));
		panelEntropy.add(scrollPaneEntropy);
		tableEntropy.getTableHeader().setReorderingAllowed(false);
		tableVectropy.getTableHeader().setReorderingAllowed(false);
		scrollPaneEntropy.setViewportView(tableEntropy);
		JPanel panelVectropy = new JPanel();
		panelVectropy.setBackground(new Color(152, 251, 152));
		tabbedPane.addTab("Vectropy", null, panelVectropy, null);
		panelVectropy.setLayout(new BorderLayout(0, 0));
		panelVectropy.add(scrollPaneVectropy);
		scrollPaneVectropy.setViewportView(tableVectropy);
		JPanel panelAchievements = new JPanel();
		tabbedPane.addTab("Achievements", null, panelAchievements, null);
		panelAchievements.setLayout(new BorderLayout(0, 0));
		panelAchievements.add(scrollPaneAchievements);
		scrollPaneAchievements.setViewportView(tableAchievements);
		tabbedPane.addTab("Global Stats", null, panelGlobalStats, null);
		panelGlobalStats.setLayout(null);
		JLabel lblTotalGamesPlayed = new JLabel("Total games played:");
		lblTotalGamesPlayed.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTotalGamesPlayed.setBounds(12, 12, 173, 32);
		panelGlobalStats.add(lblTotalGamesPlayed);
		JLabel lblTotalGameDuration = new JLabel("Total game duration:");
		lblTotalGameDuration.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTotalGameDuration.setBounds(12, 54, 173, 32);
		panelGlobalStats.add(lblTotalGameDuration);
		JLabel lblMostUsersOnline = new JLabel("Most users online:");
		lblMostUsersOnline.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblMostUsersOnline.setBounds(12, 96, 173, 32);
		panelGlobalStats.add(lblMostUsersOnline);
		lblGamesPlayed.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGamesPlayed.setBounds(192, 12, 108, 32);
		panelGlobalStats.add(lblGamesPlayed);
		lblGameDuration.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGameDuration.setBounds(192, 54, 108, 32);
		panelGlobalStats.add(lblGameDuration);
		lblUsersOnline.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUsersOnline.setBounds(192, 96, 108, 32);
		panelGlobalStats.add(lblUsersOnline);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 184, 579, 7);
		panelGlobalStats.add(separator);

		JLabel lblIndividualRooms = new JLabel("Individual Rooms");
		lblIndividualRooms.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblIndividualRooms.setHorizontalAlignment(SwingConstants.CENTER);
		lblIndividualRooms.setBounds(0, 154, 579, 24);
		panelGlobalStats.add(lblIndividualRooms);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 189, 559, 200);
		panelGlobalStats.add(scrollPane);
		scrollPane.setViewportView(tableRoomStats);

		tableEntropy.getTableHeader().setReorderingAllowed(false);
		tableVectropy.getTableHeader().setReorderingAllowed(false);
		tableAchievements.getTableHeader().setReorderingAllowed(false);
		tableRoomStats.getTableHeader().setReorderingAllowed(false);

		btnRefresh.addActionListener(this);

		setColours();
	}
	
	private final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
	private final JScrollPane scrollPaneEntropy = new JScrollPane();
	private final JTable tableEntropy = new JTable();
	private final JScrollPane scrollPaneVectropy = new JScrollPane();
	private final JTable tableVectropy = new JTable();
	private final JScrollPane scrollPaneAchievements = new JScrollPane();
	private final JTable tableAchievements = new JTable();
	private final JPanel panelRefresh = new JPanel();
	private final JButton btnRefresh = new JButton("Refresh");
	private final JPanel panelGlobalStats = new JPanel();
	private final JLabel lblGamesPlayed = new JLabel("");
	private final JLabel lblGameDuration = new JLabel("");
	private final JLabel lblUsersOnline = new JLabel("");
	private final JTable tableRoomStats = new JTable();
	
	private void setColours()
	{
		tabbedPane.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		UIManager.put("TabbedPane.selected", EntropyColour.COLOUR_LOBBY_MEDIUM_BLUE);
		tabbedPane.setUI(new javax.swing.plaf.metal.MetalTabbedPaneUI()
		{
			  @Override
			protected void paintContentBorder(Graphics g,int tabPlacement,int selectedIndex)
			  {
				  
			  }
		});
		SwingUtilities.updateComponentTreeUI(tabbedPane);
		
		getContentPane().setBackground(EntropyColour.COLOUR_LOBBY_PALER_BLUE);
		panelRefresh.setBackground(EntropyColour.COLOUR_LOBBY_PALER_BLUE);
		panelGlobalStats.setBackground(EntropyColour.COLOUR_LOBBY_PALER_BLUE);
		setTableColours(tableEntropy);
		setTableColours(tableVectropy);
		setTableColours(tableAchievements);
		setTableColours(tableRoomStats);
	}
	
	private void setTableColours(JTable table)
	{
		table.setBackground(EntropyColour.COLOUR_LEADERBOARD_PALE_GREEN);
		table.getTableHeader().setBackground(EntropyColour.COLOUR_LEADERBOARD_DARKER_GREEN);
		table.setSelectionBackground(EntropyColour.COLOUR_LEADERBOARD_MEDIUM_GREEN);
		table.getParent().setBackground(EntropyColour.COLOUR_LEADERBOARD_PALER_GREEN);
	}
	
	public void init()
	{
		setTableModels();
		refresh();
	}
	
	private void setTableModels()
	{
		setTableModelStats(tableEntropy);
		setTableModelStats(tableVectropy);
		setTableModelAchievements();
		setTableModelRoomStats();
	}
	
	private void setTableModelStats(JTable table)
	{
		DefaultTableModel model = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column) {return false;}
		};
		
		model.addColumn("Name");
		model.addColumn("2 player");
		model.addColumn("3 player");
		model.addColumn("4 player");
		model.addColumn("Points");
		
		table.setModel(model);
		
		int columns = table.getColumnCount();
		for (int i=0; i<columns; i++)
		{
			table.getColumnModel().getColumn(i).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		}
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		sorter.setComparator(4, TableUtil.INT_COMPARATOR);
		table.setRowSorter(sorter);
		sorter.toggleSortOrder(4);
		sorter.toggleSortOrder(4);
		
		table.setRowHeight(25);
	}
	
	private void setTableModelAchievements()
	{
		DefaultTableModel model = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column) {return false;}
		};
		
		model.addColumn("Name");
		model.addColumn("Achievements");
		
		tableAchievements.setModel(model);
		
		int columns = tableAchievements.getColumnCount();
		for (int i=0; i<columns; i++)
		{
			tableAchievements.getColumnModel().getColumn(i).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		}
		
		tableAchievements.setRowHeight(25);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		sorter.setComparator(1, TableUtil.INT_COMPARATOR);
		sorter.toggleSortOrder(1);
		sorter.toggleSortOrder(1);
		tableAchievements.setRowSorter(sorter);
	}
	
	private void setTableModelRoomStats()
	{
		DefaultTableModel model = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column) {return false;}
		};
		
		model.addColumn("Room");
		model.addColumn("Games Played");
		model.addColumn("Total Duration");
		
		tableRoomStats.setModel(model);
		
		tableRoomStats.getColumnModel().getColumn(0).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		tableRoomStats.getColumnModel().getColumn(1).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		tableRoomStats.getColumnModel().getColumn(2).setCellRenderer(TableUtil.TIME_RENDERER);
		
		tableRoomStats.setRowHeight(20);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		sorter.setComparator(2, TableUtil.LONG_COMPARATOR);
		sorter.setComparator(1, TableUtil.INT_COMPARATOR);
		sorter.toggleSortOrder(1);
		sorter.toggleSortOrder(1);
		tableRoomStats.setRowSorter(sorter);
	}
	
	private void refresh()
	{
		long timeSinceLastRefresh = System.currentTimeMillis() - lastRefreshMillis;
		if (timeSinceLastRefresh < MIN_REFRESH_MILLIS)
		{
			return;
		}
		
		lastRefreshMillis = System.currentTimeMillis();
		EntropyLobby lobby = ScreenCache.getEntropyLobby();
		
		String username = lobby.getUsername();
		Document leaderboardRequest = XmlBuilderClient.factoryLeaderboardRequest(username);
		MessageUtil.sendMessage(leaderboardRequest, 0);
	}
	
	private void buildTablesFromResponse(String totalGames, String totalDuration, String usersOnline,
	  ArrayList<Object[]> roomStatsRows, ArrayList<Object[]> entropyRows,
	  ArrayList<Object[]> vectropyRows, ArrayList<Object[]> achievementRows)
	{
		clearModels();
		
		//InvokeLater
		lblGamesPlayed.setText(totalGames);
		lblGameDuration.setText(totalDuration);
		lblUsersOnline.setText(usersOnline);
				
		addRows(tableRoomStats, roomStatsRows);
		addRows(tableEntropy, entropyRows);
		addRows(tableVectropy, vectropyRows);
		addRows(tableAchievements, achievementRows);
	}
	
	private void addRows(JTable table, ArrayList<Object[]> rows)
	{
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		int size = rows.size();
		for (int i=0; i<size; i++)
		{
			Object[] row = rows.get(i);
			model.addRow(row);
		}
	}
	
	public void buildTablesFromResponseLater(final Element root)
	{
		//Do in transient
		String totalDuration = root.getAttribute("TotalDuration");
		final String totalDurationFormatted = DateUtil.formatHHMMSS(Integer.parseInt(totalDuration));
		final String totalGames = root.getAttribute("TotalGames");
		final String usersOnline = root.getAttribute("UsersOnline");
		
		//To populate the table models
		final ArrayList<Object[]> roomStatsRows = getRoomStatsModel(root);
		final ArrayList<Object[]> entropyRows = new ArrayList<>();
		final ArrayList<Object[]> vectropyRows = new ArrayList<>();
		final ArrayList<Object[]> achievementRows = new ArrayList<>();
		
		NodeList children = root.getElementsByTagName("User");
		int length = children.getLength();
		for (int i=0; i<length; i++)
		{
			Element userElement = (Element)children.item(i);
			
			String username = userElement.getAttribute("Username");
			int entropy2Lost = XmlUtil.getAttributeInt(userElement, "Entropy2Lost");
			int entropy3Lost = XmlUtil.getAttributeInt(userElement, "Entropy3Lost");
			int entropy4Lost = XmlUtil.getAttributeInt(userElement, "Entropy4Lost");
			int entropy2Won = XmlUtil.getAttributeInt(userElement, "Entropy2Won");
			int entropy3Won = XmlUtil.getAttributeInt(userElement, "Entropy3Won");
			int entropy4Won = XmlUtil.getAttributeInt(userElement, "Entropy4Won");
			
			addRow(username, entropy2Won, entropy2Lost, entropy3Won, entropy3Lost, entropy4Won, entropy4Lost, entropyRows);
			
			int vectropy2Lost = XmlUtil.getAttributeInt(userElement, "Vectropy2Lost");
			int vectropy3Lost = XmlUtil.getAttributeInt(userElement, "Vectropy3Lost");
			int vectropy4Lost = XmlUtil.getAttributeInt(userElement, "Vectropy4Lost");
			int vectropy2Won = XmlUtil.getAttributeInt(userElement, "Vectropy2Won");
			int vectropy3Won = XmlUtil.getAttributeInt(userElement, "Vectropy3Won");
			int vectropy4Won = XmlUtil.getAttributeInt(userElement, "Vectropy4Won");
			
			addRow(username, vectropy2Won, vectropy2Lost, vectropy3Won, vectropy3Lost, vectropy4Won, vectropy4Lost, vectropyRows);
			
			String achievementCount = userElement.getAttribute("Achievements");
			
			if (!achievementCount.isEmpty())
			{
				String[] row = {username, achievementCount};
				achievementRows.add(row);
			}
		}
		
		Runnable updateRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				buildTablesFromResponse(totalGames, totalDurationFormatted, usersOnline, roomStatsRows, 
				  entropyRows, vectropyRows, achievementRows);
			}
		};
		
		SwingUtilities.invokeLater(updateRunnable);
	}
	
	private ArrayList<Object[]> getRoomStatsModel(Element root)
	{
		ArrayList<Object[]> model = new ArrayList<>();
		
		NodeList children = root.getElementsByTagName("RoomStats");
		int length = children.getLength();
		
		for (int i=0; i<length; i++)
		{
			Element child = (Element)children.item(i);
			String roomName = child.getAttribute("RoomName");
			int gamesPlayed = XmlUtil.getAttributeInt(child, "RoomGamesPlayed");
			long totalDuration = XmlUtil.getAttributeLong(child, "RoomDuration");
			
			Object[] row = {roomName, "" + gamesPlayed, totalDuration};
			model.add(row);
		}
		
		return model;
	}
	
	private void clearModels()
	{
		clearModel(tableEntropy);
		clearModel(tableVectropy);
		clearModel(tableAchievements);
		clearModel(tableRoomStats);
	}
	
	private void clearModel(JTable table)
	{
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		int rowCount = model.getRowCount();
		for (int i=rowCount-1; i>=0; i--)
		{
			model.removeRow(i);
		}
	}
	
	private void addRow(String username, int twoPWins, int twoPLosses, 
						int threePWins, int threePLosses, int fourPWins, int fourPLosses, ArrayList<Object[]> list)
	{
		//check if there's anything to add
		boolean shouldAddRow = false;
		shouldAddRow |= twoPWins > 0;
		shouldAddRow |= twoPLosses > 0;
		shouldAddRow |= threePWins > 0;
		shouldAddRow |= threePLosses > 0;
		shouldAddRow |= fourPWins > 0;
		shouldAddRow |= fourPLosses > 0;
		
		if (shouldAddRow)
		{
			int score = twoPWins + (2 * threePWins) + (3* fourPWins) - twoPLosses - threePLosses - fourPLosses;
			String twoPlayer = START + formatWin(twoPWins) + ", " + formatLoss(twoPLosses) + END;
			String threePlayer = START + formatWin(threePWins) + ", " + formatLoss(threePLosses) + END;
			String fourPlayer = START + formatWin(fourPWins) + ", " + formatLoss(fourPLosses) + END;
			
			String[] row = {username, twoPlayer, threePlayer, fourPlayer, "" + score};
			list.add(row);
		}
	}
	
	private String formatWin(int number)
	{
		if (number == 0)
		{
			return "<font color=\"gray\">W:0</font>";
		}
		else
		{
			return "<font color=\"green\">W:" + number + "</font>";
		}
	}
	
	private String formatLoss(int number)
	{
		if (number == 0)
		{
			return "<font color=\"gray\">L:0</font>";
		}
		else
		{
			return "<font color=\"red\">L:" + number + "</font>";
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		refresh();
	}
}
