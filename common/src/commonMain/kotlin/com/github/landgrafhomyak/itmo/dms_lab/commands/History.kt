package com.github.landgrafhomyak.itmo.dms_lab.commands

import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `history`
 * @sample History.help
 */
@Suppress("unused")
object History : BoundRequest(History), RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val id: String = "help"
    override val help: String = "Выводит последние 10 запросов"
}