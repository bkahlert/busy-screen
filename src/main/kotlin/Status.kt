import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
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
    @JsonNames("total-time")
    val totalTime: Duration? = null,
    /**
     * Optional time that already passed.
     */
    @Serializable(DurationSerializer::class)
    @JsonNames("passed-time")
    val passedTime: Duration? = null,
    /**
     * Optional remaining time.
     */
    @Serializable(DurationSerializer::class)
    @JsonNames("remaining-time")
    val remainingTime: Duration? = null,
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

    val totalMinutes
        get() = totalTime?.inWholeMinutes
            ?: if (passedTime != null && remainingTime != null) (passedTime + remainingTime).inWholeMinutes else null

    val passedMinutes
        get() = passedTime?.inWholeMinutes
            ?: if (totalTime != null && remainingTime != null) (totalTime - remainingTime).inWholeMinutes else null

    val remainingMinutes
        get() = remainingTime?.inWholeMinutes
            ?: if (totalTime != null && passedTime != null) (totalTime - passedTime).inWholeMinutes else null

    val formattedTotalTime
        get() = totalTime?.format()
            ?: if (passedTime != null && remainingTime != null) (passedTime + remainingTime).format() else null

    val formattedPassedTime
        get() = passedTime?.format()
            ?: if (totalTime != null && remainingTime != null) (totalTime - remainingTime).format() else null

    val formattedRemainingTime
        get() = remainingTime?.format()
            ?: if (totalTime != null && passedTime != null) (totalTime - passedTime).format() else null

    val avatar: Gravatar get() = Gravatar(email)
}
