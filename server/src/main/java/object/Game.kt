package `object`

import java.util.concurrent.ConcurrentHashMap

/** Wrap up a single game */
data class GameWrapper(val gameId: String, val rounds: Map<Int, Round>, val currentPlayer: Int) {
    var winningPlayer: Int = -1
    var roundNumber: Int = 1
    var gameStartMillis: Long = -1
        private set

    var gameEndMillis: Long = -1
    private var hmRoundDetailsByRoundNumber = ConcurrentHashMap<Int, HandDetails>()
    private var hmBidHistoryByRoundNumber = ConcurrentHashMap<Int, BidHistory>()
    private var countdownStartMillis: Long = -1

    fun factoryCopy(): GameWrapper {
        val wrapper = GameWrapper(gameId)
        wrapper.winningPlayer = winningPlayer
        wrapper.setRoundDetails(hmRoundDetailsByRoundNumber)
        wrapper.setBidHistory(hmBidHistoryByRoundNumber)
        wrapper.setCountdownStartMillis(countdownStartMillis)
        wrapper.setGameStartMillisIfUnset(gameStartMillis)
        wrapper.gameEndMillis = System.currentTimeMillis()
        wrapper.roundNumber = roundNumber

        return wrapper
    }

    val currentRoundDetails: HandDetails?
        get() = hmRoundDetailsByRoundNumber[roundNumber]

    fun getDetailsForRound(roundNumber: Int): HandDetails? {
        return hmRoundDetailsByRoundNumber[roundNumber]
    }

    fun setDetailsForRound(roundNumber: Int, details: HandDetails) {
        hmRoundDetailsByRoundNumber[roundNumber] = details
    }

    val currentBidHistory: BidHistory?
        get() = hmBidHistoryByRoundNumber[roundNumber]

    fun getBidHistoryForRound(roundNumber: Int): BidHistory? {
        return hmBidHistoryByRoundNumber[roundNumber]
    }

    fun setBidHistoryForRound(roundNumber: Int, bidHistory: BidHistory) {
        hmBidHistoryByRoundNumber[roundNumber] = bidHistory
    }

    fun setRoundDetails(hmRoundDetailsByRoundNumber: ConcurrentHashMap<Int, HandDetails>) {
        this.hmRoundDetailsByRoundNumber = hmRoundDetailsByRoundNumber
    }

    fun setBidHistory(hmBidHistoryByRoundNumber: ConcurrentHashMap<Int, BidHistory>) {
        this.hmBidHistoryByRoundNumber = hmBidHistoryByRoundNumber
    }

    /** Helpers */
    fun getPersonToStart(roundNumber: Int): Int {
        val history = hmBidHistoryByRoundNumber[roundNumber]
        return history!!.personToStart
    }

    val countdownTimeRemaining: Long
        get() {
            val timeElapsed = System.currentTimeMillis() - countdownStartMillis
            if (timeElapsed > COUNTDOWN_TIME_MILLIS) {
                return 0
            }

            return COUNTDOWN_TIME_MILLIS - timeElapsed
        }

    fun setCountdownStartMillisIfUnset() {
        if (countdownStartMillis == -1L) {
            countdownStartMillis = System.currentTimeMillis()
        }
    }

    fun setCountdownStartMillis(countdownStartMillis: Long) {
        this.countdownStartMillis = countdownStartMillis
    }

    fun setGameStartMillisIfUnset(millis: Long) {
        if (gameStartMillis == -1L) {
            gameStartMillis = millis
        }
    }

    val gameDurationMillis: Long
        get() = gameEndMillis - gameStartMillis

    companion object {
        private const val COUNTDOWN_TIME_MILLIS = 5000
    }
}
