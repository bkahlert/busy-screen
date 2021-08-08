import dependencies.appendNEScss
import dependencies.appendPressStart2P
import dependencies.dialog.appendDialogPolyfill
import io.ktor.http.Url
import koodies.Addresses
import koodies.dom.body
import koodies.dom.favicon
import koodies.dom.hashParams
import koodies.dom.params
import koodies.dom.queryParams
import koodies.dom.replaceChildren
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
    window.location.params.toMutableMap().also { params ->
        val hashParams = window.location.hashParams.toMutableMap()
        if (!params.containsKey("location")) {
            params["location"] = "http://localhost:1880"
        }
        hashParams.remove("location")
        if (!params.containsKey("refresh-rate")) {
            params["refresh-rate"] = "PT1S"
        }
        hashParams.remove("refresh-rate")
        window.location.hashParams = hashParams
        window.location.queryParams = params
    }

    var url = Url(window.location.params["location"] ?: error("location missing"))
    val refreshRate = Duration.parse(window.location.params["refresh-rate"] ?: error("refresh-rate missing"))

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

    while (!ready) delay(3000)

    document.body().run {
        loadingLog(url)

        val addresses = Addresses.resolve(url)
        console.warn(addresses.addresses)

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
        }.start()
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
