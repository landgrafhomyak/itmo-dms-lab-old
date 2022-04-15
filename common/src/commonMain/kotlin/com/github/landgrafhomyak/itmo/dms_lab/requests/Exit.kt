package com.github.landgrafhomyak.itmo.dms_lab.requests

import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `exit`
 * @sample Exit.help
 */
@Suppress("unused")
object Exit : BoundRequest(), RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val meta: RequestMeta
        get() = Exit

    override val id: String = "exit"
    override val help: String = "Завершает скрипт"

    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean = this === other

    override fun hashCode(): Int = super.hashCode()
}