package game

abstract class BidAction<B : BidAction<B>> : PlayerAction() {
    abstract val cardToReveal: String?
    abstract val blind: Boolean

    abstract fun higherThan(other: B): Boolean

    abstract fun overAchievementThreshold(): Boolean

    abstract fun isPerfect(cards: List<String>, settings: GameSettings): Boolean

    abstract fun isOverbid(cards: List<String>, settings: GameSettings): Boolean
}
