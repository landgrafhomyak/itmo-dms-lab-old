package com.github.landgrafhomyak.itmo.dms_lab.collections

import kotlin.jvm.JvmInline

private open class BinaryTreeLinksImpl<N : Any> : BinaryTreeLinks<N> {
    override var parent: N? = null
    override var left: N? = null
    override var right: N? = null
    override fun toString(): String = "<bin tree node>"
}

/**
 * Создаёт пустой узел для [бинарного (двоичного) дерева](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D0%BE%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
 */
@Suppress("FunctionName", "unused")
fun <N : Any> BinaryTreeLinks(): BinaryTreeLinks<N> = BinaryTreeLinksImpl()

private open class BinaryTreeLinksWithColorImpl<N : Any, C : Any> : BinaryTreeLinksImpl<N>(), BinaryTreeLinksWithColor<N, C> {
    override lateinit var color: C
    override fun toString(): String = "<bin tree node with color>"
}

/**
 * Создаёт пустой узел для [бинарного (двоичного) дерева](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D0%BE%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
 * с цветом
 */
@Suppress("FunctionName", "unused")
fun <N : Any, C : Any> BinaryTreeLinksWithColor(): BinaryTreeLinksWithColor<N, C> = BinaryTreeLinksWithColorImpl()

private open class SinglyLinkedListLinksImpl<N : Any> : SinglyLinkedListLinks<N> {
    override var next: N? = null
    override fun toString(): String = "<singly-linked list node>"
}

/**
 * Создаёт пустой узел для [односвязного списка](https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%BD%D0%BE%D1%81%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 */
@Suppress("FunctionName", "unused")
fun <N : Any> SinglyLinkedListLinks(): SinglyLinkedListLinks<N> = SinglyLinkedListLinksImpl()

private open class DoublyLinkedListLinksImpl<N : Any> : SinglyLinkedListLinksImpl<N>(), DoublyLinkedListLinks<N> {
    override var prev: N? = null
    override fun toString(): String = "<doubly-linked list node>"
}

/**
 * Создаёт пустой узел для [двусвязного списка](https://ru.wikipedia.org/wiki/%D0%A1%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 */
@Suppress("FunctionName", "unused", "SpellCheckingInspection")
fun <N : Any> DoublyLinkedListLinks(): DoublyLinkedListLinks<N> = DoublyLinkedListLinksImpl()


/**
 * Реализация словаря на [красно-чёрном дереве][AbstractRedBlackTree]
 */
@Suppress("unused")
class RedBlackTreeMap<K : Comparable<K>, V> : MutableMap<K, V> {
    private class Node<K, V>(
        override val key: K, value: V
    ) : MutableMap.MutableEntry<K, V>, BinaryTreeLinksWithColor<Node<K, V>, AbstractRedBlackTree.Color> {
        override var value: V = value
            private set
        override var parent: Node<K, V>? = null
        override var left: Node<K, V>? = null
        override var right: Node<K, V>? = null
        override lateinit var color: AbstractRedBlackTree.Color
        override fun setValue(newValue: V): V = this.value.also { this.value = newValue }
        override fun toString(): String = "<rb-tree map node key='${this.key}' value='${this.value}'>"

    }


    private val tree = AbstractRedBlackTree<Node<K, V>, K>(node@{ this@node }, node@{ this@node.key })

    override val size: Int
        get() = this.tree.size

    override fun containsKey(key: K): Boolean = key in this.tree

    override fun containsValue(value: V): Boolean = value in this.values

    override fun get(key: K): V? = this.tree[key]?.value

    override fun isEmpty(): Boolean = this.tree.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = this.tree.toMutableSet()
    override val keys: MutableSet<K>
        get() = this.tree.map { node -> node.key }.toMutableSet()
    override val values: MutableCollection<V>
        get() = this.tree.map { node -> node.value }.toMutableList()

    override fun clear() = this.tree.clear()

    override fun put(key: K, value: V): V? = this.tree.bind(Node(key, value))?.value

    override fun putAll(from: Map<out K, V>) = from.forEach { (k, v) -> this[k] = v }

    override fun remove(key: K): V? {
        val node = this.tree[key] ?: return null
        this.tree.untie(node)
        return node.value
    }
}

