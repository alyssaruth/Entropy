package screen

import bean.AchievementBadge
import java.awt.Color
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.util.*
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder
import `object`.RewardStar
import util.Images
import util.getAchievementsEarned
import utils.getAllChildComponentsForType

class AchievementsDialog : JFrame(), MouseMotionListener, MouseListener, ActionListener {
    private var currentPage = 0
    private var progressShowing = 0

    private var redrawing = false

    private val panelDescriptions = JPanel()
    private val separator = JSeparator()

    private val pages = makeAchievementPanels()
    private val btnLeft = JButton("\u25C0")
    private val btnRight = JButton("\u25B6")

    private val testTube = JLabel("")
    private val reward5: RewardStar =
        RewardStar(5, "Four Colours", RewardDialog.REWARD_BANNER_FOUR_COLOUR)
    private val reward10: RewardStar =
        RewardStar(10, "Negative Jacks", RewardDialog.REWARD_BANNER_NEGATIVE_JACKS)
    private val reward15: RewardStar =
        RewardStar(15, "Blind Play", RewardDialog.REWARD_BANNER_BLIND)
    private val reward20: RewardStar =
        RewardStar(20, "New Deck Design", RewardDialog.REWARD_BANNER_MINIMALIST)
    private val reward25: RewardStar =
        RewardStar(25, "Vectropy", RewardDialog.REWARD_BANNER_VECTROPY)
    private val reward30: RewardStar =
        RewardStar(30, "Card Reveal", RewardDialog.REWARD_BANNER_CARD_REVEAL)
    private val reward35: RewardStar =
        RewardStar(35, "Extra Suits", RewardDialog.REWARD_BANNER_EXTRA_SUITS)
    private val reward40: RewardStar =
        RewardStar(40, "Illegal!", RewardDialog.REWARD_BANNER_ILLEGAL)
    private val reward45: RewardStar =
        RewardStar(45, "New Joker Design", RewardDialog.REWARD_BANNER_DEVELOPERS)
    private val reward50: RewardStar = RewardStar(50, "Cheats", RewardDialog.REWARD_BANNER_CHEATS)

    private val title = JLabel("")
    private val achievementName = JLabel("Achievement Name")
    private val achievementExplanation = JLabel("How you earned it")
    private val achievementDescription = JLabel("Humorous description")

    init {
        setTitle("Achievements")
        setSize(860, 504)
        addMouseMotionListener(this)
        iconImage =
            ImageIcon(AchievementsDialog::class.java.getResource("/icons/achievements.png")).image
        contentPane.layout = null
        separator.setBounds(0, 342, 878, 2)
        contentPane.add(separator)
        title.horizontalAlignment = SwingConstants.CENTER
        title.font = Font("Tahoma", Font.BOLD, 20)
        title.setBounds(255, 16, 235, 25)
        contentPane.add(title)
        panelDescriptions.setBounds(10, 345, 714, 131)
        contentPane.add(panelDescriptions)
        panelDescriptions.layout = null
        achievementName.setBounds(0, 0, 714, 36)
        achievementName.font = Font("Tahoma", Font.BOLD, 15)
        panelDescriptions.add(achievementName)
        achievementExplanation.setBounds(0, 32, 714, 36)
        panelDescriptions.add(achievementExplanation)
        achievementDescription.verticalAlignment = SwingConstants.TOP
        achievementDescription.setBounds(0, 70, 714, 54)
        achievementDescription.font = Font("Tahoma", Font.ITALIC, 12)
        panelDescriptions.add(achievementDescription)

        reward5.setBounds(820, 287, 17, 16)
        contentPane.add(reward5)
        reward10.setBounds(715, 272, 17, 16)
        contentPane.add(reward10)
        reward15.setBounds(820, 257, 17, 16)
        contentPane.add(reward15)
        reward20.setBounds(715, 242, 17, 16)
        contentPane.add(reward20)
        reward25.setBounds(820, 227, 17, 16)
        contentPane.add(reward25)
        reward30.setBounds(715, 212, 17, 16)
        contentPane.add(reward30)
        reward35.setBounds(820, 197, 17, 16)
        contentPane.add(reward35)
        reward40.setBounds(715, 182, 17, 16)
        contentPane.add(reward40)
        reward45.setBounds(820, 167, 17, 16)
        contentPane.add(reward45)
        reward50.setBounds(715, 152, 17, 16)
        contentPane.add(reward50)
        testTube.icon = ImageIcon(AchievementsDialog::class.java.getResource("/tubes/t28.png"))
        testTube.setBounds(730, -28, 92, 344)
        contentPane.add(testTube)
        btnLeft.background = Color.WHITE
        btnLeft.isOpaque = false
        btnLeft.font = Font("Segoe UI Symbol", Font.BOLD, 24)
        btnLeft.border = EmptyBorder(0, 0, 0, 0)
        btnLeft.setBounds(25, 158, 30, 50)
        contentPane.add(btnLeft)
        btnLeft.addActionListener(this)
        btnRight.isOpaque = false
        btnRight.background = Color.WHITE
        btnRight.font = Font("Segoe UI Symbol", Font.BOLD, 24)
        btnRight.border = EmptyBorder(0, 0, 0, 0)
        btnRight.setBounds(645, 158, 30, 50)
        contentPane.add(btnRight)
        btnRight.addActionListener(this)

        btnLeft.isEnabled = false

        for (page in pages) {
            page.setBounds(56, 56, 590, 255)
            contentPane.add(page)
        }

        populateBadgeListAndAddMotionListeners()
        populateStarListAndAddMouseListeners()
    }

