package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.Factory
import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `add`
 * @param id идентификатор элемента, который надо заменить
 * @param factory элемент, копии которого будут добавляться в коллекцию
 * @sample Update.help
 */
@Suppress("unused", "EqualsOrHashCode")
class Update(
    @Suppress("MemberVisibilityCanBePrivate")
    val id: LabWorkId,
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: Factory<LabWork>
) : BoundRequest(), ApplicableToCollection {
    override val meta: RequestMeta
        get() = Meta

    companion object Meta : RequestMeta {
        override val id: String = "update"
        override val help: String = "Обновляет значение элемента коллекции, идентификатор которого равен заданному"
    }


    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        if (collection.update(this.id, this.factory.build()) != null) {
            logger.info("Элемент успешно обновлён")
        } else {
            logger.error("Элемента с идентификатором $id нет в коллекции, нечего обновлять")
        }
    }


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean {
        if (other !is Update) return false
        return this.id == other.id && this.factory == other.factory
    }

    override fun hashCode(): Int = this.factory.hashCode()
}