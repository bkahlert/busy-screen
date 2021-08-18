package koodies.dom

import kotlinx.dom.clear
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
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
    map(selectors) { removeChild(it) }

fun <T> T.replaceChildren(selectors: String, block: TagConsumer<HTMLElement>.() -> Unit)
    where T : ParentNode,
          T : Node {
    map(selectors) {
        it.clear()
        it.append(block)
    }
}


/**
 * Queries all children matching the given [selectors] and
 * returns a list containing the results of applying the given [transform] function
 * to each element in the original collection.
 */
inline fun <reified R> ParentNode.map(selectors: String, transform: (Node) -> R): List<R> =
    querySelectorAll(selectors).map(transform)

/**
 * Queries all children matching the given [selectors] and
 * returns a list containing the results of applying the given [transform] function
 * to each element of type [T] in the original collection.
 */
inline fun <reified T, reified R> ParentNode.mapInstance(selectors: String, transform: (T) -> R): List<R> =
    querySelectorAll(selectors).filterIsInstance<T>().map(transform)

/**
 * Queries all children matching the given [selectors] and
 * performs the given [action] on each element.
 */
inline fun ParentNode.forEach(selectors: String, action: (Node) -> Unit) {
    querySelectorAll(selectors).forEach(action)
}

/**
 * Queries all children matching the given [selectors] and
 * performs the given [action] on each element of type [T].
 */
inline fun <reified T> ParentNode.forEachInstance(selectors: String, action: (T) -> Unit) {
    querySelectorAll(selectors).forEachInstance(action)
}

/**
 * Queries all children matching the given [selectors] and
 * returns the first element of type [R], or `null` if the list contains no such element.
 */
inline fun <reified R> ParentNode.firstInstanceOrNull(selectors: String): R? =
    querySelectorAll(selectors).firstInstanceOrNull<R>()
