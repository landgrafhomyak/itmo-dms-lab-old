package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.Factory
import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `add`
 * @param factory элемент, копии которого будут добавляться в коллекцию
 * @sample Add.help
 */
@Suppress("unused", "EqualsOrHashCode")
class Add(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: Factory<LabWork>
) : BoundRequest(), ApplicableToCollection {
    override val meta: RequestMeta
        get() = Meta

    companion object Meta : RequestMeta {
        override val id: String = "add"
        override val help: String = "Добавляет новый элемент в коллекцию"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        collection.add(this.factory.build())
        logger.info("Элемент успешно добавлен")
    }


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean {
        if (other !is Add) return false
        return this.factory == other.factory
    }

    override fun hashCode(): Int = this.factory.hashCode()
}