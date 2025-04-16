package game

data class LeaveAction(override val playerName: String) : PlayerAction() {
    override fun htmlString() = "Left"
}
