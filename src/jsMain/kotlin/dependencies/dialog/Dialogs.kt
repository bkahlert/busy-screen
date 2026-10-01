package dependencies.dialog

import com.bkahlert.kommons.dom.forEachInstance
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement

fun Document.registerDialogs() {
    forEachInstance<HTMLElement>("dialog") { dialogPolyfill.registerDialog(it) }
}
