package com.github.landgrafhomyak.itmo.dms_lab

import java.util.Queue

@Suppress("unused")
@JvmInline
value class JLinkedQueue<T>(private val original: LinkedQueue<T>) : Queue<T> {
    @Suppress("unused")
    fun asKQueue() = this.original

    override fun add(element: T): Boolean {
        this.original.add(element)
        return true
    }

    override val size: Int
        get() = this.original.count()

    override fun addAll(elements: Collection<T>): Boolean =
        elements.any { element -> this.add(element) }

    override fun clear() {
        this.original.clear()
    }

    override fun iterator(): MutableIterator<T> = this.original.asMutableIterable().iterator()

    override fun remove(): T = this.original.pop()

    override fun contains(element: T): Boolean = this.original.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        elements.all { element -> this.original.contains(element) }

    override fun isEmpty(): Boolean = this.original.isEmpty()

    override fun remove(element: T): Boolean =
        this.original.asMutableIterable().iterator().run iterator@{
            while (this@iterator.hasNext()) {
                if (this@iterator.next() == element) {
                    this@iterator.remove()
                    return@iterator true
                }
            }
            return@iterator false
        }

    override fun removeAll(elements: Collection<T>): Boolean = elements.any { element ->
        this.remove(element)
    }

    override fun retainAll(elements: Collection<T>): Boolean =
        this.original.asMutableIterable().retainAll { element -> element in elements }

    override fun offer(e: T): Boolean {
        this.original.add(e)
        return true
    }

    override fun poll(): T? = this.original.popOrNull()

    override fun element(): T = this.original.get()

    override fun peek(): T? = this.original.getOrNull()
}

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun <T> LinkedQueue<T>.asJQueue() = JLinkedQueue(this)

