package io.github.landgrafhomyak.itmo.dms_lab.io

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.jvm.JvmInline

/**
 * Декодирует данные для передачи от сервера к клиенту
 * @see AsNamedByteArrayEncoder
 */
@OptIn(ExperimentalUnsignedTypes::class)
public class AsNamedByteArrayDecoder constructor(raw: UByteArray) : Decoder, CompositeDecoder {
    @JvmInline
    private value class RawWrapper(private val data: UByteArray) {
        @Suppress("NOTHING_TO_INLINE")
        inline operator fun get(index: Int): UByte {
            if (index !in this.data.indices) throw SerializationException("Unexpected end of data")
            return this.data[index]
        }

        @Suppress("NOTHING_TO_INLINE")
        inline fun decodeString(start: Int, size: Int): String {
            if (start > this.data.size || start + size > this.data.size) throw SerializationException("Unexpected end of data")
            return this.data.toByteArray().decodeToString(start, start + size)
        }

        inline val size: Int get() = this.data.size
    }

    private val raw = RawWrapper(raw)

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule

    private var pos = 0

    private lateinit var lastName: String

    /*
    private inline operator fun <T> T.rem(constructor: (String, String) -> RequestOutputList.Entry): NextEntryResult.Entry =
        this@rem / { n, v -> constructor(n, v.toString()) }

    private inline operator fun <T> T.div(constructor: (String, T) -> RequestOutputList.Entry): NextEntryResult.Entry =
        NextEntryResult.Entry(constructor(this@AsNamedByteArrayDecoder.lastName, this@div))


    internal sealed interface NextEntryResult {
        @JvmInline
        value class Entry(val e: RequestOutputList.Entry) : NextEntryResult

        @JvmInline
        value class OpenStruct(val n: String) : NextEntryResult
        object CloseStruct : NextEntryResult
        object End : NextEntryResult
    }

    @OptIn(ExperimentalSerializationApi::class)
    internal fun nextEntry(skipIndex: Boolean): NextEntryResult {
        if (this.pos >= this.raw.size) {
            return NextEntryResult.End
        }

        if (skipIndex) {
            this.decodeElementIndexEx()
        }

        return when (this.raw[this.pos].c) {
            '{'  -> this.beginStructureEx().let { s -> NextEntryResult.OpenStruct(s) }
            '}'  -> this.endStructureEx().let { NextEntryResult.CloseStruct }
            '?'  -> this.decodeBoolean() / RequestOutputList::EBoolean
            'b'  -> this.decodeByte() % RequestOutputList::ENumber
            'c'  -> this.decodeChar() % RequestOutputList::EString
            'd'  -> this.decodeDouble() % RequestOutputList::EFloat
            '#'  -> this.decodeEnumEx() / { n, (_, v) -> RequestOutputList.EEnum(n, v) }
            'f'  -> this.decodeFloat() % RequestOutputList::EFloat
            'i'  -> this.decodeInt() % RequestOutputList::ENumber
            'l'  -> this.decodeLong() % RequestOutputList::ENumber
            '@'  -> this.decodeNull() / { n, _ -> RequestOutputList.ENull(n) }
            'h'  -> this.decodeShort() % RequestOutputList::ENumber
            's'  -> this.decodeString() % RequestOutputList::EString
            else -> throw SerializationException("Unexpected type")
        }
    }
     */

