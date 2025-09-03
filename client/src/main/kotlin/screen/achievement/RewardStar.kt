package screen.achievement

import achievement.Reward
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JLabel
import util.Images

class RewardStar(val hoverDesc: String, val reward: Reward) : JLabel(), MouseListener {

    init {
        preferredSize = Dimension(17, 16)
        addMouseListener(this)
        toggle()
    }

    fun toggle() {
        if (reward.isUnlocked()) {
            setIcon(Images.REWARD_UNLOCKED)
            toolTipText = hoverDesc
        } else {
            setIcon(Images.REWARD_LOCKED)
            toolTipText = "Locked"
        }

        repaint()
    }

    override fun mouseClicked(e: MouseEvent?) {
        if (reward.isUnlocked()) {
            RewardDialog.showDialog(reward)
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        if (reward.isUnlocked()) {
            setIcon(Images.REWARD_UNLOCKED_CLICKED)
        }
    }

    override fun mouseReleased(e: MouseEvent?) {
        if (reward.isUnlocked()) {
            setIcon(Images.REWARD_UNLOCKED)
        }
    }

    override fun mouseEntered(e: MouseEvent?) {
        if (reward.isUnlocked()) {
            setIcon(Images.REWARD_UNLOCKED_HOVERED)
        }
    }

    override fun mouseExited(e: MouseEvent?) {
        if (reward.isUnlocked()) {
            setIcon(Images.REWARD_UNLOCKED)
        }
    }
}
