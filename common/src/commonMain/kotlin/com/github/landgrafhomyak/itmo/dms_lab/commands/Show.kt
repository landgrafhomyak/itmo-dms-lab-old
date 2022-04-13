package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection
import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `show`
 * @sample Show.help
 */
@Suppress("unused")
object Show : BoundRequest(Show), ApplicableToCollection, RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this


    override val id: String = "show"
    override val help: String = "Выводит все элементы коллекции"

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection) {
            logger.sendObject(elem)
        }
    }
}