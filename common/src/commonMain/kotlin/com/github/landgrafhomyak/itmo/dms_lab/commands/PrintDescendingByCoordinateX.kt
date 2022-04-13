package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection
import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `print_descending`
 * @sample PrintDescendingByCoordinateX.help
 */
@Suppress("unused")
object PrintDescendingByCoordinateX : BoundRequest(PrintDescendingByCoordinateX), ApplicableToCollection, RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this

    override val id: String = "print_descending"
    override val help: String = "Выводит все элементы коллекции в порядке убывания по координате X"

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection.descendingByCoordinateX()) {
            logger.sendObject(elem)
        }
    }
}