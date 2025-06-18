package util

import java.util.Vector

class SimulationResults {
    private var wins = 0.0
    private var perfectGames = 0.0
    private var goodChallenges = 0.0
    private var totalChallenges = 0.0
    private var opportunitiesToChallenge = 0.0

    fun incrementWins() {
        wins++
    }

    fun incrementPerfectGames() {
        perfectGames++
    }

    fun incrementGoodChallenges() {
        goodChallenges++
    }

    fun incrementTotalChallenges() {
        totalChallenges++
    }

    fun incrementOpportunitiesToChallenge() {
        opportunitiesToChallenge++
    }

    fun getWinRate(totalGames: Int) = computePercentage(wins, totalGames.toDouble())

    fun getChallengeRate() = computePercentage(totalChallenges, opportunitiesToChallenge)

    fun getChallengeSuccessRate() = computePercentage(goodChallenges, totalChallenges)

    fun getPerfectRate() = computePercentage(perfectGames, wins)

    private fun computePercentage(numerator: Double, denominator: Double): Double? {
        if (denominator == 0.0) {
            return null
        }

        return MathsUtil.getPercentage(numerator, denominator, 2)
    }

    fun generateRow(opponentNumber: Int, strategy: String, totalGames: Int): Vector<String> {
        val row = Vector<String>()
        row.add("" + opponentNumber)
        row.add(strategy)
        row.add("" + getWinRate(totalGames))
        row.add("" + getChallengeRate())
        row.add("" + getChallengeSuccessRate())
        row.add("" + getPerfectRate())

        return row
    }
}
