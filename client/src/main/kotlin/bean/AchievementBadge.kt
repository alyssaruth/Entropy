package bean

import achievement.getIcon
import achievement.isUnlocked
import java.awt.Dimension
import javax.swing.JLabel
import util.Images
import utils.Achievement

class AchievementBadge(private val achievement: Achievement) : JLabel() {
    val explanation = achievement.explanation
    val description = achievement.description

    init {
        preferredSize = Dimension(56, 56)

        this.name = achievement.title

        toggle()
    }

    fun toggle() {
        icon = if (isEarned) achievement.getIcon() else Images.ACHIEVEMENT_LOCKED

        if (achievement.hidden) {
            isVisible = isEarned
        }

        repaint()
    }

    val isEarned: Boolean
        get() = achievement.isUnlocked()
}
