package store

import auth.Session
import java.util.*

class SessionStore : MemoryStore<UUID, Session>() {
    override val name = "sessions"
}
