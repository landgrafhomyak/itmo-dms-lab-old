package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection
import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `clear`
 * @sample Clear.help
 */
@Suppress("unused")
object Clear : BoundRequest(), ApplicableToCollection, RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val meta: RequestMeta
        get() = this

    override val id: String = "clear"
    override val help: String = "Очищает коллекцию"

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        collection.clear()
        logger.info("Коллекция очищена")
    }


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean = this === other

    override fun hashCode(): Int = super.hashCode()
}