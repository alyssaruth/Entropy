package util

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SimulationResultsTest : AbstractClientTest() {
    @Test
    fun `Should compute win rate correctly`() {
        val results = SimulationResults()
        repeat(1745) { results.incrementWins() }

        results.getWinRate(10000) shouldBe 17.45
    }

    @Test
    fun `Should compute challenge rate correctly`() {
        val results = SimulationResults()
        repeat(5000) { results.incrementOpportunitiesToChallenge() }
        repeat(1011) { results.incrementTotalChallenges() }

        results.getChallengeRate() shouldBe 20.22
    }

    @Test
    fun `Should compute challenge success rate correctly`() {
        val results = SimulationResults()
        repeat(5000) { results.incrementTotalChallenges() }
        repeat(76) { results.incrementGoodChallenges() }

        results.getChallengeSuccessRate() shouldBe 1.52
    }

    @Test
    fun `Should compute perfect game rate correctly`() {
        val results = SimulationResults()
        repeat(1000) { results.incrementWins() }
        repeat(25) { results.incrementPerfectGames() }

        results.getPerfectRate() shouldBe 2.5
    }

    @Test
    fun `Should handle zero divisor`() {
        val results = SimulationResults()
        repeat(25) { results.incrementPerfectGames() }

        results.getPerfectRate() shouldBe null
    }

    @Test
    fun `Should generate a row for the report`() {
        val results = SimulationResults()
        repeat(1745) { results.incrementWins() }
        repeat(155) { results.incrementPerfectGames() }
        repeat(5000) { results.incrementOpportunitiesToChallenge() }
        repeat(1011) { results.incrementTotalChallenges() }
        repeat(685) { results.incrementGoodChallenges() }

        val row = results.generateRow(0, CpuStrategies.STRATEGY_BASIC, 10000)
        row.shouldContainExactly(
            "0",
            CpuStrategies.STRATEGY_BASIC,
            "17.45",
            "20.22",
            "67.75",
            "8.88",
        )
    }
}
