package io.github.landgrafhomyak.itmo.dms_lab.interop

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
    @DisplayName("DEBUG")
    DEBUG,

    /**
     * Обычный информационный вывод
     */
    @SerialName("i")
    @DisplayName("INFO")
    INFO,

    /**
     * Предупреждение
     */
    @SerialName("w")
    @DisplayName("WARNING")
    WARNING,

    /**
     * Ошибка
     */
    @SerialName("e")
    @DisplayName("ERROR")
    ERROR,

    /**
     * Неотловленное [исключение][Throwable] или ошибка после которой приложение прекращает свою работу
     */
    @SerialName("x")
    @DisplayName("FATAL")
    FATAL
}