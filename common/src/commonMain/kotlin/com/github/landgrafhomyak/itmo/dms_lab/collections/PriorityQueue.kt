package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Интерфейс [очереди с приоритетом](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_%D1%81_%D0%BF%D1%80%D0%B8%D0%BE%D1%80%D0%B8%D1%82%D0%B5%D1%82%D0%BE%D0%BC_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 *
 * Поддерживает вставку, удаление и просмотр первого элемента в очереди
 */
interface PriorityQueue<E : Comparable<E>> : MutableIterable<E> {
    /**
     * Добавляет элемент в очередь
     */
    fun push(element: E)

    /**
     * Удаляет первый элемент в очереди
     * @return удалённый элемент или `null`, если очередь пуста
     */
    fun popOrNull(): E?

    /**
     * Удаляет первый элемент в очереди или кидает ошибку, если очередь пуста
     * @return удалённый элемент
     */
    fun pop(): E = this.popOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустой очереди с приоритетом")

    /**
     * Первый элемент в очереди или `null`, если очередь пуста
     */
    val top: E?

    /**
     * Проверка, что очередь пустая
     */
    fun isEmpty() = this.top == null
}