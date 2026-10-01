import com.bkahlert.kommons.dom.awaitLoad
import com.bkahlert.kommons.dom.body
import com.bkahlert.kommons.dom.replaceChildren
import com.bkahlert.kommons.dom.url
import dependencies.dialog.registerDialogs
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.html.a
import kotlinx.html.div
import org.w3c.dom.HTMLElement
import status.Updater
import kotlin.js.Date
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
    )

    document.registerDialogs()
    window.awaitLoad()
    document.documentElement?.addClass("ready")

    val body = document.body()

    body.loadingLog(address)

    Updater(address, refreshRate, body) { error ->
        body.updateConnectionStatus(address, error)
    }.start()
}

fun HTMLElement.updateConnectionStatus(url: Url, error: Throwable? = null) {
    error?.also {
        loadingLog(url, it)
    }
}

private fun HTMLElement.loadingLog(url: Url, error: Throwable? = null) {
    replaceChildren(".loading__log") {
        div("nes-text") {
            +Date().toLocaleTimeString()
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