/**
 * Реализация множества на [красно-чёрном дереве][AbstractRedBlackTree]
 */
@Suppress("unused")
class RedBlackTreeSet<E : Comparable<E>> : MutableSet<E> {
    private class Node<E>(
        val element: E,
    ) : BinaryTreeLinksWithColor<Node<E>, AbstractRedBlackTree.Color> {
        override var parent: Node<E>? = null
        override var left: Node<E>? = null
        override var right: Node<E>? = null
        override lateinit var color: AbstractRedBlackTree.Color
        override fun toString(): String = "<rb-tree set node element='${this.element}'>"
    }


    private val tree = AbstractRedBlackTree<Node<E>, E>(node@{ this@node }, node@{ this@node.element })

    override fun add(element: E): Boolean = this.tree.bind(Node(element))?.also { old ->
        this.tree.bind(old)
    } == null

    override fun addAll(elements: Collection<E>): Boolean = elements.map(this::add).any { p -> p }

    override fun clear() = this.tree.clear()

    @JvmInline
    private value class Node2ElementIterator<E : Comparable<E>>(
        private val original: MutableIterator<Node<E>>
    ) : MutableIterator<E> {
        override fun hasNext(): Boolean = this.original.hasNext()
        override fun next(): E = this.original.next().element
        override fun remove() = this.original.remove()

    }

    override fun iterator(): MutableIterator<E> = Node2ElementIterator(this.tree.iterator())

    override fun remove(element: E): Boolean {
        val node = this.tree[element] ?: return false
        this.tree.untie(node)
        return true
    }

    override fun removeAll(elements: Collection<E>): Boolean = elements.map(this::remove).any { p -> p }

    override fun retainAll(elements: Collection<E>): Boolean {
        val allElements = this.iterator().asSequence().toList()
        var anyDeleted = false
        for (elem in allElements) {
            if (elem !in elements) {
                this.tree[elem]?.also { node ->
                    this.tree.untie(node)
                    anyDeleted = true
                }
            }
        }
        return anyDeleted
    }

    override val size: Int
        get() = this.tree.size

    override fun contains(element: E): Boolean = element in this.tree

    override fun containsAll(elements: Collection<E>): Boolean = this.map(this::contains).all { p -> p }

    override fun isEmpty(): Boolean = this.tree.isEmpty()
}

/**
 * Реализация словаря на [красно-чёрном дереве][AbstractRedBlackTree] с возможностью обращения к элементам по ключу, который они в себе содержат
 * @param keyGetter гетер который извлекает ключ для элемента
 * @see RedBlackTreeMap
 */
@Suppress("unused")
class RedBlackTreeSetWithKeyAccess<K : Comparable<K>, E : Any>(
    private val keyGetter: E.() -> K
) : Map<K, E>, MutableSet<E> {

    private inline val E.key
        get() = this@RedBlackTreeSetWithKeyAccess.keyGetter(this)

    private inner class Node(
        val element: E
    ) : Map.Entry<K, E>, BinaryTreeLinksWithColor<Node, AbstractRedBlackTree.Color> {
        override var parent: Node? = null
        override var left: Node? = null
        override var right: Node? = null
        override lateinit var color: AbstractRedBlackTree.Color

        override val key: K
            get() = this@Node.element.key
        override val value: E
            get() = this.element

        override fun toString(): String = "<rb-tree set with key access node element='${this.element}' key='${this.key}'>"
    }

    private val tree = AbstractRedBlackTree<Node, K>(node@{ this@node }, node@{ this@node.key })

    override val size: Int
        get() = this.tree.size

    override fun containsKey(key: K): Boolean = key in this.tree

    override fun containsValue(value: E): Boolean = value in this.values

    override fun get(key: K): E? = this.tree[key]?.element

    override fun isEmpty(): Boolean = this.tree.isEmpty()

    override val entries: Set<Map.Entry<K, E>>
        get() = this.tree.toSet()
    override val keys: Set<K>
        get() = this.tree.map { node -> node.key }.toSet()
    override val values: Collection<E>
        get() = this.tree.map { node -> node.element }

    override fun add(element: E): Boolean = this.tree.bind(this.Node(element)) != null

    override fun addAll(elements: Collection<E>): Boolean = elements.map(this::add).any { p -> p }

    override fun clear() = this.tree.clear()

    @JvmInline
    private value class Node2ElementIterator<K : Comparable<K>, E : Any>(
        private val original: MutableIterator<RedBlackTreeSetWithKeyAccess<K, E>.Node>
    ) : MutableIterator<E> {
        override fun hasNext(): Boolean = this.original.hasNext()
        override fun next(): E = this.original.next().element
        override fun remove() = this.original.remove()

    }

    override fun iterator(): MutableIterator<E> = Node2ElementIterator(this.tree.iterator())

    override fun remove(element: E): Boolean {
        val node = this.tree[element.key] ?: return false
        this.tree.untie(node)
        return true
    }

    override fun removeAll(elements: Collection<E>): Boolean = elements.map(this::remove).any { p -> p }

    override fun retainAll(elements: Collection<E>): Boolean {
        val allElements = this.iterator().asSequence().toList()
        var anyDeleted = false
        for (elem in allElements) {
            if (elem !in elements) {
                this.tree[elem.key]?.also { node ->
                    this.tree.untie(node)
                    anyDeleted = true
                }
            }
        }
        return anyDeleted
    }

    override fun contains(element: E): Boolean = element.key in this.tree

    override fun containsAll(elements: Collection<E>): Boolean = elements.map(this::contains).all { p -> p }
}

