package screen;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import game.GameMode;
import object.ApiStrategy;
import object.LimitedDocument;
import util.ApiUtil;
import util.CpuStrategies;
import util.Debug;
import util.DialogUtil;
import util.GameConstants;
import util.TableUtil;
import util.TableUtil.DefaultModel;

public class PreferencesPanelPlayers extends AbstractPreferencesPanel
									 implements MouseListener,
									 			ItemListener
{
	private String playerName = "Player";
	private String opponentOneName = "Mark";
	private String opponentTwoName = "Dave";
	private String opponentThreeName = "Tom";
	private boolean opponentTwoEnabled = false;
	private boolean opponentThreeEnabled = false;
	private String opponentOneStrategy = "Mark";
	private String opponentTwoStrategy = "Basic";
	private String opponentThreeStrategy = "Basic";
	private ArrayList<ApiStrategy> apiStrategies = null;
	private GameMode gameMode = GameMode.Entropy;
	
	public PreferencesPanelPlayers()
	{
		setLayout(null);
		
		separator_2.setBounds(0, 38, 434, 2);
		add(separator_2);
		lblPlayers.setBounds(172, 12, 70, 17);
		add(lblPlayers);
		lblPlayers.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayers.setFont(new Font("Tahoma", Font.PLAIN, 14));
		playerNameField.setDocument(new LimitedDocument(10));
		opponentOneNameField.setDocument(new LimitedDocument(10));
		opponentTwoNameField.setDocument(new LimitedDocument(10));
		opponentThreeNameField.setDocument(new LimitedDocument(10));
		opponentOneNameField.setBounds(60, 102, 86, 22);
		add(opponentOneNameField);
		opponentOneNameField.setText(opponentOneName);
		opponentOneNameField.setColumns(10);
		cbOpponentTwo.setBounds(22, 143, 29, 23);
		add(cbOpponentTwo);
		cbOpponentThree.setBounds(22, 185, 29, 23);
		add(cbOpponentThree);
		opponentTwoNameField.setBounds(60, 144, 86, 22);
		add(opponentTwoNameField);
		opponentTwoNameField.setText(opponentTwoName);
		opponentTwoNameField.setColumns(10);
		opponentThreeNameField.setBounds(60, 186, 86, 22);
		add(opponentThreeNameField);
		opponentThreeNameField.setText(opponentThreeName);
		opponentThreeNameField.setColumns(10);
		opponentTwoStrat.setBounds(172, 144, 197, 22);
		add(opponentTwoStrat);
		opponentThreeStrat.setBounds(172, 186, 197, 22);
		add(opponentThreeStrat);
		opponentOneStrat.setBounds(172, 102, 197, 22);
		add(opponentOneStrat);
		playerNameField.setBounds(212, 60, 86, 22);
		add(playerNameField);
		playerNameField.setColumns(10);
		lblPlayerName.setBounds(122, 60, 80, 22);
		add(lblPlayerName);
		label.setFont(new Font("Tahoma", Font.ITALIC, 11));
		label.setBounds(65, 395, 304, 14);
		add(label);
		lblApiHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblApiHeader.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblApiHeader.setBounds(171, 240, 86, 17);
		add(lblApiHeader);
		separator_4.setBounds(0, 265, 429, 2);
		add(separator_4);
		scrollPane.setBounds(17, 307, 402, 80);
		add(scrollPane);
		tableApiStrategies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(tableApiStrategies);
		btnNewApiStrategy.setBounds(17, 278, 151, 23);
		add(btnNewApiStrategy);
		tableApiStrategies.getTableHeader().setReorderingAllowed(false);
		popupMenu.add(enableItem);
		popupMenu.add(amendItem);
		popupMenu.add(deleteItem);
	
		btnNewApiStrategy.addActionListener(this);
		enableItem.addActionListener(this);
		deleteItem.addActionListener(this);
		amendItem.addActionListener(this);
		tableApiStrategies.addMouseListener(this);
		cbOpponentTwo.addItemListener(this);
		cbOpponentThree.addItemListener(this);
	}
	
	private final JLabel lblPlayers = new JLabel("Players");
	private final JSeparator separator_2 = new JSeparator();
	private final JLabel lblPlayerName = new JLabel("Player Name");
	private final JTextField playerNameField = new JTextField();
	private final JTextField opponentOneNameField = new JTextField();
	private final JTextField opponentTwoNameField = new JTextField();
	private final JTextField opponentThreeNameField = new JTextField();
	private final JCheckBox cbOpponentTwo = new JCheckBox();
	private final JCheckBox cbOpponentThree = new JCheckBox();
	private final JComboBox<String> opponentOneStrat = new JComboBox<>();
	private final JComboBox<String> opponentTwoStrat = new JComboBox<>();
	private final JComboBox<String> opponentThreeStrat = new JComboBox<>();
	private final JLabel label = new JLabel("Note: Changes will not take effect until you start a new game.");
	private final JSeparator separator_4 = new JSeparator();
	private final JLabel lblApiHeader = new JLabel("API Options");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTable tableApiStrategies = new JTable();
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem amendItem = new JMenuItem("Amend");
	private final JMenuItem deleteItem = new JMenuItem("Delete");
	private final JMenuItem enableItem = new JMenuItem("Enable");
	private final JButton btnNewApiStrategy = new JButton("New API Strategy");
	
	/**
	 * Abstract methods
	 */
	@Override
	public void initVariables()
	{
		getVariablesFromPrefs();
		buildApiTable();
		setPlayerNames();
		setOpponentEnablementAndStrategies();
		setOpponentStrategies();
	}
	
	@Override
	public boolean valid()
	{
		String nameOne = playerNameField.getText();
		String nameTwo = opponentOneNameField.getText();
		String nameThree = opponentTwoNameField.getText();
		String nameFour = opponentThreeNameField.getText();

		if (nameOne.length() == 0
		  || nameTwo.length() == 0
		  || nameThree.length() == 0
		  || nameFour.length() == 0)
		{
			DialogUtil.showError("You must enter a name for each player.");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void savePreferences()
	{
		playerName = playerNameField.getText();
		opponentOneName = opponentOneNameField.getText();
		opponentTwoName = opponentTwoNameField.getText();
		opponentThreeName = opponentThreeNameField.getText();
		opponentOneStrategy = (String) opponentOneStrat.getSelectedItem();
		opponentTwoStrategy = (String) opponentTwoStrat.getSelectedItem();
		opponentThreeStrategy = (String) opponentThreeStrat.getSelectedItem();
		
		prefs.put(PREFERENCES_STRING_PLAYER_NAME, playerName);
		prefs.put(PREFERENCES_STRING_OPPONENT_ONE_NAME, opponentOneName);
		prefs.put(PREFERENCES_STRING_OPPONENT_TWO_NAME, opponentTwoName);
		prefs.put(PREFERENCES_STRING_OPPONENT_THREE_NAME, opponentThreeName);
		prefs.putBoolean(PREFERENCES_BOOLEAN_OPPONENT_TWO_ENABLED, opponentTwoEnabled);
		prefs.putBoolean(PREFERENCES_BOOLEAN_OPPONENT_THREE_ENABLED, opponentThreeEnabled);
		prefs.put(PREFERENCES_STRING_OPPONENT_ONE_STRATEGY, opponentOneStrategy);
		prefs.put(PREFERENCES_STRING_OPPONENT_TWO_STRATEGY, opponentTwoStrategy);
		prefs.put(PREFERENCES_STRING_OPPONENT_THREE_STRATEGY, opponentThreeStrategy);
		
		ApiUtil.saveApiStrategiesToPreferences(apiStrategies);
	}
	
	
	private void getVariablesFromPrefs()
	{
		playerName = prefs.get(PREFERENCES_STRING_PLAYER_NAME, "Player");
		opponentOneName = prefs.get(PREFERENCES_STRING_OPPONENT_ONE_NAME, "Mark");
		opponentTwoName = prefs.get(PREFERENCES_STRING_OPPONENT_TWO_NAME, "Dave");
		opponentThreeName = prefs.get(PREFERENCES_STRING_OPPONENT_THREE_NAME, "Tom");
		opponentTwoEnabled = prefs.getBoolean(PREFERENCES_BOOLEAN_OPPONENT_TWO_ENABLED, true);
		opponentThreeEnabled = prefs.getBoolean(PREFERENCES_BOOLEAN_OPPONENT_THREE_ENABLED, true);
		opponentOneStrategy = prefs.get(PREFERENCES_STRING_OPPONENT_ONE_STRATEGY, "Basic");
		opponentTwoStrategy = prefs.get(PREFERENCES_STRING_OPPONENT_TWO_STRATEGY, "Basic");
		opponentThreeStrategy = prefs.get(PREFERENCES_STRING_OPPONENT_THREE_STRATEGY, "Basic");
		apiStrategies = ApiUtil.getApiStrategiesFromPreferences();
		
		gameMode = GameMode.valueOf(prefs.get(PREFERENCES_STRING_GAME_MODE, GameMode.Entropy.name()));
	}
	
	private void setPlayerNames()
	{
		playerNameField.setText(playerName);
		opponentOneNameField.setText(opponentOneName);
		opponentTwoNameField.setText(opponentTwoName);
		opponentThreeNameField.setText(opponentThreeName);
	}
	
	private void setOpponentEnablementAndStrategies()
	{
		cbOpponentThree.setEnabled(opponentTwoEnabled);
		cbOpponentTwo.setEnabled(!opponentThreeEnabled);
		
		cbOpponentTwo.setSelected(opponentTwoEnabled);
		cbOpponentThree.setSelected(opponentThreeEnabled);
		opponentTwoNameField.setEnabled(opponentTwoEnabled);
		opponentThreeNameField.setEnabled(opponentThreeEnabled);
		opponentTwoStrat.setEnabled(opponentTwoEnabled);
		opponentThreeStrat.setEnabled(opponentThreeEnabled);
	}
	
	private void setOpponentStrategies()
	{
		opponentOneStrat.setSelectedItem(opponentOneStrategy);
		opponentTwoStrat.setSelectedItem(opponentTwoStrategy);
		opponentThreeStrat.setSelectedItem(opponentThreeStrategy);
	}
	
	private void buildApiTable()
	{
		DefaultModel model = new TableUtil.DefaultModel();
		tableApiStrategies.setModel(model);
		
		//Columns
		model.addColumn("Name");
		model.addColumn("Port");
		model.addColumn("Game");
		model.addColumn("Messaging");
		model.addColumn("Enabled");
		
		//Centre rendering for everything but the last column
		for (int i=0; i<model.getColumnCount()-1; i++)
		{
			tableApiStrategies.getColumnModel().getColumn(i).setCellRenderer(new TableUtil.SimpleRenderer(null));
		}
		
		//Sorting
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		tableApiStrategies.setRowSorter(sorter);
		
		//Populate the rows
		for (int i=0; i<apiStrategies.size(); i++)
		{
			ApiStrategy strategy = apiStrategies.get(i);
			Object[] row = strategy.getTableModelRow();
			model.addRow(row);
		}
		
		updateStrategySelection(gameMode);
	}
	
	public void updateStrategySelection(GameMode gameMode)
	{
		this.gameMode = gameMode;
		Vector<String> allStrategies = CpuStrategies.getAllStrategies(gameMode == GameMode.Entropy, apiStrategies);
		
		ComboBoxModel<String> comboModel = new DefaultComboBoxModel<>(allStrategies);
		opponentOneStrat.setModel(comboModel);
		comboModel = new DefaultComboBoxModel<>(allStrategies);
		opponentTwoStrat.setModel(comboModel);
		comboModel = new DefaultComboBoxModel<>(allStrategies);
		opponentThreeStrat.setModel(comboModel);
	}
	
	private void enableApi(ApiStrategy strategy)
	{
		String question = "Strategy " + strategy.getName() + " was disabled due to the following error:"
						+ "\n\n" + strategy.getError()
						+ "\n\nWould you like to re-enable it?";
		
		int option = DialogUtil.showQuestion(question, false);
		if (option == JOptionPane.YES_OPTION)
		{
			strategy.setError("");
			buildApiTable();
		}
	}
	
	private void amendApi(ApiStrategy strategy)
	{
		ApiAmendDialog.amendStrategy(strategy);
		buildApiTable();
	}
	
	private void deleteApi(ApiStrategy strategy)
	{
		String question = "Are you sure you want to delete the " + strategy.getName() + " strategy?";
		int option = DialogUtil.showQuestion(question, false);
		if (option == JOptionPane.YES_OPTION)
		{
			apiStrategies.remove(strategy);
			buildApiTable();
		}
	}
	
	private ApiStrategy getSelectedStrategyFromTable()
	{
		int row = tableApiStrategies.getSelectedRow();
		if (row == -1)
		{
			return null;
		}
		
		int internalRow = tableApiStrategies.convertRowIndexToModel(row);
		DefaultModel model = (DefaultModel)tableApiStrategies.getModel();
		String name = (String)model.getValueAt(internalRow, 0);
		
		int size = apiStrategies.size();
		for (int i=0; i<size; i++)
		{
			ApiStrategy strategy = apiStrategies.get(i);
			if (strategy.getName().equals(name))
			{
				return strategy;
			}
		}
		
		Debug.stackTrace("Failed to find strategy in list for name " + name);
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == btnNewApiStrategy)
		{
			ApiStrategy strategy = ApiAmendDialog.createStrategy();
			if (strategy != null)
			{
				apiStrategies.add(strategy);
				buildApiTable();
			}
		}
		else if (source == amendItem)
		{
			ApiStrategy strategy = getSelectedStrategyFromTable();
			if (strategy != null)
			{
				amendApi(strategy);
			}
		}
		else if (source == deleteItem)
		{
			ApiStrategy strategy = getSelectedStrategyFromTable();
			if (strategy != null)
			{
				deleteApi(strategy);
			}
		}
		else if (source == enableItem)
		{
			ApiStrategy strategy = getSelectedStrategyFromTable();
			if (strategy != null)
			{
				enableApi(strategy);
			}
		}
		else
		{
			Debug.stackTrace("Unexpected actionPerformed: " + source);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent arg0){}
	
	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		if (SwingUtilities.isRightMouseButton(arg0))
		{
			Point point = arg0.getPoint();
			int row = tableApiStrategies.rowAtPoint(point);
			if (!tableApiStrategies.isRowSelected(row))
			{
				int column = tableApiStrategies.columnAtPoint(point);
				tableApiStrategies.changeSelection(row, column, false, false);
			}
			
			ApiStrategy strategy = getSelectedStrategyFromTable();
			if (strategy != null)
			{
				enableItem.setEnabled(!strategy.isEnabled());
				
				//Show the popup menu
				popupMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
		}
		else if (arg0.getClickCount() == 2)
		{
			//Double-click
			ApiStrategy strategy = getSelectedStrategyFromTable();
			if (strategy != null)
			{
				if (!strategy.isEnabled())
				{
					enableApi(strategy);
				}
				else
				{
					amendApi(strategy);
				}
			}
		}
	}
	
	@Override
	public void mouseExited(MouseEvent arg0){}
	@Override
	public void mouseEntered(MouseEvent arg0){}
	@Override
	public void mouseReleased(MouseEvent arg0){}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == cbOpponentTwo)
		{
			opponentTwoEnabled = cbOpponentTwo.isSelected();
			setOpponentEnablementAndStrategies();
		}
		else if (source == cbOpponentThree)
		{
			opponentThreeEnabled = cbOpponentThree.isSelected();
			setOpponentEnablementAndStrategies();
		}
	}
}
