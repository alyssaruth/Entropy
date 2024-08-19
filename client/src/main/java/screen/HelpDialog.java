package screen;
import help.FundamentalsGlossary;
import help.FundamentalsTheDeck;
import help.HelpPanel;
import help.MiscBugReport;
import help.MiscCheatCodes;
import help.MiscClearingSaveData;
import help.RulesEntropyBidding;
import help.RulesEntropyChallenging;
import help.RulesEntropyIntroduction;
import help.RulesIllegal;
import help.RulesVectropyBidding;
import help.RulesVectropyChallenging;
import help.RulesVectropyIntroduction;
import help.ToolsGameplaySettings;
import help.ToolsReplayViewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import util.AchievementsUtil;
import util.Debug;
import util.Registry;

public class HelpDialog extends JFrame
						implements TreeSelectionListener,
								   WindowListener,
								   Registry
{
	private final HelpPanel fundamentalsTheDeck = new FundamentalsTheDeck();
	private final HelpPanel fundamentalsGlossary = new FundamentalsGlossary();
	private final HelpPanel rulesEntropyIntroduction = new RulesEntropyIntroduction();
	private final HelpPanel rulesEntropyBidding = new RulesEntropyBidding();
	private final HelpPanel rulesEntropyChallenging = new RulesEntropyChallenging();
	private final HelpPanel rulesVectropyIntroduction = new RulesVectropyIntroduction();
	private final HelpPanel rulesVectropyBidding = new RulesVectropyBidding();
	private final HelpPanel rulesVectropyChallenging = new RulesVectropyChallenging();
	private final HelpPanel rulesIllegal = new RulesIllegal();
	private final HelpPanel toolsGameplaySettings = new ToolsGameplaySettings();
	private final HelpPanel toolsReplayViewer = new ToolsReplayViewer();
	private final HelpPanel miscBugReport = new MiscBugReport();
	private final HelpPanel miscClearingSaveData = new MiscClearingSaveData();
	private final HelpPanel miscCheatCodes = new MiscCheatCodes();
	
	private DefaultMutableTreeNode nodeToHighlightAfterSearch = null;
	private DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode();
	
	private Timer bookwormTimer = null;
	private long startTime = -1;
	
	public boolean fourColours = false;
	public String backs = "";
	
	public HelpDialog() 
	{
		setTitle("Help");
		setSize(750, 550);
		setResizable(false);
		setIconImage(new ImageIcon(AchievementsDialog.class.getResource("/icons/help.png")).getImage());
		getContentPane().setLayout(null);
		JPanel leftPane = new JPanel();
		leftPane.setBorder(null);
		leftPane.setBounds(0, 0, 250, 522);
		getContentPane().add(leftPane);
		leftPane.setLayout(null);
		searchBox.setBounds(62, 11, 178, 20);
		leftPane.add(searchBox);
		searchBox.setColumns(10);
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setBounds(10, 14, 46, 14);
		leftPane.add(lblSearch);
		JPanel selectionTreePanel = new JPanel();
		selectionTreePanel.setBorder(null);
		selectionTreePanel.setBounds(0, 40, 250, 482);
		leftPane.add(selectionTreePanel);
		selectionTreePanel.setLayout(null);
		treePane.setBounds(10, 0, 240, 471);
		selectionTreePanel.add(treePane);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		treePane.setViewportView(tree);
		JPanel rightPane = new JPanel();
		rightPane.setBorder(null);
		rightPane.setBounds(250, 0, 494, 522);
		getContentPane().add(rightPane);
		rightPane.setLayout(null);
		helpPane.setBounds(10, 11, 474, 500);
		rightPane.add(helpPane);
		helpPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		helpPane.setViewportView(new JPanel());
		helpPane.getVerticalScrollBar().setUnitIncrement(16);
		noSearchResults.setVerticalAlignment(SwingConstants.TOP);
		noSearchResults.setHorizontalAlignment(SwingConstants.CENTER);
		noSearchResults.setFont(new Font("Tahoma", Font.ITALIC, 12));
		noSearchResults.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		searchBox.getDocument().addDocumentListener(new DocumentListener() 
		{
			@Override
			public void changedUpdate(DocumentEvent e) 
			{
				refreshNodes(searchBox.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) 
			{
				refreshNodes(searchBox.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) 
			{
				refreshNodes(searchBox.getText());
			}
		});
		
		addWindowListener(this);
	}
	
	private final JFormattedTextField searchBox = new JFormattedTextField();
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Index");
	private JTree tree = new JTree(root);
	private final JScrollPane helpPane = new JScrollPane();
	private final JScrollPane treePane = new JScrollPane();
	private final JLabel noSearchResults = new JLabel("There are no results to display.");
	
	public void initVariables()
	{
		fireAppearancePreferencesChange();
		
		createNodes();
		toolsReplayViewer.setPreferredSize(new Dimension(455, 1830));
		miscClearingSaveData.setPreferredSize(new Dimension(455, 660));
		miscBugReport.setPreferredSize(new Dimension(455, 720));
		
		startTime = System.currentTimeMillis();
		startTimer();
	}
	
	private void startTimer()
	{
		if (!achievements.getBoolean(ACHIEVEMENTS_BOOLEAN_BOOKWORM, false))
		{
			bookwormTimer = new Timer("BookwormTimer");
			
			TimerTask task = new AchievementsUtil.UnlockAchievementTask(ACHIEVEMENTS_BOOLEAN_BOOKWORM);
			long time = Math.max(60000*5 - achievements.getLong(ACHIEVEMENTS_LONG_BOOKWORM_TIME, 0), 0);
			bookwormTimer.schedule(task, time);
		}
	}
	
	private void createNodes()
	{
		populateFundamentals("");
		populateGameRules("");
		populateTools("");
		populateMisc("");
	    
	    for (int i = 0; i < tree.getRowCount(); i++) {
        	tree.expandRow(i);
        }
	    
	    tree.setRootVisible(false);
	}
	
	public void refreshNodes(String searchStr)
	{
		try
		{
			nodeToHighlightAfterSearch = null;
			root = new DefaultMutableTreeNode("Index");

			populateFundamentals(searchStr);
			populateGameRules(searchStr);
			populateTools(searchStr);
			populateMisc(searchStr);

			tree = new JTree(root);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addTreeSelectionListener(this);

			int count = tree.getRowCount();

			if (count == 1)
			{
				treePane.setViewportView(noSearchResults);
				helpPane.setViewportView(new JPanel());
			}
			else
			{
				for (int i = 0; i < tree.getRowCount(); i++) 
				{
					tree.expandRow(i);
				}

				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				TreeNode[] nodePath = model.getPathToRoot(nodeToHighlightAfterSearch);
				tree.setSelectionPath(new TreePath(nodePath));
				tree.setRootVisible(false);
				treePane.setViewportView(tree);
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private void populateFundamentals(String searchStr)
	{
		DefaultMutableTreeNode fundamentals = new DefaultMutableTreeNode("Fundamentals");
		
		addNodeBasedOnString(fundamentals, fundamentalsTheDeck, searchStr);
		addNodeBasedOnString(fundamentals, fundamentalsGlossary, searchStr);
		
		if (!fundamentals.isLeaf())
		{
			root.add(fundamentals);
		}
	}
	
	private void populateGameRules(String searchStr)
	{
		DefaultMutableTreeNode gameRules = new DefaultMutableTreeNode("Game Rules");
		
		DefaultMutableTreeNode entropyRules = new DefaultMutableTreeNode("Entropy");
		addNodeBasedOnString(entropyRules, rulesEntropyIntroduction, searchStr);
	    addNodeBasedOnString(entropyRules, rulesEntropyBidding, searchStr);
    	addNodeBasedOnString(entropyRules, rulesEntropyChallenging, searchStr);
    	
    	if (!entropyRules.isLeaf())
    	{
    		gameRules.add(entropyRules);
    	}
    	
    	if (rewards.getBoolean(REWARDS_BOOLEAN_VECTROPY, false))
    	{
    		DefaultMutableTreeNode vectropyRules = new DefaultMutableTreeNode("Vectropy");
    		addNodeBasedOnString(vectropyRules, rulesVectropyIntroduction, searchStr);
    		addNodeBasedOnString(vectropyRules, rulesVectropyBidding, searchStr);
    		addNodeBasedOnString(vectropyRules, rulesVectropyChallenging, searchStr);

    		if (!vectropyRules.isLeaf())
    		{
    			gameRules.add(vectropyRules);
    		}
    	}
    	
    	if (rewards.getBoolean(REWARDS_BOOLEAN_ILLEGAL, false))
    	{
    		addNodeBasedOnString(gameRules, rulesIllegal, searchStr);
    	}
	    
	    if (!gameRules.isLeaf())
	    {
	    	root.add(gameRules);
	    }
	}
	
	private void populateTools(String searchStr)
	{
		DefaultMutableTreeNode preferences = new DefaultMutableTreeNode("Tools");
		
		addNodeBasedOnString(preferences, toolsGameplaySettings, searchStr);
		addNodeBasedOnString(preferences, toolsReplayViewer, searchStr);

	    if (!preferences.isLeaf())
	    {
	    	root.add(preferences);
	    }
	}
	
	private void populateMisc(String searchStr)
	{
		DefaultMutableTreeNode misc = new DefaultMutableTreeNode("Miscellaneous");
		
		addNodeBasedOnString(misc, miscBugReport, searchStr);
		addNodeBasedOnString(misc, miscClearingSaveData, searchStr);
		
		if (rewards.getBoolean(REWARDS_BOOLEAN_CHEATS, false))
		{
			addNodeBasedOnString(misc, miscCheatCodes, searchStr);
		}
		
		if (!misc.isLeaf())
		{
			root.add(misc);
		}
	}
	
	private void addNodeBasedOnString(DefaultMutableTreeNode parent, HelpPanel node, String searchStr)
	{
		if (node.contains(searchStr))
    	{
			node.refresh();
			node.highlight(searchStr);
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
			parent.add(childNode);
			
			String childStr = childNode.toString();
			String currentStr = currentNode.toString();
			
			if (childStr.equals(currentStr))
			{
				nodeToHighlightAfterSearch = childNode;
			}
			else if (nodeToHighlightAfterSearch == null)
			{
				nodeToHighlightAfterSearch = childNode;
			}
    	}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		try
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			currentNode = node;

			if (node == null) 
			{
				return;
			}

			Object nodeInfo = node.getUserObject();
			if (node.isLeaf()) 
			{
				HelpPanel page = (HelpPanel)nodeInfo;

				if (page == null)
				{
					Debug.append("Page was null for node " + node, true);
					return;
				}

				helpPane.setViewport(null);
				helpPane.setViewportView((HelpPanel)nodeInfo);
				helpPane.createVerticalScrollBar();
			} 
			else 
			{
				helpPane.setViewportView(new JPanel());
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
    }

	public void setSelectionForWord(String word)
	{
		searchBox.setText("");
		refreshNodes("");
		Enumeration<TreeNode> children = root.children();
		setSelectionForWordRecursively(children, word);
	}

	private void setSelectionForWordRecursively(Enumeration<TreeNode> nodes, String word)
	{
		while (nodes.hasMoreElements())
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)nodes.nextElement();
			if (node.isLeaf())
			{
				HelpPanel panel = (HelpPanel)node.getUserObject();
				String panelName = panel.getPanelName();
				if (panelName.equals(word))
				{
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					TreeNode[] nodePath = model.getPathToRoot(node);
		        	tree.setSelectionPath(new TreePath(nodePath));
					break;
				}
			}
			else
			{
				Enumeration<TreeNode> children = node.children();
				setSelectionForWordRecursively(children, word);
			}
		}
	}
	
	public void fireAppearancePreferencesChange()
	{
		try
		{
			backs = prefs.get(PREFERENCES_STRING_CARD_BACKS, Registry.BACK_CODE_CLASSIC_BLUE);
			fourColours = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS).equals(Registry.FOUR_COLOURS);

			if (isVisible())
			{
				fundamentalsTheDeck.fireAppearancePreferencesChange();
				rulesVectropyBidding.fireAppearancePreferencesChange();
			}
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) 
	{
		long timeSpentOnDialog = System.currentTimeMillis() - startTime;
		long currentBookwormTime = achievements.getLong(ACHIEVEMENTS_LONG_BOOKWORM_TIME, 0);
		achievements.putLong(ACHIEVEMENTS_LONG_BOOKWORM_TIME, currentBookwormTime + timeSpentOnDialog);
		
		bookwormTimer.cancel();
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}