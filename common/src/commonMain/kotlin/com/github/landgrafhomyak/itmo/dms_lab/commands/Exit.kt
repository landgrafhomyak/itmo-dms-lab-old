package com.github.landgrafhomyak.itmo.dms_lab.commands

import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `exit`
 * @sample Exit.help
 */
@Suppress("unused")
object Exit : BoundRequest(Exit), RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val id: String = "exit"
    override val help: String = "Завершает скрипт"
}