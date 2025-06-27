package game

data class ChallengeAction(override val playerName: String, override val blind: Boolean) :
    PlayerAction() {
    override fun plainString() = "Challenge"
}
