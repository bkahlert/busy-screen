package com.bkahlert.kommons.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.Date

class DateSerializer : KSerializer<Date> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(value.toISOString())
    }

    override fun deserialize(decoder: Decoder): Date {
        val text = decoder.decodeString()
        return Date(text).takeUnless { it.getTime().isNaN() }
            ?: throw SerializationException("'$text' is not a date")
    }
}
