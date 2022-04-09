package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptOutput
import com.github.landgrafhomyak.itmo.dms_lab.collections.LabWorksCollection

/**
 * Конечный объект команды `help`
 * @see CommandMeta.HELP
 */
object Help : BoundCommand(CommandMeta.HELP) {
    override fun applyTo(logger: ScriptOutput, collection: LabWorksCollection) {
        val idMaxSize = CommandMeta.all().maxOf { cmd -> cmd.id.length }
        CommandMeta.all().joinToString(separator = "\n") { cmd ->
            "${cmd.id.padEnd(idMaxSize, ' ')} - ${cmd.help}"
        }.also(logger::info)
    }
}