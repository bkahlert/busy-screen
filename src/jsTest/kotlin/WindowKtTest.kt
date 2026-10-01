import com.bkahlert.kommons.dom.awaitLoad
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.test.runTest
import org.w3c.dom.COMPLETE
import org.w3c.dom.DocumentReadyState
import kotlin.test.Test
import kotlin.test.assertEquals

class WindowKtTest {

    @Test
    fun shouldReturnOnceTheDocumentIsComplete() = runTest {
        window.awaitLoad()

        assertEquals(DocumentReadyState.COMPLETE, document.readyState)
    }
}