/**
 * Реализация очереди с приоритетом на [двусвязном списке][AbstractLinkedListPriorityQueue]
 */
@Suppress("unused", "SpellCheckingInspection")
class LinkedListPriorityQueue<E : Comparable<E>> : PriorityQueue<E>, MutableIterable<E> {
    private class Node<E>(val element: E) : DoublyLinkedListLinks<Node<E>> {
        override var prev: Node<E>? = null
        override var next: Node<E>? = null
        override fun toString(): String = "<linked list priority queue node element='${this.element}'>"
    }


    private val queue = AbstractLinkedListPriorityQueue<Node<E>, E>(
        node@{ this@node },
        node@{ this@node.element }
    )

    override val maxOrNull: E?
        get() = this.queue.max?.element

    @JvmInline
    private value class Node2ElementIterator<E>(
        private val original: MutableIterator<Node<E>>
    ) : MutableIterator<E> {
        override fun hasNext(): Boolean = this.original.hasNext()
        override fun next(): E = this.original.next().element
        override fun remove() = this.original.remove()
    }

    override fun iterator(): MutableIterator<E> = Node2ElementIterator(this.queue.iterator())

    override fun push(element: E) {
        this.queue.bind(Node(element))
    }

    override fun popOrNull(): E? = this.queue.max?.also { start -> this.queue.untie(start) }?.element

    override fun clear() = this.queue.clear()
}

/**
 * Реализация [стека](https://ru.wikipedia.org/wiki/%D0%A1%D1%82%D0%B5%D0%BA)
 * на списке
 */
@Suppress("unused")
class ArrayStack<E> : Stack<E>, MutableList<E> {
    private val list: MutableList<E> = ArrayList()
    override val size: Int
        get() = this.list.size


    override fun push(element: E) {
        this.list.add(element)
    }

    override fun popOrNull(): E? = this.list.removeLastOrNull()

    override val topOrNull: E?
        get() = this.list.lastOrNull()

    override operator fun contains(element: E): Boolean = this.list.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = this.list.containsAll(elements)

    override operator fun get(index: Int): E = this.list[index]

    override fun indexOf(element: E): Int = this.list.indexOf(element)

    override fun isEmpty(): Boolean = this.list.isEmpty()

    override fun iterator(): MutableIterator<E> = this.list.iterator()

    override fun lastIndexOf(element: E): Int = this.list.lastIndexOf(element)

    override fun listIterator(): MutableListIterator<E> = this.list.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = this.list.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = this.list.subList(fromIndex, toIndex)

    override fun add(element: E): Boolean = this.list.add(element)

    override fun add(index: Int, element: E) = this.list.add(index, element)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = this.list.addAll(index, elements)

    override fun addAll(elements: Collection<E>): Boolean = this.list.addAll(elements)

    override fun clear() = this.list.clear()

    override fun remove(element: E): Boolean = this.list.remove(element)

