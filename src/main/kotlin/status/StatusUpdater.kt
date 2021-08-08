package status

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration

class StatusUpdater(
    url: Url,
    private val refreshRate: Duration,
    private val callback: (Status?, Throwable?) -> Unit,
) {
    private val url = URLBuilder(url).apply { path("status") }.build()

    private var lastStatus: Status? = null
    private var lastError: Throwable? = null

    suspend fun start() {
        while (true) {
            coroutineScope {
                pollForStatusUpdate()
                listenForStatusUpdates()
            }
        }
    }

    private fun CoroutineScope.pollForStatusUpdate() = launch {
        while (true) {
            console.log("Fetching", url.toString())
            kotlin.runCatching { fetchStatus(url) }.fold(
                {
                    console.log("Fetched", it)
                    lastStatus = it
                    lastError = null
                    callback(it, null)
                    return@launch
                },
                {
                    lastError = it
                    console.error("Fetch failed", it)
                    callback(lastStatus, it)
                    delay(refreshRate * 5)
                }
            )
        }
    }

    private suspend fun fetchStatus(url: Url): Status = window
        .fetch(url)
        .then { if (it.ok) it.text() else error(it.statusText) }
        .then { Status.fromJson(it) }
        .await()

    private suspend fun listenForStatusUpdates(): HttpClient {
        val client = HttpClient(Js) {
            install(WebSockets)
        }
        console.info("Connecting", url.toString())
        client.webSocket(method = HttpMethod.Get, host = url.host, port = url.port, path = url.encodedPath) {
            val messageOutputRoutine = launch { receivingStatusUpdates() }
            val refreshDisplayRoutine = launch { refreshDisplay() }

            messageOutputRoutine.join()
            console.log("Joined receiving status updates")
            refreshDisplayRoutine.cancelAndJoin()
            console.log("Joined refresh display")

        }
        client.close()
        console.log("Connection closed")
        return client
    }

    private suspend fun DefaultClientWebSocketSession.receivingStatusUpdates() {
        try {
            console.info("Listening for status updates")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val text = frame.readText()
                console.log("Text received", text)
                val status = Status.fromJson(text)
                lastStatus = status
                lastError = null
                callback(lastStatus, lastError)
            }
        } catch (e: Throwable) {
            console.error("Error while status updates", e)
            lastError = e
            callback(lastStatus, lastError)
        }
    }

    private suspend fun refreshDisplay() {
        console.log("Refreshing display")
        while (lastError == null) {
            callback(lastStatus, lastError)
            console.log("Refreshed display")
            delay(refreshRate)
        }
    }
}
