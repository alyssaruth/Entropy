package screen;

import bean.NumberField;
import game.GameMode;
import game.GameSettings;
import util.*;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Vector;

import static utils.CoreGlobals.logger;

public class SimulationDialog extends JDialog
{
	private static final long serialVersionUID = 1;

	private HashMap<Integer, SimulationResults> hmSimulationResultsByOpponentNumber = new HashMap<>();

	private int numberOfGames = 0;
	
	private static final String[] COLUMN_TOOL_TIPS = {null, null, null, 
													  "#(Challenges) / #(Opportunities to Challenge)", 
													  "#(Successful Challenges) / #(Challenges)", 
													  "#(Perfect Games) / #(Wins)"};

	public SimulationDialog() 
	{
		getContentPane().setLayout(null);
		scrollPane.setBounds(0, 323, 434, 87);
		getContentPane().add(scrollPane);
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(0, 323, 434, 2);
		getContentPane().add(separator_2);
		btnRunSimulation.setBounds(159, 257, 126, 23);
		getContentPane().add(btnRunSimulation);
		gamesToSimulate.setBounds(231, 207, 86, 20);
		getContentPane().add(gamesToSimulate);
		gamesToSimulate.setValue(10000);
		JLabel lblSimulate = new JLabel("Games to simulate");
		lblSimulate.setBounds(119, 210, 126, 14);
		getContentPane().add(lblSimulate);
		tabbedPane.setBounds(0, 0, 434, 184);
		getContentPane().add(tabbedPane);
		tabbedPane.addTab("CPU Settings", null, cpuPanel, null);
		cpuPanel.setLayout(null);
		cbOpponentThree.setBounds(368, 54, 29, 23);
		cpuPanel.add(cbOpponentThree);
		opponentOneStrat.setBounds(149, 26, 135, 20);
		cpuPanel.add(opponentOneStrat);
		opponentTwoStrat.setBounds(10, 78, 135, 20);
		opponentTwoStrat.setEnabled(false);
		cpuPanel.add(opponentTwoStrat);
		opponentThreeStrat.setBounds(284, 78, 135, 20);
		opponentThreeStrat.setEnabled(false);
		cpuPanel.add(opponentThreeStrat);
		opponentZeroStrat.setBounds(149, 125, 135, 20);
		cpuPanel.add(opponentZeroStrat);
		cbOpponentTwo.setSelected(false);
		cbOpponentTwo.setEnabled(true);
		cbOpponentTwo.setBounds(83, 54, 29, 23);
		cpuPanel.add(cbOpponentTwo);
		JLabel lblCpu_1 = new JLabel("CPU 0");
		lblCpu_1.setForeground(Color.RED);
		lblCpu_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblCpu_1.setBounds(193, 109, 46, 14);
		cpuPanel.add(lblCpu_1);
		JLabel lblCpu_2 = new JLabel("CPU 1");
		lblCpu_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblCpu_2.setForeground(Color.BLUE);
		lblCpu_2.setBounds(193, 11, 46, 14);
		cpuPanel.add(lblCpu_2);
		lblCpu_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblCpu_3.setForeground(new Color(128, 0, 128));
		lblCpu_3.setBounds(317, 58, 46, 14);
		cpuPanel.add(lblCpu_3);
		lblCpu_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblCpu_4.setForeground(new Color(0, 128, 0));
		lblCpu_4.setBounds(31, 58, 46, 14);
		cpuPanel.add(lblCpu_4);
		JPanel gameModePanel = new JPanel();
		tabbedPane.addTab("Game Mode", null, gameModePanel, null);
		gameModePanel.setLayout(null);
		rdbtnEntropy.setBounds(18, 17, 109, 23);
		gameModePanel.add(rdbtnEntropy);
		rdbtnVectropy.setBounds(18, 43, 109, 23);
		gameModePanel.add(rdbtnVectropy);
		JPanel gameplayPanel = new JPanel();
		tabbedPane.addTab("Gameplay", null, gameplayPanel, null);
		gameplayPanel.setLayout(null);
		JLabel label = new JLabel("Cards dealt to each player");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(23, 22, 158, 14);
		gameplayPanel.add(label);
		slider.setValue(5);
		slider.setToolTipText("");
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(1);
		slider.setMinimum(1);
		slider.setMaximum(5);
		slider.setMajorTickSpacing(4);
		slider.setBounds(10, 47, 184, 45);
		gameplayPanel.add(slider);
		cbIncludeJokers.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cbIncludeJokers.setSelected(false);
		cbIncludeJokers.setBounds(6, 108, 115, 22);
		gameplayPanel.add(cbIncludeJokers);
		spinnerJokerQuantity.setEnabled(false);
		spinnerJokerQuantity.setBounds(127, 104, 38, 22);
		spinnerJokerQuantity.setModel(new SpinnerNumberModel(2, 1, 4, 1));
		gameplayPanel.add(spinnerJokerQuantity);
		spinnerJokerValue.setEnabled(false);
		spinnerJokerValue.setBounds(179, 104, 38, 22);
		spinnerJokerValue.setModel(new SpinnerNumberModel(2, 2, 4, 1));
		gameplayPanel.add(spinnerJokerValue);
		lblValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblValue.setEnabled(false);
		lblValue.setBounds(171, 131, 55, 14);
		gameplayPanel.add(lblValue);
		lblQuantity.setHorizontalAlignment(SwingConstants.CENTER);
		lblQuantity.setEnabled(false);
		lblQuantity.setBounds(118, 131, 57, 14);
		gameplayPanel.add(lblQuantity);
		chckbxIncludeMoons.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxIncludeMoons.setBounds(232, 18, 115, 23);
		gameplayPanel.add(chckbxIncludeMoons);
		chckbxIncludeStars.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxIncludeStars.setBounds(232, 47, 115, 23);
		gameplayPanel.add(chckbxIncludeStars);
		chckbxNegativeJacks.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxNegativeJacks.setBounds(232, 73, 115, 23);

		gameplayPanel.add(chckbxNegativeJacks);
		chckbxCardReveal.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxCardReveal.setBounds(232, 99, 115, 23);
		gameplayPanel.add(chckbxCardReveal);
		tabbedPane.addTab("Advanced Settings", null, advancedSettingsPanel, null);
		advancedSettingsPanel.setLayout(null);
		cbLogging.setBounds(15, 10, 141, 22);
		advancedSettingsPanel.add(cbLogging);
		cbLogging.setState(false);
		cbLogging.setEnabled(true);
		cbForceStart.setState(false);
		cbForceStart.setEnabled(true);
		cbForceStart.setBounds(15, 39, 141, 22);
		advancedSettingsPanel.add(cbForceStart);
		cbRandomiseOrder.setState(false);
		cbRandomiseOrder.setEnabled(true);
		cbRandomiseOrder.setBounds(15, 67, 141, 22);
		advancedSettingsPanel.add(cbRandomiseOrder);
		lblCpu.setHorizontalAlignment(SwingConstants.CENTER);
		lblCpu.setFont(new Font("Tahoma", Font.BOLD, 15));
		JLabel lblResults = new JLabel("Results");
		lblResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblResults.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblResults.setBounds(150, 294, 135, 31);
		getContentPane().add(lblResults);
		lblTimeTaken.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimeTaken.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblTimeTaken.setBounds(33, 438, 379, 14);
		getContentPane().add(lblTimeTaken);
		ButtonGroup modeGroup = new ButtonGroup();
		rdbtnEntropy.setSelected(true);
		modeGroup.add(rdbtnEntropy);
		modeGroup.add(rdbtnVectropy);


		setUpListeners();
	}

