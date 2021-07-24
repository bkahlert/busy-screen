import koodies.time.Now
import koodies.time.minus
import koodies.time.minutes
import koodies.time.plus
import koodies.time.seconds
import kotlinx.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals

class TestClient {

    @Test
    fun testSayHello() {
        val container = document.createElement("div")
        Status(
            name = "busy",
            task = "ABC-123",
            duration = 50.minutes,
            timestamp = Now - 37.minutes + 9.seconds,
            email = "john.doe@example.com",
        ).appendTo(container)
        assertEquals("ABC-123busyremaining13m", container.textContent)
    }
} 
