package status

import io.ktor.http.Url
import koodies.dom.forEach
import koodies.dom.forEachInstance
import koodies.serialization.DateSerializer
import koodies.serialization.DurationSerializer
import koodies.serialization.UrlSerializer
import koodies.time.Now
import koodies.time.minus
import koodies.time.minutes
import koodies.time.plus
import koodies.time.seconds
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLProgressElement
import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.DurationUnit.MINUTES
import kotlin.time.DurationUnit.SECONDS

@Serializable
data class Status(
    /**
     * Name of the status, e.g. `busy`.
     */
    val name: String,
    /**
     * Optional task currently working on, e.g. `ABC-1234`.
     */
    val task: String? = null,
    /**
     * Optional total time the status is valid.
     */
    @Serializable(DurationSerializer::class)
    val duration: Duration? = null,
    /**
     * Optional time that already passed.
     */
    @Serializable(DateSerializer::class)
    val timestamp: Date? = null,
    /**
     * Optional eMail address to get the [Gravatar] for.
     */
    val email: String? = null,
    /**
     * Optional URL to load the avatar from.
     */
    @Serializable(UrlSerializer::class)
    val avatar: Url? = null,
) {
    companion object {
        private val format = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        fun fromJson(json: String): Status = format.decodeFromString(json)
    }

    val passed: Duration? get() = timestamp?.let { Now - it }

    val remaining: Duration?
        get() = if (duration != null && timestamp != null) {
            timestamp + duration - Now
        } else {
            null
        }

    val done: Boolean? get() = if (duration != null) passed?.let { it >= duration } else null

    val image: Url get() = avatar ?: Gravatar(email).url

    fun update(element: HTMLElement): HTMLElement {
        when (done) {
            true -> {
                element.removeClass("status--busy")
                element.addClass("status--done")
            }
            false -> {
                element.addClass("status--busy")
                element.removeClass("status--done")
            }
            else -> {
                element.removeClass("status--busy")
                element.removeClass("status--done")
            }
        }

        element.forEach(".status__task") {
            val text = task ?: "Status"
            if (it.textContent != text) it.textContent = text
        }

        element.forEachInstance<HTMLImageElement>(".status__avatar img") {
            it.alt = "Avatar"
            it.src = image.toString()
        }

        element.forEach(".status__name .nes-text") {
            it.textContent = name
        }

        element.forEachInstance<HTMLProgressElement>(".status__passed") {
            val max = duration?.inWholeSeconds?.toDouble() ?: 0.0
            val value = passed?.inWholeSeconds?.toDouble() ?: 0.0
            it.max = max
            it.value = value.coerceAtMost(max)
        }

        element.forEach(".status__remaining") {
            val content = remaining?.let { remaining ->
                if (remaining <= .5.seconds) {
                    "Done"
                } else if (remaining.absoluteValue >= 1.minutes) {
                    (remaining + 30.seconds).toString(MINUTES)
                } else {
                    remaining.toString(SECONDS)
                }
            } ?: ""
            if (it.textContent != content) it.textContent = content
        }

        return element
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Status

        if (name != other.name) return false
        if (task != other.task) return false
        if (duration != other.duration) return false
        if (timestamp?.getTime() != other.timestamp?.getTime()) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + task.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + timestamp?.getTime().hashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}
