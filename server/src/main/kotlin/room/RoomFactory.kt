package room

import game.GameMode
import game.GameSettings
import java.util.*
import util.ServerGlobals

object RoomFactory {
    fun registerStarterRooms() {
        makeStarterRooms().forEach(ServerGlobals.roomStore::put)
    }

    private fun makeStarterRooms(): List<Room> {
        val elementGenerator = getElementNames().iterator()

        return GameMode.entries.flatMap { gameMode ->
            val configs = getConfigs(gameMode)
            val capacities = listOf(2, 3, 4)

            capacities.flatMap { capacity ->
                configs.map { Room(UUID.randomUUID(), elementGenerator.next(), it, capacity) }
            }
        }
    }

    private fun getElementNames(): List<String> {
        val inputStream =
            javaClass.getResourceAsStream("/elements.txt")
                ?: throw RuntimeException("Unable to read elements list")

        return inputStream.bufferedReader().readLines()
    }

    private fun getConfigs(mode: GameMode): List<GameSettings> =
        listOf(
            GameSettings(mode),
            GameSettings(mode, illegalAllowed = true),
            GameSettings(mode, jokerQuantity = 2, jokerValue = 2),
            GameSettings(mode, includeMoons = true, includeStars = true),
            GameSettings(mode, includeMoons = true, includeStars = true, cardReveal = true),
            GameSettings(
                mode,
                jokerQuantity = 2,
                jokerValue = 2,
                negativeJacks = true,
                cardReveal = true,
            ),
            GameSettings(mode, includeMoons = true, includeStars = true, negativeJacks = true),
            GameSettings(
                mode,
                jokerQuantity = 2,
                jokerValue = 2,
                includeMoons = true,
                includeStars = true,
                illegalAllowed = true,
            ),
        )
}
