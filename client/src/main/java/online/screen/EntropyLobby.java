package online.screen;

import game.GameMode;
import http.dto.LobbyMessage;
import http.dto.OnlineUser;
import http.dto.RoomSummary;
import object.RoomTable;
import online.util.HeartbeatRunnable;
import online.util.XmlBuilderClient;
import online.util.XmlBuilderDesktop;
import org.w3c.dom.Document;
import screen.ScreenCache;
import util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.Beans;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static achievement.AchievementUtilKt.getAchievementsEarned;
import static util.Images.ICON_ONLINE;

public class EntropyLobby extends JFrame
						  implements WindowListener,
						  			 ActionListener
{
	public static final String LOBBY_ID = "Lobby";
	
	//Declaring these as 'Map' to fix obscure Java8 bug
	private Map<String, GameRoom> hmGameRoomByRoomName = new ConcurrentHashMap<>();
	private Map<String, RoomSummary> hmRoomByRoomName = new ConcurrentHashMap<>();
	
	private DefaultListModel<OnlineUser> usernamesModel = new DefaultListModel<>();
	private UsernameRenderer usernameRenderer = new UsernameRenderer();
	
	private String username = "";
	
	public EntropyLobby() 
	{
		setSize(780, 500);
		getContentPane().setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		setIconImage(ICON_ONLINE.getImage());
		getContentPane().add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new BorderLayout(0, 0));
		rightPanel.setPreferredSize(new Dimension(280, 0));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		rightPanel.add(statsPanel, BorderLayout.NORTH);
		panelUsersOnline.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		panelUsersOnline.setBorder(new EmptyBorder(20, 0, 0, 0));
		rightPanel.add(panelUsersOnline, BorderLayout.CENTER);
		panelUsersOnline.setLayout(new BorderLayout(0, 0));
		onlineUserList.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		usersScrollPane.setHorizontalScrollBar(null);
		panelUsersOnline.add(usersScrollPane, BorderLayout.CENTER);
		usersScrollPane.setViewportView(onlineUserList);
		lblUsersOnline.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUsersOnline.setOpaque(true);
		lblUsersOnline.setBackground(EntropyColour.COLOUR_LOBBY_DARK_BLUE);
		lblUsersOnline.setPreferredSize(new Dimension(60, 30));
		lblUsersOnline.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblUsersOnline.setHorizontalAlignment(SwingConstants.CENTER);
		
		panelUsersOnline.add(lblUsersOnline, BorderLayout.NORTH);
		chatPanel.setBackgroundColour(EntropyColour.COLOUR_LOBBY_PALER_BLUE);
		rightPanel.add(chatPanel, BorderLayout.SOUTH);
		
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		centerPanel.add(roomsScrollPane, BorderLayout.CENTER);
		roomsScrollPane.setOpaque(true);
		roomsScrollPane.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		roomsScrollPane.setViewportView(roomTable);
		centerPanel.add(northPanel, BorderLayout.NORTH);
		roomTable.getTableHeader().setReorderingAllowed(false);
		roomTable.getParent().setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		northPanel.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		ButtonGroup modeGroup = new ButtonGroup();
		northPanel.setLayout(new BorderLayout(0, 0));
		settingsPanel.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		northPanel.add(settingsPanel, BorderLayout.WEST);
		btnSettings.setToolTipText("Account settings");
		btnSettings.setPreferredSize(new Dimension(26, 26));
		btnSettings.setSelectedIcon(new ImageIcon(EntropyLobby.class.getResource("/buttons/settingsSelected.png")));
		btnSettings.setIcon(new ImageIcon(EntropyLobby.class.getResource("/buttons/settings.png")));
		
		settingsPanel.add(btnSettings);
		filterPanel.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		northPanel.add(filterPanel);
		filterPanel.add(chckbxShowFull);
		chckbxShowFull.setOpaque(false);
		filterPanel.add(rdbtnEntropy);
		rdbtnEntropy.setOpaque(false);
		modeGroup.add(rdbtnEntropy);
		filterPanel.add(rdbtnVectropy);
		rdbtnVectropy.setOpaque(false);
		modeGroup.add(rdbtnVectropy);
		filterPanel.add(rdbtnAll);
		rdbtnAll.setOpaque(false);
		modeGroup.add(rdbtnAll);
		onlineUserList.setFont(new Font("Segoe UI Symbol", Font.BOLD, 12));
		
		rdbtnAll.addActionListener(this);
		rdbtnVectropy.addActionListener(this);
		rdbtnEntropy.addActionListener(this);
		chckbxShowFull.addActionListener(this);
		btnSettings.addActionListener(this);
		
		if (!Beans.isDesignTime())
		{
			addWindowListener(this);
		}
	}
	
	private final JScrollPane roomsScrollPane = new JScrollPane();
	private final JScrollPane usersScrollPane = new JScrollPane();
	private final JList<OnlineUser> onlineUserList = new JList<>(usernamesModel);
	private final RoomTable roomTable = new RoomTable(this);
	private final JPanel rightPanel = new JPanel();
	private final OnlineStatsPanel statsPanel = new OnlineStatsPanel();
	private final JPanel panelUsersOnline = new JPanel();
	private final JLabel lblUsersOnline = new JLabel("Users Online");
	private final JPanel centerPanel = new JPanel();
	private final JPanel northPanel = new JPanel();
	private final JCheckBox chckbxShowFull = new JCheckBox("Show full");
	private final JRadioButton rdbtnEntropy = new JRadioButton("Entropy");
	private final JRadioButton rdbtnVectropy = new JRadioButton("Vectropy");
	private final JRadioButton rdbtnAll = new JRadioButton("All");
	private final JPanel filterPanel = new JPanel();
	private final JPanel settingsPanel = new JPanel();
	private final JButton btnSettings = new JButton("");
	private final OnlineChatPanel chatPanel = new OnlineChatPanel(LOBBY_ID);
	
	public void init(LobbyMessage lobbyMessage)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				roomTable.reset();
				chatPanel.clear();
				chatPanel.setEnabled(false);
				initFilterPanel();
				
				usernamesModel = new DefaultListModel<>();
				
				onlineUserList.setModel(usernamesModel);
				onlineUserList.setCellRenderer(usernameRenderer);
				statsPanel.init();

				initChatPanelIfNecessary();

				syncLobbyOnEventThread(lobbyMessage);
			}
		});
		
		AchievementsUtil.unlockConnected();
		
		//Start the notification thread, this is how the server will send us unsolicited messages
		ClientUtil.startNotificationThreads();

		int achievementsEarned = getAchievementsEarned();
		Document achievementsUpdate = XmlBuilderDesktop.factoryAchievementsUpdate(username, null, achievementsEarned);
		MessageUtil.sendMessage(achievementsUpdate, 500);
		
		hmGameRoomByRoomName = new ConcurrentHashMap<>();
		hmRoomByRoomName = new ConcurrentHashMap<>();
		
		HeartbeatRunnable heartbeatRunnable = new HeartbeatRunnable(this);
		Thread heartBeatThread = new Thread(heartbeatRunnable, "Heartbeat");
		heartBeatThread.start();
	}
	
	private void initFilterPanel()
	{
		rdbtnAll.setSelected(true);
		chckbxShowFull.setSelected(true);
	}
	
	public OnlineChatPanel getChatPanelForRoomName(String roomName)
	{
		if (roomName.equals(LOBBY_ID))
		{
			return chatPanel;
		}
		
		GameRoom room = hmGameRoomByRoomName.get(roomName);
		return room.getChatPanel();
	}
	
	public OnlineStatsPanel getOnlineStatsPanel()
	{
		return statsPanel;
	}
	
	public RoomSummary getRoomForName(String roomName)
	{
		return hmRoomByRoomName.get(roomName);
	}
	
	public GameRoom getGameRoomForName(String roomName)
	{
		return hmGameRoomByRoomName.get(roomName);
	}
	
	public void addOrUpdateRoom(String roomName, RoomSummary room)
	{
		hmRoomByRoomName.put(roomName, room);
	}
	
	public GameRoom createGameRoom(RoomSummary room)
	{
		String roomName = room.getName();
		GameRoom gameRoom = GameRoom.factoryCreate(room);
		
		hmGameRoomByRoomName.put(roomName, gameRoom);
		
		return gameRoom;
	}

	public void syncLobby(LobbyMessage response)
	{
		SwingUtilities.invokeLater(() -> {
			syncLobbyOnEventThread(response);
        });
	}

	private void syncLobbyOnEventThread(LobbyMessage response) {
		roomTable.synchroniseRooms(response.getRooms());
		synchUsernames(response.getUsers());
	}
	
	private boolean otherRoomsAreVisible()
	{
		Iterator<Map.Entry<String, GameRoom>> it = hmGameRoomByRoomName.entrySet().iterator();
		
		for (; it.hasNext(); )
		{
			Map.Entry<String, GameRoom> entry = it.next();
			GameRoom gameRoom = entry.getValue();
			if (gameRoom.isVisible())
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void closeRooms()
	{
		Iterator<Map.Entry<String, GameRoom>> it = hmGameRoomByRoomName.entrySet().iterator();
		
		for (; it.hasNext(); )
		{
			Map.Entry<String, GameRoom> entry = it.next();
			GameRoom gameRoom = entry.getValue();
			gameRoom.dispose();
		}
	}

	private void initChatPanelIfNecessary()
	{
		if (!chatPanel.getInitted())
		{
			synchronized (chatPanel)
			{
				if (!chatPanel.getInitted())
				{
					chatPanel.setUsername(username);
					chatPanel.init();
					chatPanel.setWrapWidth(200);
					chatPanel.setEnabled(true);
				}
			}
		}
	}
	
	private void synchUsernames(List<OnlineUser> usernamesFromServer)
	{
		initChatPanelIfNecessary();
		
		//add any new ones
		usernamesModel.clear();

		int size = usernamesFromServer.size();
		for (int i=0; i<size; i++)
		{
			OnlineUser userFromServer = usernamesFromServer.get(i);
			usernamesModel.addElement(userFromServer);
			if (!usernamesModel.contains(userFromServer))
			{
				usernamesModel.addElement(userFromServer);
			}
		}
		
		//Update the count of people online
		lblUsersOnline.setText("Users Online (" + size + ")");
	}
	
	public void setUsername(String username)
	{
		this.username = username;
		setTitle("Entropy Lobby - Logged in as " + username);
	}
	public String getUsername()
	{
		return username;
	}
	
	public boolean confirmExit()
	{
		int option = JOptionPane.YES_OPTION;
		
		if (otherRoomsAreVisible())
		{
			option = DialogUtil.showQuestion("Closing the lobby will disconnect you from EntropyOnline"
										   + "\n and close all active games."
										   + "\n\nContinue?", false);
		}
		
		if (option == JOptionPane.YES_OPTION)
		{
			return true;
		}
		
		return false;
	}
	
	public void fireAppearancePreferencesChange()
	{
		Iterator<Map.Entry<String, GameRoom>> it = hmGameRoomByRoomName.entrySet().iterator();
		
		for (; it.hasNext(); )
		{
			Map.Entry<String, GameRoom> entry = it.next();
			GameRoom gameRoom = entry.getValue();
			
			if (gameRoom.isVisible())
			{
				gameRoom.fireAppearancePreferencesChange();
			}
		}
	}
	
	private void refreshRoomTable()
	{
		boolean includeFull = chckbxShowFull.isSelected();
		GameMode mode = null;
		if (rdbtnEntropy.isSelected())
		{
			mode = GameMode.Entropy;
		}
		else if (rdbtnVectropy.isSelected())
		{
			mode = GameMode.Vectropy;
		}
		
		roomTable.refresh(includeFull, mode);
	}
	
	public void exit(boolean forceClose)
	{
		ScreenCache.getLeaderboard().dispose();
		closeRooms();
		dispose();
		
		if (!forceClose)
		{
			//Send a disconnect message.
			Document disconnectRequest = XmlBuilderClient.factoryDisconnectRequest(username);
			String messageString = XmlUtil.getStringFromDocument(disconnectRequest);
			
			MessageSenderParams params = new MessageSenderParams(messageString, 0, 5);
			params.setExpectResponse(false);
			MessageUtil.sendMessage(params, true);
		}
		
		ScreenCache.getMainScreen().maximise();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) 
	{
		if (confirmExit())
		{
			exit(false);
		}
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == btnSettings)
		{
			AccountSettingsDialog dialog = new AccountSettingsDialog(username);
			dialog.setModal(true);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
		}
		else
		{
			refreshRoomTable();
		}
	}
	
	private static class UsernameRenderer extends DefaultListCellRenderer
	{
		@Override
		@SuppressWarnings("rawtypes")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) 
		{
			OnlineUser username = (OnlineUser)value;
			return super.getListCellRendererComponent(list, StringUtil.escapeHtml(username.getName()), index, isSelected, cellHasFocus);
		}
	}
}
