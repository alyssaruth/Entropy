package game

data class LeaveAction(override val playerName: String) : PlayerAction() {
    override val blind = false

    override fun plainString() = "Left"
}
