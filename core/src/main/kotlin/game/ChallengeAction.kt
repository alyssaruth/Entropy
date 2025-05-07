package game

data class ChallengeAction(override val playerName: String) : PlayerAction() {
    override fun plainString() = "Challenge"
}
