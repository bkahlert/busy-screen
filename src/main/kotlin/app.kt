import dependencies.appendNEScss
import dependencies.appendPressStart2P
import dependencies.dialog.appendDialogPolyfill
import io.ktor.http.Parameters
import io.ktor.http.Url
import koodies.Info
import koodies.dom.allParameters
import koodies.dom.body
import koodies.dom.favicon
import koodies.dom.hashParameters
import koodies.dom.parameters
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
    window.location.allParameters.also { params ->
        Parameters.build {
            append("location", params["location"] ?: "http://localhost:1880")
            append("refresh-rate", params["refresh-rate"] ?: "PT1S")
        }
        window.location.hashParameters = Parameters.Empty
        window.location.parameters = params
    }

    val url = Url(window.location.allParameters["location"] ?: error("location missing"))
    val refreshRate = Duration.parse(window.location.allParameters["refresh-rate"] ?: error("refresh-rate missing"))

    document.appendPressStart2P()
    document.appendNEScss()
    document.appendDialogPolyfill()
    require("./styles/base.css")
    require("./styles/animations.css")
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

        Info.resolve(url).update(this)

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
