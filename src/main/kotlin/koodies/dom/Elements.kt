package koodies.dom

import kotlinx.html.head
import kotlinx.html.link
import org.w3c.dom.Document
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLHeadElement
import org.w3c.dom.HTMLLinkElement

/**
 * Creates the [head] element if it does not already exist and returns it.
 */
fun Document.head(): HTMLHeadElement = getOrCreate({ head }) {
    createElement("head").also { prepend(it) }
}

/**
 * Creates the [body] element if it does not already exist and returns it.
 */
fun Document.body(): HTMLBodyElement = getOrCreate({ body as? HTMLBodyElement }) {
    createElement("head").also { append(it) }
}

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
