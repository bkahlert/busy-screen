import kotlinx.browser.document
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class FontsKtTest {

    @Test
    fun shouldReturnOnceTheDeclaredFontsAreLoaded() = runTest {
        document.awaitFonts()

        assertTrue(document.fonts.check("1em serif"))
    }
}
