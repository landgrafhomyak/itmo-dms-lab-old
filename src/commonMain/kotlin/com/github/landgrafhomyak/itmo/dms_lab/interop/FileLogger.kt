package com.github.landgrafhomyak.itmo.dms_lab.interop

/**
 * [Логгер][Logger] выводящий логи в файл, реализация для разных платформ различается
 * @param path путь к файлу
 * @see ConsoleLogger
 * @see AbstractTextLogger
 */
@Suppress( "SpellCheckingInspection")
public expect class FileLogger public constructor(path: String) : AbstractTextLogger {
    /**
     * Закрывает файл
     */
    public suspend fun close()
}