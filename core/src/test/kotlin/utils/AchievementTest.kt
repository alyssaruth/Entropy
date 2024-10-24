package utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class AchievementTest : AbstractTest() {
    @Test
    fun `Setting locations should not overlap`() {
        val achievements = Achievement.entries
        val distinctSettings = achievements.map { it.settingName }.distinct()

        achievements.size shouldBe distinctSettings.size
    }
}
