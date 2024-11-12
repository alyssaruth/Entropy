package room

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import util.makeGameSettings
import util.makeRoom

class RoomTest : AbstractTest() {
    @Test
    fun `should report correct name`() {
        makeRoom(baseName = "Mercury", index = 1).name shouldBe "Mercury 1"
        makeRoom(baseName = "Carbon", index = 5).name shouldBe "Carbon 5"
    }

    @Test
    fun `should report if room is a copy`() {
        makeRoom(baseName = "Mercury", index = 1).isCopy shouldBe false
        makeRoom(baseName = "Carbon", index = 2).isCopy shouldBe true
        makeRoom(baseName = "Oxygen", index = 5).isCopy shouldBe true
    }

    @Test
    fun `should make a copy of a room`() {
        val settings = makeGameSettings(includeMoons = true, jokerQuantity = 2)
        val room = makeRoom(baseName = "Yttrium", index = 1, settings = settings, capacity = 3)

        val copy = room.makeCopy()
        copy.id shouldNotBe room.id
        copy.name shouldBe "Yttrium 2"
        copy.settings shouldBe settings
        copy.capacity shouldBe room.capacity
    }
}
