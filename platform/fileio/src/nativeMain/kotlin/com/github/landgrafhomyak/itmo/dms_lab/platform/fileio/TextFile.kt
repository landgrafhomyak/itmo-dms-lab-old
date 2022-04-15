package com.github.landgrafhomyak.itmo.dms_lab.platform.fileio

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fputs
import platform.posix.fread

/**
 * Платформо-зависимая обёртка над операциями с локальным текстовым файлом
 * @constructor Оборачивает [platform.posix.FILE]
 */
@Suppress("SpellCheckingInspection", "unused")
actual class TextFile(
    @Suppress("MemberVisibilityCanBePrivate")
    val cFile: CPointer<FILE>
) {
    /**
     * открывает файл по указанному пути
     */
    actual constructor(path: String) : this(fopen(path, "a+t") ?: throw IllegalArgumentException("Не удаётся открыть файл '$path'"))

    /**
     * Считывает из файла не больше [count] символов
     */
    actual fun readChars(count: UInt): String = ByteArray(count.toInt()).usePinned { pinned ->
        val readCount = fread(pinned.addressOf(0), 1uL, count.toULong(), this.cFile)
        if (readCount == count.toULong()) {
            return@usePinned pinned.get()
        } else {
            return@usePinned pinned.get().copyOfRange(0, readCount.toInt())
        }
    }.toKString()

    /**
     * Считывает их файла строку
     */
    actual fun readLine(): String {
        val chuks = mutableListOf<ByteArray>()
        while (true) {
            val ba = ByteArray(256)
            val readCount = ba.usePinned { pinned ->
                return@usePinned fread(pinned.addressOf(0), 1uL, 256uL, this.cFile)
            }
            if (readCount == 0uL) {
                break
            }

            val endIndex = ba.indexOf(0xA)
            if (endIndex >= 0) {
                chuks.add(ba.copyOfRange(0, endIndex))
                break
            }

            if (readCount < 256uL) {
                chuks.add(ba.copyOfRange(0, readCount.toInt()))
            } else {
                chuks.add(ba)
            }
        }
        return chuks.joinToString { ba -> ba.toKString() }
    }

    /**
     * Дописывает строку в конец файла
     */
    actual fun write(string: String) {
        fputs(string, this.cFile)
    }

    /**
     * Закрывает файл. После этого над файлом нельзя производить никакие операции
     */
    actual fun close() {
        fclose(this.cFile)
    }

    /**
     * Проверяет, что файл доступен для чтения
     */
    actual val isReadable: Boolean
        get() = true

    /**
     * Проверяет, что файл доступен для записи
     */
    actual val isWritable: Boolean
        get() = true

}