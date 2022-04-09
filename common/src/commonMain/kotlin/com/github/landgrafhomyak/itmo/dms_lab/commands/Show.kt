package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptOutput
import com.github.landgrafhomyak.itmo.dms_lab.collections.LabWorksCollection

/**
 * Конечный объект команды `show`
 * @see CommandMeta.SHOW
 */
object Show : BoundCommand(CommandMeta.SHOW) {
    override fun applyTo(logger: ScriptOutput, collection: LabWorksCollection) = TODO() /*collection.forEach { elem ->
        TODO()
    }*/
}