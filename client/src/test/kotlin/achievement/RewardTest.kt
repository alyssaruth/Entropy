package achievement

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import settings.Setting
import util.AbstractClientTest
import util.ClientGlobals

class RewardTest : AbstractClientTest() {
    @Test
    fun `Should be increasing order by unlock threshold`() {
        val sorted = Reward.entries.sortedBy { it.threshold }
        Reward.entries shouldContainExactly sorted
    }

    @Test
    fun `Thresholds should be distinct`() {
        val groups = Reward.entries.groupBy { it.threshold }
        groups.forEach { threshold, rewards ->
            withClue("should only be 1 reward with threshold $threshold") {
                rewards.shouldHaveSize(1)
            }
        }
    }

    @Test
    fun `Image assets should exist`() {
        Reward.entries.forEach { reward ->
            withClue("asset ${reward.settingName}.png should exist") {
                reward.getResource().shouldNotBeNull()
            }
        }
    }

    @Test
    fun `Should report whether its unlocked or not`() {
        Reward.NegativeJacks.isUnlocked() shouldBe false

        Reward.NegativeJacks.unlock()

        Reward.NegativeJacks.isUnlocked() shouldBe true
        ClientGlobals.rewardStore.get(Setting("negativeJacks", false)) shouldBe true
    }
}
