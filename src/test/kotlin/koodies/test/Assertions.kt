package koodies.test

import org.w3c.dom.Element
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

inline infix fun <reified T> T.shouldBe(expected: T): T =
    also { assertEquals(expected, this) }

inline infix fun <reified T : Element> T.shouldBe(expected: String): T =
    also { assertEquals(expected.lines().joinToString("") { it.trim() }, innerHTML) }

inline fun <reified T : Element> T.shouldContain(vararg expected: String): T =
    also {
        expected.forEach {
            assertContains(innerHTML, it)
        }
    }

inline fun <reified T : Element> T.shouldNotContain(vararg expected: String): T =
    also {
        expected.forEach {
            assertFalse("$this does not contain $it") { !innerHTML.contains(it) }
        }
    }
