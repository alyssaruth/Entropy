package store

import `object`.UserConnection

class MemoryUserConnectionStore : MemoryStore<UserConnection>() {
    override val name = "user_connections"
}
