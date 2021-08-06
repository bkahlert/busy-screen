package status

import koodies.serialization.DateSerializer
import koodies.serialization.DurationSerializer
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
import org.w3c.dom.asList
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
) {
    companion object {
        private val format = Json { isLenient = true }
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

    val avatar: Gravatar get() = Gravatar(email)

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

        element.querySelectorAll(".status__task").asList().forEach {
            it.textContent = task ?: "Status"
        }

        element.querySelectorAll(".status__avatar").asList().mapNotNull { it as? HTMLImageElement }.forEach {
            it.alt = avatar.email?.let { "Avatar for $it" } ?: "Default avatar"
            it.src = avatar.url
        }

        element.querySelectorAll(".status__name .nes-text").asList().forEach {
            it.textContent = name
        }

        element.querySelectorAll(".status__passed").asList().mapNotNull { it as? HTMLProgressElement }.forEach {
            it.max = duration?.inWholeSeconds?.toDouble() ?: 0.0
            it.value = passed?.inWholeSeconds?.toDouble() ?: 0.0
        }

        element.querySelectorAll(".status__remaining").asList().forEach {
            remaining?.let { remaining ->
                if (remaining >= 1.minutes || remaining <= (-1).minutes) {
                    it.textContent = (remaining + 30.seconds).toString(MINUTES)
                } else {
                    it.textContent = remaining.toString(SECONDS)
                }
            } ?: run {
                it.textContent = "-"
            }
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
