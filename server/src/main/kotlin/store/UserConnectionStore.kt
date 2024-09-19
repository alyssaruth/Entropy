package store

import auth.UserConnection

class MemoryUserConnectionStore : MemoryStore<UserConnection>() {
    override val name = "user_connections"
}