    override fun removeAll(elements: Collection<E>): Boolean = this.list.removeAll(elements)

    override fun removeAt(index: Int): E = this.list.removeAt(index)

    override fun retainAll(elements: Collection<E>): Boolean = this.list.retainAll(elements)

    override operator fun set(index: Int, element: E): E = this.list.set(index, element)
}


/**
 * Реализация [очереди](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 * на [односвязном списке][AbstractLinkedQueue]
 */
class LinkedQueue<E : Any> : Queue<E> {
    private class Node<E>(val element: E) : SinglyLinkedListLinks<Node<E>> {
        override var next: Node<E>? = null
        override fun toString(): String = "<linked queue node element='${this.element}'>"
    }

    private val queue = AbstractLinkedQueue<Node<E>> node@{ this@node }

    @JvmInline
    private value class Node2ElementIterator<E>(
        private val original: Iterator<Node<E>>
    ) : Iterator<E> {
        override fun hasNext(): Boolean = this.original.hasNext()
        override fun next(): E = this.original.next().element
    }

    override fun iterator(): Iterator<E> = Node2ElementIterator(this.queue.iterator())

    override fun push(element: E) {
        this.queue.bind(Node(element))
    }

    override fun popOrNull(): E? = this.queue.popOrNull()?.element

    override val firstOrNull: E?
        get() = this.queue.start?.element

    override fun clear() = this.queue.clear()
}


/**
 * Реализация [стека](https://ru.wikipedia.org/wiki/%D0%A1%D1%82%D0%B5%D0%BA)
 * на [односвязном списке][AbstractLinkedStack]
 */
class LinkedStack<E : Any> : Stack<E> {
    private class Node<E>(val element: E) : SinglyLinkedListLinks<Node<E>> {
        override var next: Node<E>? = null
        override fun toString(): String = "<linked stack node element='${this.element}'>"
    }

    private val stack = AbstractLinkedStack<Node<E>> node@{ this@node }

    override fun push(element: E) {
        this.stack.bind(Node(element))
    }

    override fun popOrNull(): E? = this.stack.popOrNull()?.element

    override val topOrNull: E?
        get() = this.stack.top?.element

    @JvmInline
    private value class Node2ElementIterator<E>(
        private val original: Iterator<Node<E>>
    ) : Iterator<E> {
        override fun hasNext(): Boolean = this.original.hasNext()
        override fun next(): E = this.original.next().element
    }

    override fun iterator(): Iterator<E> = Node2ElementIterator(this.stack.iterator())
    override fun clear() = this.stack.clear()
}


/**
 * Реализация [дека (двусторонней очереди)](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D1%83%D1%85%D1%81%D1%82%D0%BE%D1%80%D0%BE%D0%BD%D0%BD%D1%8F%D1%8F_%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C)
 * на [двусвязном списке][AbstractLinkedDeque]
 */
@Suppress("unused", "SpellCheckingInspection")
class LinkedDeque<E : Comparable<E>> : Deque<E> {
    private class Node<E>(val element: E) : DoublyLinkedListLinks<Node<E>> {
        override var prev: Node<E>? = null
        override var next: Node<E>? = null
        override fun toString(): String = "<linked deque node element='${this.element}'>"
    }

    private val deque = AbstractLinkedDeque<Node<E>> node@{ this@node }

    @JvmInline
    private value class Node2ElementIterator<E>(
        private val original: MutableIterator<Node<E>>
    ) : MutableIterator<E> {
        override fun hasNext(): Boolean = this.original.hasNext()
        override fun next(): E = this.original.next().element
        override fun remove() = this.original.remove()
    }

    override fun iterator(): MutableIterator<E> = Node2ElementIterator(this.deque.iterator())

    override fun pushBack(element: E) = this.deque.pushBack(Node(element))

    override fun pushFront(element: E) = this.deque.pushFront(Node(element))

    override fun popBackOrNull(): E? = this.deque.popBackOrNull()?.element

    override fun popFrontOrNull(): E? = this.deque.popFrontOrNull()?.element

    override val frontOrNull: E?
        get() = this.deque.front?.element
    override val backOrNull: E?
        get() = this.deque.back?.element

    override fun clear() = this.deque.clear()
}