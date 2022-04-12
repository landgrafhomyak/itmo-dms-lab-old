package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `add_if_max`
 * @param factory элемент, копии которого будут добавляться в коллекцию
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
class AddIfMax(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: LabWork
) : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "add"
        override val help: String = "Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции по координате X"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        if (collection.addIfGreatestCoordinateX(this.factory.copy())) {
            logger.info("Элемент успешно добавлен")
        } else {
            logger.info("Элемент не был добавлен")
        }
    }
}