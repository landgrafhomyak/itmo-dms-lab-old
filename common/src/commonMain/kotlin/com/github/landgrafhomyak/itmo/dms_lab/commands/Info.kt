package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptOutput
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды <code>info</code>
 * @see CommandMeta.INFO
 */
object Info : BoundCommand(CommandMeta.INFO) {
    override fun applyTo(logger: ScriptOutput, collection: LabWorksCollection) {
        TODO()
    }
}