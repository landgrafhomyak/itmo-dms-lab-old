package io.github.landgrafhomyak.itmo.dms_lab.io

import io.github.landgrafhomyak.itmo.dms_lab.interop.displayOrSerialName
import io.github.landgrafhomyak.itmo.dms_lab.interop.getElementDisplayOrSerialName
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


public data class RequestOutputMessage(val type: RequestOutputMessageType, val message: RequestOutputEntity)

@Serializable
public enum class RequestOutputMessageType {
    @SerialName("i")
    INFO,

    @SerialName("w")
    WARNING,

    @SerialName("e")
    ERROR
}

/**
 * Вывод [запроса][BoundRequest]
 */
public interface RequestOutputBuilder {
    public suspend fun info(message: String)
    public suspend fun <T> info(obj: T, serializer: KSerializer<T>)
    public suspend fun warning(message: String)
    public suspend fun <T> warning(obj: T, serializer: KSerializer<T>)
    public suspend fun error(message: String)
    public suspend fun <T> error(obj: T, serializer: KSerializer<T>)
    public suspend fun addFrom(src: RequestOutputAccessor)
}

public interface RequestOutputAccessor : Iterable<RequestOutputMessage> {
    public operator fun get(index: Int): RequestOutputMessage
    public val size: Int
}

@Suppress("SpellCheckingInspection")
public class RequestOutputDefaultEncodedInMemoryList : RequestOutputBuilder, RequestOutputAccessor {
    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T> write(type: RequestOutputMessageType, serializer: KSerializer<T>, message: T) {
        var alreadyAdded = false
        val encoder = RequestOutputSimplifierEncoder { e ->
            if (alreadyAdded) throw IllegalStateException()
            alreadyAdded = false
            this@RequestOutputDefaultEncodedInMemoryList.list.add(RequestOutputMessage(type, e))
        }
        serializer.serialize(encoder, message)
    }

    private val list = mutableListOf<RequestOutputMessage>()

    override suspend fun info(message: String): Unit =
        this.write(RequestOutputMessageType.INFO, String.serializer(), message)

    override suspend fun <T> info(obj: T, serializer: KSerializer<T>): Unit =
        this.write(RequestOutputMessageType.INFO, serializer, obj)

    override suspend fun warning(message: String): Unit =
        this.write(RequestOutputMessageType.WARNING, String.serializer(), message)

    override suspend fun <T> warning(obj: T, serializer: KSerializer<T>): Unit =
        this.write(RequestOutputMessageType.WARNING, serializer, obj)

    override suspend fun error(message: String): Unit =
        this.write(RequestOutputMessageType.ERROR, String.serializer(), message)

    override suspend fun <T> error(obj: T, serializer: KSerializer<T>): Unit =
        this.write(RequestOutputMessageType.ERROR, serializer, obj)

    override suspend fun addFrom(src: RequestOutputAccessor) {
        this.list.addAll(src)
    }

    override fun get(index: Int): RequestOutputMessage = this.list[index]

    override val size: Int
        get() = this.list.size

    override fun iterator(): Iterator<RequestOutputMessage> = this.list.iterator()
}

public class RequestOutputObjectedList

public sealed class RequestOutputEntity {
    public abstract val fieldName: String
    public abstract val value: Any?


    @Suppress("ArrayInDataClass")
    public data class Struct(override val fieldName: String, val structureName: String, override val value: Array<RequestOutputEntity>) : RequestOutputEntity()
    public data class Bool(override val fieldName: String, override val value: Boolean) : RequestOutputEntity()
    public data class Str(override val fieldName: String, override val value: String) : RequestOutputEntity()
    public data class Number(override val fieldName: String, override val value: String) : RequestOutputEntity()
    public data class Dbl(override val fieldName: String, override val value: String) : RequestOutputEntity()
    public data class Enum(override val fieldName: String, override val value: String) : RequestOutputEntity()
    public data class Null(override val fieldName: String) : RequestOutputEntity() {
        override val value: Nothing? get() = null
    }
}

