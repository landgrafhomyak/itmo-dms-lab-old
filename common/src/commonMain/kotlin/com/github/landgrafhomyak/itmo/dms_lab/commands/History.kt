package com.github.landgrafhomyak.itmo.dms_lab.commands

/**
 * Конечный объект команды `history`
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
object History : BoundCommand(Meta) {
    object Meta : CommandMeta() {
        override val id: String = "help"
        override val help: String = "Выводит последние 10 команд"
    }
}