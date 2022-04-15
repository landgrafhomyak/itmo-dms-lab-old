package com.github.landgrafhomyak.itmo.dms_lab.platform.fileio

import java.io.BufferedReader
import java.io.File

/**
 * Платформо-зависимая обёртка над операциями с локальным текстовым файлом
 * @constructor оборачивает [java.io.File]
 * @property jFile оригинальный объект файла, над которым производятся операции
 */
@Suppress("SpellCheckingInspection", "unused")
actual class TextFile(
    @Suppress("MemberVisibilityCanBePrivate")
    val jFile: File
) {
    init {
        if (!this.jFile.isFile) {
            throw IllegalArgumentException("\"${this.jFile.name}\" не является файлом")
        }
    }

    /**
     * Вспомогательный объект для чтения из файла
     */
    private val reader = BufferedReader(this.jFile.reader(Charsets.UTF_8))

    /**
     * Открывает файл по указанному пути
     */
    actual constructor(path: String) : this(File(path))

    /**
     * Считывает из файла не больше [count] символов
     */
    actual fun readChars(count: UInt): String {
        val array = CharArray(count.toInt())
        return when (val charsRead = this.reader.read(array, 0, count.toInt())) {
            -1   -> ""
            else -> String(array.copyOfRange(0, charsRead))
        }

    }

    actual fun readLine(): String = this.reader.readLine()!!

    /**
     * Дописывает строку в конец файла
     */
    actual fun write(string: String) {
        this.jFile.appendText(string, Charsets.UTF_8)
    }

    /**
     * Закрывает файл. После этого над файлом нельзя производить никакие операции
     */
    actual fun close() {}

    /**
     * Проверяет, что файл доступен для чтения
     */
    actual val isReadable: Boolean
        inline get() = this.jFile.canRead()

    /**
     * Проверяет, что файл доступен для записи
     */
    actual val isWritable: Boolean
        inline get() = this.jFile.canWrite()
}