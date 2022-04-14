package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `remove_greater`
 * @param key элементы, которые имеют значение больше этого, будут удалены
 * @sample RemoveGreater.help
 */
@Suppress("unused", "EqualsOrHashCode")
class RemoveGreater(
    @Suppress("MemberVisibilityCanBePrivate")
    val key: Long,
) : BoundRequest(), ApplicableToCollection {
    override val meta: RequestMeta
        get() = Meta

    companion object Meta : RequestMeta {
        override val id: String = "remove_greater"
        override val help: String = "Удаляет из коллекции все элементы, координата X которых больше заданной"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        logger.info(
            "Удалено ${
                collection.removeGreaterThanCoordinateX(key).processRemaining()
            } элементов"
        )
    }


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean {
        if (other !is RemoveGreater) return false
        return this.key == other.key
    }

    override fun hashCode(): Int = this.key.hashCode()
}