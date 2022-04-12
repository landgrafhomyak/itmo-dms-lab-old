package com.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Конструктор потока с командами для скрипта
 */
@Suppress("unused")
interface ScriptSourceFactory {
    /**
     * Аргумент, на основании которого был определён поток
     */
    val commandArgument: String

    /**
     * Возвращает готовый поток
     */
    fun build(logger: Logger): ScriptSource
}