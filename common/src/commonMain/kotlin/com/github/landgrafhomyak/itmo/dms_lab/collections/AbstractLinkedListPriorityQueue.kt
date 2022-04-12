package com.github.landgrafhomyak.itmo.dms_lab.collections

@Suppress("SpellCheckingInspection")
/**
 * Абстрактная реализация [очереди с приоритетом](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_%D1%81_%D0%BF%D1%80%D0%B8%D0%BE%D1%80%D0%B8%D1%82%D0%B5%D1%82%D0%BE%D0%BC_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 * на [двусвязном списке](https://ru.wikipedia.org/wiki/%D0%A1%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 * @see AbstractLinkedHeapPriorityQueue
 * @see LinkedListPriorityQueue
 */
class AbstractLinkedListPriorityQueue<N : Any, K : Comparable<K>>(
    private val linksGetter: N.() -> DoublyLinkedListLinks<N>, private val keyGetter: N.() -> K
) : AbstractMutableLinkedCollection<N>, MutableIterable<N> {
    /**
     * Ссылка на начало списка
     */
    var start: N? = null

    /**
     * Сокращение для поля [DoublyLinkedListLinks.prev]
     */
    private inline var N.prev: N?
        get() = this@AbstractLinkedListPriorityQueue.linksGetter(this).prev
        set(value) {
            this@AbstractLinkedListPriorityQueue.linksGetter(this).prev = value
        }

    /**
     * Сокращение для поля [DoublyLinkedListLinks.next]
     */
    private inline var N.next: N?
        get() = this@AbstractLinkedListPriorityQueue.linksGetter(this).next
        set(value) {
            this@AbstractLinkedListPriorityQueue.linksGetter(this).next = value
        }

    /**
     * Сокращение [гетера для извлечения ключа][keyGetter] из узла
     */
    private inline val N.key: K
        get() = this@AbstractLinkedListPriorityQueue.keyGetter(this)


    override fun bind(node: N): N? {
        var prevPointer: N? = null
        var pointer: N? = this.start

        while (pointer != null && node.key < pointer.key) {
            prevPointer = pointer
            pointer = pointer.next
        }

        if (prevPointer == null) {
            node.next = this.start
            this.start?.prev = node
            this.start = node
            node.prev = null
            return null
        }

        node.next = prevPointer.next
        node.prev = prevPointer
        prevPointer.next?.prev = node
        prevPointer.next = node

        return null
    }

    override fun untie(node: N) {
        if (node === this.start) {
            this.start = node.next
        }

        node.prev?.next = node.next
        node.next?.prev = node.prev

        node.prev = null
        node.next = null
    }

    override fun iterator(): MutableIterator<N> = MutableDoublyLinkedListIterator<N>(this, this.start, this.linksGetter)
}