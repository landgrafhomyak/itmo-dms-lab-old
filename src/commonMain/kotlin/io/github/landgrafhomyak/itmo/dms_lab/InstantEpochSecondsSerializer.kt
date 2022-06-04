package io.github.landgrafhomyak.itmo.dms_lab

import kotlinx.datetime.Instant
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.KSerializer

/**
 * Сериализует [Instant] как [количество секунд с начала эпохи](https://ru.wikipedia.org/wiki/Unix-%D0%B2%D1%80%D0%B5%D0%BC%D1%8F)
 *
 * Использование:
 * ```
 * @Serializable
 * class InstantContainer(
 *     @Serializable(with=InstantEpochSecondsSerializer::class)
 *     val value:Instant
 * )
 * ```
 */
@Suppress("SpellCheckingInspection")
public object InstantEpochSecondsSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant =
        Instant.fromEpochSeconds(decoder.decodeLong())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSeconds)
    }
}