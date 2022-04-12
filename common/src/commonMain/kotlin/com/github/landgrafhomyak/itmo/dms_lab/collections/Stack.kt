package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Интерфейс [стека](https://ru.wikipedia.org/wiki/%D0%A1%D1%82%D0%B5%D0%BA)
 *
 * Первым извлекается элемент который был добавлен последним
 */
interface Stack<E> : Iterable<E> {
    /**
     * Добавляет элемент в стек
     */
    fun push(element: E)

    /**
     * Удаляет элемент из вершины стека
     * @return удалённый элемент или `null`, если стек пустой
     */
    fun popOrNull(): E?

    /**
     * Удаляет элемент из вершины стека или кидает ошибку, если стек пустой
     * @return удалённый элемент
     */
    fun pop(): E = this.popOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустого стека")

    /**
     * вершина стека или `null`, если стек пустой
     */
    val topOrNull: E?

    /**
     * Вершина стека или кидается ошибка, если стек пустой
     */
    val top: E
        get() = this.topOrNull ?: throw NoSuchElementException("Нельзя получить вершину из пустого стека")

    /**
     * Проверка, что стек не пустой
     */
    fun isNotEmpty() = this.topOrNull != null

    /**
     * Проверка, что стек пустой
     */
    fun isEmpty() = this.topOrNull == null

    /**
     * Удаляет все элементы из стека
     */
    fun clear()
}