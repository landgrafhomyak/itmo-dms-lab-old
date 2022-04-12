package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `clear`
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
object Clear : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "clear"
        override val help: String = "Очищает коллекцию"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        collection.clear()
        logger.info("Коллекция очищена")
    }
}