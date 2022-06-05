package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.InstantEpochSecondsSerializer
import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@DisplayName("Лабораторная работа")
@Serializable(with = AbstractLabWorkWithIdSerializer::class)
public interface AbstractLabWorkWithId : AbstractLabWork {
    @DisplayName("Уникальный идентификатор")
    public val id: LabWorkId
}

public typealias LabWorkId = Long

public object AbstractLabWorkWithIdSerializer : KSerializer<AbstractLabWorkWithId> {
    override fun deserialize(decoder: Decoder): AbstractLabWorkWithId {
        throw RuntimeException("Use LabWorkSerializerInstead")
    }

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("LabWorkWithId") {
            element<Long>("id")
            element<String>("name")
            element<Coordinates>("coordinates")
            element<Instant>("creationDate")
            element<Long>("minimalPoint")
            element<Double>("maximumPoint")
            element<Int>("personalQualitiesMaximum")
            element<Difficulty>("difficulty")
            element<Person>("author")
        }

    override fun serialize(encoder: Encoder, value: AbstractLabWorkWithId) {
        encoder.encodeStructure(this.descriptor) {
            encodeLongElement(this@AbstractLabWorkWithIdSerializer.descriptor, 0, value.id)
            encodeStringElement(this@AbstractLabWorkWithIdSerializer.descriptor, 1, value.name)
            encodeSerializableElement(this@AbstractLabWorkWithIdSerializer.descriptor, 2, Coordinates.serializer(), value.coordinates)
            encodeSerializableElement(this@AbstractLabWorkWithIdSerializer.descriptor, 3, InstantEpochSecondsSerializer, value.creationDate)
            encodeLongElement(this@AbstractLabWorkWithIdSerializer.descriptor, 4, value.minimalPoint)
            encodeDoubleElement(this@AbstractLabWorkWithIdSerializer.descriptor, 5, value.maximumPoint)
            encodeIntElement(this@AbstractLabWorkWithIdSerializer.descriptor, 6, value.personalQualitiesMaximum)
            encodeSerializableElement(this@AbstractLabWorkWithIdSerializer.descriptor, 7, Difficulty.serializer(), value.difficulty)
            encodeSerializableElement(this@AbstractLabWorkWithIdSerializer.descriptor, 8, Person.serializer(), value.author)
        }
    }
}