import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.w3c.dom.url.URL
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
object StatusUpdater {

    private var lastStatus = Status("???")

    suspend fun poll(url: URL, callback: (Status, Boolean) -> Unit) {
        coroutineScope {
            launch {
                while (true) {
                    console.log("Fetching", url)
                    val (status, online) = fetchStatus(url).let { status ->
                        if (status != null) {
                            lastStatus = status
                            status to true
                        } else {
                            lastStatus to false
                        }
                    }
                    callback(status, online)
                    delay(1000)
                }
            }
        }
    }

    private suspend fun fetchStatus(url: URL): Status? = window
        .fetch(url)
        .runCatching {
            await()
                .text()
                .await()
                .let { Status.fromJson(it) }
        }
        .onFailure { console.warn(it) }
        .getOrNull()
}
