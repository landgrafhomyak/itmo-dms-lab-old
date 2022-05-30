package com.github.landgrafhomyak.itmo.dms_lab.io

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Уровень логгирования
 */
@Suppress("SpellCheckingInspection")
@Serializable
public enum class LogLevel {
    /**
     * Отладочный вывод
     */
    @SerialName("d")
    DEBUG,

    /**
     * Обычный информационный вывод
     */
    @SerialName("i")
    INFO,

    /**
     * Предупреждение
     */
    @SerialName("w")
    WARNING,

    /**
     * Ошибка
     */
    @SerialName("e")
    ERROR,

    /**
     * Неотловленное [исключение][Throwable] или ошибка после которой приложение прекращает свою работу
     */
    @SerialName("x")
    FATAL
}