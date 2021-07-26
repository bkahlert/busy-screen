package koodies.dom

import kotlinx.dom.clear
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.ParentNode
import org.w3c.dom.asList

inline fun <T, E> T.getOrCreate(get: T.() -> E?, crossinline create: TagConsumer<HTMLElement>.() -> Unit): E
    where T : ParentNode,
          T : Node =
    get() ?: run {
        append { create() }
        get() ?: error("Required element was not created by specified create function.")
    }

fun <T> T.removeChildren(predicate: (Node) -> Boolean = { true }): List<Node>
    where T : ParentNode,
          T : Node =
    childNodes.asList().filter(predicate).map { removeChild(it) }

fun <T> T.removeChildren(selectors: String): List<Node>
    where T : ParentNode,
          T : Node =
    querySelectorAll(selectors).asList().map { removeChild(it) }

fun <T> T.replaceChildren(selectors: String, block: TagConsumer<HTMLElement>.() -> Unit): List<Element>
    where T : ParentNode,
          T : Node =
    querySelector(selectors)?.run {
        clear()
        (this as Node).append(block)
    } ?: emptyList()
