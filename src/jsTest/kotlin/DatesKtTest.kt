import com.bkahlert.kommons.minus
import com.bkahlert.kommons.plus
import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class DatesKtTest {

    @Test
    fun shouldSubtractDates() {
        val duration = Date(4_500) - Date(1_000)

        assertEquals(3.5.seconds, duration)
    }

    @Test
    fun shouldAddDuration() {
        val date = Date(1_000) + 3.5.seconds

        assertEquals(4_500.0, date.getTime())
    }

    @Test
    fun shouldSubtractDuration() {
        val date = Date(4_500) - 3.5.seconds

        assertEquals(1_000.0, date.getTime())
    }
}
