package game

data class IllegalAction(override val playerName: String) : PlayerAction() {
    override fun plainString() = "Illegal!"
}
