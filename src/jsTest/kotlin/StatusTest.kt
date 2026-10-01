import com.bkahlert.kommons.minus
import com.bkahlert.kommons.plus
import io.ktor.http.Url
import status.Status
import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class StatusTest {

    @Test
    fun shouldDeserialize() {
        val timestamp = Date() - 37.minutes + 9.seconds

        val status = Status.fromJson(
            """
            {
              name: "busy",
              task: "ABC-123",
              duration: 3000000,
              timestamp: "${timestamp.toISOString()}",
              email: "john.doe@example.com",
              on: {
                finish: {}
              }
            }
        """.trimIndent()
        )

        assertEquals(
            Status(
                name = "busy",
                task = "ABC-123",
                duration = 50.minutes,
                timestamp = timestamp,
                email = "john.doe@example.com",
            ), status
        )
    }

    @Test
    fun shouldDeserializeMinimal() {
        val status = Status.fromJson(
            """
            {
              name: "busy"
            }
        """.trimIndent()
        )

        assertEquals(Status(name = "busy"), status)
    }

    @Test
    fun shouldDifferByAvatar() {
        val status = Status(name = "busy", avatar = Url("https://example.com/avatar.png"))

        assertNotEquals(Status(name = "busy"), status)
    }
}
