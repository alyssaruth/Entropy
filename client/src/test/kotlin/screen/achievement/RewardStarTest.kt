package screen.achievement

import achievement.Reward
import com.github.alyssaburlton.swingtest.shouldMatchImage
import org.junit.jupiter.api.Test

class RewardStarTest {
    @Test
    fun `Screenshot - locked`() {
        val star = RewardStar("", Reward.NegativeJacks)
        star.shouldMatchImage("rewardLocked")
    }
}
