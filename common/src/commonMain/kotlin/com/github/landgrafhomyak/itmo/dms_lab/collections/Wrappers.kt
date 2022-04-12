package com.github.landgrafhomyak.itmo.dms_lab.collections

import kotlin.jvm.JvmInline

private open class BinaryTreeLinksImpl<N : Any> : BinaryTreeLinks<N> {
    override var parent: N? = null
    override var left: N? = null
    override var right: N? = null
}

@Suppress("FunctionName", "unused")
fun <N : Any> BinaryTreeLinks(): BinaryTreeLinks<N> = BinaryTreeLinksImpl()

private open class BinaryTreeLinksWithColorImpl<N : Any, C : Any> : BinaryTreeLinksImpl<N>(), BinaryTreeLinksWithColor<N, C> {
    override lateinit var color: C
}

@Suppress("FunctionName", "unused")
fun <N : Any, C : Any> BinaryTreeLinksWithColor(): BinaryTreeLinksWithColor<N, C> = BinaryTreeLinksWithColorImpl()

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


@Suppress("unused")
class RedBlackTreeSet<E : Comparable<E>> : MutableSet<E> {
    private class Node<E>(
        val element: E,
    ) : BinaryTreeLinksWithColor<Node<E>, AbstractRedBlackTree.Color> {
        override var parent: Node<E>? = null
        override var left: Node<E>? = null
        override var right: Node<E>? = null
        override lateinit var color: AbstractRedBlackTree.Color
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

@Suppress("unused")
class LinkedListPriorityQueue<E : Comparable<E>> : PriorityQueue<E> {
    private class Node<E>(val element: E) : DoublyLinkedListLinks<Node<E>> {
        override var prev: Node<E>? = null
        override var next: Node<E>? = null
    }


    private val queue = AbstractLinkedListPriorityQueue<Node<E>, E>(
        node@{ this@node },
        node@{ this@node.element }
    )

    override val top: E?
        get() = this.queue.start?.element

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

    override fun popOrNull(): E? = this.queue.start?.also { start -> this.queue.untie(start) }?.element

}