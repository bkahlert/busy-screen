package koodies.dom

import kotlinx.css.CSSBuilder
import kotlinx.css.body
import kotlinx.css.head
import kotlinx.html.dom.append
import kotlinx.html.head
import kotlinx.html.js.style
import kotlinx.html.link
import org.w3c.dom.Document
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLHeadElement
import org.w3c.dom.HTMLLinkElement

/**
 * [Favicon](https://en.wikipedia.org/wiki/Favicon) of this [Document].
 *
 * On set, necessary tags are created implicitly.
 */
var Document.favicon: String?
    get() = querySelector("head link[rel='shortcut icon']")?.asDynamic()?.href as? String
    set(value) {
        head().getOrCreate({ querySelector("link[rel='shortcut icon']") as? HTMLLinkElement }) {
            link(value, "shortcut icon")
        }.href = value ?: ""
    }

/**
 * Creates the [head] element if it does not already exist and returns it.
 */
fun Document.head(): HTMLHeadElement = getOrCreate({ head }) {
    createElement("head").also { prepend(it) }
}

/**
 * Appends the CSS built with the given [block] to this [head] element.
 */
fun HTMLHeadElement.appendCss(block: CSSBuilder.() -> Unit) {
    val css = CSSBuilder().apply(block).toString()
    console.info(css)
    append {
        style { +css }
    }
}

/**
 * Creates the [body] element if it does not already exist and returns it.
 */
fun Document.body(): HTMLBodyElement = getOrCreate({ body as? HTMLBodyElement }) {
    createElement("head").also { append(it) }
}
