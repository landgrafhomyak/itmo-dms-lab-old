package io.github.landgrafhomyak.itmo.dms_lab.interop

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * Состояние парсера разбора выражения составного объекта данных
 */
@Suppress("SpellCheckingInspection")
public class InLineObjectDecoder(
    private val raw: String
) : Decoder, CompositeDecoder {
    private enum class State(val objectStart: Boolean, val value: Boolean, val key: Boolean, val objectEnd: Boolean) {
        START(true, false, true, false),
        KEY(false, false, true, true),
        VALUE(true, true, false, false),
        OBJECT_END(false, false, false, true)
    }

    private var state = State.START

    private var pos: Int = 0

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule

    private var indent: Int = 0

    @Suppress("NOTHING_TO_INLINE")
    private inline fun skipSpaces() {
        while (this.pos < this.raw.length) {
            if (!this.raw[this.pos].isWhitespace()) break
            this.pos++
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (!this.state.objectStart) throw IllegalStateException("Can't read object yet")
        this.skipSpaces()
        if (this.pos < this.raw.length && this.raw[this.pos] == '{') this.pos++
        else {
            if (this.indent != 0) throw SerializationException("Не получается прочитать начало объекта")
        }
        this.state = State.KEY
        this.indent++
        return this
    }

    private inline fun <T> decodeValue(converter: String.() -> T): T {
        if (!this.state.value) throw IllegalStateException("Can't read value yet")
        this.skipSpaces()
        var slashChar = false
        var inString = false
        val value = StringBuilder()
        s@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            if (slashChar) {
                if (c == '\"' || c == '\\') value.append(c)
                else throw SerializationException("Неправильный символ после \\")
                slashChar = false
            } else {
                when {
                    c == '\"'        -> inString = !inString
                    c.isWhitespace() ->
                        if (!inString) break@s
                        else value.append(c)
                    c == '\\'        -> if (inString) slashChar = true
                    c == '}'         -> if (!inString) break@s
                    else             -> value.append(c)
                }
            }
            this.pos++
        }
        if (inString || slashChar) throw SerializationException("Конец ввода")
        this.state = State.KEY
        return converter(value.toString())
    }

    override fun decodeBoolean(): Boolean =
        this.decodeValue {
            lowercase().toBooleanStrictOrNull() ?: throw SerializationException("Неправильное логическое значение")
        }

    override fun decodeByte(): Byte =
        this.decodeValue {
            toByteOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    override fun decodeChar(): Char =
        this.decodeValue {
            if (length != 1) throw SerializationException("Неправильное количество символов")
            get(0)
        }

    override fun decodeDouble(): Double =
        this.decodeValue {
            toDoubleOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        this.decodeValue {
            enumDescriptor.elementNames
                .indexOf(this@decodeValue)
                .apply index@{ if (this@index < 0) throw SerializationException("Неизвестное значение перечисления") }
        }

    override fun decodeFloat(): Float =
        this.decodeValue {
            toFloatOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    @ExperimentalSerializationApi
    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder {
        return this
    }

    override fun decodeInt(): Int =
        this.decodeValue {
            toIntOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    override fun decodeLong(): Long =
        this.decodeValue {
            toLongOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean =
        this.decodeValue {
            lowercase() != "null"
        }

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? =
        this.decodeValue {
            if (lowercase() != "null") throw SerializationException("Неправильный null")
            null
        }

    override fun decodeShort(): Short =
        this.decodeValue {
            toShortOrNull() ?: throw SerializationException("Неправильное числовое значение")

        }

    override fun decodeString(): String =
        this.decodeValue {
            this@decodeValue
        }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean = this.decodeBoolean()


    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte = this.decodeByte()


    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char = this.decodeChar()


    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double = this.decodeDouble()


    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (!this.state.key) throw IllegalArgumentException()
        this.skipSpaces()
        val startPos = this.pos
        s@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isLetterOrDigit()  -> {}
                c == '-' || c == '_' -> {}
                c == '}'             -> {
                    this.state = State.OBJECT_END
                    return CompositeDecoder.DECODE_DONE
                }
                else                 -> break@s
            }
            this.pos++
        }
        if (this.pos >= this.raw.length) {
            this.state = State.OBJECT_END
            return CompositeDecoder.DECODE_DONE
        }
        if (this.pos == startPos) throw SerializationException("Пустой ключ")
        val key = this.raw.subSequence(startPos until this.pos)
        this.skipSpaces()
        if (this.pos >= this.raw.length || this.raw[this.pos] != '=') throw SerializationException("Пропущен переход от ключа к значению")
        this.pos++
        this.state = State.VALUE
        return descriptor.elementNames
            .indexOf(key)
            .run index@{ if (this@index < 0) CompositeDecoder.UNKNOWN_NAME else this@index }
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float = this.decodeFloat()


    @ExperimentalSerializationApi
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = this


    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int = this.decodeInt()


    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long = this.decodeLong()


    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, previousValue: T?): T? {
        @Suppress("LiftReturnOrAssignment")
        if (this.decodeNotNullMark()) return this.decodeSerializableElement(descriptor, index, deserializer, previousValue)
        else return this.decodeNull()
    }

    override fun <T> decodeSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, previousValue: T?): T =
        deserializer.deserialize(this)


    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short = this.decodeShort()


    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String = this.decodeString()


    override fun endStructure(descriptor: SerialDescriptor) {
        if (!this.state.objectEnd) throw IllegalStateException("Can't read object end yet")
        this.indent--
        if (this.pos < this.raw.length && this.raw[this.pos] == '}') this.pos++
        else {
            if (this.indent != 0) throw SerializationException("Не получается прочитать конец объекта")
        }
        this.state = State.KEY
    }
}

