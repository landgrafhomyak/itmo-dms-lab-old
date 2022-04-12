package com.github.landgrafhomyak.itmo.dms_lab.collections

interface PriorityQueue<E : Comparable<E>> : MutableIterable<E> {
    fun push(element: E)
    fun popOrNull(): E?
    fun pop(): E = this.popOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустой очереди с приоритетом")
    val top: E?
    fun isEmpty() = this.top == null
}