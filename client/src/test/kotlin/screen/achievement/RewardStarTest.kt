package screen.achievement

import achievement.Reward
import com.github.alyssaburlton.swingtest.doClick
import com.github.alyssaburlton.swingtest.doHover
import com.github.alyssaburlton.swingtest.doHoverAway
import com.github.alyssaburlton.swingtest.findWindow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import util.AbstractClientTest
import util.Images

class RewardStarTest : AbstractClientTest() {
    @Test
    fun `Screenshot - locked`() {
        val star = RewardStar("", Reward.NegativeJacks)
        star.icon shouldBe Images.REWARD_LOCKED
    }

    @Test
    fun `Screenshot - unlocked`() {
        Reward.NegativeJacks.unlock()

        val star = RewardStar("", Reward.NegativeJacks)
        star.icon shouldBe Images.REWARD_UNLOCKED

        star.doHover()
        star.icon shouldBe Images.REWARD_UNLOCKED_HOVERED

        star.doHoverAway()
        star.icon shouldBe Images.REWARD_UNLOCKED
    }

    @Test
    fun `Should do nothing on click if locked`() {
        Reward.NegativeJacks.unlock()

        val star = RewardStar("", Reward.NegativeJacks)
        star.doClick(async = true)

        findWindow<RewardDialog>().shouldNotBeNull()
    }

    @Test
    fun `Should launch reward dialog on click`() {
        val star = RewardStar("", Reward.NegativeJacks)
        star.doClick(async = true)

        findWindow<RewardDialog>() shouldBe null
    }
}
