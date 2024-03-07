import com.bkahlert.kommons.dom.body
import com.bkahlert.kommons.dom.replaceChildren
import com.bkahlert.kommons.dom.url
import com.bkahlert.kommons.time.Now
import dependencies.appendNEScss
import dependencies.appendPressStart2P
import dependencies.dialog.appendDialogPolyfill
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.datetime.toJSDate
import kotlinx.dom.addClass
import kotlinx.html.a
import kotlinx.html.div
import org.w3c.dom.HTMLElement
import status.Updater
import kotlin.time.Duration.Companion.seconds

suspend fun main() {
    val (address, refreshRate) = initParameters(
        defaultAddress = if (window.location.url.port == 8080) {
            Url("http://busy-screen.local:1880")
        } else {
            URLBuilder(window.location.url).apply {
                port = 1880
                parameters.clear()
                fragment = ""
            }.build()
        },
        defaultRefreshRate = 1.seconds,
    ).also {
        loadAssets()
        waitUntilReady()
    }

    val body = document.body()

    body.loadingLog(address)

    Updater(address, refreshRate, body) { error ->
        body.updateConnectionStatus(address, error)
    }.start()
}


private fun loadAssets() {
    document.appendPressStart2P()
    document.appendNEScss()
    document.appendDialogPolyfill()
}

private suspend fun waitUntilReady() {
    var ready = false
    window.onload = {
        ready = true
        document.documentElement?.addClass("ready")
        null
    }

    while (!ready) delay(3000)
}

fun HTMLElement.updateConnectionStatus(url: Url, error: Throwable? = null) {
    error?.also {
        loadingLog(url, it)
    }
}

private fun HTMLElement.loadingLog(url: Url, error: Throwable? = null) {
    replaceChildren(".loading__log") {
        div("nes-text") {
            +Now.toJSDate().toLocaleTimeString()
            +"..."
            +" "
            a(url.toString()) { +url.toString() }
            error?.also {
                div("nes-text is-error") { +it.toString() }
                div("nes-text") { +"Retrying..." }
            }
        }
    }
}
