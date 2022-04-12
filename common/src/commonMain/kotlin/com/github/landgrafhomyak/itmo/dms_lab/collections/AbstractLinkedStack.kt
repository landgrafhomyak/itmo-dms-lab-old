package com.github.landgrafhomyak.itmo.dms_lab.collections

@Suppress("SpellCheckingInspection")
/**
 * Абстрактная реализация [стека](https://ru.wikipedia.org/wiki/%D0%A1%D1%82%D0%B5%D0%BA)
 * на [односвязном списке](https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%BD%D0%BE%D1%81%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 * @see LinkedStack
 */
class AbstractLinkedStack<N : Any>(
    private val linksGetter: N.() -> SinglyLinkedListLinks<N>
) : AbstractExtendableLinkedCollection<N> {
    /**
     * Ссылка на вершину стека
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var top: N? = null

    /**
     * Сокращение для поля [SinglyLinkedListLinks.next]
     */
    private inline var N.next: N?
        get() = this@AbstractLinkedStack.linksGetter(this).next
        set(value) {
            this@AbstractLinkedStack.linksGetter(this).next = value
        }

    override fun bind(node: N): N? {
        node.next = this.top
        this.top = node
        return null
    }

    /**
     * Удаляет узел на вершине стека, если такой существует, и возвращает его
     * @return удалённый узел или `null`, если очередь пустая
     * @see AbstractMutableLinkedCollection.untie
     */
    fun popOrNull(): N? = this.top.also {
        this.top = this.top?.next
    }

    override fun iterator(): Iterator<N> = SinglyLinkedListIterator(this.top, this.linksGetter)
}