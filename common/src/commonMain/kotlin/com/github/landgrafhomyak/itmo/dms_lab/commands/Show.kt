package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `show`
 * @see Show.Meta
 * @sample Meta.help
 */
@Suppress("unused")
object Show : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "show"
        override val help: String = "Выводит все элементы коллекции"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection) {
            logger.sendObject(elem)
        }
    }
}