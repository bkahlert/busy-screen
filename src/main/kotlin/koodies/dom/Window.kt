package koodies.dom

import io.ktor.http.Url
import io.ktor.util.toMap
import org.w3c.dom.Location

private fun CharSequence.deserialize() =
    split('&')
        .mapNotNull {
            it.split('=', limit = 2)
                .takeIf { it.size == 2 }
                ?.run { first() to last() }
        }.toMap()

private fun Map<out Any, Any?>.serialize() =
    toList().joinToString("&") { "${it.first}=${it.second}" }

/**
 * Contains key-value pairs if they are encoded in the form:
 * `?param=1=value1&param2=value2`
 */
var Location.queryParams: Map<String, String>
    get() = Url(href).parameters.toMap().mapValues { (_, values) -> values.firstOrNull() ?: "" }
    set(value) {
        search = value.serialize()
    }

/**
 * Contains key-value pairs if they are encoded in the form:
 * `#param1=value1&param2=value2`
 */
var Location.hashParams: Map<String, String>
    get() = Url(href).fragment.deserialize()
    set(value) {
        hash = value.serialize()
    }

/**
 * Contains both [queryParams] and [hashParams].
 */
val Location.params: Map<String, String>
    get() = mapOf(*queryParams.toList().toTypedArray(), *hashParams.toList().toTypedArray())
