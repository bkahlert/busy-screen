package status

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.Url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.fullPath
import koodies.dom.forEachInstance
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLElement
import status.Info.Companion.infoEndpoint
import kotlin.time.Duration

class Updater(
    private val url: Url,
    private val refreshRate: Duration,
    private val element: HTMLBodyElement,
    private val callback: (Throwable?) -> Unit,
) {

    private var lastInfo: Info? = null
    private var lastError: Throwable? = null

    suspend fun start() {
        while (true) {
            coroutineScope {
                val info = Info.poll(url) {
                    lastError = it
                    callback(it)
                }
                lastInfo = info.merge(lastInfo)
                lastError = null
                info.update(element)
                listenForUpdates()
            }
        }
    }

    private suspend fun listenForUpdates(): HttpClient {
        val client = HttpClient(Js) {
            install(WebSockets)
        }
        val endpoint = url.infoEndpoint
        console.info("Connecting to $endpoint")
        client.webSocket(host = endpoint.host, port = endpoint.port, path = endpoint.fullPath) {
            console.info("Connected to $endpoint")
            val messageOutputRoutine = launch { receivingUpdates() }
            val refreshDisplayRoutine = launch { refreshDisplay() }

            messageOutputRoutine.join()
            console.log("Stopped listening")
            refreshDisplayRoutine.cancelAndJoin()
            console.log("Stopped refreshing")
        }
        client.close()
        console.log("Connection closed")
        return client
    }

    private suspend fun DefaultClientWebSocketSession.receivingUpdates() {
        try {
            console.info("Listening for updates")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val text = frame.readText()
                console.log("Text received", text)
                val info = Info.fromJson(text)
                lastInfo = info.merge(lastInfo)
                lastError = null
                info.update(element)
            }
        } catch (error: Throwable) {
            console.error("Error while status updates", error)
            lastError = error
            Info.update(element, error)
            callback(lastError)
        }
    }

    private suspend fun refreshDisplay() {
        console.log("Refreshing display")
        while (lastError == null) {
            lastInfo?.status?.also { status ->
                element.forEachInstance<HTMLElement>(".status") { status.update(it) }
            }
            console.log("Refreshed display")
            delay(refreshRate)
        }
    }
}
