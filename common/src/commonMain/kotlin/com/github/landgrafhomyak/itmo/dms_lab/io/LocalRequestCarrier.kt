package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.LinkedQueue
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield

/**
 * _**My life for Aiur!**_
 */
@Suppress("SpellCheckingInspection")
class LocalRequestCarrier<C : AbstractRecordsCollection<*>> : RequestReceiver<C>, RequestTransmitter<C> {
    private val queue = LinkedQueue<BoundRequest<C>>()
    private val mutex = Mutex()
    var isClosed: Boolean = false

    @Suppress("unused", "NOTHING_TO_INLINE")
    inline fun close() {
        this.isClosed = true
    }

    @Suppress("unused", "NOTHING_TO_INLINE")
    inline fun reopen() {
        this.isClosed = false
    }

    override suspend fun fetch(): BoundRequest<C>? {
        while (true) {
            if (this.isClosed) return null
            this.mutex.lock()
            if (this.queue.isNotEmpty()) break
            this.mutex.unlock()
            yield()
        }
        try {
            return this.queue.pop()
        } finally {
            this.mutex.unlock()
        }
    }

    override suspend fun request(data: BoundRequest<C>) {
        this.mutex.lock()
        try {
            this.queue.push(data)
        } finally {
            this.mutex.unlock()
        }
    }

}