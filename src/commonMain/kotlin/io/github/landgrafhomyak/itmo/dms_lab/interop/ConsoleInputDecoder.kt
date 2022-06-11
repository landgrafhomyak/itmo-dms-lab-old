package io.github.landgrafhomyak.itmo.dms_lab.interop

import io.github.landgrafhomyak.itmo.dms_lab.io.Coloring
import io.github.landgrafhomyak.itmo.dms_lab.io.NoColoring
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * Исключение сигнализирующее о том, что пользователь исчерпал свои попытки при вводе
 */
public class TriesLimitExceedException : SerializationException("Количество попыток исчерпано")

/**
 * Предоставляет интерактивный ввод через консоль
 * @param triesCount максимальное количество попыток
 */
public class ConsoleInputDecoder(private val triesCount: UInt, private val coloring: Coloring = NoColoring) : Decoder, CompositeDecoder {
    /**
     * Стек имён структур и полей
     */
    private val nameStack = mutableListOf<String>()

    init {
        require(triesCount > 0u) { "Tries count must be greater than 0" }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule

    /**
     * Стек индексов в структурах
     */
    private val indexStack = mutableListOf<Int>()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        // if (this.nameStack.isEmpty()) this.nameStack.add(descriptor.displayOrSerialName)
        this.indexStack.add(0)
        return this
    }

    /**
     * Собирает [стек имён][ConsoleInputDecoder.nameStack] в строку
     */
    private inline val buildClassStack: String
        get() = this.nameStack.joinToString(separator = " -> ")

    /**
     * Опрашивает пользователя [triesCount][ConsoleInputDecoder.triesCount] раз
     * @param allowedValues аргумент функции [title][ConsoleInputDecoder.title]
     * @param defaultValue аргумент функции [emptyValue][ConsoleInputDecoder.emptyValue], если равен `null` проверка не проводится
     * @param getter декодер вызываемый в цикле, попытка считается неуспешной если вернул `null`
     */
    private inline fun <T : Any> tries(allowedValues: String, defaultValue: String? = null, getter: (String) -> T?): T {
        this.title(allowedValues)
        for (tryNumber in 0u until this.triesCount) {
            val raw = readln().trim()
            if (raw.isBlank() && defaultValue != null) {
                this.emptyValue(defaultValue)
                continue
            }
            val parsed = getter(raw)
            if (parsed != null) return parsed
        }
        println("Достигнут лимит попыток")
        throw TriesLimitExceedException()
    }

