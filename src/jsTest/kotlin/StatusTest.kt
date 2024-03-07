import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.minus
import com.bkahlert.kommons.time.plus
import kotlinx.datetime.toJSDate
import status.Status
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class StatusTest {

    @Test
    fun shouldDeserialize() {
        val timestamp = Now.toJSDate() - 37.minutes + 9.seconds

        val status = Status.fromJson(
            """
            {
              name: "busy",
              task: "ABC-123",
              duration: "PT50M",
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
}
