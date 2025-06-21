package util

import game.GameMode
import io.kotest.matchers.doubles.shouldBeBetween
import makeSimulationParams
import org.junit.jupiter.api.Test
import screen.ScreenCache.get
import screen.SimulationDialog
import testCore.makeGameSettings

class GameSimulatorBenchmarkTest : AbstractClientTest() {
    @Test
    fun `Entropy - Hard vs Easy`() {
        val settings = makeGameSettings(mode = GameMode.Entropy)
        val params =
            makeSimulationParams(
                settings,
                opponentTwoEnabled = false,
                opponentThreeEnabled = false,
                opponentZeroStrategy = CpuStrategies.STRATEGY_BASIC,
                opponentOneStrategy = CpuStrategies.STRATEGY_EV,
                randomiseOrder = true,
            )

        val games = 10000
        val simulator = GameSimulator(params)
        repeat(games) { simulator.startNewGame(it) }

        val results = get(SimulationDialog::class.java).getResults(1)
        results.getWinRate(games)!!.shouldBeBetween(80.0, 85.0, 0.1)
        results.getChallengeRate()!!.shouldBeBetween(20.0, 22.0, 0.1)
        results.getChallengeSuccessRate()!!.shouldBeBetween(77.0, 81.0, 0.1)
        results.getPerfectRate()!!.shouldBeBetween(29.0, 31.0, 0.1)
    }

    @Test
    fun `Vectropy - Hard vs Easy`() {
        val settings = makeGameSettings(mode = GameMode.Vectropy)
        val params =
            makeSimulationParams(
                settings,
                opponentTwoEnabled = false,
                opponentThreeEnabled = false,
                opponentZeroStrategy = CpuStrategies.STRATEGY_BASIC,
                opponentOneStrategy = CpuStrategies.STRATEGY_EV,
                randomiseOrder = true,
            )

        val games = 10000
        val simulator = GameSimulator(params)
        repeat(games) { simulator.startNewGame(it) }

        val results = get(SimulationDialog::class.java).getResults(1)
        results.getWinRate(games)!!.shouldBeBetween(57.0, 61.0, 0.1)
        results.getChallengeRate()!!.shouldBeBetween(24.0, 26.0, 0.1)
    }
}
