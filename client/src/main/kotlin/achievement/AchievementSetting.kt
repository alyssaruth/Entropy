package achievement

import preference.Setting

object AchievementSetting {
    val BookwormTime = Setting("bookwormTime", 0L)
    val VanityCount = Setting("vanityCount", 0)
    val ChattyCount = Setting("chattyCount", 0)
    @JvmField val PlayerCount = Setting("playerCount", 0L)
    @JvmField val TimePlayed = Setting("timePlayed", 0L)
    @JvmField val WillUnlockCoward = Setting("willUnlockCoward", false)
    @JvmField val BestStreak = Setting("bestStreak", 0)
    @JvmField val CurrentStreak = Setting("currentStreak", 0)
    @JvmField val EntropyGamesPlayed = Setting("entropyGamesPlayed", 0)
    @JvmField val VectropyGamesPlayed = Setting("vectropyGamesPlayed", 0)
    @JvmField val EntropyGamesWon = Setting("entropyGamesWon", 0)
    @JvmField val VectropyGamesWon = Setting("vectropyGamesWon", 0)
}