    @Suppress("NOTHING_TO_INLINE")
    public fun beginStructureEx(): String {
        if (this.raw[this.pos++].c != '{') throw SerializationException("Expected structure begin")
        val size = decodeNumber(Int.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt()
        val parsed = this.raw.decodeString(this.pos, size)
        this.pos += size
        return parsed
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        this.beginStructureEx().let { this }

    override fun decodeBoolean(): Boolean {
        if (this.raw[this.pos++].c != '?') throw SerializationException("Expected boolean value")
        return when (this.raw[this.pos++].c) {
            '+'  -> true
            '-'  -> false
            else -> throw SerializationException("Unexpected boolean value")
        }
    }

    override fun decodeByte(): Byte {
        if (this.raw[this.pos++].c != 'b') throw SerializationException("Expected byte value")
        return decodeNumber(Byte.SIZE_BYTES) { this.raw[this.pos++] }.toUByte().toByte()
    }

    override fun decodeChar(): Char {
        if (this.raw[this.pos++].c != 'c') throw SerializationException("Expected char code value")
        return decodeNumber(Char.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt().toChar()
    }

    override fun decodeDouble(): Double {
        if (this.raw[this.pos++].c != 'd') throw SerializationException("Expected double value")
        return Double.fromBits(decodeNumber(Double.SIZE_BYTES) { this.raw[this.pos++] }.toLong())
    }

    @Suppress("NOTHING_TO_INLINE")
    public fun decodeEnumEx(): Pair<Int, String> {
        if (this.raw[this.pos++].c != '#') throw SerializationException("Expected enum value")
        val index = decodeNumber(Int.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt()
        val size = decodeNumber(Int.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt()
        val parsed = this.raw.decodeString(this.pos, size)
        this.pos += size
        return index to parsed
    }


    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = this.decodeEnumEx().first

    override fun decodeFloat(): Float {
        if (this.raw[this.pos++].c != 'f') throw SerializationException("Expected double value")
        return Float.fromBits(decodeNumber(Float.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt())
    }

    @ExperimentalSerializationApi
    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder {
        return this
    }

    override fun decodeInt(): Int {
        if (this.raw[this.pos++].c != 'i') throw SerializationException("Expected int value")
        return decodeNumber(Int.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt()
    }

    override fun decodeLong(): Long {
        if (this.raw[this.pos++].c != 'l') throw SerializationException("Expected long value")
        return decodeNumber(Long.SIZE_BYTES) { this.raw[this.pos++] }.toLong()
    }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean {
        return this.raw[this.pos].c != '@'
    }

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? {
        if (this.raw[this.pos++].c != '@') throw SerializationException("Expected null")
        return null
    }

    override fun decodeShort(): Short {
        if (this.raw[this.pos++].c != 'h') throw SerializationException("Expected short value")
        return decodeNumber(Short.SIZE_BYTES) { this.raw[this.pos++] }.toUShort().toShort()
    }

    override fun decodeString(): String {
        if (this.raw[this.pos++].c != 's') throw SerializationException("Expected string value")
        val size = decodeNumber(Int.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt()
        val parsed = this.raw.decodeString(this.pos, size)
        this.pos += size
        return parsed
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        this.decodeBoolean()


    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        this.decodeByte()

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        this.decodeChar()

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        this.decodeDouble()

    public fun decodeElementIndexEx(): Int {
        val index = decodeNumber(Int.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt()
        if (index >= 0) {
            val size = decodeNumber(Int.SIZE_BYTES) { this.raw[this.pos++] }.toUInt().toInt()
            val parsed = this.raw.decodeString(this.pos, size)
            this.pos += size
            this.lastName = parsed
        }
        return index
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        this.decodeElementIndexEx()

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        this.decodeFloat()

    @ExperimentalSerializationApi
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = this

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        this.decodeInt()

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        this.decodeLong()

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, previousValue: T?): T? {
        return if (this.decodeNotNullMark()) this.decodeSerializableElement(descriptor, index, deserializer, previousValue)
        else this.decodeNull()
    }

    override fun <T> decodeSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, previousValue: T?): T {
        return deserializer.deserialize(this)
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        this.decodeShort()

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
        this.decodeString()

    @Suppress("NOTHING_TO_INLINE")
    public fun endStructureEx() {
        if (this.raw[this.pos++].c != '}') throw SerializationException("Expected end of structure")
    }

    override fun endStructure(descriptor: SerialDescriptor): Unit =
        this.endStructureEx()
}