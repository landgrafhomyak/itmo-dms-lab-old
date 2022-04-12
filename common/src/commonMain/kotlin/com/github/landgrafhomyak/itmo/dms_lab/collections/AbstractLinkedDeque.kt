package com.github.landgrafhomyak.itmo.dms_lab.collections

@Suppress("SpellCheckingInspection")
/**
 * Абстрактная реализация [дека (двусторонней очереди)](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D1%83%D1%85%D1%81%D1%82%D0%BE%D1%80%D0%BE%D0%BD%D0%BD%D1%8F%D1%8F_%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C)
 * на [двусвязном списке](https://ru.wikipedia.org/wiki/%D0%A1%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 * @see LinkedDeque
 */
class AbstractLinkedDeque<N : Any>(
    private val linksGetter: N.() -> DoublyLinkedListLinks<N>
) : AbstractClearableLinkedCollection<N> {
    /**
     * Ссылка на начало дека
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var front: N? = null

    /**
     * Ссылка на конец дека
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var back: N? = null

    /**
     * Сокращение для поля [DoublyLinkedListLinks.prev]
     */
    private inline var N.prev: N?
        get() = this@AbstractLinkedDeque.linksGetter(this).prev
        set(value) {
            this@AbstractLinkedDeque.linksGetter(this).prev = value
        }

    /**
     * Сокращение для поля [DoublyLinkedListLinks.next]
     */
    private inline var N.next: N?
        get() = this@AbstractLinkedDeque.linksGetter(this).next
        set(value) {
            this@AbstractLinkedDeque.linksGetter(this).next = value
        }

    /**
     * Добавляет узел в начало дека
     */
    fun pushFront(node: N) {
        node.next = this.front
        this.front?.prev = node
        node.prev = null
        this.front = node
        if (this.back == null)
            this.back = node
    }

    /**
     * Добавляет узел в конец дека
     */
    fun pushBack(node: N) {
        node.prev = this.back
        this.back?.next = node
        node.next = null
        this.back = node
        if (this.front == null)
            this.front = node
    }

    override fun untie(node: N) {
        if (node === this.front) {
            this.front = node.next
        }

        if (node === this.back) {
            this.back = node.prev
        }

        node.prev?.next = node.next
        node.next?.prev = node.prev

        node.prev = null
        node.next = null
    }


    /**
     * Удаляет узел из начала дека, если такой существует, и возвращает его
     * @return удалённый узел или `null`, если дек пустой
     * @see AbstractLinkedDeque.untie
     */
    fun popFrontOrNull(): N? = this.front?.also(this::untie)


    /**
     * Удаляет узел из конца дека, если такой существует, и возвращает его
     * @return удалённый узел или `null`, если дек пустой
     * @see AbstractLinkedDeque.untie
     */
    fun popBackOrNull(): N? = this.back?.also(this::untie)

    override fun iterator(): MutableIterator<N> = MutableDoublyLinkedListIterator(this, this.front, this.linksGetter)

    /**
     * Удаляет все узлы из дек
     */
    fun clear() {
        this.front = null
        this.back = null
    }
}