package io.github.landgrafhomyak.itmo.dms_lab

import kotlinx.datetime.Instant

/**
 * Базовый интерфейс, который должны имплементировать все коллекции используемые в этой библиотеке
 *
 * @param E тип хранящихся в коллекции предметов
 */
public interface AbstractRecordsCollection<E : Any> : Iterable<E> {
    /**
     * Дата создания [коллекции][AbstractRecordsCollection].
     * Цикл жизни объекта-обёртки не должен влиять на этот показатель.
     */
    public val creationDate: Instant

    /**
     * Описание [коллекции][AbstractRecordsCollection]
     */
    public val description: String

    /**
     * Количество записей в [коллекции][AbstractRecordsCollection]
     */
    public val size: UInt

    /**
     * Итератор для перебора всех записей в [коллекции][AbstractRecordsCollection].
     * Порядок не важен. Для получения итераторов, соблюдающих определённый
     * порядок, нужно объявить отдельные методы.
     */
    override fun iterator(): Iterator<E>
}