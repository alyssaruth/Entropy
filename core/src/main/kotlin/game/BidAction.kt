package game

abstract class BidAction<B : BidAction<B>> : PlayerAction() {

    var cardToReveal: String? = null

    abstract fun higherThan(other: B): Boolean

    abstract fun overAchievementThreshold(): Boolean

    abstract fun isPerfect(cards: List<String>, settings: GameSettings): Boolean

    abstract fun isOverbid(cards: List<String>, settings: GameSettings): Boolean

    override fun toString() =
        plainString() + if (cardToReveal != null) " (Shows: $cardToReveal)" else ""
}
