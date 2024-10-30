package store

interface Store<K, T : IHasId<K>> {
    val name: String

    fun put(value: T)

    fun find(key: K): T?

    fun get(key: K): T

    fun getAll(): List<T>

    fun remove(key: K)

    fun putAll(vararg items: T)

    fun update(key: K, updater: (T) -> T) {
        val current = get(key)
        put(updater(current))
    }
}

abstract class MemoryStore<K, T : IHasId<K>> : Store<K, T> {
    private val map = mutableMapOf<K, T>()

    override fun put(value: T) {
        map[value.id] = value
    }

    override fun find(key: K): T? = map[key]

    override fun get(key: K): T = map.getValue(key)

    override fun getAll(): List<T> = map.values.toList()

    override fun remove(key: K) {
        map.remove(key)
    }

    override fun putAll(vararg items: T) {
        items.forEach(::put)
    }
}
