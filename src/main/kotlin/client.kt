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
import org.w3c.dom.Node

fun main() {
    window.onload = { document.body?.sayHello() }
}

fun Node.sayHello() {
    append {
        div("nes-container with-title is-centered") {
            h1("title") { +"""{{#task}}{{task}}{{/task}}{{^task}}Status{{/task}}""" }

            div("nes-balloon from-right") {
                span("nes-text is-error") { +"""{{status}}""" }
            }
            img("avatar", classes = "nes-bcrikko nes-avatar is-large") {
                attributes["data-replace"] = "src=gravatar-image-url"
                src = "https://www.gravatar.com/avatar?s=16"
                style = "image-rendering: pixelated;"
            }

            progress("nes-progress is-success") {
                attributes["data-replace"] = "value=passed-time,max=initial-time"
                max = "{{initial-time}}"
                value = "67"
            }
            small {
                span {
                    attributes["data-replace"] = "text=remaining-time--words"
                }
                +"""remaining"""
            }
            h1 { +"""{{remaining-time}}""" }
        }
    }
}

const val DEFAULT_GRAVATAR = "data:image/gif;base64,R0lGODlhEAAQAKIHAPDw8OtPI" +
    "h/gPi3iSuvr6yQkJCvlSv///yH/C1hNUCBEYXRhWE1QPD94cGFja2V0IGJlZ2luPSLvu78iI" +
    "GlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZ" +
    "G9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNi4wLWMwMDIgNzkuMTY0N" +
    "DYwLCAyMDIwLzA1LzEyLTE2OjA0OjE3ICAgICAgICAiPiA8cmRmOlJERiB4bWxuczpyZGY9I" +
    "mh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPiA8cmRmOkRlc" +
    "2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvb" +
    "S94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuM" +
    "C9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94Y" +
    "XAvMS4wLyIgeG1wTU06T3JpZ2luYWxEb2N1bWVudElEPSJ4bXAuZGlkOjkyN2Y4YWI0LWQxN" +
    "zgtNDVhNy1iMGMwLTc3MTZlNzY2NDBmYSIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDoyN" +
    "0Y0MTdGRUNENDQxMUVBQTUxMkM0MTFENkZCQjUzNCIgeG1wTU06SW5zdGFuY2VJRD0ieG1wL" +
    "mlpZDoyN0Y0MTdGRENENDQxMUVBQTUxMkM0MTFENkZCQjUzNCIgeG1wOkNyZWF0b3JUb29sP" +
    "SJBZG9iZSBQaG90b3Nob3AgMjEuMiAoTWFjaW50b3NoKSI+IDx4bXBNTTpEZXJpdmVkRnJvb" +
    "SBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjkyN2Y4YWI0LWQxNzgtNDVhNy1iMGMwLTc3M" +
    "TZlNzY2NDBmYSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo5MjdmOGFiNC1kMTc4LTQ1Y" +
    "TctYjBjMC03NzE2ZTc2NjQwZmEiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gP" +
    "C94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4B//79/Pv6+fj39vX08/Lx8O/u7ezr6" +
    "uno5+bl5OPi4eDf3t3c29rZ2NfW1dTT0tHQz87NzMvKycjHxsXEw8LBwL++vby7urm4t7a1t" +
    "LOysbCvrq2sq6qpqKempaSjoqGgn56dnJuamZiXlpWUk5KRkI+OjYyLiomIh4aFhIOCgYB/f" +
    "n18e3p5eHd2dXRzcnFwb25tbGtqaWhnZmVkY2JhYF9eXVxbWllYV1ZVVFNSUVBPTk1MS0pJS" +
    "EdGRURDQkFAPz49PDs6OTg3NjU0MzIxMC8uLSwrKikoJyYlJCMiISAfHh0cGxoZGBcWFRQTE" +
    "hEQDw4NDAsKCQgHBgUEAwIBAAAh+QQBAAAHACwAAAAAEAAQAAADSHi6B2IsjnkKGXGNIqal2" +
    "SCMZBeEJaGakjiq6nC2AwATc6YMcJDrjtGvFRQAfAcIY+J6AQbKSJFUA06rusPECABkF76hL" +
    "jxMAAA7"
