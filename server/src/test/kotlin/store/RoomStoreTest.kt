package store

import game.GameMode
import game.GameSettings
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.UUID
import org.junit.jupiter.api.Test
import room.Room

class RoomStoreTest : AbstractStoreTest<UUID, Room>() {
    override fun makeStore() = RoomStore()

    override fun makeIdA(): UUID = UUID.fromString("e115f1ce-0021-4653-9888-ea330b07f3a8")

    override fun makeIdB(): UUID = UUID.fromString("7a10f6a0-03bc-4e95-a4cb-44b92582f016")

    override fun makeItemA(id: UUID) = Room(id, "Mercury", GameSettings(GameMode.Entropy), 2)

    override fun makeItemB(id: UUID) = Room(id, "Oxygen", GameSettings(GameMode.Vectropy), 3)

    @Test
    fun `Should be able to find a room for name`() {
        val store = makeStore()
        val roomA = makeItemA(UUID.randomUUID())
        val roomB = makeItemB(UUID.randomUUID())
        store.putAll(roomA, roomB)

        store.findForName("Mercury 1") shouldBe roomA
        store.findForName("Oxygen 1") shouldBe roomB
        store.findForName("Mercury 2") shouldBe null
    }

    @Test
    fun `Reset should remove all room copies`() {
        val store = makeStore()

        val room = makeItemA(UUID.randomUUID())
        val roomCopy = room.makeCopy()
        val roomCopy2 = roomCopy.makeCopy()

        store.putAll(room, roomCopy, roomCopy2)
        store.reset()

        store.getAll().shouldContainExactly(room)
    }

    @Test
    fun `Should add a copy`() {
        val store = makeStore()

        val room = makeItemA(UUID.randomUUID())
        store.put(room)
        store.addCopy(room)

        val rooms = store.getAll()
        rooms.shouldHaveSize(2)
        rooms.map { it.name }.shouldContainExactlyInAnyOrder("Mercury 1", "Mercury 2")
    }

    @Test
    fun `Should not add a copy if it would be redundant`() {
        val store = makeStore()

        val room = makeItemA(UUID.randomUUID())
        store.put(room)
        store.addCopy(room)
        store.getAll().shouldHaveSize(2)

        store.addCopy(room)
        store.getAll().shouldHaveSize(2)
        verifyLog("roomExists")
    }
}
