package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection
import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `info`
 * @sample Info.help
 */
@Suppress("unused")
object Info : BoundRequest(), ApplicableToCollection, RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this
    override val meta: RequestMeta
        get() = Info
    override val id: String = "info"
    override val help: String = "Выводит информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)"

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        logger.info(
            "Коллекция лабораторных работ была создана ${collection.creationDate} и имеет ${collection.size}"
        )
    }


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean = this === other

    override fun hashCode(): Int = super.hashCode()
}