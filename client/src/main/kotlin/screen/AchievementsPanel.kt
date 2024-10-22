package screen

import bean.AchievementBadge
import javax.swing.JPanel
import utils.Achievement

const val MAX_PER_PAGE = 36

fun makeAchievementPanels() =
    Achievement.entries.chunked(MAX_PER_PAGE).mapIndexed { ix, badges ->
        AchievementsPanel(ix + 1, badges.map(::AchievementBadge))
    }

class AchievementsPanel(private val number: Int, val badges: List<AchievementBadge>) : JPanel() {
    init {
        badges.forEach(::add)
    }

    fun getTitle() = "Page $number (${badges.count { it.isEarned }} / ${badges.size})"
}