    fun init() {
        achievementName.text = ""
        achievementDescription.text = ""
        achievementExplanation.text = ""

        updatePagination()
        refresh(true)
    }

    private fun populateBadgeListAndAddMotionListeners() {
        getAllChildComponentsForType<AchievementBadge>().forEach { it.addMouseMotionListener(this) }
    }

    private fun populateStarListAndAddMouseListeners() {
        getAllChildComponentsForType<RewardStar>().forEach { it.addMouseListener(this) }
    }

    private fun animateTestTube() {
        redrawing = true

        val timer = Timer("Timer-TestTube")
        progressShowing = 0
        testTube.icon = getTubeIconForIndex(0)

        for (i in 1..getAchievementsEarned()) {
            timer.schedule(RedrawTestTube(), (30 * i).toLong())
        }
    }

    private fun updateTestTube() {
        progressShowing = getAchievementsEarned()
        testTube.icon = getTubeIconForIndex(getAchievementsEarned())
        redrawStars()
    }

    private fun getTubeIconForIndex(i: Int): ImageIcon {
        val name = "t$i.png"
        return ImageIcon(AchievementsDialog::class.java.getResource("/tubes/$name"))
    }

    private fun redrawStars() {
        getAllChildComponentsForType<RewardStar>().forEach { star ->
            if (star.isUnlocked(getAchievementsEarned())) {
                star.setIcon(Images.REWARD_UNLOCKED)
                star.setToolTipText(star.hoverDesc)
            } else {
                star.setIcon(Images.REWARD_LOCKED)
                star.setToolTipText("Locked")
            }

            star.repaint()
        }
    }

    fun refresh(restartTube: Boolean) {
        if (restartTube) {
            animateTestTube()
        } else {
            updateTestTube()
        }

        getAllChildComponentsForType<AchievementBadge>().forEach { it.toggle() }

        updateTitle()
        repaint()
    }

    override fun mouseDragged(arg0: MouseEvent) {}

    override fun mouseMoved(arg0: MouseEvent) {
        val c = arg0.component

        if (c is AchievementBadge) {
            val badge: AchievementBadge = arg0.component as AchievementBadge
            achievementName.text = c.name

            if (badge.isEarned) {
                achievementExplanation.text = badge.explanation
                achievementDescription.text = badge.description
            } else {
                achievementExplanation.text = "???"
                achievementDescription.text = ""
            }
        } else {
            achievementName.text = ""
            achievementExplanation.text = ""
            achievementDescription.text = ""
        }
    }

    private inner class RedrawTestTube : TimerTask() {
        override fun run() {
            if (isVisible) {
                if (progressShowing < getAchievementsEarned()) {
                    progressShowing++
                }

                testTube.icon = getTubeIconForIndex(progressShowing)
                redrawStars()

                if (progressShowing == getAchievementsEarned()) {
                    redrawing = false
                }
            }
        }
    }

    override fun mouseClicked(e: MouseEvent) {
        val c: RewardStar = e.component as RewardStar
        if (c.isUnlocked(getAchievementsEarned()) && !redrawing) {
            RewardDialog.showDialog(c.imageName)
        }
    }

    override fun mouseEntered(e: MouseEvent) {
        val source: RewardStar = e.component as RewardStar
        if (source.isUnlocked(progressShowing) && !redrawing) {
            source.setIcon(Images.REWARD_UNLOCKED_HOVERED)
        }
    }

    override fun mouseExited(e: MouseEvent) {
        val source: RewardStar = e.component as RewardStar
        if (source.isUnlocked(progressShowing) && !redrawing) {
            source.setIcon(Images.REWARD_UNLOCKED)
        }
    }

    override fun mousePressed(e: MouseEvent) {
        val source: RewardStar = e.component as RewardStar
        if (source.isUnlocked(getAchievementsEarned()) && !redrawing) {
            source.setIcon(Images.REWARD_UNLOCKED_CLICKED)
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        val source: RewardStar = e.component as RewardStar
        if (source.isUnlocked(getAchievementsEarned()) && !redrawing) {
            source.setIcon(Images.REWARD_UNLOCKED)
        }
    }

    override fun actionPerformed(arg0: ActionEvent) {
        if (arg0.source === btnLeft) {
            currentPage--
        } else {
            currentPage++
        }

        updatePagination()
    }

    private fun updatePagination() {
        btnLeft.isEnabled = currentPage > 0
        btnRight.isEnabled = currentPage < pages.size - 1

        pages.forEachIndexed { i, page -> page.isVisible = i == currentPage }
        updateTitle()
    }

    private fun updateTitle() {
        title.text = pages[currentPage].getTitle()
    }
}
