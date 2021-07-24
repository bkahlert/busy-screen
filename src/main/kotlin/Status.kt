import koodies.serialization.DateSerializer
import koodies.serialization.DurationSerializer
import koodies.time.Now
import koodies.time.minus
import koodies.time.plus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.DurationUnit.MINUTES
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
@Serializable
data class Status constructor(
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
        private fun Duration.format() = toString(MINUTES)
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

    @Transient
    val totalSeconds = duration?.inWholeSeconds
    val passedSeconds get() = passed?.inWholeSeconds
    val remainingSeconds get() = remaining?.inWholeSeconds

    val formattedTotal get() = duration?.format()
    val formattedPassed get() = passed?.format()
    val formattedRemaining get() = remaining?.format()

    val avatar: Gravatar get() = Gravatar(email)

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
