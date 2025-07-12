package game

data class IllegalAction(override val playerName: String, override val blind: Boolean) :
    PlayerAction() {
    override fun plainString() = "Illegal!"
}
