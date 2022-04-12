package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `info`
 * @see Info.Meta
 * @sample Meta.help
 */
@Suppress("unused")
object Info : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "info"
        override val help: String = "Выводит информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        logger.info(
            "Коллекция лабораторных работ была создана ${collection.creationDate} и имеет ${collection.size}"
        )
    }
}