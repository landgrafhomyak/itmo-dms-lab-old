package com.github.landgrafhomyak.itmo.dms_lab.requests

import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `history`
 * @sample History.help
 */
@Suppress("unused")
object History : BoundRequest(), RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val meta: RequestMeta
        get() = this

    override val id: String = "help"
    override val help: String = "Выводит последние 10 запросов"


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean = this === other

    override fun hashCode(): Int = super.hashCode()
}