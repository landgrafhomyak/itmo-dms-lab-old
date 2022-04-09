package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptOutput
import com.github.landgrafhomyak.itmo.dms_lab.collections.LabWorksCollection

/**
 * Конечный объект команды `info`
 * @see CommandMeta.INFO
 */
object Info : BoundCommand(CommandMeta.INFO) {
    override fun applyTo(logger: ScriptOutput, collection: LabWorksCollection) {
        TODO()
    }
}