package util

import com.github.alyssaburlton.swingtest.purgeWindows
import java.awt.Container
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import screen.ScreenCache
import settings.InMemorySettingStore
import testCore.AbstractTest

abstract class AbstractClientTest : AbstractTest() {
    @BeforeEach
    fun beforeEach() {
        ClientGlobals.achievementStore = InMemorySettingStore()
        ScreenCache.emptyCache()
    }

    @AfterEach
    fun afterEach() {
        purgeWindows()
    }
}

fun ScreenCache.put(screen: Container) {
    hmClassToScreen[screen::class.java] = screen
}
