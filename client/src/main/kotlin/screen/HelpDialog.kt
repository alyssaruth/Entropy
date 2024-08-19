package screen

import help.FundamentalsGlossary
import help.FundamentalsTheDeck
import help.HelpPanel
import help.MiscBugReport
import help.MiscCheatCodes
import help.MiscClearingSaveData
import help.RulesEntropyBidding
import help.RulesEntropyChallenging
import help.RulesEntropyIntroduction
import help.RulesIllegal
import help.RulesVectropyBidding
import help.RulesVectropyChallenging
import help.RulesVectropyIntroduction
import help.ToolsGameplaySettings
import help.ToolsReplayViewer
import java.awt.Dimension
import java.awt.Font
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.util.*
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JFormattedTextField
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.ScrollPaneConstants
import javax.swing.SwingConstants
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel
import kotlin.math.max
import util.AchievementsUtil.UnlockAchievementTask
import util.Registry
import utils.InjectedThings.logger

class HelpDialog : JFrame(), TreeSelectionListener, WindowListener, Registry {
    private val fundamentalsTheDeck = FundamentalsTheDeck()
    private val fundamentalsGlossary = FundamentalsGlossary()
    private val rulesEntropyIntroduction = RulesEntropyIntroduction()
    private val rulesEntropyBidding = RulesEntropyBidding()
    private val rulesEntropyChallenging = RulesEntropyChallenging()
    private val rulesVectropyIntroduction = RulesVectropyIntroduction()
    private val rulesVectropyBidding = RulesVectropyBidding()
    private val rulesVectropyChallenging = RulesVectropyChallenging()
    private val rulesIllegal = RulesIllegal()
    private val toolsGameplaySettings = ToolsGameplaySettings()
    private val toolsReplayViewer = ToolsReplayViewer()
    private val miscBugReport = MiscBugReport()
    private val miscClearingSaveData = MiscClearingSaveData()
    private val miscCheatCodes = MiscCheatCodes()

    private var nodeToHighlightAfterSearch: DefaultMutableTreeNode? = null
    private var currentNode: DefaultMutableTreeNode? = DefaultMutableTreeNode()

    private var bookwormTimer: Timer? = null
    private var startTime: Long = -1

    private val searchBox = JFormattedTextField()
    private var root = DefaultMutableTreeNode("Index")
    private var tree = JTree(root)
    private val helpPane = JScrollPane()
    private val treePane = JScrollPane()
    private val noSearchResults = JLabel("There are no results to display.")

    init {
        title = "Help"
        setSize(750, 550)
        isResizable = false
        iconImage = ImageIcon(AchievementsDialog::class.java.getResource("/icons/help.png")).image
        contentPane.layout = null
        val leftPane = JPanel()
        leftPane.border = null
        leftPane.setBounds(0, 0, 250, 522)
        contentPane.add(leftPane)
        leftPane.layout = null
        searchBox.setBounds(62, 11, 178, 20)
        leftPane.add(searchBox)
        searchBox.columns = 10
        val lblSearch = JLabel("Search:")
        lblSearch.setBounds(10, 14, 46, 14)
        leftPane.add(lblSearch)
        val selectionTreePanel = JPanel()
        selectionTreePanel.border = null
        selectionTreePanel.setBounds(0, 40, 250, 482)
        leftPane.add(selectionTreePanel)
        selectionTreePanel.layout = null
        treePane.setBounds(10, 0, 240, 471)
        selectionTreePanel.add(treePane)
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        tree.addTreeSelectionListener(this)
        treePane.setViewportView(tree)
        val rightPane = JPanel()
        rightPane.border = null
        rightPane.setBounds(250, 0, 494, 522)
        contentPane.add(rightPane)
        rightPane.layout = null
        helpPane.setBounds(10, 11, 474, 500)
        rightPane.add(helpPane)
        helpPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        helpPane.setViewportView(JPanel())
        helpPane.verticalScrollBar.unitIncrement = 16
        noSearchResults.verticalAlignment = SwingConstants.TOP
        noSearchResults.horizontalAlignment = SwingConstants.CENTER
        noSearchResults.font = Font("Tahoma", Font.ITALIC, 12)
        noSearchResults.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        searchBox.document.addDocumentListener(
            object : DocumentListener {
                override fun changedUpdate(e: DocumentEvent) {
                    refreshNodes(searchBox.text)
                }

                override fun removeUpdate(e: DocumentEvent) {
                    refreshNodes(searchBox.text)
                }

                override fun insertUpdate(e: DocumentEvent) {
                    refreshNodes(searchBox.text)
                }
            }
        )

        addWindowListener(this)
    }

