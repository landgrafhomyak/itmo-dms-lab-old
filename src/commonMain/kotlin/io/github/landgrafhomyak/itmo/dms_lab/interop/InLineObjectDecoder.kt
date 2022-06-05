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
    /**
     * Итератор для извлечения срезов из строки, исключающий из [обычного отрезка][IntRange] значения множества.
     * Наследуется от [Iterable] для передачи в [String.slice] без создания дополнительного объекта, является одноразовым
     * @param exclude последовательность чисел для исключения, должна быть строго возрастающей
     * @param from начальное значение отрезка
     * @param until конечное значение отрезка (включительно)
     */
    private class IntRangeWithExclude(
        exclude: Iterable<Int>,
        from: Int,
        private val until: Int
    ) : Iterable<Int>, Iterator<Int> {
        init {
            if (from > this.until) {
                throw IllegalStateException("Начальное отрезка значение должно быть не больше конечного")
            }
        }

        /**
         * Текущая позиция итератора
         */
        private var pos = from

        /**
         * Итаратор последовательности с исключёнными индексами
         */
        private val excludeIterator = exclude.iterator()

        /**
         * Следующее исключённое число
         */
        private var nextExclude: Int = from - 1

        init {
            for (e in this.excludeIterator) {
                this.nextExclude = e
                if (from <= e) {
                    break
                }
            }
        }

        override fun iterator(): Iterator<Int> = this

        override fun hasNext(): Boolean {
            while (this.pos <= this.until) {
                if (this.pos != this.nextExclude) {
                    return true
                }
                this.pos++
                if (this.excludeIterator.hasNext()) {
                    this.nextExclude = this.excludeIterator.next()
                }
            }
            return false
        }

        override fun next(): Int {
            if (!this.hasNext())
                throw IllegalStateException("Все индексы были перебраны")
            return this.pos++
        }
    }

    private sealed class Entity {
        data class Pair(val key: String, val value: String?) : Entity()

        object ObjectOpen : Entity()
        object ObjectClose : Entity()
    }

    private var lastEntity: Entity? = null

    private var pos: Int = 0

    private fun nextEntry(): Entity {
        this.lastEntity?.apply le@{ return@nextEntry this@le }

        spaces@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> {}
                else             -> break@spaces
            }
            this.pos++
        }
        if (this.raw[this.pos] == '{') {
            this.lastEntity = Entity.ObjectOpen
            return Entity.ObjectOpen
        }

        val keyStartPos = this.pos
        key@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> break@key
                c.isDigit()      -> {}
                c.isLetter()     -> {}
                c == '_'         -> {}
                c == '}'         -> break@key
                else             -> throw SerializationException("Неправильный символ в ключе")
            }
            this.pos++
        }
        val key = this.raw.substring(keyStartPos until this.pos)

        spaces@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> {}
                else             -> break@spaces
            }
            this.pos++
        }
        when (this.raw[this.pos]) {
            '}'  ->
                if (key.isEmpty()) {
                    this.lastEntity = Entity.ObjectClose
                    return Entity.ObjectClose
                } else
                    throw SerializationException("Ключ не может быть без значения или содержать в себе скобки")
            '='  -> this.pos++
            else -> throw SerializationException("Невалидный переход от ключа к значению")
        }

        spaces@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> {}
                else             -> break@spaces
            }
            this.pos++
        }

        val valueStartPos = this.pos
        var expectingSlash = false
        var inString = false
        val excludedIndicies = mutableListOf<Int>()

        value@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos++]
            if (expectingSlash) {
                when (c) {
                    '"', '\\' -> {
                        expectingSlash = false
                    }
                    else      -> throw SerializationException("Невалидный специальный символ, разрешены только '\"' и '\\'")
                }
            } else {
                when {
                    c == '"'         -> {
                        excludedIndicies.add(this.pos)
                        inString = !inString
                    }

                    c == '\\'        -> if (inString) {
                        excludedIndicies.add(this.pos)
                        expectingSlash = true
                    }

                    c.isWhitespace() -> if (!inString) {
                        break@value
                    }

                    c == '}'         -> if (!inString) {
                        this.pos--
                        break@value
                    }

                    c == '{'         -> if (valueStartPos == this.pos) {
                        this.pos--
                        break@value
                    }
                }

            }
        }

        val value: String? =
            if (this.pos == valueStartPos && this.raw[this.pos] == '{') null
            else this.raw.slice(IntRangeWithExclude(excludedIndicies, valueStartPos, this.pos))

        return Entity.Pair(key, value)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun clear() {
        this.lastEntity = null
    }

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (this.nextEntry() != Entity.ObjectOpen) throw SerializationException("Не получается привести примитивное значение к объекту")
        this.clear()
        return this
    }

    private inline fun <T> decode(converter: String.() -> T): T {
        val e = this.lastEntity ?: throw IllegalStateException("Pair not fetched yet")
        if (e !is Entity.Pair || e.value == null) {
            throw SerializationException("Объект не может быть примитвом")
        }
        this.clear()
        return converter(e.value)
    }

    override fun decodeBoolean(): Boolean =
        this.decode {
            lowercase().toBooleanStrictOrNull() ?: throw SerializationException("Неправильное логическое значение")
        }

    override fun decodeByte(): Byte =
        this.decode {
            toByteOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    override fun decodeChar(): Char =
        this.decode {
            if (length != 1) throw SerializationException("Неправильное количество символов")
            get(0)
        }

    override fun decodeDouble(): Double =
        this.decode {
            toDoubleOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        this.decode {
            enumDescriptor.elementNames
                .indexOf(this@decode)
                .apply index@{ if (this@index < 0) throw SerializationException("Неизвестное значение перечисления") }
        }

    override fun decodeFloat(): Float =
        this.decode {
            toFloatOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    @ExperimentalSerializationApi
    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder {
        return this
    }

    override fun decodeInt(): Int =
        this.decode {
            toIntOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    override fun decodeLong(): Long =
        this.decode {
            toLongOrNull() ?: throw SerializationException("Неправильное числовое значение")
        }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean =
        this.decode {
            lowercase() != "null"
        }

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? =
        this.decode {
            if (lowercase() != "null") throw SerializationException("Неправильный null")
            null
        }

    override fun decodeShort(): Short =
        this.decode {
            toShortOrNull() ?: throw SerializationException("Неправильное числовое значение")

        }

    override fun decodeString(): String =
        this.decode {
            this@decode
        }

    private var currentElementIndex: Int? = null

    private inline fun validateIndex(
        @Suppress("UNUSED_PARAMETER")
        descriptor: SerialDescriptor,
        index: Int
    ) {
        val cei = this.currentElementIndex ?: throw IllegalStateException()
        if (cei != index) {
            throw IllegalArgumentException("Invalid index")
        }
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        this.validateIndex(descriptor, index)
        return this.decodeBoolean()
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
        this.validateIndex(descriptor, index)
        return this.decodeByte()
    }

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
        this.validateIndex(descriptor, index)
        return this.decodeChar()
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
        this.validateIndex(descriptor, index)
        return this.decodeDouble()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        when (val e = this.nextEntry()) {
            is Entity.Pair -> {
                return descriptor.elementNames
                    .indexOf(e.key)
                    .apply index@{ if (this@index < 0) throw SerializationException("Поле ${e.key} не найдено") }
            }
            else           -> throw SerializationException()
        }
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
        this.validateIndex(descriptor, index)
        return this.decodeFloat()
    }

    @ExperimentalSerializationApi
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder {
        this.validateIndex(descriptor, index)
        return this
    }

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
        this.validateIndex(descriptor, index)
        return this.decodeInt()
    }

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
        this.validateIndex(descriptor, index)
        return this.decodeLong()
    }

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, previousValue: T?): T? {
        this.validateIndex(descriptor, index)
        @Suppress("LiftReturnOrAssignment")
        if (this.decodeNotNullMark()) return this.decodeSerializableElement(descriptor, index, deserializer, previousValue)
        else return this.decodeNull()
    }

    override fun <T> decodeSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, previousValue: T?): T {
        this.validateIndex(descriptor, index)
        return deserializer.deserialize(this)
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short {
        this.validateIndex(descriptor, index)
        return this.decodeShort()
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        this.validateIndex(descriptor, index)
        return this.decodeString()
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if (this.nextEntry() !is Entity.ObjectClose) throw SerializationException("Конец объекта не получен")
        this.clear()
    }
}

