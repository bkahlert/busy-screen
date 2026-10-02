import kotlinx.browser.document
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FontsKtTest {

    private val face = fontFace("Unloadable", "url(data:,)")

    @Test
    fun shouldLoadEveryDeclaredFontBeforeReturning() = runTest {
        document.fonts.add(face)

        document.awaitFonts()

        assertEquals("error", face.status)
    }

    @AfterTest
    fun removeFace() {
        document.fonts.delete(face)
    }
}

private fun fontFace(family: String, source: String): FontFace = js("new FontFace(family, source)").unsafeCast<FontFace>()
