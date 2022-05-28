package com.github.landgrafhomyak.itmo.dms_lab

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class LinkedQueue<T> : Iterable<T> {
    private data class Node<T>(
        val value: T,
        var newer: Node<T>? = null
    )

    private var newest: Node<T>? = null
    private var oldest: Node<T>? = null

    fun isEmpty(): Boolean = this.newest == null

    fun isNotEmpty(): Boolean = this.newest != null

    @Suppress("unused")
    fun clear() {
        this.newest = null
        this.oldest = null
    }

    fun push(value: T) {
        this.newest = Node(value)
            .apply node@{ this@LinkedQueue.newest?.newer = this@node }
        if (this.oldest == null) this.oldest = this.newest
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun assertOldest(): Node<T> = this.oldest ?: throw NoSuchElementException("Queue is empty")

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Node<T>.exclude(): T {
        this@LinkedQueue.oldest = this@exclude.newer
        if (this@LinkedQueue.oldest == null) this@LinkedQueue.newest = null
        return this@exclude.value
    }

    @Suppress("unused")
    fun getOrNull(): T? = this.oldest?.value

    @Suppress("unused")
    fun get(): T = this.assertOldest().value

    @Suppress("unused")
    fun popOrNull(): T? = this.oldest?.exclude()

    fun pop(): T = this.assertOldest().exclude()

    private class IteratorImpl<T>(private var node: Node<T>?) : Iterator<T> {
        override fun hasNext(): Boolean = this.node != null

        override fun next(): T = (this.node ?: throw IllegalStateException("Queue iteration has been ended"))
            .apply node@{ this@IteratorImpl.node = this@node.newer }
            .value
    }

    override fun iterator(): Iterator<T> = IteratorImpl(this.oldest)

    private inner class MutableIteratorImpl(private var node: Node<T>?) : MutableIterator<T> {
        @Suppress("UNCHECKED_CAST")
        private var older2: Node<T?> = Node(null, Node(null, this.node as Node<T?>))

        override fun hasNext(): Boolean = this.node != null

        override fun next(): T = (this.node ?: throw IllegalStateException("Queue iteration has been ended"))
            .apply node@{ this@MutableIteratorImpl.node = this@node.newer }
            .apply node@{
                if (this@MutableIteratorImpl.older2.newer != this@node)
                    this@MutableIteratorImpl.older2 = this@MutableIteratorImpl.older2.newer ?: throw RuntimeException("Queue was corrupted")
            }
            .value

        override fun remove() {
            if (this.older2.newer == this.node) throw IllegalStateException("Removing element that not in queue")
            val removedNode = this.older2.newer ?: throw RuntimeException("Queue was corrupted")
            @Suppress("UNCHECKED_CAST")
            this.older2.newer = this.node as Node<T?>?
            @Suppress("UNCHECKED_CAST")
            if (this@LinkedQueue.oldest == removedNode) this@LinkedQueue.oldest = removedNode.newer as Node<T>?
            if (this@LinkedQueue.newest == removedNode) {
                @Suppress("UNCHECKED_CAST")
                if (this@LinkedQueue.oldest == null) this@LinkedQueue.newest = null
                else this@LinkedQueue.newest = this.older2 as Node<T>?
            }
        }
    }

    private inner class MutableIteratorDelegate : MutableIterable<T> {
        override fun iterator(): MutableIterator<T> = this@LinkedQueue.MutableIteratorImpl(this@LinkedQueue.oldest)
    }

    @Suppress("unused")
    fun asMutableIterable(): MutableIterable<T> = this.MutableIteratorDelegate()
}


@OptIn(ExperimentalContracts::class)
@Suppress("unused")
inline fun <T> buildQueue(builder: LinkedQueue<T>.() -> Unit): LinkedQueue<T> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return LinkedQueue<T>().apply(builder)
}