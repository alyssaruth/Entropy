package bean

import achievement.getIcon
import achievement.unlockAchievement
import com.github.alyssaburlton.swingtest.shouldBeVisible
import com.github.alyssaburlton.swingtest.shouldMatch
import com.github.alyssaburlton.swingtest.shouldNotBeVisible
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import preference.InMemorySettingStore
import testCore.AbstractTest
import util.ClientGlobals
import util.Images
import utils.Achievement

class AchievementBadgeTest : AbstractTest() {
    @BeforeEach
    fun before() {
        ClientGlobals.achievementStore = InMemorySettingStore()
    }

    @Test
    fun `Should defer to achievement for explanation and description`() {
        val badge = AchievementBadge(Achievement.Caveman)
        badge.description shouldBe Achievement.Caveman.description
        badge.explanation shouldBe Achievement.Caveman.explanation
    }

    @Test
    fun `Should be visible with locked icon for a regular achievement that has not been earned`() {
        val badge = AchievementBadge(Achievement.Lion)
        badge.shouldBeVisible()
        badge.icon shouldBe Images.ACHIEVEMENT_LOCKED
    }

    @Test
    fun `Should not be visible for a hidden achievement that has not been earned`() {
        val badge = AchievementBadge(Achievement.KonamiCode)
        badge.shouldNotBeVisible()
    }

    @Test
    fun `Should have unlocked icon for unlocked achievement`() {
        unlockAchievement(Achievement.Lion)

        val badge = AchievementBadge(Achievement.Lion)
        badge.icon.shouldMatch(Achievement.Lion.getIcon())
    }

    @Test
    fun `Should be able to toggle when achievement status has changed`() {
        val badge = AchievementBadge(Achievement.KonamiCode)
        badge.shouldNotBeVisible()
        badge.isEarned shouldBe false

        unlockAchievement(Achievement.KonamiCode)

        badge.toggle()
        badge.shouldBeVisible()
        badge.icon.shouldMatch(Achievement.KonamiCode.getIcon())
        badge.isEarned shouldBe true
    }
}
