package settings

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import testCore.AbstractTest

private val STRING_PREF = Setting("fake_string", "foo")
private val INT_PREF = Setting("fake_int", 20)
private val DOUBLE_PREF = Setting("fake_double", 2.5)
private val BOOLEAN_PREF = Setting("fake_boolean", false)

class InMemorySettingStoreTest : SettingStoreTest() {
    override val implementation = InMemorySettingStore()
}

class DefaultSettingStoreTest : SettingStoreTest() {
    override val implementation = DefaultSettingStore("EntropyTesting")
}

abstract class SettingStoreTest : AbstractTest() {
    abstract val implementation: AbstractSettingStore

    @AfterEach
    fun afterEach() {
        implementation.delete(STRING_PREF)
        implementation.delete(INT_PREF)
        implementation.delete(DOUBLE_PREF)
        implementation.delete(BOOLEAN_PREF)
    }

    @Test
    fun `Getting and setting strings`() {
        implementation.find(STRING_PREF) shouldBe null
        implementation.get(STRING_PREF) shouldBe "foo"

        implementation.save(STRING_PREF, "bar")

        implementation.get(STRING_PREF) shouldBe "bar"
        implementation.get(STRING_PREF, true) shouldBe "foo"
    }

    @Test
    fun `Getting and setting booleans`() {
        implementation.find(BOOLEAN_PREF) shouldBe null
        implementation.get(BOOLEAN_PREF) shouldBe false

        implementation.save(BOOLEAN_PREF, true)

        implementation.get(BOOLEAN_PREF) shouldBe true
        implementation.get(BOOLEAN_PREF, true) shouldBe false
    }

    @Test
    fun `Getting and setting ints`() {
        implementation.find(INT_PREF) shouldBe null
        implementation.get(INT_PREF) shouldBe 20

        implementation.save(INT_PREF, 5000)

        implementation.get(INT_PREF) shouldBe 5000
        implementation.get(INT_PREF, true) shouldBe 20
    }

    @Test
    fun `Getting and setting doubles`() {
        implementation.find(DOUBLE_PREF) shouldBe null
        implementation.get(DOUBLE_PREF) shouldBe 2.5

        implementation.save(DOUBLE_PREF, 0.00017)

        implementation.get(DOUBLE_PREF) shouldBe 0.00017
        implementation.get(DOUBLE_PREF, true) shouldBe 2.5
    }

    @Test
    fun `Unsupported type`() {
        val pref = Setting("listPreference", listOf(5))
        implementation.save(pref, listOf(10))

        val t = shouldThrow<TypeCastException> { implementation.get(pref) }
        t.message shouldBe
            "Unhandled type [class java.util.Collections\$SingletonList] for preference listPreference"
    }

    @Test
    fun `Should notify listener on update & delete`() {
        val listener = mockk<SettingChangeListener>(relaxed = true)
        implementation.addChangeListener(listener)

        // Update
        implementation.save(STRING_PREF, "foobar")
        verify { listener.settingChanged(STRING_PREF, "foobar") }

        // Delete
        clearMocks(listener)

        implementation.delete(STRING_PREF)
        verify { listener.settingChanged(STRING_PREF, null) }
    }
}
