package com.github.landgrafhomyak.itmo.dms_lab.commands


@Suppress("SpellCheckingInspection")
/**
 * Базовый класс для объектов запросов упакованных вместе с их аргументами
 *
 * Является конечным продуктом разбора и обработки запроса, может быть применён к любой коллекции из коробки
 * @property meta ссылка на метаобъект
 * @see ApplicableToCollection
 */
abstract class BoundRequest {
    abstract val meta: RequestMeta

    override fun equals(other: Any?): Boolean = if (other is BoundRequest) this.equals(other) else false

    /**
     * Сравнивает два запроса на идентичность
     */
    abstract fun equals(other: BoundRequest): Boolean

    abstract override fun hashCode(): Int
}