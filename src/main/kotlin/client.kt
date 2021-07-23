import koodies.time.minutes
import kotlinx.browser.document
import kotlinx.browser.window
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
import kotlin.time.ExperimentalTime

// TODO get status
// TODO update display
// TODO put on raspy
@ExperimentalTime
@ExperimentalSerializationApi
fun main() {
    window.onload = {
        console.log("fuck")
//        window.fetch("http://192.168.168.168/status") {
//         console.log("fuck")
//        }
        document.body?.run {
            addStatus(Status(
                name = "Having Lunch",
                task = "ABC-123",
                totalTime = 25.minutes,
                passedTime = 5.minutes,
                email = "john.doe@example.com",
            ))
            addStatus(Status(
                name = "Fighting Bowser",
                task = "Level 2-1",
                totalTime = 50.minutes,
                remainingTime = 4.5.minutes,
            ))
            addStatus(Status(
                name = "Working on Dashboard",
                task = "MOMA-686",
                passedTime = 23.minutes,
                remainingTime = 10.minutes,
                email = "mail@bkahlert.com",
            ))
        }
    }
}


@ExperimentalTime
@ExperimentalSerializationApi
fun Node.addStatus(status: Status) {

    append {
        div("nes-container with-title is-centered") {
            h1("title") {
                +(status.task ?: "Status")
            }

            div("nes-balloon from-right") {
                span("nes-text is-error") { +status.name }
            }
            img("avatar", status.avatar.url, "nes-bcrikko nes-avatar is-large") {
                style = "image-rendering: pixelated;"
            }

            progress("nes-progress is-success") {
                status.totalMinutes?.also { max = it.toString() }
                status.passedMinutes?.also { value = it.toString() }
            }
            status.formattedRemainingTime?.also {
                small { +"remaining" }
                h1 { +it }
            }
        }
    }
}
