package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `add`
 * @param factory элемент, копии которого будут добавляться в коллекцию
 * @sample Add.help
 */
@Suppress("unused")
class Add(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: LabWork
) : BoundRequest(Meta), ApplicableToCollection {
    companion object Meta : RequestMeta {
        override val id: String = "add"
        override val help: String = "Добавляет новый элемент в коллекцию"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        collection.add(this.factory.copy())
        logger.info("Элемент успешно добавлен")
    }
}