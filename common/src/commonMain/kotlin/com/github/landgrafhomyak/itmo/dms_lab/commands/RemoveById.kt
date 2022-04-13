package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `remove_by_id`
 * @param id идентификатор элемента, который надо удалить
 * @sample RemoveById.help
 */
@Suppress("unused")
class RemoveById(
    @Suppress("MemberVisibilityCanBePrivate")
    val id: LabWorkId,
) : BoundRequest(Meta), ApplicableToCollection {
    companion object Meta : RequestMeta {
        override val id: String = "remove_by_id"
        override val help: String = "Удаляет элемент из коллекции по его идентификатору"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        if (collection.remove(this.id) != null) {
            logger.info("Элемент успешно удалён")
        } else {
            logger.error("Элемента с идентификатором $id нет в коллекции, нечего удалять")
        }
    }
}