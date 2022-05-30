package com.github.landgrafhomyak.itmo.dms_lab.io

import kotlinx.serialization.encoding.Encoder

/**
 * Модификация [энкодера][Encoder] для разделения генерации сериализованных данных и
 * дорогостоящей операции для их отправки или отображения
 */
@Suppress("SpellCheckingInspection")
public interface FlushableEncoder: Encoder {
    /**
     * Отправляет или отображает сериализованные данные
     */
    public suspend fun flush()
}