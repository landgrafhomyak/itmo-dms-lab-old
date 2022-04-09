package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptOutput
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды <code>show</code>
 * @see CommandMeta.SHOW
 */
object Show : BoundCommand(CommandMeta.SHOW) {
    override fun applyTo(logger: ScriptOutput, collection: LabWorksCollection) = collection.forEach { elem ->
        TODO()
    }
}