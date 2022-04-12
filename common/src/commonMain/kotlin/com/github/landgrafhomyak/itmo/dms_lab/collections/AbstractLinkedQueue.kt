package com.github.landgrafhomyak.itmo.dms_lab.collections

@Suppress("SpellCheckingInspection")
/**
 * Абстрактная реализация [очереди](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 * на [односвязном списке](https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%BD%D0%BE%D1%81%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 * @see LinkedQueue
 */
class AbstractLinkedQueue<N : Any>(
    private val linksGetter: N.() -> SinglyLinkedListLinks<N>
) : AbstractExtendableLinkedCollection<N> {
    /**
     * Ссылка на начало (выход) очереди
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var start: N? = null

    /**
     * Ссылка на конец (вход) очереди
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var end: N? = null

    /**
     * Сокращение для поля [SinglyLinkedListLinks.next]
     */
    private inline var N.next: N?
        get() = this@AbstractLinkedQueue.linksGetter(this).next
        set(value) {
            this@AbstractLinkedQueue.linksGetter(this).next = value
        }

    override fun bind(node: N): N? {
        this.end?.next = node
        this.end = node
        node.next = null
        if (this.start == null)
            this.start = node
        return null
    }

    /**
     * Удаляет первый узел, если такой существует, из очереди и возвращает его
     * @return удалённый узел или `null`, если очередь пустая
     * @see AbstractMutableLinkedCollection.untie
     */
    fun popOrNull(): N? = this.start.also {
        this.start = this.start?.next
        if (this.start == null)
            this.end = null
    }

    override fun iterator(): Iterator<N> = SinglyLinkedListIterator(this.start, this.linksGetter)
}