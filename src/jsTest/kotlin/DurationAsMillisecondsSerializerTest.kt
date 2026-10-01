import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationAsMillisecondsSerializerTest {

    @Test
    fun shouldDecodeMilliseconds() {
        val duration = Json.decodeFromString(DurationAsMillisecondsSerializer, "120000")

        assertEquals(expected = 2.minutes, actual = duration)
    }

    @Test
    fun shouldEncodeWholeMilliseconds() {
        val json = Json.encodeToString(DurationAsMillisecondsSerializer, 1.5.seconds)

        assertEquals("1500", json)
    }
}
