package screen

import achievement.unlockAchievement
import bean.AchievementBadge
import com.github.alyssaburlton.swingtest.doMouseMove
import com.github.alyssaburlton.swingtest.getChild
import io.kotest.matchers.shouldBe
import javax.swing.JLabel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import preference.InMemorySettingStore
import testCore.AbstractTest
import util.ClientGlobals.achievementStore
import utils.Achievement

class AchievementsDialogTest : AbstractTest() {
    @BeforeEach
    fun before() {
        achievementStore = InMemorySettingStore()
    }

    @Test
    fun `Should update achievement details on hover`() {
        unlockAchievement(Achievement.Caveman)

        val dlg = AchievementsDialog()
        dlg.init()
        dlg.getDisplayedAchievementName() shouldBe ""
        dlg.getDisplayedAchievementExplanation() shouldBe ""
        dlg.getDisplayedAchievementDescription() shouldBe ""

        val cavemanBadge =
            dlg.getChild<AchievementBadge> { it.explanation == Achievement.Caveman.explanation }
        cavemanBadge.doMouseMove()

        dlg.getDisplayedAchievementName() shouldBe Achievement.Caveman.title
        dlg.getDisplayedAchievementExplanation() shouldBe Achievement.Caveman.explanation
        dlg.getDisplayedAchievementDescription() shouldBe Achievement.Caveman.description

        // Hover over a locked achievement
        val lionBadge =
            dlg.getChild<AchievementBadge> { it.explanation == Achievement.Lion.explanation }
        lionBadge.doMouseMove()

        dlg.getDisplayedAchievementName() shouldBe Achievement.Lion.title
        dlg.getDisplayedAchievementExplanation() shouldBe "???"
        dlg.getDisplayedAchievementDescription() shouldBe ""

        // Hover elsewhere
        dlg.doMouseMove()

        dlg.getDisplayedAchievementName() shouldBe ""
        dlg.getDisplayedAchievementExplanation() shouldBe ""
        dlg.getDisplayedAchievementDescription() shouldBe ""
    }

    private fun AchievementsDialog.getDisplayedAchievementName() =
        getChild<JLabel>("AchievementName").text

    private fun AchievementsDialog.getDisplayedAchievementExplanation() =
        getChild<JLabel>("AchievementExplanation").text

    private fun AchievementsDialog.getDisplayedAchievementDescription() =
        getChild<JLabel>("AchievementDescription").text
}
