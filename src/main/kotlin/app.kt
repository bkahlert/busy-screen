import com.bkahlert.kommons.dom.body
import com.bkahlert.kommons.dom.replaceChildren
import com.bkahlert.kommons.dom.url
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.seconds
import dependencies.appendNEScss
import dependencies.appendPressStart2P
import dependencies.dialog.appendDialogPolyfill
import io.ktor.http.Parameters
import io.ktor.http.Url
import kotlinext.js.require
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.dom.addClass
import kotlinx.html.a
import kotlinx.html.div
import org.w3c.dom.HTMLElement
import status.Updater

suspend fun main() {
    val (address, refreshRate) = initParameters(
        defaultAddress = if (window.location.url.port == 8080) {
            Url("http://192.168.168.168:1880")
        } else {
            window.location.url.copy(specifiedPort = 1880, parameters = Parameters.Empty, fragment = "")
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
    require("./styles/base.css")
    require("./styles/animations.css")
    require("./styles/loading.css")
    require("./styles/nearby.css")
    require("./styles/status.css")
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
            +Now.toLocaleTimeString()
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
