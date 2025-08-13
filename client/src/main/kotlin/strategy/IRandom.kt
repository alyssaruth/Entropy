package strategy

import kotlin.random.Random

interface IRandom {
    fun nextInt(until: Int): Int
}

class DefaultRandom : IRandom {
    override fun nextInt(until: Int) = Random.nextInt(until)
}
