package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `info`
 * @see CommandMeta.INFO
 */
object Info : BoundCommand(CommandMeta.INFO) {
    override fun applyTo(logger: Logger, collection: LabWorksCollection) {
        TODO()
    }
}