package util

import utils.Achievement
import utils.isUnlocked

fun getAchievementsEarned() = Achievement.entries.count { it.isUnlocked() }
