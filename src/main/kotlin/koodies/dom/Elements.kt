package koodies.dom

import koodies.text.takeUnlessBlank
import kotlinx.html.dom.append
import kotlinx.html.link
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.get

var Document.favicon: String?
    get() = querySelector("head link[rel='shortcut icon']")?.asDynamic()?.href as? String
    set(value) {
        val head = querySelector("head") ?: createElement("head").also { prepend(it) }
        head.querySelector("link[rel='shortcut icon']")?.also { head.removeChild(it) }
        if (value != null) head.append { link(value, "shortcut icon") }
    }

var Element.classNames: Set<String>
    get() = className.takeUnlessBlank()?.split(' ')?.toSet() ?: emptySet()
    set(value) {
        className = value.joinToString(" ")
    }

fun Element.removeChildren() {
    while (children.length > 0) {
        children[0]?.also { removeChild(it) }
    }
}
