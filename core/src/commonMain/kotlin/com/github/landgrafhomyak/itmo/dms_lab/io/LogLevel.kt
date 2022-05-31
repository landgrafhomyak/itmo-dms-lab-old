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
    @DisplayName("Отладка")
    DEBUG,

    /**
     * Обычный информационный вывод
     */
    @SerialName("i")
    @DisplayName("")
    INFO,

    /**
     * Предупреждение
     */
    @SerialName("w")
    @DisplayName("Предупреждение")
    WARNING,

    /**
     * Ошибка
     */
    @SerialName("e")
    @DisplayName("Ошибка")
    ERROR,

    /**
     * Неотловленное [исключение][Throwable] или ошибка после которой приложение прекращает свою работу
     */
    @SerialName("x")
    @DisplayName("Критическая ошибка")
    FATAL
}