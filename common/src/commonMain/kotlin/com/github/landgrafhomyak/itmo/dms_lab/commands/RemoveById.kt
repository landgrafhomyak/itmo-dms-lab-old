package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `remove_by_id`
 * @param id идентификатор элемента, который надо удалить
 * @sample RemoveById.help
 */
@Suppress("unused", "EqualsOrHashCode")
class RemoveById(
    @Suppress("MemberVisibilityCanBePrivate")
    val id: LabWorkId,
) : BoundRequest(), ApplicableToCollection {
    override val meta: RequestMeta
        get() = Meta

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



    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean {
        if (other !is RemoveById) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = this.id.hashCode()
}