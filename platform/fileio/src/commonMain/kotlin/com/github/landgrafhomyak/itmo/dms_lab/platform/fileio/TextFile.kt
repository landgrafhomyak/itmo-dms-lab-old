package com.github.landgrafhomyak.itmo.dms_lab.platform.fileio

/**
 * Платформо-зависимая обёртка над операциями с локальным текстовым файлом
 * @constructor открывает файл по указанному пути
 */
@Suppress("SpellCheckingInspection", "unused")
expect class TextFile(path: String) {
    /**
     * Считывает из файла не больше [count] символов
     */
    fun readChars(count: UInt): String

    /**
     * Считывает их файла строку
     */
    fun readLine(): String

    /**
     * Дописывает строку в конец файла
     */
    fun write(string: String)

    /**
     * Закрывает файл. После этого над файлом нельзя производить никакие операции
     */
    fun close()

    /**
     * Проверяет, что файл доступен для чтения
     */
    val isReadable: Boolean

    /**
     * Проверяет, что файл доступен для записи
     */
    val isWritable: Boolean
}