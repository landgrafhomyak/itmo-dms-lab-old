package com.github.landgrafhomyak.itmo.dms_lab.collections

class MutableDoublyLinkedListIterator<N : Any>(
    private val collection: AbstractMutableLinkedCollection<N>,
    start: N?,
    private val linksGetter: N.() -> DoublyLinkedListLinks<N>
) : MutableIterator<N> {
    /**
     * Ссылка на текущую позицию итератора (следующий узел, который будет возвращён)
     *
     * Равна `null` если дерево пустое или итерация была завершена
     */
    private var pointer: N? = start

    /**
     * Ссылка на последний возвращённый итератором элемент
     *
     * Равна `null` если дерево пустое или итерация не была начата
     */
    private var last: N? = null

     /**
     * Сокращение для поля [DoublyLinkedListLinks.next]
     */
    private inline var N.next: N?
        get() = this@MutableDoublyLinkedListIterator.linksGetter(this).next
        set(value) {
            this@MutableDoublyLinkedListIterator.linksGetter(this).next = value
        }

    override fun hasNext(): Boolean = this.pointer != null

    @Suppress("SpellCheckingInspection")
    override fun next(): N =
        this.pointer?.also { pointerNN ->
            this.pointer = pointerNN.next
            this.last = pointerNN
        } ?: throw IllegalStateException("Итерирование двусвязного списка завершено")

    override fun remove() = this.collection.untie(
        this.last ?: throw IllegalStateException("Состояние итератора не позволяет удалить элемент из списка")
    )
}