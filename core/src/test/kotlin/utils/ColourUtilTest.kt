package utils

import io.kotest.matchers.shouldBe
import java.awt.Color
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class ColourUtilTest : AbstractTest() {
    @Test
    fun `Should produce correct colour hexes`() {
        Color.black.toHexCode() shouldBe "#000000FF"
        Color.white.toHexCode() shouldBe "#FFFFFFFF"
        Color.red.toHexCode() shouldBe "#FF0000FF"
        COLOUR_SUIT_PURPLE.toHexCode() shouldBe "#AA00FFFF"
    }
}
