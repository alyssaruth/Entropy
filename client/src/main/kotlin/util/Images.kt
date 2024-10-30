package util

import javax.swing.ImageIcon

object Images {
    val ACHIEVEMENT_LOCKED = ImageIcon(javaClass.getResource("/achievements/locked.png"))

    val REWARD_LOCKED = ImageIcon(javaClass.getResource("/rewards/lockedReward.png"))
    val REWARD_UNLOCKED = ImageIcon(javaClass.getResource("/rewards/unlockedReward.png"))
    val REWARD_UNLOCKED_HOVERED =
        ImageIcon(javaClass.getResource("/rewards/unlockedRewardDark.png"))
    val REWARD_UNLOCKED_CLICKED =
        ImageIcon(javaClass.getResource("/rewards/unlockedRewardDarkest.png"))

    @JvmField val ICON_ONLINE = ImageIcon(javaClass.getResource("/icons/onlineIcon.png"))
}
