package achievement

import com.github.alyssaburlton.swingtest.getChild
import com.github.alyssaburlton.swingtest.shouldMatch
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import javax.swing.ImageIcon
import javax.swing.JLabel
import org.junit.jupiter.api.Test
import preference.Setting
import screen.AchievementsDialog
import screen.MainScreen
import screen.ScreenCache
import testCore.verifyNotCalled
import util.AbstractClientTest
import util.ClientGlobals.achievementStore
import util.put
import utils.Achievement
import utils.CHATTY_THRESHOLD
import utils.VANITY_THRESHOLD

class AchievementUtilTest : AbstractClientTest() {

    @Test
    fun `Should correctly report whether an achievement is unlocked`() {
        Achievement.Lion.isUnlocked() shouldBe false

        achievementStore.save(Setting(Achievement.Lion.settingName, false), true)

        Achievement.Lion.isUnlocked() shouldBe true
    }

    @Test
    fun `Should count total achievements unlocked`() {
        getAchievementsEarned() shouldBe 0

        unlockAchievement(Achievement.Lion)
        unlockAchievement(Achievement.Caveman)

        getAchievementsEarned() shouldBe 2

        unlockAchievement(Achievement.Lion)
        unlockAchievement(Achievement.KonamiCode)
        getAchievementsEarned() shouldBe 3
    }

    @Test
    fun `Should notify screens when achievement is unlocked for the first time`() {
        val mainScreen = mockk<MainScreen>(relaxed = true)
        ScreenCache.put(mainScreen)

        unlockAchievement(Achievement.Lion)

        Achievement.Lion.isUnlocked() shouldBe true

        verify { mainScreen.showAchievementPopup(Achievement.Lion.title, any()) }
        val tubeLabel = ScreenCache.get<AchievementsDialog>().getChild<JLabel>("testTube")
        tubeLabel.icon.shouldMatch(ImageIcon(javaClass.getResource("/tubes/t1.png")))

        clearMocks(mainScreen)
        unlockAchievement(Achievement.Lion)

        verifyNotCalled { mainScreen.showAchievementPopup(any(), any()) }
    }

    @Test
    fun `Should increment threshold and unlock vanity`() {
        updateAndUnlockVanity()
        achievementStore.get(AchievementSetting.VanityCount) shouldBe 1

        updateAndUnlockVanity()
        achievementStore.get(AchievementSetting.VanityCount) shouldBe 2
        Achievement.Vanity.isUnlocked() shouldBe false

        achievementStore.save(AchievementSetting.VanityCount, VANITY_THRESHOLD - 1)
        updateAndUnlockVanity()

        achievementStore.get(AchievementSetting.VanityCount) shouldBe VANITY_THRESHOLD
        Achievement.Vanity.isUnlocked() shouldBe true
    }

    @Test
    fun `Should increment threshold and unlock chatty`() {
        updateAndUnlockChatty()
        achievementStore.get(AchievementSetting.ChattyCount) shouldBe 1

        updateAndUnlockChatty()
        achievementStore.get(AchievementSetting.ChattyCount) shouldBe 2
        Achievement.Chatty.isUnlocked() shouldBe false

        achievementStore.save(AchievementSetting.ChattyCount, CHATTY_THRESHOLD - 1)
        updateAndUnlockChatty()

        achievementStore.get(AchievementSetting.ChattyCount) shouldBe CHATTY_THRESHOLD
        Achievement.Chatty.isUnlocked() shouldBe true
    }
}
