package util

import javax.swing.ImageIcon
import utils.Achievement
import utils.isUnlocked

@JvmField
val LOCKED_ICON = ImageIcon(AchievementsUtil::class.java.getResource("/achievements/locked.png"))

fun getAchievementsEarned() = Achievement.entries.count { it.isUnlocked() }
