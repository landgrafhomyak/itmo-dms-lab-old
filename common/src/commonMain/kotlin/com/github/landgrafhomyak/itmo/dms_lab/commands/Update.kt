package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект команды `add`
 * @param id идентификатор элемента, который надо заменить
 * @param factory элемент, копии которого будут добавляться в коллекцию
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
class Update(
    @Suppress("MemberVisibilityCanBePrivate")
    val id: LabWorkId,
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: LabWork
) : BoundCommand(Meta), ApplicableToCollection {
    object Meta : CommandMeta() {
        override val id: String = "update"
        override val help: String = "Обновляет значение элемента коллекции, идентификатор которого равен заданному"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        if (collection.update(this.id, this.factory.copy()) != null) {
            logger.info("Элемент успешно обновлён")
        } else {
            logger.error("Элемента с идентификатором $id нет в коллекции, нечего обновлять")
        }
    }
}