    /**
     * Информирует пользователя о том что именно ему надо ввести
     * @param allowedValues разрешённые значения
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun title(allowedValues: String) {
        println("Введите $allowedValues '${this.buildClassStack}':")
    }

    /**
     * Сообщение о том что введено недопустимое пустое значение
     * @param defaultValue значение по умолчанию, ассоциированное с пустым значением
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun emptyValue(defaultValue: String): Nothing? {
        println("Пустое значение можно было бы расценить как $defaultValue, но нет")
        return null
    }

    /**
     * Сообщение о том что введено недопустимое значение
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun invalidValue(message: String = "Невалидное значение"): Nothing? {
        println(message)
        return null
    }

    override fun decodeBoolean(): Boolean = this.tries(
        "yes/no/да/нет/true/false",
        "false"
    ) { raw ->
        return@tries when {
            "да".startsWith(raw)  -> true
            "yes".startsWith(raw) -> true
            "нет".startsWith(raw) -> false
            "no".startsWith(raw)  -> false
            raw == "true"         -> true
            raw == "false"        -> false
            else                  -> this.invalidValue()
        }
    }

    override fun decodeByte(): Byte = this.tries(
        "целое число в диапазоне [0; ${UByte.MAX_VALUE}]",
        "0"
    ) { raw ->
        return@tries raw.toUByteOrNull()?.toByte() ?: this.invalidValue()
    }

    override fun decodeChar(): Char = this.tries(
        "символ",
        "\\0"
    ) { raw ->
        if (raw.length != 1) return@tries this.invalidValue("Нужен ровно 1 символ")
        return@tries raw[0]
    }

    override fun decodeDouble(): Double = this.tries(
        "вещественное число",
        "0"
    ) { raw ->
        return@tries raw.toDoubleOrNull() ?: this.invalidValue()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val names = enumDescriptor
            .elementDescriptors
            .withIndex()
            .associate { (i, d) -> d.displayOrSerialName to i }

        if (names.size != names.keys.size) {
            throw SerializationException("Enum has duplicated names")
        }

        return this.tries(
            names.keys.joinToString(separator = "/")
        ) { raw ->
            names[raw] ?: this.invalidValue()
        }
    }

    override fun decodeFloat(): Float = this.tries(
        "вещественное число",
        "0"
    ) { raw ->
        return@tries raw.toFloatOrNull() ?: this.invalidValue()
    }

    @ExperimentalSerializationApi
    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder {
        return this
    }

    override fun decodeInt(): Int = this.tries(
        "целое число в диапазоне [0; ${UInt.MAX_VALUE}]",
        "0"
    ) { raw ->
        return@tries raw.toUIntOrNull()?.toInt() ?: this.invalidValue()
    }

    override fun decodeLong(): Long = this.tries(
        "целое число в диапазоне [0; ${ULong.MAX_VALUE}]",
        "0"
    ) { raw ->
        return@tries raw.toULongOrNull()?.toLong() ?: this.invalidValue()
    }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean {
        TODO("Not yet implemented")
    }

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? {
        TODO("Not yet implemented")
    }

    override fun decodeShort(): Short = this.tries(
        "целое число в диапазоне [0; ${UShort.MAX_VALUE}]",
        "0"
    ) { raw ->
        return@tries raw.toUShortOrNull()?.toShort() ?: this.invalidValue()
    }

    override fun decodeString(): String = this.tries(
        "строку"
    ) { raw -> raw }

    private inline fun <T> injectName(descriptor: SerialDescriptor, index: Int, getter: () -> T): T {
        this.nameStack.add(descriptor.getElementDisplayOrSerialName(index))
        val parsed = getter()
        this.nameStack.removeLast()
        return parsed
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        this.injectName(descriptor, index, this::decodeBoolean)

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        this.injectName(descriptor, index, this::decodeByte)

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        this.injectName(descriptor, index, this::decodeChar)

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        this.injectName(descriptor, index, this::decodeDouble)

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        this.indexStack
            .lastOrNull()
            .run index@{ this@index ?: throw IllegalStateException("Missed index state") }
            .takeIf { i -> i < descriptor.elementsCount }
            ?.apply index@{
                this@ConsoleInputDecoder.indexStack[this@ConsoleInputDecoder.indexStack.lastIndex] = this@index + 1
            } ?: CompositeDecoder.DECODE_DONE


    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        this.injectName(descriptor, index, this::decodeFloat)

    @ExperimentalSerializationApi
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = this

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        this.injectName(descriptor, index, this::decodeInt)

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        this.injectName(descriptor, index, this::decodeLong)

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, previousValue: T?): T? =
        this.injectName(descriptor, index) { deserializer.deserialize(this) } // TODO

    override fun <T> decodeSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, previousValue: T?): T =
        this.injectName(descriptor, index) { deserializer.deserialize(this) }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        this.injectName(descriptor, index, this::decodeShort)


    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
        this.injectName(descriptor, index, this::decodeString)


    override fun endStructure(descriptor: SerialDescriptor) {
        this.indexStack.removeLastOrNull() ?: throw IllegalStateException("Index stack was corrupted")
        // if (this.indexStack.isNotEmpty())
        //    this.nameStack.removeLastOrNull() ?: throw IllegalStateException("Class names stack was corrupted")
    }
}