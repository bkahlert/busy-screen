package koodies.serialization

import koodies.parse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration

class DurationSerializer : KSerializer<Duration> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Duration", STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): Duration = Duration.parse(decoder.decodeString())
}
