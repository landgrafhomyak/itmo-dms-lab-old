package com.github.landgrafhomyak.itmo.dms_lab.commands

import kotlin.jvm.JvmField

/**
 * Пустая запрос
 * @see Meta
 * @sample Empty.help
 */
@Suppress("unused")
object Empty : BoundRequest(Empty), RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this


    override val id: String = ""
    override val help: String = "Пустой запрос"

}