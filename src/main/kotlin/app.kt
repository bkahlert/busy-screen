import dependencies.appendNEScss
import dependencies.appendPressStart2P
import dependencies.dialog.appendDialogPolyfill
import io.ktor.http.Url
import koodies.dom.body
import koodies.dom.favicon
import koodies.dom.replaceChildren
import koodies.dom.searchParams
import koodies.parse
import koodies.time.Now
import kotlinext.js.require
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.a
import kotlinx.html.div
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import status.StatusUpdater
import kotlin.time.Duration

// TODO use relative url
// TODO put on raspy automatically
suspend fun main() {
    document.appendPressStart2P()
    document.appendNEScss()
    document.appendDialogPolyfill()
    require("./styles/base.css")
    require("./styles/loading.css")
    require("./styles/status.css")

    var ready = false
    window.onload = {
        ready = true
        document.documentElement?.addClass("ready")
        null
    }

    if (!window.location.searchParams.has("location")) {
        window.location.href += "?location=http://localhost:1880/status"
    }

    val url = Url(window.location.searchParams.get("location") ?: error("location missing"))

    if (!window.location.searchParams.has("refresh-rate")) {
        window.location.href += "&refresh-rate=PT1S"
    }

    val refreshRate = Duration.parse(window.location.searchParams.get("refresh-rate") ?: error("location missing"))

    while (!ready) {
        delay(3000)
    }

    document.body().run {
        loadingLog(url)

        StatusUpdater(url, refreshRate) { status, error ->
            updateConnectionStatus(url, error)

            if (status != null) {
                removeClass("init")
                document.title = status.name
                document.favicon = status.avatar.url
                querySelectorAll(".status").asList().mapNotNull { it as? HTMLElement }.forEach {
                    status.update(it)
                }
            }
        }.poll()
    }
}

fun HTMLElement.updateConnectionStatus(url: Url, error: Throwable? = null) {
    error?.also {
        removeClass("online")
        addClass("offline")
        loadingLog(url, error)
    } ?: run {
        removeClass("offline")
        addClass("online")
    }
}

private fun HTMLElement.loadingLog(url: Url, error: Throwable? = null): List<Element> =
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
