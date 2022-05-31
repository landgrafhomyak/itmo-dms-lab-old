@file:OptIn(ExperimentalSerializationApi::class)

package com.github.landgrafhomyak.itmo.dms_lab.io

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * Форматирует объект в текстовый вид
 * ```
 * Class @DisplayName:
 *   Field1 @DisplayName:
 *     Field11 @DisplayName: value
 *     Field12 @DisplayName: value
 *   Field2 @DisplayName: value
 * ```
 *
 * @param builder строка в которую будет напечатан объект
 * @param initialIndent начальный отступ
 * @see DisplayName
 */
public class TextViewEncoder(
    private val builder: StringBuilder,
    private val initialIndent: UInt = 0u
) : Encoder, CompositeEncoder {
    /**
     * Текущий отступ
     */
    private var indent: UInt = 0u

    /**
     * Сокращение от [TextViewEncoder.builder].[append][StringBuilder.append]
     */
    private inline fun add(s: String) = this.builder.append(s)

    override val serializersModule: SerializersModule
        get() = EmptySerializersModule


    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (this.indent == 0u) {
            this.add(this.buildIndent)
            this.add(descriptor.displayOrSerialName)
        }
        this.add("\n")

        this.indent++
        return this
    }

    override fun encodeBoolean(value: Boolean) {
        this.add(value.toString())
    }

    override fun encodeByte(value: Byte) {
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeChar(value: Char) {
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeDouble(value: Double) {
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        this.add(enumDescriptor.getElementDisplayOrSerialName(index))
        this.add("\n")
    }

    override fun encodeFloat(value: Float) {
        this.add(value.toString())
        this.add("\n")
    }

    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
        return this
    }

    override fun encodeInt(value: Int) {
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeLong(value: Long) {
        this.add(value.toString())
        this.add("\n")
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        this.add("null\n")
    }

    override fun encodeShort(value: Short) {
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeString(value: String) {
        this.add(value)
        this.add("\n")
    }

    /**
     * Генерирует строку с отступом длины [TextViewEncoder.indent]
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline val buildIndent: String
        get() = "  ".repeat((this.indent + this.initialIndent).toInt())


    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        return this
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        if (value == null) this.add("null")
        else serializer.serialize(this, value)
    }

    override fun <T> encodeSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        serializer.serialize(this, value)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value.toString())
        this.add("\n")
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        this.add(this.buildIndent)
        this.add(descriptor.getElementDisplayOrSerialName(index))
        this.add(": ")
        this.add(value)
        this.add("\n")
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        this.indent--
    }
}