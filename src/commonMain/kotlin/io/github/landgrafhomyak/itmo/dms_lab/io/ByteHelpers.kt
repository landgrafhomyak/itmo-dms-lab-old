@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.landgrafhomyak.itmo.dms_lab.io

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
    val bytes = mutableListOf<UByte>()
    while (value != 0UL) {
        bytes.add((value % 256UL).toUByte())
        value /= 256UL
    }
    return UByteArray(size - bytes.size) { 0u } + bytes.reversed()
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