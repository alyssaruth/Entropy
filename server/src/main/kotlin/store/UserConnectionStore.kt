package store

import auth.UserConnection

interface UserConnectionStore : Store<UserConnection> {
    fun findForName(name: String): UserConnection?

    fun getAllForNames(names: Set<String>): List<UserConnection>
}

class MemoryUserConnectionStore : MemoryStore<UserConnection>(), UserConnectionStore {
    override val name = "user_connections"

    override fun findForName(name: String) = getAll().find { it.name == name }

    override fun getAllForNames(names: Set<String>) = getAll().filter { names.contains(it.name) }
}
