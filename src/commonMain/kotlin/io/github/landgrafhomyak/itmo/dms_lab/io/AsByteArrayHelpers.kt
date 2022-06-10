@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.landgrafhomyak.itmo.dms_lab.io

import kotlin.jvm.JvmInline

/**
 * Конвертирует ASCII символ в байт
 */
internal inline val Char.ub: UByte
    get() {
        require(this.code in (UByte.MIN_VALUE.toInt())..(Byte.MAX_VALUE)) { "Overflowed byte value" }
        return this.code.toUByte()
    }

/**
 * Конвертирует байт в символ
 */
internal inline val UByte.c: Char
    get() = this.toByte().toInt().toChar()

/**
 * Конвертирует символ в [UByteArray]
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun ubyteArrayOf(char: Char): UByteArray {
    return ubyteArrayOf(char.ub)
}

/**
 * Кодирует число в [big-endian](https://ru.wikipedia.org/wiki/%D0%9F%D0%BE%D1%80%D1%8F%D0%B4%D0%BE%D0%BA_%D0%B1%D0%B0%D0%B9%D1%82%D0%BE%D0%B2#%D0%9F%D0%BE%D1%80%D1%8F%D0%B4%D0%BE%D0%BA_%D0%BE%D1%82_%D1%81%D1%82%D0%B0%D1%80%D1%88%D0%B5%D0%B3%D0%BE_%D0%BA_%D0%BC%D0%BB%D0%B0%D0%B4%D1%88%D0%B5%D0%BC%D1%83)
 * последовательность байтов
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun encodeNumber(value: ULong, size: Int): UByteArray {
    @Suppress("NAME_SHADOWING")
    var value = value

    @Suppress("NAME_SHADOWING")
    var size = size
    val uba = UByteArray(size)
    while (size-- > 0) {
        uba[size] = (value % 256UL).toUByte()
        value /= 256UL
    }
    return uba
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun UByteArrayBuilder.encodeNumber(value: ULong, size: Int) {
    this.add(io.github.landgrafhomyak.itmo.dms_lab.io.encodeNumber(value, size))
}

/**
 * Декодирует число из [big-endian](https://ru.wikipedia.org/wiki/%D0%9F%D0%BE%D1%80%D1%8F%D0%B4%D0%BE%D0%BA_%D0%B1%D0%B0%D0%B9%D1%82%D0%BE%D0%B2#%D0%9F%D0%BE%D1%80%D1%8F%D0%B4%D0%BE%D0%BA_%D0%BE%D1%82_%D1%81%D1%82%D0%B0%D1%80%D1%88%D0%B5%D0%B3%D0%BE_%D0%BA_%D0%BC%D0%BB%D0%B0%D0%B4%D1%88%D0%B5%D0%BC%D1%83)
 * последовательности байтов
 */
internal inline fun decodeNumber(size: Int, getter: () -> UByte): ULong {
    @Suppress("NAME_SHADOWING")
    var size: Int = size
    var number: ULong = 0U

    while (size-- > 0) {
        number = number * 256U + getter()
    }
    return number
}

internal enum class TypeMarkers(val c: Char) {
    STRUCT_BEGIN('{'),
    STRUCT_END('}'),
    STRING('s'),
    CHAR('c'),
    BYTE('b'),
    SHORT('s'),
    INT('i'),
    LONG('l'),
    FLOAT('f'),
    DOUBLE('d'),
    ENUM('#'),
    BOOLEAN('?'),
    NULL('@')
}


@JvmInline
internal value class UByteArrayBuilder private constructor(private val parts: MutableList<Parts>) {
    constructor() : this(mutableListOf())

    private sealed interface Parts {
        @JvmInline
        value class Single(val char: UByte) : Parts

        @JvmInline
        value class Seq(val array: UByteArray) : Parts
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun add(marker: TypeMarkers) = this.add(marker.c)

    @Suppress("NOTHING_TO_INLINE")
    inline fun add(char: Char) {
        require(char.code in UByte.MIN_VALUE.toUInt().toInt() until UByte.MAX_VALUE.toUInt().toInt()) { "Non ascii character" }
        this.add(char.code.toUInt().toUByte())
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun add(byte: UByte) {
        this.parts.add(Parts.Single(byte))
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun add(array: UByteArray) {
        this.parts.add(Parts.Seq(array))
    }

    fun build(): UByteArray {
        val size = this.parts.sumOf { b ->
            when (b) {
                is Parts.Single -> 1
                is Parts.Seq    -> b.array.size
            }
        }
        var i = 0
        val arr = UByteArray(size)
        for (p in this.parts) {
            when (p) {
                is Parts.Single -> arr[i++] = p.char
                is Parts.Seq    -> {
                    p.array.copyInto(arr, i)
                    i += p.array.size
                }
            }
        }
        return arr
    }
}


internal fun UByteArrayBuilder.encodeString(s: String) {
    val encoded = s.encodeToByteArray().asUByteArray()
    this.encodeNumber(encoded.size.toUInt().toULong(), Int.SIZE_BYTES)
    this.add(encoded)
}