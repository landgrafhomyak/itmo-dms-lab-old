package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection
import kotlin.jvm.JvmField

/**
 * Конечный объект запроса `print_field_descending_maximum_point`
 * @sample PrintFieldDescendingMaximumPoint.help
 */
@Suppress("unused")
object PrintFieldDescendingMaximumPoint : BoundRequest(), ApplicableToCollection, RequestMeta {
    /**
     * Поле для совместимости с запросами которые имеют аргументы
     */
    @JvmField
    val Meta: RequestMeta = this
    override val meta: RequestMeta
        get() = PrintFieldDescendingMaximumPoint

    override val id: String = "print_field_descending_maximum_point"
    override val help: String = "Выводит все элементы коллекции в порядке убывания по максимальной точке"

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection.descendingByMaximumPoint()) {
            logger.sendObject(elem)
        }
    }


    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean = this === other

    override fun hashCode(): Int = super.hashCode()
}