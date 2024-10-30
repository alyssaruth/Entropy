package screen

import bean.AchievementBadge
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import utils.Achievement
import utils.getAllChildComponentsForType

class AchievementsPanelTest : AbstractTest() {
    @Test
    fun `should include all achievements across the pages`() {
        val pages = makeAchievementPanels()
        val badges = pages.flatMap { it.getAllChildComponentsForType<AchievementBadge>() }
        val descriptions = badges.map { it.description }
        val expectedDescriptions = Achievement.entries.map { it.description }
        descriptions.shouldContainExactlyInAnyOrder(expectedDescriptions)
    }

    @Test
    fun `should split into pages based on size`() {
        val pages = makeAchievementPanels()
        val pageOneBadges = pages.first().getAllChildComponentsForType<AchievementBadge>()
        pageOneBadges.size shouldBe MAX_ACHIEVEMENTS_PER_PAGE
    }

    @Test
    fun `should display correct title based on what's been earned`() {
        val locked = (1..3).map { makeAchievementBadge(false, false) }
        val hidden = (1..2).map { makeAchievementBadge(false, true) }
        val earned = (1..5).map { makeAchievementBadge(true, false) }

        val panel = AchievementsPanel(3, locked + hidden + earned)
        panel.getTitle() shouldBe "Page 3 (5 / 8)"
    }

    private fun makeAchievementBadge(earned: Boolean, hidden: Boolean): AchievementBadge {
        val badge = mockk<AchievementBadge>(relaxed = true)
        every { badge.isVisible } returns !hidden
        every { badge.isEarned } returns earned
        return badge
    }
}
