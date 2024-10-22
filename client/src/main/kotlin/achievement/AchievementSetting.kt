package achievement

import preference.Setting

object AchievementSetting {
    val BookwormTime = Setting("bookwormTime", 0L)
    val VanityCount = Setting("vanityCount", 0)
    val ChattyCount = Setting("chattyCount", 0)
    @JvmField val TimePlayed = Setting("timePlayed", 0L)
    @JvmField val WillUnlockCoward = Setting("willUnlockCoward", false)
}