    fun initVariables() {
        fireAppearancePreferencesChange()

        createNodes()
        toolsReplayViewer.preferredSize = Dimension(455, 1830)
        miscClearingSaveData.preferredSize = Dimension(455, 660)
        miscBugReport.preferredSize = Dimension(455, 720)

        startTime = System.currentTimeMillis()
        startTimer()
    }

    private fun startTimer() {
        if (!Registry.achievements.getBoolean(Registry.ACHIEVEMENTS_BOOLEAN_BOOKWORM, false)) {
            bookwormTimer = Timer("BookwormTimer")

            val task: TimerTask = UnlockAchievementTask(Registry.ACHIEVEMENTS_BOOLEAN_BOOKWORM)
            val time =
                max(
                        (60000 * 5 -
                                Registry.achievements.getLong(
                                    Registry.ACHIEVEMENTS_LONG_BOOKWORM_TIME,
                                    0
                                ))
                            .toDouble(),
                        0.0
                    )
                    .toLong()
            bookwormTimer!!.schedule(task, time)
        }
    }

    private fun createNodes() {
        populateFundamentals("")
        populateGameRules("")
        populateTools("")
        populateMisc("")

        for (i in 0 ..< tree.rowCount) {
            tree.expandRow(i)
        }

        tree.isRootVisible = false
    }

    fun refreshNodes(searchStr: String) {
        nodeToHighlightAfterSearch = null
        root = DefaultMutableTreeNode("Index")

        populateFundamentals(searchStr)
        populateGameRules(searchStr)
        populateTools(searchStr)
        populateMisc(searchStr)

        tree = JTree(root)
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        tree.addTreeSelectionListener(this)

        val count = tree.rowCount

        if (count == 1) {
            treePane.setViewportView(noSearchResults)
            helpPane.setViewportView(JPanel())
        } else {
            for (i in 0 ..< tree.rowCount) {
                tree.expandRow(i)
            }

            val model = tree.model as DefaultTreeModel
            val nodePath = model.getPathToRoot(nodeToHighlightAfterSearch)
            tree.selectionPath = TreePath(nodePath)
            tree.isRootVisible = false
            treePane.setViewportView(tree)
        }
    }

    private fun populateFundamentals(searchStr: String) {
        val fundamentals = DefaultMutableTreeNode("Fundamentals")

        addNodeBasedOnString(fundamentals, fundamentalsTheDeck, searchStr)
        addNodeBasedOnString(fundamentals, fundamentalsGlossary, searchStr)

        if (!fundamentals.isLeaf) {
            root.add(fundamentals)
        }
    }

    private fun populateGameRules(searchStr: String) {
        val gameRules = DefaultMutableTreeNode("Game Rules")

        val entropyRules = DefaultMutableTreeNode("Entropy")
        addNodeBasedOnString(entropyRules, rulesEntropyIntroduction, searchStr)
        addNodeBasedOnString(entropyRules, rulesEntropyBidding, searchStr)
        addNodeBasedOnString(entropyRules, rulesEntropyChallenging, searchStr)

        if (!entropyRules.isLeaf) {
            gameRules.add(entropyRules)
        }

        if (Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_VECTROPY, false)) {
            val vectropyRules = DefaultMutableTreeNode("Vectropy")
            addNodeBasedOnString(vectropyRules, rulesVectropyIntroduction, searchStr)
            addNodeBasedOnString(vectropyRules, rulesVectropyBidding, searchStr)
            addNodeBasedOnString(vectropyRules, rulesVectropyChallenging, searchStr)

            if (!vectropyRules.isLeaf) {
                gameRules.add(vectropyRules)
            }
        }

