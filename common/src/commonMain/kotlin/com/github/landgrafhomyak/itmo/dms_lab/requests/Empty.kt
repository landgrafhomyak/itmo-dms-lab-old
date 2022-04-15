package com.github.landgrafhomyak.itmo.dms_lab.requests

import kotlin.jvm.JvmField

/**
 * Пустая запрос
 * @see Meta
 * @sample Empty.help
 */
@Suppress("unused")
object Empty : BoundRequest(), RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val meta: RequestMeta
        get() = this

    override val id: String = ""
    override val help: String = "Пустой запрос"


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean = this === other

    override fun hashCode(): Int = super.hashCode()
}