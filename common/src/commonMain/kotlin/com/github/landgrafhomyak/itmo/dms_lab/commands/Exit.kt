package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.commands.Exit.Meta

/**
 * Конечный объект команды `exit`
 * @see Exit.Meta
 * @sample Meta.help
 */
@Suppress("unused")
object Exit : BoundCommand(Meta) {
    object Meta : CommandMeta() {
        override val id: String = "exit"
        override val help: String = "Завершает скрипт"
    }
}