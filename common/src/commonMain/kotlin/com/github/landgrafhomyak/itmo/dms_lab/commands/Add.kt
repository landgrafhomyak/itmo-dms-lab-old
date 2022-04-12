package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `add`
 * @param factory элемент, копии которого будут добавляться в коллекцию
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
class Add(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: LabWork
) : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "add"
        override val help: String = "Добавляет новый элемент в коллекцию"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        collection.add(this.factory.copy())
        logger.info("Элемент успешно добавлен")
    }
}