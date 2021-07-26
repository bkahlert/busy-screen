import koodies.serialization.DateSerializer
import koodies.serialization.DurationSerializer
import koodies.time.Now
import koodies.time.minus
import koodies.time.minutes
import koodies.time.plus
import koodies.time.seconds
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.progress
import kotlinx.html.small
import kotlinx.html.span
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
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

    fun TagConsumer<HTMLElement>.append(): HTMLElement =
        div("status nes-container is-centered") {
            h1("status__task") {
                span {
                    +(task ?: "Status")
                }
            }

            div("status__status") {
                div("status__name nes-balloon from-right") {
                    if (done == true) {
                        span("nes-text") { +name }
                    } else {
                        span("nes-text is-error") { +name }
                    }
                }
                img("avatar", avatar.url, "status__avatar nes-avatar is-large")
            }

            duration?.inWholeSeconds?.let { totalSeconds ->
                passed?.inWholeSeconds?.also { passedSeconds ->
                    if (totalSeconds <= passedSeconds) {
                        progress("status__passed nes-progress is-success") {
                            max = "1"
                            value = "1"
                        }

                        div("status__remaining") { +"Done" }
                    } else {
                        progress("status__passed nes-progress is-warning") {
                            max = totalSeconds.toString()
                            value = passedSeconds.toString()
                        }

                        remaining?.also {
                            div("status__remaining") {
                                small { +"remaining" }
                                if (it >= 1.minutes) {
                                    div { +(it + 30.seconds).toString(MINUTES) }
                                } else {
                                    div { +it.toString(SECONDS) }
                                }
                            }
                        }
                    }
                }
            }
        }

    fun appendTo(node: Node): List<HTMLElement> = node.append { append() }

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
        result = 31 * result + (task?.hashCode() ?: 0)
        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.getTime()?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        return result
    }
}
