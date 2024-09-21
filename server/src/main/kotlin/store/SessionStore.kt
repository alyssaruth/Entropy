package store

import auth.Session

class SessionStore : MemoryStore<Session>() {
    override val name = "sessions"
}
