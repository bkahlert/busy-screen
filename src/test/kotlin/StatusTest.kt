import koodies.time.Now
import koodies.time.minus
import koodies.time.minutes
import koodies.time.plus
import koodies.time.seconds
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusTest {

    @Test
    fun shouldDeserialize() {
        val timestamp = Now - 37.minutes + 9.seconds

        val status = Status.fromJson("""
            {
              name: "busy",
              task: "ABC-123",
              duration: "PT50M",
              timestamp: "${timestamp.toISOString()}",
              email: "john.doe@example.com"
            }
        """.trimIndent())

        assertEquals(Status(
            name = "busy",
            task = "ABC-123",
            duration = 50.minutes,
            timestamp = timestamp,
            email = "john.doe@example.com",
        ), status)
    }

    @Test
    fun shouldDeserializeMinimal() {
        val status = Status.fromJson("""
            {
              name: "busy"
            }
        """.trimIndent())

        assertEquals(Status(name = "busy"), status)
    }
}
