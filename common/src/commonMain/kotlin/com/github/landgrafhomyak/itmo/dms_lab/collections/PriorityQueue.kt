package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Интерфейс [очереди с приоритетом](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_%D1%81_%D0%BF%D1%80%D0%B8%D0%BE%D1%80%D0%B8%D1%82%D0%B5%D1%82%D0%BE%D0%BC_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 *
 * Первый элемент в очереди всегда наибольший (определяется функцией [Comparable.compareTo]) из имеющихся
 */
interface PriorityQueue<E : Comparable<E>> : Queue<E> {
    /**
     * Удаляет наибольший элемент из очереди
     * @return удалённый элемент или `null`, если очередь пуста
     */
    override fun popOrNull(): E?

    /**
     * Удаляет наибольший элемент из очереди или кидает ошибку, если очередь пуста
     * @return удалённый элемент
     */
    @Suppress("RedundantOverride")
    override fun pop(): E = this.popOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустой очереди с приоритетом")

    /**
     * Максимальный элемент в очереди или `null`, если очередь пуста
     *
     * Обратная совместимость с [обычной очередью][Queue]
     */
    @Deprecated("В очереди с приоритетом логичнее использовать поле '.maxOrNull'", ReplaceWith("maxOrNull"))
    override val firstOrNull: E?
        get() = this.maxOrNull

    /**
     * Максимальный элемент в очереди или кидается ошибка, если очередь пустая
     *
     * Обратная совместимость с [обычной очередью][Queue]
     */
    @Suppress("DEPRECATION")
    @Deprecated("В очереди с приоритетом логичнее использовать поле '.max'", ReplaceWith("max"))
    override val first: E
        get() = this.firstOrNull ?: throw NoSuchElementException("Нельзя получить элемент из пустой очереди с приоритетом")


    /**
     * Максимальный элемент в очереди или `null`, если очередь пуста
     */
    val maxOrNull: E?

    /**
     * Максимальный элемент в очереди или кидается ошибка, если очередь пустая
     */
    val max: E
        get() = this.maxOrNull ?: throw NoSuchElementException("Нельзя получить элемент из пустой очереди с приоритетом")


}