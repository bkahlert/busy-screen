import koodies.time.minutes
import koodies.time.seconds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DurationSerializer : KSerializer<Duration> {

    private val regex = Regex("P.*T(?:(?<minutes>\\d+)M)?(?:(?<seconds>\\d+)S)?")

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Duration", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): Duration {
        val string = decoder.decodeString()
        return when {
            string.startsWith("P") -> {
                requireNotNull(regex.matchEntire(string)
                    ?.run {
                        var duration = Duration.ZERO
                        val minutes = groups[1]?.value?.toInt()
                        if (minutes != null) duration += minutes.minutes
                        val seconds = groups[2]?.value?.toInt()
                        if (seconds != null) duration += seconds.seconds
                        duration
                    }) { "Failed to parse duration" }
            }
            else -> {
                Duration.milliseconds(string.toLong())
            }
        }
    }
}
