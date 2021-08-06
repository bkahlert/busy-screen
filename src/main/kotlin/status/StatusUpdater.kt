package status

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.url.URL
import kotlin.time.Duration

object StatusUpdater {

    private var lastStatus: Status? = null

    suspend fun poll(url: URL, refreshRate: Duration, callback: (Status?, Throwable?) -> Unit) {
        coroutineScope {
            launch {
                while (true) {
                    console.log("Fetching", url)
                    kotlin.runCatching { fetchStatus(url) }.fold(
                        {
                            console.log("Fetched", it)
                            lastStatus = it
                            callback(it, null)
                            delay(refreshRate)
                        },
                        {
                            console.error(it)
                            callback(lastStatus, it)
                            delay(refreshRate * 5)
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
