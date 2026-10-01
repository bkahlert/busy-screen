package com.bkahlert.kommons.dom

import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.Url
import io.ktor.util.toMap
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.AddEventListenerOptions
import org.w3c.dom.COMPLETE
import org.w3c.dom.DocumentReadyState
import org.w3c.dom.Location
import org.w3c.dom.Window
import org.w3c.dom.events.Event
import kotlin.coroutines.resume

private fun CharSequence.deserialize(): Parameters =
    Parameters.build {
        split('&')
            .mapNotNull {
                it.split('=', limit = 2)
                    .takeIf { it.size == 2 }
                    ?.run { append(first(), last()) }
            }
    }

private fun Parameters.serialize() =
    toMap().toList().joinToString("&") { (key, values) ->
        values.joinToString("&") { value -> "$key=$value" }
    }

/**
 * Builds a [Parameters] instance with the given [builder] function and
 * the builder initialized with a copy of this instance.
 * @param builder specifies a function to build a map
 */
inline fun Parameters.copy(builder: ParametersBuilder.() -> Unit): Parameters =
    ParametersBuilder()
        .also { forEach { key, values -> it.appendAll(key, values) } }
        .apply(builder)
        .build()

/**
 * Contains the [Url] of this location.
 */
var Location.url: Url
    get() = Url(href)
    set(value) {
        href = value.toString()
    }

/**
 * Contains key-value pairs if they are encoded in the form:
 * `#param1=value1&param2=value2`
 */
val Url.hashParameters: Parameters
    get() = fragment.deserialize()

/**
 * Contains key-value pairs if they are encoded in the form:
 * `?param=1=value1&param2=value2`
 */
var Location.parameters: Parameters
    get() = url.parameters
    set(value) {
        value.serialize()
            .takeIf { it != search.removePrefix("?") }
            ?.also { search = "?$it" }
    }

/**
 * Contains key-value pairs if they are encoded in the form:
 * `#param1=value1&param2=value2`
 */
var Location.hashParameters: Parameters
    get() = url.hashParameters
    set(value) {
        value.serialize()
            .takeIf { it != hash.removePrefix("#") }
            ?.also { hash = "#$it" }
    }

/**
 * Contains both [parameters] and [hashParameters].
 */
val Location.allParameters: Parameters
    get() = url.parameters

/**
 * Suspends until this window has fired its `load` event; returns at once if the document is already complete.
 */
suspend fun Window.awaitLoad() {
    if (document.readyState == DocumentReadyState.COMPLETE) return
    suspendCancellableCoroutine { continuation ->
        val listener: (Event) -> Unit = { continuation.resume(Unit) }
        addEventListener("load", listener, AddEventListenerOptions(once = true))
        continuation.invokeOnCancellation { removeEventListener("load", listener) }
    }
}
