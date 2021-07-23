import koodies.time.minutes
import koodies.time.seconds
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class StatusTest {

    @Test
    fun shouldDeserialize() {
        val status = Status.fromJson("""
            {
              name: "busy",
              task: "ABC-123",
              "total-time": "PT50M",
              "passed-time": "PT37M9S",
              "remaining-time": "771000",
              email: "john.doe@example.com"
            }
        """.trimIndent())
        
        assertEquals(Status(
            name = "busy",
            task = "ABC-123",
            totalTime = 50.minutes,
            passedTime = 37.minutes + 9.seconds,
            remainingTime = 12.minutes + 51.seconds,
            email = "john.doe@example.com",
        ), status)
    }
} 
