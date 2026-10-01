import com.bkahlert.kommons.serialization.DateSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DateSerializerTest {

    @Test
    fun shouldDecodeIsoStrings() {
        val date = Json.decodeFromString(DateSerializer(), "\"2021-08-03T05:52:10.104Z\"")

        assertEquals(1627969930104.0, date.getTime())
    }

    @Test
    fun shouldRejectStringsThatAreNoDate() {
        assertFailsWith<SerializationException> {
            Json.decodeFromString(DateSerializer(), "\"yesterday\"")
        }
    }
}
