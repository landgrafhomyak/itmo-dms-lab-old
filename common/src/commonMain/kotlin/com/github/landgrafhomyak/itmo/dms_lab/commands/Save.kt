package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `save`
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
class Save : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "save"
        override val help: String = "Экспортирует коллекцию"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection)
    {
        TODO()
    }
}