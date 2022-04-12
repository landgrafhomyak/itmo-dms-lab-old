package com.github.landgrafhomyak.itmo.dms_lab.collections

@Suppress("SpellCheckingInspection")
/**
 * Итератор для [односвязном списке](https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%BD%D0%BE%D1%81%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 */
class SinglyLinkedListIterator<N : Any>(
    start: N?,
    private val linksGetter: N.() -> SinglyLinkedListLinks<N>
) : Iterator<N> {
    /**
     * Ссылка на текущую позицию итератора (следующий узел, который будет возвращён)
     *
     * Равна `null` если дерево пустое или итерация была завершена
     */
    private var pointer: N? = start

    /**
     * Сокращение для поля [SinglyLinkedListLinks.next]
     */
    private inline var N.next: N?
        get() = this@SinglyLinkedListIterator.linksGetter(this).next
        set(value) {
            this@SinglyLinkedListIterator.linksGetter(this).next = value
        }

    override fun hasNext(): Boolean = this.pointer != null

    @Suppress("SpellCheckingInspection")
    override fun next(): N =
        this.pointer?.also { pointerNN ->
            this.pointer = pointerNN.next
        } ?: throw IllegalStateException("Итерирование односвязного списка завершено")
}