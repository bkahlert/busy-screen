package dependencies

import com.bkahlert.kommons.dom.appendCss
import com.bkahlert.kommons.dom.head
import kotlinext.js.require
import kotlinx.css.Display.none
import kotlinx.css.TextAlign.right
import kotlinx.css.display
import kotlinx.css.fontFamily
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.textAlign
import kotlinx.css.vh
import kotlinx.css.vw
import kotlinx.css.width
import org.w3c.dom.Document

fun Document.appendNEScss() {
    require("nes.css/css/nes.css")

    head().appendCss {
        ".dialog-menu" {
            textAlign = right
        }

        ".nes-balloon" {
            padding(2.vh, 3.vw)

        }

        ".nes-dialog" {
            padding(2.vh, 2.vw)
        }

        "select option" {
            fontFamily = "monospace, monospace"
        }

        // extra small devices (phones, 600px and down)
        media("only screen and(max - width: 600px)") {
            ".nes-balloon" {
                width = 100.pct
                margin(0.px)
            }

            ".nes - balloon.from - left::before" { display = none }
            ".nes - balloon.from - left::after" { display = none }
        }
    }
}
