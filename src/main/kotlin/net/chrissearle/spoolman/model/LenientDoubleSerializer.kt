package net.chrissearle.spoolman.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

/**
 * Accepts any number-ish value spoolman sends us - integer (279), decimal (279.0) or a
 * quoted string using either separator ("279,0") - and decodes it as a [Double].
 */
object LenientDoubleSerializer : KSerializer<Double> {
    override val descriptor = PrimitiveSerialDescriptor("LenientDouble", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double {
        val raw =
            when (decoder) {
                is JsonDecoder -> (decoder.decodeJsonElement() as? JsonPrimitive)?.content
                else -> return decoder.decodeDouble()
            } ?: throw SerializationException("Expected a number but found a JSON object or array")

        return raw.trim().replace(',', '.').toDoubleOrNull()
            ?: throw SerializationException("Cannot read '$raw' as a number")
    }

    override fun serialize(
        encoder: Encoder,
        value: Double,
    ) = encoder.encodeDouble(value)
}
