package store

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

abstract class AbstractStoreTest<K, T : IHasId<K>> : AbstractTest() {
    abstract fun makeStore(): Store<K, T>

    abstract fun makeIdA(): K

    abstract fun makeIdB(): K

    abstract fun makeItemA(id: K = makeIdA()): T

    abstract fun makeItemB(id: K = makeIdB()): T

    @Test
    fun `PUT should overwrite existing value`() {
        val store = makeStore()

        store.put(makeItemA(makeIdA()))
        store.put(makeItemB(makeIdA()))

        store.get(makeIdA()) shouldBe makeItemB(makeIdA())
    }

    @Test
    fun `FIND should return null for non-existent key`() {
        makeStore().find(makeIdA()) shouldBe null
    }

    @Test
    fun `GET should throw an error for non-existent key`() {
        shouldThrow<Exception> { makeStore().get(makeIdA()) }
    }

    @Test
    fun `GET and FIND should retrieve the appropriate record`() {
        val store = makeStore()

        store.put(makeItemA())
        store.put(makeItemB())

        store.find(makeIdA()) shouldBe makeItemA()
        store.find(makeIdB()) shouldBe makeItemB()

        store.get(makeIdA()) shouldBe makeItemA()
        store.get(makeIdB()) shouldBe makeItemB()
    }

    @Test
    fun `Should be able to retrieve all items`() {
        val store = makeStore()
        store.getAll().shouldBeEmpty()

        store.put(makeItemA())
        store.put(makeItemB())

        store.getAll().shouldContainExactlyInAnyOrder(makeItemA(), makeItemB())
    }

    @Test
    fun `Should be able to PUT multiple items`() {
        val store = makeStore()
        store.getAll().shouldBeEmpty()

        store.putAll(makeItemA(), makeItemB())
        store.getAll().shouldContainExactlyInAnyOrder(makeItemA(), makeItemB())
    }

    @Test
    fun `Should be able to delete items`() {
        val store = makeStore()
        store.put(makeItemA())
        store.put(makeItemB())

        store.remove(makeIdB())

        store.find(makeIdA()) shouldBe makeItemA()
        store.find(makeIdB()) shouldBe null
    }

    @Test
    fun `Should be able to count items`() {
        val store = makeStore()
        store.count() shouldBe 0

        store.put(makeItemA())
        store.put(makeItemB())
        store.count() shouldBe 2

        store.remove(makeIdA())
        store.count() shouldBe 1
    }
}