	private final JPanel cpuPanel = new JPanel();
	private final JCheckBox cbOpponentTwo = new JCheckBox();
	private final JCheckBox cbOpponentThree = new JCheckBox();
	private final JComboBox<String> opponentOneStrat = new JComboBox<>();
	private final JComboBox<String> opponentTwoStrat = new JComboBox<>();
	private final JComboBox<String> opponentThreeStrat = new JComboBox<>();
	private final JComboBox<String> opponentZeroStrat = new JComboBox<>();
	private final JLabel lblTimeTaken = new JLabel("<Time Taken>");
	private final Checkbox cbLogging = new Checkbox("Enable Logging");
	private final Checkbox cbForceStart = new Checkbox("CPU 0 Starts");
	private final Checkbox cbRandomiseOrder = new Checkbox("Randomise Play Order");
	private final NumberField gamesToSimulate = new NumberField(1);
	private final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
	private final JPanel advancedSettingsPanel = new JPanel();
	private final JLabel lblCpu = new JLabel("CPU 0");
	private final JLabel lblCpu_3 = new JLabel("CPU 3");
	private final JLabel lblCpu_4 = new JLabel("CPU 2");
	private JTable resultsTable = new JTable();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JSlider slider = new JSlider();
	private final JSpinner spinnerJokerQuantity = new JSpinner();
	private final JSpinner spinnerJokerValue = new JSpinner();
	private final JLabel lblValue = new JLabel("Value");
	private final JLabel lblQuantity = new JLabel("Quantity");
	private final JCheckBox cbIncludeJokers = new JCheckBox("Include Jokers");
	private final JButton btnRunSimulation = new JButton("Run Simulation");
	private final JRadioButton rdbtnEntropy = new JRadioButton("Entropy");
	private final JRadioButton rdbtnVectropy = new JRadioButton("Vectropy");
	private final JCheckBox chckbxIncludeMoons = new JCheckBox("Include Moons");
	private final JCheckBox chckbxIncludeStars = new JCheckBox("Include Stars");
	private final JCheckBox chckbxNegativeJacks = new JCheckBox("Jacks worth -1");
	private final JCheckBox chckbxCardReveal = new JCheckBox("Card reveal");

