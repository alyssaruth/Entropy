package bean

import java.awt.Dimension
import javax.swing.JLabel
import util.AchievementsUtil
import util.Images
import util.Registry
import utils.Achievement

class AchievementBadge(private val achievement: Achievement) : JLabel(), Registry {
    private val registryLocation: String
    private val name: String
    val explanation: String
    val description: String

    init {
        preferredSize = Dimension(56, 56)

        this.registryLocation = achievement.registryLocation
        this.name = achievement.title
        this.explanation = achievement.explanation
        this.description = achievement.description

        toggle()
    }

    fun toggle() {
        val unlocked = Registry.achievements.getBoolean(registryLocation, false)
        icon =
            if (unlocked) AchievementsUtil.getIconForAchievement(registryLocation)
            else Images.ACHIEVEMENT_LOCKED

        if (achievement.hidden) {
            isVisible = unlocked
        }
    }

    override fun toString(): String {
        return name
    }

    val isEarned: Boolean
        get() = Registry.achievements.getBoolean(registryLocation, false)

    override fun getName(): String {
        return name
    }
}
