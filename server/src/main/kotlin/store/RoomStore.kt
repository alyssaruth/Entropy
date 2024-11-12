package store

import java.util.*
import room.Room
import util.ServerGlobals
import utils.CoreGlobals.logger

class RoomStore : MemoryStore<UUID, Room>() {
    override val name = "rooms"

    fun findForName(name: String) = getAll().find { it.name == name }

    fun reset() {
        val copies = getAll().filter { it.isCopy }
        copies.forEach { remove(it.id) }

        logger.info("roomsRemoved", "Removed ${copies.size} room copies")
    }

    fun addCopy(room: Room) {
        val copy = room.makeCopy()

        if (findForName(copy.name) == null) {
            logger.info("newRoom", "New room generated: ${copy.name}")
            put(copy)
            ServerGlobals.lobbyService.lobbyChanged()
        } else {
            logger.info("roomExists", "Room ${copy.name} already exists")
        }
    }
}
