import koodies.dom.classNames
import koodies.dom.favicon
import koodies.dom.removeChildren
import koodies.time.Now
import koodies.time.minus
import koodies.time.minutes
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.progress
import kotlinx.html.small
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.serialization.ExperimentalSerializationApi
import org.w3c.dom.Node
import org.w3c.dom.url.URL
import kotlin.time.ExperimentalTime

// TODO use relative url
// TODO check if / why not working on midori
// TODO put on raspy automatically
@ExperimentalTime
@ExperimentalSerializationApi
suspend fun main() {
    window.onload = {
        document.body?.querySelector("#root")?.run {
            addStatus(Status(
                name = "Having Lunch",
                task = "ABC-123",
                duration = 25.minutes,
                timestamp = Now - 20.minutes,
                email = "john.doe@example.com",
            ))
            addStatus(Status(
                name = "Fighting Bowser",
                task = "Level 2-1",
                timestamp = Now - 45.5.minutes,
            ))
            addStatus(Status(
                name = "Working on dashboard and other stuff",
                task = "MOMA-686",
                duration = 23.minutes,
                email = "mail@bkahlert.com",
            ))
            addStatus(Status(
                name = "Developing",
            ))
        }


        document.body?.append {

        }
    }

    delay(3000)
    StatusUpdater.poll(URL("http://192.168.16.60:1880/status")) { status, online ->
        if (online) {
            document.body?.let { it.classNames += "online" }
        } else {
            document.body?.let { it.classNames -= "online" }
        }

        document.title = status.name
        document.favicon = status.avatar.url
        document.body?.querySelector("#root")?.run {
            removeChildren()
            addStatus(status)
        }
    }
}

@ExperimentalTime
@ExperimentalSerializationApi
fun Node.addStatus(status: Status) {

    append {
        div("status nes-container is-centered") {
            h1("status__task") {
                span {
                    +(status.task ?: "Status")
                }
            }

            div("status__status") {
                div("status__name nes-balloon from-right") {
                    if (status.done == true) {
                        span("nes-text") { +status.name }
                    } else {
                        span("nes-text is-error") { +status.name }
                    }
                }
                img("avatar", status.avatar.url, "status__avatar nes-bcrikko nes-avatar is-large") {
                    style = "image-rendering: pixelated;"
                }
            }

            if (status.totalSeconds != null) {
                status.passedSeconds?.also { passedSeconds ->
                    if (status.totalSeconds <= passedSeconds) {
                        progress("status__passed nes-progress is-success") {
                            max = "1"
                            value = "1"
                        }

                        div("status__remaining") { +"Done" }
                    } else {
                        progress("status__passed nes-progress is-warning") {
                            max = status.totalSeconds.toString()
                            value = passedSeconds.toString()
                        }

                        status.formattedRemaining?.also {
                            div("status__remaining") {
                                small { +"remaining" }
                                div { +it }
                            }
                        }
                    }
                }
            }
        }
    }
}
