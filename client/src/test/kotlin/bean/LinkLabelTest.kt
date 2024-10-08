package bean

import com.github.alyssaburlton.swingtest.doClick
import com.github.alyssaburlton.swingtest.doHover
import com.github.alyssaburlton.swingtest.doHoverAway
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import java.awt.Color
import java.awt.Cursor
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class LinkLabelTest : AbstractTest() {
    @Test
    fun `Should style text as URL`() {
        val label = LinkLabel("https://foo.bar") {}

        label.text shouldBe "<html><u>https://foo.bar</u></html>"
        label.foreground shouldBe Color.BLUE
    }

    @Test
    fun `Should update cursor on hover, and execute callback on click`() {
        val callback = mockk<() -> Unit>(relaxed = true)
        val label = LinkLabel("test", callback)

        label.doHover()
        label.cursor shouldBe Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

        label.doHoverAway()
        label.cursor shouldBe Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)

        label.doClick()
        verify { callback() }
    }
}
