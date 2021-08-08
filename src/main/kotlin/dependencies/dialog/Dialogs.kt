package dependencies.dialog

import koodies.dom.appendCss
import koodies.dom.head
import kotlinext.js.require
import kotlinx.css.Position.fixed
import kotlinx.css.backgroundColor
import kotlinx.css.dialog
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import kotlinx.css.px
import kotlinx.css.rgba
import kotlinx.css.top
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

fun Document.appendDialogPolyfill() {

    require("dialog-polyfill")
    require("dialog-polyfill/dialog-polyfill.css")

    head().appendCss {
        dialog {
            position = fixed
            top = 50.pct
            transform {
                translate(0.px, (-50).pct)
            }
        }

        // native
        "dialog::backdrop" {
            backgroundColor = rgba(0, 0, 0, 0.3)
        }

        // polyfill
        "dialog + .backdrop" {
            backgroundColor = rgba(0, 0, 0, 0.3)
        }
    }

    querySelectorAll("dialog")
        .asList()
        .filterIsInstance<HTMLElement>()
        .forEach { dialogPolyfill.registerDialog(it) }
}