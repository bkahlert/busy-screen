import koodies.time.minutes
import koodies.time.seconds
import kotlinx.browser.document
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class TestClient {

    @Test
    fun testSayHello() {
        val container = document.createElement("div")
        container.addStatus(Status(
            name = "busy",
            task = "ABC-123",
            totalTime = 50.minutes,
            passedTime = 37.minutes + 9.seconds,
            remainingTime = 12.minutes + 51.seconds,
            email = "john.doe@example.com",
        ))
        assertEquals("ABC-123busyremaining13m", container.textContent)
    }
} 
