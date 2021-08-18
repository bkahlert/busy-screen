package com.bkahlert.kommons.dom

import io.ktor.http.Url
import kotlinx.css.CSSBuilder
import kotlinx.css.body
import kotlinx.css.head
import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import kotlinx.html.dom.append
import kotlinx.html.head
import kotlinx.html.js.style
import kotlinx.html.link
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLHeadElement
import org.w3c.dom.HTMLLinkElement

/**
 * [Favicon](https://en.wikipedia.org/wiki/Favicon) of this [Document].
 *
 * On set, necessary tags are created implicitly.
 */
var Document.favicon: Url?
    get() = firstInstanceOrNull<HTMLLinkElement>("head link[rel='shortcut icon']")
        ?.href
        ?.takeUnless { it.isBlank() }
        ?.let { Url(it) }
    set(value) {
        head().getOrCreate({ firstInstanceOrNull<HTMLLinkElement>("head link[rel='shortcut icon']") }) {
            link(value?.toString() ?: "", "shortcut icon")
        }.href = value?.toString() ?: ""
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

/**
 * Adds the given [cssClass] if this element does not have the given CSS class style in its 'class' attribute
 * and removes it otherwise.
 */
fun Element.toggleClass(cssClass: String): Boolean =
    if (hasClass(cssClass)) removeClass(cssClass)
    else addClass(cssClass)
