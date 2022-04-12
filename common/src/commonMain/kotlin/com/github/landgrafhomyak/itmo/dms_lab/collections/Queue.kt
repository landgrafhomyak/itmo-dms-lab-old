package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Интерфейс [очереди](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 *
 * Первым извлекается элемент который был добавлен первым
 */
interface Queue<E> : Iterable<E> {
    /**
     * Добавляет элемент в очередь
     */
    fun push(element: E)

    /**
     * Удаляет первый элемент из очереди
     * @return удалённый элемент или `null`, если очередь пуста
     */
    fun popOrNull(): E?

    /**
     * Удаляет первый элемент из очереди или кидает ошибку, если очередь пуста
     * @return удалённый элемент
     */
    fun pop(): E = this.popOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустой очереди")

    /**
     * Первый элемент в очереди или `null`, если очередь пуста
     */
    val firstOrNull: E?

    /**
     * Первый элемент в очереди или кидается ошибка, если очередь пустая
     */
    val first: E
        get() = this.firstOrNull ?: throw NoSuchElementException("Нельзя получить элемент из пустой очереди")

    /**
     * Проверка, что очередь не пустая
     */
    fun isNotEmpty() = this.firstOrNull != null

    /**
     * Проверка, что очередь пустая
     */
    fun isEmpty() = this.firstOrNull == null

    /**
     * Удаляет все элементы из очереди
     */
    fun clear()
}