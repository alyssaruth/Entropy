package store

interface Store<T> {
    val name: String

    fun put(key: String, value: T)
    fun find(key: String): T?
    fun get(key: String): T
}

abstract class InMemoryStore<T> : Store<T> {
    private val map = mutableMapOf<String, T>()

    override fun put(key: String, value: T) {
        map[key] = value
    }

    override fun find(key: String): T? = map[key]

    override fun get(key: String): T = map.getValue(key)
}