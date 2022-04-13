package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `save`
 * @sample Meta.help
 */
@Suppress("unused")
class Save : BoundRequest(Meta), ApplicableToCollection {
    companion object Meta : RequestMeta {
        override val id: String = "save"
        override val help: String = "Экспортирует коллекцию"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection)
    {
        TODO()
    }
}