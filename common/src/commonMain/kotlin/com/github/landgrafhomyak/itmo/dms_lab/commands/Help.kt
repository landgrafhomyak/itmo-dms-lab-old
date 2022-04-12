package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger

/**
 * Конечный объект команды `help`
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
object Help : BoundCommand(Meta) {
    object Meta : CommandMeta() {
        override val id: String = "help"
        override val help: String = "Выводит справку по доступным командам"
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    suspend fun print(logger: Logger, commands: Iterable<CommandMeta>) {
        val idMaxSize = commands.maxOf { cmd -> cmd.id.length }
        commands.joinToString(separator = "\n") { cmd ->
            "${cmd.id.padEnd(idMaxSize, ' ')} - ${cmd.help}"
        }.also { s ->
            logger.info(s)
        }
    }
}