        if (Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_ILLEGAL, false)) {
            addNodeBasedOnString(gameRules, rulesIllegal, searchStr)
        }

        if (!gameRules.isLeaf) {
            root.add(gameRules)
        }
    }

    private fun populateTools(searchStr: String) {
        val preferences = DefaultMutableTreeNode("Tools")

        addNodeBasedOnString(preferences, toolsGameplaySettings, searchStr)
        addNodeBasedOnString(preferences, toolsReplayViewer, searchStr)

        if (!preferences.isLeaf) {
            root.add(preferences)
        }
    }

    private fun populateMisc(searchStr: String) {
        val misc = DefaultMutableTreeNode("Miscellaneous")

        addNodeBasedOnString(misc, miscBugReport, searchStr)
        addNodeBasedOnString(misc, miscClearingSaveData, searchStr)

        if (Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_CHEATS, false)) {
            addNodeBasedOnString(misc, miscCheatCodes, searchStr)
        }

        if (!misc.isLeaf) {
            root.add(misc)
        }
    }

    private fun addNodeBasedOnString(
        parent: DefaultMutableTreeNode,
        node: HelpPanel,
        searchStr: String
    ) {
        if (node.contains(searchStr)) {
            node.refresh()
            node.highlight(searchStr)
            val childNode = DefaultMutableTreeNode(node)
            parent.add(childNode)

            val childStr = childNode.toString()
            val currentStr = currentNode.toString()

            if (childStr == currentStr) {
                nodeToHighlightAfterSearch = childNode
            } else if (nodeToHighlightAfterSearch == null) {
                nodeToHighlightAfterSearch = childNode
            }
        }
    }

    override fun valueChanged(e: TreeSelectionEvent) {
        val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode
        currentNode = node

        if (node == null) {
            return
        }

        val nodeInfo = node.userObject
        if (node.isLeaf) {
            val page = nodeInfo as? HelpPanel

            if (page == null) {
                logger.info("nullHelpPage", "Page was null for node $node")
                return
            }

            helpPane.viewport = null
            helpPane.setViewportView(nodeInfo)
            helpPane.createVerticalScrollBar()
        } else {
            helpPane.setViewportView(JPanel())
        }
    }

    fun setSelectionForWord(word: String) {
        searchBox.text = ""
        refreshNodes("")
        val children = root.children()
        setSelectionForWordRecursively(children, word)
    }

    private fun setSelectionForWordRecursively(nodes: Enumeration<TreeNode>, word: String) {
        while (nodes.hasMoreElements()) {
            val node = nodes.nextElement() as DefaultMutableTreeNode
            if (node.isLeaf) {
                val panel = node.userObject as HelpPanel
                val panelName = panel.panelName
                if (panelName == word) {
                    val model = tree.model as DefaultTreeModel
                    val nodePath = model.getPathToRoot(node)
                    tree.selectionPath = TreePath(nodePath)
                    break
                }
            } else {
                val children = node.children()
                setSelectionForWordRecursively(children, word)
            }
        }
    }

    fun fireAppearancePreferencesChange() {
        if (isVisible) {
            fundamentalsTheDeck.fireAppearancePreferencesChange()
            rulesVectropyBidding.fireAppearancePreferencesChange()
        }
    }

    override fun windowActivated(arg0: WindowEvent) {}

    override fun windowClosed(arg0: WindowEvent) {}

    override fun windowClosing(arg0: WindowEvent) {
        val timeSpentOnDialog = System.currentTimeMillis() - startTime
        val currentBookwormTime =
            Registry.achievements.getLong(Registry.ACHIEVEMENTS_LONG_BOOKWORM_TIME, 0)
        Registry.achievements.putLong(
            Registry.ACHIEVEMENTS_LONG_BOOKWORM_TIME,
            currentBookwormTime + timeSpentOnDialog
        )

        bookwormTimer?.cancel()
    }

    override fun windowDeactivated(arg0: WindowEvent) {}

    override fun windowDeiconified(arg0: WindowEvent) {}

    override fun windowIconified(arg0: WindowEvent) {}

    override fun windowOpened(arg0: WindowEvent) {}
}
