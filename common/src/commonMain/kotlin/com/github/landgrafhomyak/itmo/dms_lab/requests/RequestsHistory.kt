package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class RequestsHistory<C : AbstractRecordsCollection<*>>(capacity: UInt) : Iterable<BoundRequest<C>> {
    private val storage = Array<BoundRequest<C>?>(capacity.toInt()) { null }
    private var nextPos = 0u

    @Suppress("MemberVisibilityCanBePrivate")
    val size: UInt
        get() = this.storage.size.toUInt()

    private inline fun UInt.rInc(): UInt = (this@rInc + 1u) % this@RequestsHistory.size

    private inline operator fun get(index: UInt) = this.storage[index.toInt()]
    private inline operator fun set(index: UInt, value: BoundRequest<C>) {
        this.storage[index.toInt()] = value
    }

    @Suppress("unused")
    fun push(request: BoundRequest<C>) {
        if (this.size == 0u) return
        this[this.nextPos] = request
        this.nextPos = this.nextPos.rInc()
    }

    private inner class IteratorImpl(startPos: UInt) : Iterator<BoundRequest<C>> {
        private var pos = startPos
        private var anyReturned = false

        override fun hasNext(): Boolean = !this.anyReturned || this.pos != this@RequestsHistory.nextPos

        override fun next(): BoundRequest<C> {
            if (!this.hasNext()) throw IllegalStateException("History iteration has been ended")
            this.anyReturned = true
            return (this@RequestsHistory[this.pos] ?: throw IllegalStateException("History was corrupted"))
                .also { this.pos = this.pos.rInc() }
        }
    }

    private object EmptyIteratorImpl : Iterator<BoundRequest<*>> {
        override fun hasNext(): Boolean = false

        override fun next(): BoundRequest<*> = throw IllegalStateException("Empty history hasn't requests")
    }

    override fun iterator(): Iterator<BoundRequest<C>> {
        @Suppress("UNCHECKED_CAST")
        if (this.size == 0u) return EmptyIteratorImpl as Iterator<BoundRequest<C>>
        @Suppress("UNCHECKED_CAST")
        if (this[0u] == null) return EmptyIteratorImpl as Iterator<BoundRequest<C>>
        if (this[this.size - 1u] == null) return this.IteratorImpl(0u)
        return this.IteratorImpl(this.nextPos)
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("unused")
inline fun <C : AbstractRecordsCollection<*>> buildRequestsHistory(capacity: UInt, builder: RequestsHistory<C>.() -> Unit): RequestsHistory<C> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return RequestsHistory<C>(capacity).apply(builder)
}