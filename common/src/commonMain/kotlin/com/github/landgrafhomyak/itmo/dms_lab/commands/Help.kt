package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `help`
 * @sample Help.help
 */
@Suppress("unused")
object Help : BoundRequest(), RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val meta: RequestMeta
        get() = Help

    override val id: String = "help"
    override val help: String = "Выводит справку по доступным запросам"

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    suspend fun print(logger: Logger, commands: Iterable<RequestMeta>) {
        val idMaxSize = commands.maxOf { cmd -> cmd.id.length }
        commands.joinToString(separator = "\n") { cmd ->
            "${cmd.id.padEnd(idMaxSize, ' ')} - ${cmd.help}"
        }.also { s ->
            logger.info(s)
        }
    }

    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean = this === other

    override fun hashCode(): Int = super.hashCode()
}