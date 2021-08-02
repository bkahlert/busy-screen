import koodies.dom.body
import koodies.dom.classNames
import koodies.dom.favicon
import koodies.dom.getOrCreate
import koodies.dom.replaceChildren
import koodies.time.Now
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.dom.clear
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.dom.append
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.url.URL

// TODO use relative url
// TODO scale on small screens
// TODO put on raspy automatically
suspend fun main() {
    var ready = false
    window.onload = {
        ready = true
        null
    }

    val url = URL(
        if (window.location.port == "8080") "http://192.168.168.168:1880/status"
//        if (window.location.port == "8080") "http://192.168.16.69:1880/status"
        else "http://localhost:1880/status")

    while (!ready) {
        delay(3000)
    }

    document.body().run {
        loadingLog(url)

        StatusUpdater.poll(url) { status, error ->
            updateConnectionStatus(url, error)

            status?.run {
                document.title = name
                document.favicon = avatar.url
                root().run {
                    clear()
                    append { append() }
                }
            }
        }
    }
}

fun HTMLElement.root(): Element = getOrCreate({ querySelector("#root") }) {
    document.createElement("#root").also { prepend(it) }
}

fun HTMLElement.updateConnectionStatus(url: URL, error: Throwable? = null) {
    error?.also {
        classNames = classNames - "online"
        loadingLog(url, error)
    } ?: run { classNames = classNames + "online" }
}

private fun HTMLElement.loadingLog(url: URL, error: Throwable? = null): List<Element> =
    replaceChildren(".loading__log") {
        div("nes-text") {
            +Now.toLocaleTimeString()
            +"..."
            +" "
            a(url.href) { +url.href }
            error?.also {
                div("nes-text is-error") { +it.toString() }
                div("nes-text") { +"Retrying..." }
            }
        }
    }
