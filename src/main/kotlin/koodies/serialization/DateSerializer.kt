package koodies.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.Date
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DateSerializer : KSerializer<Date> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(value.toISOString())
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeString())
    }
}
