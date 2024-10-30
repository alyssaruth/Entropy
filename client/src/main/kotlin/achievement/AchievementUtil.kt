package achievement

import javax.swing.ImageIcon
import preference.Setting
import screen.AchievementsDialog
import screen.MainScreen
import screen.ScreenCache
import util.AchievementsUtil
import util.ClientGlobals
import util.ClientGlobals.achievementStore
import util.ClientUtil
import utils.Achievement
import utils.CHATTY_THRESHOLD
import utils.VANITY_THRESHOLD

fun getAchievementsEarned() = Achievement.entries.count { it.isUnlocked() }

fun Achievement.isUnlocked(): Boolean {
    val setting = Setting(settingName, false)
    return achievementStore.get(setting)
}

fun Achievement.getIcon() = ImageIcon(javaClass.getResource("/achievements/$settingName.png"))

fun unlockAchievement(achievement: Achievement) {
    if (achievement.isUnlocked()) {
        return
    }

    achievementStore.save(Setting(achievement.settingName, false), true)

    ScreenCache.get<AchievementsDialog>().refresh(false)
    val icon = achievement.getIcon()

    val screen = ScreenCache.get<MainScreen>()
    screen.showAchievementPopup(achievement.title, icon)

    val achievementsEarned = getAchievementsEarned()
    if (ClientUtil.isOnline()) {
        ClientGlobals.sessionApi.updateAchievementCount(achievementsEarned)
    }

    AchievementsUtil.unlockRewards(achievementsEarned)
}

fun updateAndUnlockVanity() {
    updateAchievementAndCounter(
        Achievement.Vanity,
        AchievementSetting.VanityCount,
        VANITY_THRESHOLD,
    )
}

fun updateAndUnlockChatty() {
    updateAchievementAndCounter(
        Achievement.Chatty,
        AchievementSetting.ChattyCount,
        CHATTY_THRESHOLD,
    )
}

private fun updateAchievementAndCounter(
    achievement: Achievement,
    counter: Setting<Int>,
    threshold: Int,
) {
    val count = achievementStore.get(counter) + 1
    achievementStore.save(counter, count)

    if (count >= threshold) {
        unlockAchievement(achievement)
    }
}
