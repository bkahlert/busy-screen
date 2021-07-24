import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.url.URL

object StatusUpdater {

    private var lastStatus: Status? = null

    suspend fun poll(url: URL, callback: (Status?, Throwable?) -> Unit) {
        coroutineScope {
            launch {
                while (true) {
                    console.log("Fetching", url)
                    kotlin.runCatching { fetchStatus(url) }.fold(
                        {
                            console.log("Fetched", it)
                            lastStatus = it
                            callback(it, null)
                            delay(1000)
                        },
                        {
                            console.error(it)
                            callback(lastStatus, it)
                            delay(5000)
                        }
                    )
                }
            }
        }
    }

    private suspend fun fetchStatus(url: URL): Status = window
        .fetch(url)
        .then { if (it.ok) it.text() else error(it.statusText) }
        .then { Status.fromJson(it) }
        .await()
}
