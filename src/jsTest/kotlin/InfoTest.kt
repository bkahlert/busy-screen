import status.Info
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InfoTest {

    @Test
    fun shouldDeserializeWithoutNearby() {
        val info = Info.fromJson(
            """
            {
              "addresses": ["http://192.168.17.106:1880"],
              "status": { "name": "busy" },
              "hostname": "busy-screen",
              "username": "busy-screen"
            }
            """.trimIndent()
        )

        assertEquals("busy-screen", info.hostname)
        assertNull(info.nearby)
    }
}
