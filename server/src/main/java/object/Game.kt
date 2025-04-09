package `object`

typealias Millis = Long

data class Game(
    val gameId: String,
    val rounds: Map<Int, Round>,
    val currentPlayer: Int,
    val countdownStart: Millis?,
    val gameStart: Millis?,
    val gameEnd: Millis?,
) {
    fun startCountdown() = copy(countdownStart = System.currentTimeMillis())

    fun cancelCountdown() = copy(countdownStart = null)

    val countdownTimeRemaining =
        countdownStart?.let {
            val timeElapsed = System.currentTimeMillis() - countdownStart
            if (timeElapsed > COUNTDOWN_TIME_MILLIS) {
                0
            } else {
                COUNTDOWN_TIME_MILLIS - timeElapsed
            }
        } ?: 0

    companion object {
        private const val COUNTDOWN_TIME_MILLIS = 5000
    }
}
