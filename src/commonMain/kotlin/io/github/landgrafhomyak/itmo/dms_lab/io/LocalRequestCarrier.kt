package io.github.landgrafhomyak.itmo.dms_lab.io

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.RequestsRedirector
import io.github.landgrafhomyak.itmo.dms_lab.LinkedQueue
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield

/**
 * _**My life for Aiur!**_
 *
 * Курьер для доставки [запросов][BoundRequest] между корутинами.
 * Имеет синхронизацию.
 *
 * @param R общий тип [запросов][BoundRequest] с указанием типа [коллекции][AbstractRecordsCollection] и элементов в ней
 * @property isClosed флаг для блокировки получения запросов, если установлен,
 * то [`fetch`][RequestReceiver.fetch] возвращает `null`, а [`send`][RequestTransmitter] работает без изменений
 * @see LocalRequestReceiver
 * @see RequestsRedirector
 */
@Suppress("unused", "SpellCheckingInspection")
public class LocalRequestCarrier<R : BoundRequest<*, *>> : RequestReceiver<R>, RequestTransmitter<R> {
    /**
     * Очередь из необработанных [запросов][BoundRequest]
     */
    private val queue = LinkedQueue<R>()

    /**
     * Синхранизационный примитив
     */
    private val mutex = Mutex()

    public var isClosed: Boolean = false

    /**
     * Закрывает поток
     * @see LocalRequestCarrier.isClosed
     */
    @Suppress("unused", "NOTHING_TO_INLINE")
    public inline fun close() {
        this.isClosed = true
    }

    /**
     * Открывает поток
     * @see LocalRequestCarrier.isClosed
     */
    @Suppress("unused", "NOTHING_TO_INLINE")
    public inline fun reopen() {
        this.isClosed = false
    }

    override suspend fun fetch(): R? {
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

    override suspend fun send(request: R) {
        this.mutex.lock()
        try {
            this.queue.push(request)
        } finally {
            this.mutex.unlock()
        }
    }
}