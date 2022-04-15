package com.github.landgrafhomyak.itmo.dms_lab

/**
 * Замыкание для предварительного создания объектов и получения их без дополнительных аргументов
 */
interface Factory<T> {
    /**
     * Возвращает новый объект
     */
    fun build(): T

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}