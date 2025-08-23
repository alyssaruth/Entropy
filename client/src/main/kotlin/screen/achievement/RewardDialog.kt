package screen.achievement

import achievement.Reward
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.SwingConstants
import screen.SimpleDialog

class RewardDialog(reward: Reward) : SimpleDialog(false) {
    private val rewardTitle = JLabel("Title")
    private val imageBanner = JLabel("")
    private val rewardDescription = JTextPane()
    private val topPanel = JPanel()
    private val buttonPanel = JPanel()

    init {
        topPanel.setLayout(BorderLayout(0, 0))
        rewardTitle.setFont(Font("Tahoma", Font.BOLD, 16))
        rewardTitle.setHorizontalAlignment(SwingConstants.CENTER)
        rewardTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0))
        topPanel.add(rewardTitle, BorderLayout.NORTH)
        val banner = ImageIcon(reward.getResource())
        imageBanner.setHorizontalAlignment(SwingConstants.CENTER)
        imageBanner.setIcon(banner)
        topPanel.add(imageBanner, BorderLayout.SOUTH)
        contentPane.add(topPanel, BorderLayout.NORTH)

        setRewardDescriptionAndDialogSize(reward)
        rewardDescription.setEditable(false)
        rewardDescription.setOpaque(false)
        rewardDescription.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10))
        rewardDescription.setBackground(Color(0, 0, 0, 0))
        contentPane.add(rewardDescription, BorderLayout.CENTER)

        buttonPanel.add(btnOk)
        contentPane.add(buttonPanel, BorderLayout.SOUTH)
    }

    override fun okPressed() {
        dispose()
    }

    private fun setRewardDescriptionAndDialogSize(reward: Reward) {
        var rewardStr: String

        if (reward == Reward.Blind) {
            rewardTitle.setText("Blind play unlocked!")
            rewardStr =
                ("Ramp up the challenge by making decisions without looking at your cards!" +
                    "\n\nWith this option enabled, your cards will be dealt face-down by default. You can look at any time " +
                    "by clicking the eye but be warned: some achievements require you to go an entire game without peeking once!" +
                    "\n\nEnable this option by going to Tools > Preferences > Gameplay and ticking 'Play Blind'.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 410)
        } else if (reward == Reward.DeveloperSet) {
            rewardTitle.setText("'Developers' joker set unlocked!")
            rewardStr =
                ("These jokers feature the faces of four people who helped to develop Entropy." +
                    "\n\nUse them by going to Tools > Preferences > Appearance and selecting 'Developers' as the Joker Design.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 330)
        } else if (reward == Reward.FourColours) {
            rewardTitle.setText("Four colour deck unlocked!")
            rewardStr =
                ("You can now play with a four-colour deck, where clubs are green and diamonds are blue." +
                    "\n\nEnable this option by going to Tools > Preferences > Appearance and ticking 'Use 4 colour deck'.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 330)
        } else if (reward == Reward.CardReveal) {
            rewardTitle.setText("Card reveal unlocked!")
            rewardStr =
                ("Add extra pressure to the game by forcing players to reveal their cards!" +
                    "\n\nWith this option set, players will be forced to show a card each time they make a bid. " +
                    "Players do not have to reveal their last card so not all cards will be shown." +
                    "\n\nEnable this option by going to Tools > Preferences > Gameplay and ticking 'Players reveal cards'.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 390)
        } else if (reward == Reward.NegativeJacks) {
            rewardTitle.setText("Negative jacks unlocked!")
            rewardStr =
                ("Spice up the deck by makings Jacks worth -1 of their suit!" +
                    "\n\nEnable this option by going to Tools > Preferences > Gameplay and ticking 'Jacks worth -1'")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 310)
        } else if (reward == Reward.MinimalistDeck) {
            rewardTitle.setText("'Minimalist' deck design unlocked!")
            rewardStr =
                ("Go for a more minimalist feel with this new deck design." +
                    "\n\nUse it by going to Tools > Preferences > Appearance and selecting 'Minimalist' as the Deck Design.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 310)
        } else if (reward == Reward.Vectropy) {
            rewardTitle.setText("Vectropy unlocked!")
            rewardStr =
                ("Vectropy is a variant where you have to bid in all four suits at once. " +
                    "New help pages have been added that detail the rules for this new game." +
                    "\n\nPlay Vectropy by going to Tools > Preferences > Gameplay and selecting 'Vectropy' (under Game Mode).")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 350)
        } else if (reward == Reward.Illegal) {
            rewardTitle.setText("'Illegal' option unlocked!")
            rewardStr =
                ("You can now shout 'Illegal!' in response to a bid that you think is perfect! " +
                    "\n\nIf you're right your opponent loses a card, but if you're wrong you'll lose one - even if a challenge would have been correct!" +
                    "\n\nYou will see the new 'Illegal!' option the next time you play a game.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 370)
        } else if (reward == Reward.ExtraSuits) {
            rewardTitle.setText("Extra suits unlocked!")
            rewardStr =
                ("You can now play with Stars and Moons, making for up to 6 suits in total! " +
                    "\nSuit order remains alphabetical, making Stars the strongest suit if they are in play." +
                    "\n\nChoose which suits to play with by going to Tools > Preferences > Gameplay and using the options under 'Deck Setup'.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 370)
        } else if (reward == Reward.Cheats) {
            rewardTitle.setText("Cheats unlocked!")
            rewardStr =
                ("Now you can use certain codes to cheat on your opponents, and even access hidden screens!" +
                    "\n\nThese were first created to speed up testing, especially for things that relied on being good - seriously, who has time for that?" +
                    "\n\nA full list of cheats can be found under Help > Miscellaneous > Cheat Codes.")

            rewardDescription.setText(rewardStr)
            size = Dimension(400, 390)
        }
    }

    companion object {
        fun showDialog(reward: Reward) {
            val dialog = RewardDialog(reward)
            dialog.setLocationRelativeTo(null)
            dialog.isVisible = true
        }
    }
}