	private void showLoggingWarningMessage()
	{
		int dialogButton = DialogUtilNew.showQuestion("To avoid intensive memory use, "
				+ "logging should only be turned on for a small number of games. \n\nProceed?", false);

		if (dialogButton == JOptionPane.NO_OPTION)
		{
			cbLogging.setState(false);
		}
	}

	public void initVariables()
	{
		cbOpponentThree.setSelected(false);
		cbOpponentTwo.setSelected(false);
		opponentTwoStrat.setEnabled(false);
		opponentThreeStrat.setEnabled(false);
		
		scrollPane.setVisible(false);
		lblTimeTaken.setText("");
		
		updateStrategySelection();
	}

	private void runSimulation()
	{
		numberOfGames = (int) gamesToSimulate.getValue();

		SimulationParams parms = factorySimulationParms();

		btnRunSimulation.setEnabled(false);
		runSimulationInSeparateThread(parms);
	}
	
	private void runSimulationInSeparateThread(final SimulationParams parms)
	{
		Runnable simulationRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				final double startTime = System.currentTimeMillis();
				final ProgressDialog dialog = ProgressDialog.factory("Simulating games...", "games remaining", numberOfGames);
				dialog.showCancel(true);
				dialog.setVisibleLater();
				
				Debug.appendBanner("Starting simulation for " + numberOfGames + " games");
				GameSimulator simulator = new GameSimulator(parms);
				
				for (int i = 1; i <= numberOfGames; i++)
				{
					try
					{
						simulator.startNewGame(i);
						dialog.incrementProgressLater();
						
						logger.logProgress("simulationProgress", i, numberOfGames);
						
						if (dialog.cancelPressed())
						{
							dialog.disposeLater();
							simulationCancelled(startTime, i, parms);
							return;
						}
					}
					catch (SimulationException se)
					{
						dialog.disposeLater();
						updateStrategySelection();
						simulationCancelled(startTime, i, parms);
						return;
					}
					catch (Throwable t)
					{
						dialog.disposeLater();
						btnRunSimulation.setEnabled(true);
						dumpSimulationDetails(i, numberOfGames, parms);
						logger.error("simulation.error", "error running simulation", t);
						DialogUtilNew.showErrorLater("A serious problem has occurred with the simulation.");
						resetVariables();
						return;
					}
				}
				
				dialog.disposeLater();
				simulationCompleted(startTime, parms);
			}
		};
		
		Thread t = new Thread(simulationRunnable, "Simulation Thread");
		t.start();
	}
	
	private void simulationCancelled(double startTime, int gamesSimulated, SimulationParams parms)
	{
		numberOfGames = gamesSimulated;
		generateAndShowTable(parms);
		resetVariables();
		
		double endTime = System.currentTimeMillis();
		double timeTaken = ((endTime - startTime)/1000);
		Debug.appendBanner("Simulation cancelled on game " + gamesSimulated + " after "  + timeTaken + "s");
		lblTimeTaken.setText("Simulation cancelled - did " + gamesSimulated + " game(s) in " + timeTaken + "s");
		
		btnRunSimulation.setEnabled(true);
	}
	
	private void simulationCompleted(double startTime, SimulationParams parms)
	{
		btnRunSimulation.setEnabled(true);
		generateAndShowTable(parms);
		double endTime = System.currentTimeMillis();
		lblTimeTaken.setText("Simulation took " + ((endTime - startTime)/1000) + "s");
		Debug.appendBanner("Simulation Finished in " + ((endTime - startTime)/1000) + "s");
		resetVariables();
	}

	private void resetVariables()
	{
		hmSimulationResultsByOpponentNumber = new HashMap<>();
	}

	private void generateAndShowTable(SimulationParams parms)
	{	
		scrollPane.setVisible(true);

		Vector<Vector<String>> resultsData = new Vector<>();
		addRowIfApplicable(resultsData, parms.getOpponentZeroStrategy(), 0);
		addRowIfApplicable(resultsData, parms.getOpponentOneStrategy(), 1);
		addRowIfApplicable(resultsData, parms.getOpponentTwoStrategy(), 2);
		addRowIfApplicable(resultsData, parms.getOpponentThreeStrategy(), 3);
		
		Vector<String> columnTitles = getColumnTitles();
		
		resultsTable = new JTable(resultsData, columnTitles)
		{
		    @Override
			protected JTableHeader createDefaultTableHeader() {
		        return new JTableHeader(columnModel) {
		            @Override
					public String getToolTipText(MouseEvent e) {
		                java.awt.Point p = e.getPoint();
		                int index = columnModel.getColumnIndexAtX(p.x);
		                int realIndex = columnModel.getColumn(index).getModelIndex();
		                return COLUMN_TOOL_TIPS[realIndex];
		            }
		        };
		    }
		    
		    @Override
			public boolean isCellEditable(int row, int column) {return false;}
		};
		
		resultsTable.setRowSelectionAllowed(true);
		resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		resultsTable.getTableHeader().setReorderingAllowed(false);
		
		//set the column widths
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		resultsTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		resultsTable.getColumnModel().getColumn(3).setPreferredWidth(50);
		resultsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		resultsTable.getColumnModel().getColumn(5).setPreferredWidth(60);

		scrollPane.setViewportView(resultsTable);
		resultsTable.setFillsViewportHeight(true);
	}
	
	private void addRowIfApplicable(Vector<Vector<String>> resultsData, String strategy, int opponentNumber)
	{
		SimulationResults results = hmSimulationResultsByOpponentNumber.get(opponentNumber);
		if (results != null)
		{
			Vector<String> row = results.generateRow(opponentNumber, strategy, numberOfGames);
			resultsData.add(row);
		}
	}
	
	private Vector<String> getColumnTitles()
	{
		Vector<String> columnTitles = new Vector<>();
		columnTitles.add("CPU");
		columnTitles.add("Strategy");
		columnTitles.add("Win %");
		columnTitles.add("Chall. %");
		columnTitles.add("Chall. Success %");
		columnTitles.add("Perfect %");
		
		return columnTitles;
	}

	public void recordWin(int opponent, boolean perfectGame)
	{
		SimulationResults results = getResults(opponent);
		if (perfectGame)
		{
			results.incrementPerfectGames();
		}
		
		results.incrementWins();
	}
	
	public void recordOpportunityToChallenge(int opponent)
	{
		SimulationResults results = getResults(opponent);
		results.incrementOpportunitiesToChallenge();
	}

	public void recordChallenge(int challenger, boolean successful)
	{
		SimulationResults results = getResults(challenger);
		if (successful)
		{
			results.incrementGoodChallenges();
		}
		
		results.incrementTotalChallenges();
	}
	
	private SimulationResults getResults(int playerNumber)
	{
		SimulationResults results = hmSimulationResultsByOpponentNumber.get(playerNumber);
		if (results == null)
		{
			results = new SimulationResults();
			hmSimulationResultsByOpponentNumber.put(playerNumber, results);
		}
		
		return results;
	}
	
	private void setUpListeners()
	{
		btnRunSimulation.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				runSimulation();
			}
		});
		
		cbLogging.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged (ItemEvent event)
			{
				boolean enableLogging = cbLogging.getState();
				if (enableLogging)
				{
					showLoggingWarningMessage();
				}
			}
		});

		cbIncludeJokers.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged (ItemEvent event)
			{
				boolean enabled = cbIncludeJokers.isSelected();
				lblValue.setEnabled(enabled);
				lblQuantity.setEnabled(enabled);
				spinnerJokerQuantity.setEnabled(enabled);
				spinnerJokerValue.setEnabled(enabled);
			}
		});

		cbOpponentTwo.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged (ItemEvent event)
			{
				boolean enabled = cbOpponentTwo.isSelected();
				opponentTwoStrat.setEnabled(enabled);
			}
		});

		cbOpponentThree.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged (ItemEvent event)
			{
				boolean enabled = cbOpponentThree.isSelected();
				opponentThreeStrat.setEnabled(enabled);
			}
		});
		
		rdbtnEntropy.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				updateStrategySelection();
			}
		});
		
		rdbtnVectropy.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				updateStrategySelection();
			}
		});
	}
	
	private void updateStrategySelection()
	{
		Vector<String> allStrategies = CpuStrategies.getAllStrategies(rdbtnEntropy.isSelected(), null);
			
		ComboBoxModel<String> comboModel = new DefaultComboBoxModel<>(allStrategies);
		opponentZeroStrat.setModel(comboModel);
		comboModel = new DefaultComboBoxModel<>(allStrategies);
		opponentOneStrat.setModel(comboModel);
		comboModel = new DefaultComboBoxModel<>(allStrategies);
		opponentTwoStrat.setModel(comboModel);
		comboModel = new DefaultComboBoxModel<>(allStrategies);
		opponentThreeStrat.setModel(comboModel);
	}
	
	private SimulationParams factorySimulationParms()
	{
		GameMode gameMode = GameMode.Entropy;
		if (rdbtnVectropy.isSelected())
		{
			gameMode = GameMode.Vectropy;
		}
		
		int numberOfCards = slider.getValue();
		boolean includeJokers = cbIncludeJokers.isSelected();
		int jokerValue =  (int) spinnerJokerValue.getValue();
		int jokerQuantity = includeJokers ? (int) spinnerJokerQuantity.getValue() : 0;
		boolean includeMoons = chckbxIncludeMoons.isSelected();
		boolean includeStars = chckbxIncludeStars.isSelected();
		boolean negativeJacks = chckbxNegativeJacks.isSelected();
		boolean cardReveal = chckbxCardReveal.isSelected();
		boolean opponentTwoEnabled = cbOpponentTwo.isSelected();
		boolean opponentThreeEnabled = cbOpponentThree.isSelected();
		String opponentZeroStrategy = (String) opponentZeroStrat.getSelectedItem();
		String opponentOneStrategy = (String) opponentOneStrat.getSelectedItem();
		String opponentTwoStrategy = (String) opponentTwoStrat.getSelectedItem();
		String opponentThreeStrategy = (String) opponentThreeStrat.getSelectedItem();
		boolean forceStart = cbForceStart.getState();
		boolean randomiseOrder = cbRandomiseOrder.getState();
		boolean enableLogging = cbLogging.getState();

		var settings = new GameSettings(gameMode, numberOfCards, jokerQuantity, jokerValue, includeMoons, includeStars, negativeJacks, cardReveal, false);

		return new SimulationParams(settings, opponentTwoEnabled, opponentThreeEnabled, opponentZeroStrategy, opponentOneStrategy,
				opponentTwoStrategy, opponentThreeStrategy, enableLogging, randomiseOrder, forceStart);
	}
	
	private void dumpSimulationDetails(int i, int numberOfGames, SimulationParams parms)
	{
		logger.info("simulationDump", "Dumping simulation details");
		logger.info("simulationDump", "Opponent 0: " + parms.getOpponentZeroStrategy());
		logger.info("simulationDump", "Opponent 1: " + parms.getOpponentOneStrategy());

		if (parms.getOpponentTwoEnabled())
		{
			logger.info("simulationDump", "Opponent 2: " + parms.getOpponentTwoStrategy());
		}
		
		if (parms.getOpponentThreeEnabled())
		{
			logger.info("simulationDump", "Opponent 3: " + parms.getOpponentThreeStrategy());
		}

		logger.info("simulationDump", "Was simulating game " + i + "/" + numberOfGames);
	}
}