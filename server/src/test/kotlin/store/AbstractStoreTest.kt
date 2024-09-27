package store

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

abstract class AbstractStoreTest<T> {
    abstract fun makeStore(): Store<T>

    abstract fun makeItemA(): T

    abstract fun makeItemB(): T

    @Test
    fun `PUT should overwrite existing value`() {
        val store = makeStore()

        store.put("1", makeItemA())
        store.put("1", makeItemB())

        store.get("1") shouldBe makeItemB()
    }

    @Test
    fun `FIND should return null for non-existent key`() {
        makeStore().find("invalid") shouldBe null
    }

    @Test
    fun `GET should throw an error for non-existent key`() {
        shouldThrow<Exception> { makeStore().get("invalid") }
    }

    @Test
    fun `GET and FIND should retrieve the appropriate record`() {
        val store = makeStore()

        store.put("A", makeItemA())
        store.put("B", makeItemB())

        store.find("A") shouldBe makeItemA()
        store.find("B") shouldBe makeItemB()

        store.get("A") shouldBe makeItemA()
        store.get("B") shouldBe makeItemB()
    }

    @Test
    fun `Should be able to retrieve all items`() {
        val store = makeStore()
        store.getAll().shouldBeEmpty()

        store.put("A", makeItemA())
        store.put("B", makeItemB())

        store.getAll().shouldContainExactlyInAnyOrder(makeItemA(), makeItemB())
    }

    @Test
    fun `Should be able to delete items`() {
        val store = makeStore()
        store.put("A", makeItemA())
        store.put("B", makeItemB())

        store.remove("B")

        store.find("A") shouldBe makeItemA()
        store.find("B") shouldBe null
    }
}
