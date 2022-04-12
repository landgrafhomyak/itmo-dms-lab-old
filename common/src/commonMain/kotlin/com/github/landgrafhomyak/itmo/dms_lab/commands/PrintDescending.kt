package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `print_descending`
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
object PrintDescending : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "print_descending"
        override val help: String = "Выводит все элементы коллекции в порядке убывания по координате X"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection.descendingByCoordinateX()) {
            logger.sendObject(elem)
        }
    }
}