package screen

import bean.AchievementBadge
import javax.swing.JPanel
import utils.Achievement

const val MAX_ACHIEVEMENTS_PER_PAGE = 36

fun makeAchievementPanels() =
    Achievement.entries.chunked(MAX_ACHIEVEMENTS_PER_PAGE).mapIndexed { ix, badges ->
        AchievementsPanel(ix + 1, badges.map(::AchievementBadge))
    }

class AchievementsPanel(private val number: Int, private val badges: List<AchievementBadge>) :
    JPanel() {
    init {
        badges.forEach(::add)
    }

    fun getTitle(): String {
        val total = badges.count { it.isVisible }
        return "Page $number (${badges.count { it.isEarned }} / $total)"
    }
}
