package io.github.landgrafhomyak.itmo.dms_lab.io

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


public typealias Client2ServerEncoder = AsByteArrayEncoder

/**
 * Кодирует данные для передачи от клиента к серверу
 * @see AsByteArrayDecoder
 */
@OptIn(ExperimentalUnsignedTypes::class)
public class AsByteArrayEncoder internal constructor(private val buffer: UByteArrayBuilder) : Encoder, CompositeEncoder {
    public constructor() : this(UByteArrayBuilder())

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule

    private var level = 0L

    public fun export(): UByteArray = this.buffer.build()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        this.buffer.add(TypeMarkers.STRUCT_BEGIN)
        this.level++
        return this
    }

    override fun encodeBoolean(value: Boolean) {
        this.buffer.add(TypeMarkers.BOOLEAN)
        this.buffer.add(if (value) '+' else '-')
    }

    override fun encodeByte(value: Byte) {
        this.buffer.add(TypeMarkers.BOOLEAN)
        this.buffer.encodeNumber(value.toUByte().toULong(), Byte.SIZE_BYTES)
    }

    override fun encodeChar(value: Char) {
        this.buffer.add(TypeMarkers.CHAR)
        this.buffer.encodeNumber(value.code.toUInt().toULong(), Char.SIZE_BYTES)
    }

    override fun encodeDouble(value: Double) {
        this.buffer.add(TypeMarkers.DOUBLE)
        this.buffer.encodeNumber(value.toRawBits().toULong(), Double.SIZE_BYTES)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        this.buffer.add(TypeMarkers.ENUM)
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
    }

    override fun encodeFloat(value: Float) {
        this.buffer.add(TypeMarkers.FLOAT)
        this.buffer.encodeNumber(value.toRawBits().toUInt().toULong(), Float.SIZE_BYTES)
    }

    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder = this

    override fun encodeInt(value: Int) {
        this.buffer.add(TypeMarkers.INT)
        this.buffer.encodeNumber(value.toUInt().toULong(), Int.SIZE_BYTES)
    }

    override fun encodeLong(value: Long) {
        this.buffer.add(TypeMarkers.LONG)
        this.buffer.encodeNumber(value.toULong(), Long.SIZE_BYTES)
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        this.buffer.add(TypeMarkers.NULL)
    }

    override fun encodeShort(value: Short) {
        this.buffer.add(TypeMarkers.SHORT)
        this.buffer.encodeNumber(value.toUShort().toULong(), Short.SIZE_BYTES)
    }

    override fun encodeString(value: String) {
        this.buffer.add(TypeMarkers.STRING)
        this.buffer.encodeString(value)
    }

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeBoolean(value)
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeByte(value)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeChar(value)
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeDouble(value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeFloat(value)
    }

    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        // this.buffer.add(ubyteArrayOf(':'))
        return this
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeInt(value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeLong(value)
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) {
        if (value == null) {
            this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
            this.encodeNull()
        } else
            this.encodeSerializableElement(descriptor, index, serializer, value)
    }

    override fun <T> encodeSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        // this.buffer.add(ubyteArrayOf(':'))
        serializer.serialize(this, value)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeShort(value)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        this.buffer.encodeNumber(index.toUInt().toULong(), Int.SIZE_BYTES)
        this.encodeString(value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if (--this.level < 0) {
            throw IllegalStateException("Unexpected .endStructure()")
        }
        this.buffer.encodeNumber(CompositeDecoder.DECODE_DONE.toUInt().toULong(), Int.SIZE_BYTES)
        this.buffer.add('}')
    }
}