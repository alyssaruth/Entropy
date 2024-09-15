package testCore

import org.skyscreamer.jsonassert.JSONAssert

infix fun String.shouldMatchJson(expected: String) {
    JSONAssert.assertEquals(expected, this, false)
}
