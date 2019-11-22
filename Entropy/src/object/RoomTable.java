package object;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import online.screen.EntropyLobby;
import online.screen.GameRoom;
import online.util.XmlBuilderClient;

import org.w3c.dom.Document;

import util.Debug;
import util.EntropyColour;
import util.MessageUtil;
import util.TableUtil;

public final class RoomTable extends JTable
					   		 implements MouseListener
{
	private static final Font TABLE_FONT = new Font("Arial",Font.BOLD,15);
	private static final String CODE_ILLEGAL = "z";
	private static final String CODE_JOKERS = "y";
	private static final String CODE_INCLUDE_MOONS_AND_STARS = "x";
	private static final String CODE_INCLUDE_STARS = "w";
	private static final String CODE_INCLUDE_MOONS = "v";
	private static final String CODE_NEGATIVE_JACKS = "u";
	private static final String CODE_CARD_REVEAL = "t";
	
	private static int INDEX_OF_NAME_COLUMN = 0;
	private static int INDEX_OF_MODE_COLUMN = 1;
	private static int INDEX_OF_PLAYERS_COLUMN = 2;
	private static int INDEX_OF_OBSERVERS_COLUMN = 3;
	private static int INDEX_OF_FLAGS_COLUMN = 4;
	
	private EntropyLobby lobby = null;
	private DefaultTableModel model = new DefaultTableModel()
	{
		@Override
		public boolean isCellEditable(int row, int column) {return false;}
	};
	
	public RoomTable(EntropyLobby lobby)
	{
		super();
		
		setBackground(EntropyColour.COLOUR_LOBBY_MEDIUM_BLUE);
		getTableHeader().setBackground(EntropyColour.COLOUR_LOBBY_DARK_BLUE);
		
		if (!Beans.isDesignTime())
		{
			this.lobby = lobby;
			setTableModel();
			addMouseListener(this);
		}
	}
	
	public void reset()
	{
		model = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column) {return false;}
		};
		setTableModel();
	}
	
	private void setTableModel()
	{
		setModel(model);
		
		model.addColumn("Name");
		model.addColumn("Mode");
		model.addColumn("Players");
		model.addColumn("Observers");
		model.addColumn("Flags");
		
		getColumnModel().getColumn(INDEX_OF_NAME_COLUMN).setPreferredWidth(150);
		getColumnModel().getColumn(INDEX_OF_MODE_COLUMN).setPreferredWidth(75);
		getColumnModel().getColumn(INDEX_OF_FLAGS_COLUMN).setPreferredWidth(100);
		setRowHeight(25);
		setRenderersAndSorters();
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		getActionMap().put("Enter", new AbstractAction() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) 
			{
				joinRoom();
			}
		});
	}
	
	private void setRenderersAndSorters()
	{
		columnModel.getColumn(INDEX_OF_NAME_COLUMN).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		columnModel.getColumn(INDEX_OF_MODE_COLUMN).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		columnModel.getColumn(INDEX_OF_PLAYERS_COLUMN).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		columnModel.getColumn(INDEX_OF_OBSERVERS_COLUMN).setCellRenderer(new TableUtil.SimpleRenderer(TABLE_FONT));
		columnModel.getColumn(INDEX_OF_FLAGS_COLUMN).setCellRenderer(TableUtil.FLAG_RENDERER);

		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		sorter.setComparator(INDEX_OF_PLAYERS_COLUMN, TableUtil.PLAYERS_COMPARATOR);
		sorter.setComparator(INDEX_OF_FLAGS_COLUMN, TableUtil.FLAG_COMPARATOR);
		
		//Set the initial sorting to be most players first
		sorter.toggleSortOrder(INDEX_OF_PLAYERS_COLUMN);
		sorter.toggleSortOrder(INDEX_OF_PLAYERS_COLUMN);
		
		setRowSorter(sorter);
	}
	
	public void synchroniseRooms(List<RoomWrapper> rooms)
	{
		addNewRooms(rooms);
		updateRooms(rooms);
		
		//Fire this so the table stays sorted after the synch
		getRowSorter().allRowsChanged();
	}
	
	private void addNewRooms(List<RoomWrapper> rooms)
	{
		for (int i=0; i<rooms.size(); i++)
		{
			RoomWrapper room = rooms.get(i);
			String name = room.getRoomName();
			
			if (!modelContainsRoom(name))
			{
				lobby.addOrUpdateRoom(name, room);
				Object[] row = factoryRowForRoom(room);
				model.addRow(row);
			}
		}
	}
	
	private void updateRooms(List<RoomWrapper> rooms)
	{
		int rowCount = model.getRowCount();
		for (int i=0; i<rowCount; i++)
		{
			String roomName = (String)model.getValueAt(i, INDEX_OF_NAME_COLUMN);
			RoomWrapper tableRoom = lobby.getRoomForName(roomName);
			if (tableRoom == null)
			{
				Debug.append("roomName: " + roomName);
				Debug.append("rowCount: " + rowCount);
				Debug.append("rooms from server: " + rooms);
				Debug.stackTraceNoError("NULL room in lobby hashmap, despite it being in tablemodel");
			}
			
			RoomWrapper listRoom = getListRoomForName(rooms, roomName);
			
			if (tableRoom == null
			  || !listRoom.isInSync(tableRoom))
			{
				Object[] newRoomRow = factoryRowForRoom(listRoom);
				
				//Don't do it like this...
				//model.insertRow(i, newRoomRow);
				//model.removeRow(i+1);
				updateRow(i, newRoomRow);
				
				lobby.addOrUpdateRoom(roomName, listRoom);
			}
		}
	}
	
	private void updateRow(int rowNumber, Object[] newRow)
	{
		int length = newRow.length;
		for (int i=0; i<length; i++)
		{
			Object value = newRow[i];
			model.setValueAt(value, rowNumber, i);
		}
	}
	
	private boolean modelContainsRoom(String name)
	{
		int count = model.getRowCount();
		for (int i=0; i<count; i++)
		{
			String rowName = (String)model.getValueAt(i, INDEX_OF_NAME_COLUMN);
			if (rowName.equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private RoomWrapper getListRoomForName(List<RoomWrapper> rooms, String name)
	{
		int size = rooms.size();
		for (int i=0; i<size; i++)
		{
			RoomWrapper room = rooms.get(i);
			String roomName = room.getRoomName();
			if (name.equals(roomName))
			{
				return room;
			}
		}
		
		return null;
	}
	
	private Object[] factoryRowForRoom(RoomWrapper room)
	{
		String name = room.getRoomName();
		String mode = room.getModeDesc();
		String players = room.getCurrentPlayerCount() + "/" + room.getPlayers();
		String observers = "" + room.getObserverCount();
		
		FlagImage image = createFlagsForRoom(room);
		
		Object[] row = {name, mode, players, observers, image};
		return row;
	}
	
	private FlagImage createFlagsForRoom(RoomWrapper room)
	{
		FlagImage image = new FlagImage();
		
		boolean illegalAllowed = room.getIllegalAllowed();
		if (illegalAllowed)
		{
			image.appendImage("illegal", CODE_ILLEGAL);
			image.appendToolTip("Illegal available");
		}
		
		int jokerQuantity = room.getJokerQuantity();
		if (jokerQuantity > 0)
		{
			int jokerValue = room.getJokerValue();
			String iconStr = "jokers" + jokerValue + jokerQuantity;
			image.appendImage(iconStr, CODE_JOKERS);
			image.appendToolTip(jokerQuantity + " Jokers worth " + jokerValue);
		}
		
		boolean includeMoons = room.getIncludeMoons();
		boolean includeStars = room.getIncludeStars();
		if (includeMoons && includeStars)
		{
			image.appendImage("moonAndStar", CODE_INCLUDE_MOONS_AND_STARS);
			image.appendToolTip("Moons and Stars included");
		}
		else if (includeStars)
		{
			image.appendImage("star", CODE_INCLUDE_STARS);
			image.appendToolTip("Stars are included");
		}
		else if (includeMoons)
		{
			image.appendImage("moon", CODE_INCLUDE_MOONS);
			image.appendToolTip("Moons are included");
		}
		
		boolean negativeJacks = room.getNegativeJacks();
		if (negativeJacks)
		{
			image.appendImage("negativeJacks", CODE_NEGATIVE_JACKS);
			image.appendToolTip("Jacks worth -1");
		}
		
		boolean cardReveal = room.getCardReveal();
		if (cardReveal)
		{
			image.appendImage("cardReveal", CODE_CARD_REVEAL);
			image.appendToolTip("Cards are revealed");
		}
		
		return image;
	}
	
	private void joinRoom()
	{
		int viewRow = getSelectedRow();
		if (viewRow == -1)
		{
			return;
		}
		
		int internalRow = convertRowIndexToModel(viewRow);
		
		String roomName = (String)model.getValueAt(internalRow, INDEX_OF_NAME_COLUMN);
		GameRoom gameRoom = lobby.getGameRoomForName(roomName);
		RoomWrapper room = lobby.getRoomForName(roomName);
		
		if (gameRoom == null)
		{
			gameRoom = lobby.createGameRoom(room);
		}
		
		if (!gameRoom.isVisible())
		{
			Document joinRequest = XmlBuilderClient.factoryRoomJoinRequestXml(roomName, lobby.getUsername(), true, -1);
			MessageUtil.sendMessage(joinRequest, 0);
		}
		else
		{
			gameRoom.requestFocus();
		}
	}
	
	public void refresh(boolean includeFull, int mode)
	{
		RoomTableFilter filter = new RoomTableFilter(includeFull, mode);
		@SuppressWarnings("unchecked")
		TableRowSorter<? extends DefaultTableModel> sorter = (TableRowSorter<? extends DefaultTableModel>) getRowSorter();
		sorter.setRowFilter(filter);
		setRowSorter(sorter);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		if (arg0.getClickCount() == 2) 
		{
			joinRoom();
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}

	class RoomTableFilter extends RowFilter<DefaultTableModel, Integer>
	{
		private boolean includeFull = true;
		private int mode = -1;
		
		public RoomTableFilter(boolean includeFull, int mode)
		{
			this.includeFull = includeFull;
			this.mode = mode;
		}
		
		@Override
		public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> arg0) 
		{
			DefaultTableModel entryModel = arg0.getModel();
			Integer identifierInt = arg0.getIdentifier();
			int index = identifierInt.intValue();
			
			String name = (String)entryModel.getValueAt(index, INDEX_OF_NAME_COLUMN);
			RoomWrapper room = lobby.getRoomForName(name);
			
			int currentPlayers = room.getCurrentPlayerCount();
			int capacity = room.getPlayers();
			int roomMode = room.getMode();
			
			if (!includeFull
			  && currentPlayers == capacity)
			{
				return false;
			}
			
			if (mode != -1
			  && roomMode != mode)
			{
				return false;
			}
			
			return true;
		}
		
	}
}