public class RequestOutputSimplifierEncoder(private val saver: (RequestOutputEntity) -> Unit) : Encoder {
    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        var isSet = false
        return RequestOutputSimplifierCompositeEncoder(mutableListOf()) { res ->
            if (isSet) throw IllegalStateException()
            isSet = true
            this@RequestOutputSimplifierEncoder.saver(RequestOutputEntity.Struct("", descriptor.displayOrSerialName, res))
        }
    }

    override fun encodeBoolean(value: Boolean) {
        this.saver(RequestOutputEntity.Bool("", value))
    }

    override fun encodeByte(value: Byte) {
        this.saver(RequestOutputEntity.Number("", value.toString()))
    }

    override fun encodeChar(value: Char) {
        this.saver(RequestOutputEntity.Str("", value.toString()))
    }

    override fun encodeDouble(value: Double) {
        this.saver(RequestOutputEntity.Dbl("", value.toString()))
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        this.saver(RequestOutputEntity.Enum("", enumDescriptor.getElementDisplayOrSerialName(index)))
    }

    override fun encodeFloat(value: Float) {
        this.saver(RequestOutputEntity.Dbl("", value.toString()))
    }

    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
        return this
    }

    override fun encodeInt(value: Int) {
        this.saver(RequestOutputEntity.Number("", value.toString()))
    }

    override fun encodeLong(value: Long) {
        this.saver(RequestOutputEntity.Number("", value.toString()))
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        this.saver(RequestOutputEntity.Null(""))
    }

    override fun encodeShort(value: Short) {
        this.saver(RequestOutputEntity.Number("", value.toString()))
    }

    override fun encodeString(value: String) {
        this.saver(RequestOutputEntity.Str("", value))
    }
}

public class RequestOutputSimplifierCompositeEncoder(private val destination: MutableList<RequestOutputEntity>, private val saver: (Array<RequestOutputEntity>) -> Unit) : CompositeEncoder {
    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        this.destination.add(RequestOutputEntity.Bool(descriptor.getElementDisplayOrSerialName(index), value))
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        this.destination.add(RequestOutputEntity.Number(descriptor.getElementDisplayOrSerialName(index), value.toString()))
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        this.destination.add(RequestOutputEntity.Str(descriptor.getElementDisplayOrSerialName(index), value.toString()))
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        this.destination.add(RequestOutputEntity.Dbl(descriptor.getElementDisplayOrSerialName(index), value.toString()))
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        this.destination.add(RequestOutputEntity.Dbl(descriptor.getElementDisplayOrSerialName(index), value.toString()))
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun buildEncoder(fieldName: String): RequestOutputSimplifierEncoder {
        var isSaved = true
        return RequestOutputSimplifierEncoder { e ->
            if (isSaved) throw IllegalStateException()
            isSaved = true
            this@RequestOutputSimplifierCompositeEncoder.destination.add(
                when (e) {
                    is RequestOutputEntity.Bool   -> RequestOutputEntity.Bool(fieldName, e.value)
                    is RequestOutputEntity.Dbl    -> RequestOutputEntity.Dbl(fieldName, e.value)
                    is RequestOutputEntity.Enum   -> RequestOutputEntity.Enum(fieldName, e.value)
                    is RequestOutputEntity.Null   -> RequestOutputEntity.Null(fieldName)
                    is RequestOutputEntity.Number -> RequestOutputEntity.Number(fieldName, e.value)
                    is RequestOutputEntity.Str    -> RequestOutputEntity.Str(fieldName, e.value)
                    is RequestOutputEntity.Struct -> RequestOutputEntity.Struct(fieldName, e.structureName, e.value)
                }
            )
        }
    }

    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        return this.buildEncoder(descriptor.getElementDisplayOrSerialName(index))
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        this.destination.add(RequestOutputEntity.Number(descriptor.getElementDisplayOrSerialName(index), value.toString()))
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        this.destination.add(RequestOutputEntity.Number(descriptor.getElementDisplayOrSerialName(index), value.toString()))
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) {
        if (value == null) this.destination.add(RequestOutputEntity.Null(descriptor.getElementDisplayOrSerialName(index)))
        else this.encodeSerializableElement(descriptor, index, serializer, value)
    }

    override fun <T> encodeSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T) {
        serializer.serialize(
            this.buildEncoder(descriptor.getElementDisplayOrSerialName(index)),
            value
        )
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        this.destination.add(RequestOutputEntity.Number(descriptor.getElementDisplayOrSerialName(index), value.toString()))
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        this.destination.add(RequestOutputEntity.Str(descriptor.getElementDisplayOrSerialName(index), value))
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        this.saver(this.destination.toTypedArray())
    }

}