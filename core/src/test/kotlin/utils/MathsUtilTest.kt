package utils

import formatAsFileSize
import getPercentage
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class MathsUtilTest : AbstractTest() {
    @Test
    fun `Should get percentages to 1 dp`() {
        val total = 10000.0

        getPercentage(1, total) shouldBe 0.0
        getPercentage(5, total) shouldBe 0.1
        getPercentage(55, total) shouldBe 0.6
        getPercentage(673, total) shouldBe 6.7
        getPercentage(678, total) shouldBe 6.8
        getPercentage(9994, total) shouldBe 99.9
        getPercentage(9999, total) shouldBe 100.0
        getPercentage(10000, total) shouldBe 100.0
    }

    @Test
    fun `Percentages should still work for large numbers`() {
        val total = 123456788.0

        getPercentage(123456788, total) shouldBe 100.0
        getPercentage(61728394, total) shouldBe 50.0
    }

    @Test
    fun `Percentage for thirds`() {
        val total = 3.0

        getPercentage(0, total) shouldBe 0.0
        getPercentage(1, total) shouldBe 33.3
        getPercentage(2, total) shouldBe 66.7
    }

    @Test
    fun `Format as file size`() {
        0L.formatAsFileSize() shouldBe "0 B"
        568L.formatAsFileSize() shouldBe "568 B"
        1024L.formatAsFileSize() shouldBe "1.0 KB"
        34304L.formatAsFileSize() shouldBe "33.5 KB"
        5242880L.formatAsFileSize() shouldBe "5.0 MB"
        107374182400L.formatAsFileSize() shouldBe "100.0 GB"
    }
}
