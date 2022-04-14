package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `add_if_max`
 * @param factory элемент, копии которого будут добавляться в коллекцию
 * @sample AddIfMax.help
 */
@Suppress("unused", "EqualsOrHashCode")
class AddIfMax(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: LabWork
) : BoundRequest(), ApplicableToCollection {
    override val meta: RequestMeta
        get() = Meta

    companion object Meta : RequestMeta {
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


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean {
        if (other !is AddIfMax) return false
        return this.factory == other.factory
    }

    override fun hashCode(): Int = this.factory.hashCode()
}