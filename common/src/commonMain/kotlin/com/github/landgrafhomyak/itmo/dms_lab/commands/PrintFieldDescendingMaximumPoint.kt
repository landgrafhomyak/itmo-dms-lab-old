package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `print_field_descending_maximum_point`
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
object PrintFieldDescendingMaximumPoint : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "print_field_descending_maximum_point"
        override val help: String = "Выводит все элементы коллекции в порядке убывания по максимальной точке"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection.descendingByMaximumPoint()) {
            logger.sendObject(elem)
        }
    }
}