package io.github.landgrafhomyak.itmo.dms_lab.io

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.RequestsRedirector
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield
import kotlin.jvm.JvmInline

/**
 * _**My life for Aiur!**_
 *
 * Курьер для доставки [запросов][BoundRequest] между корутинами.
 * Имеет синхронизацию.
 *
 * @param R общий тип [запросов][BoundRequest] с указанием типа [коллекции][AbstractRecordsCollection] и элементов в ней
 * @property isClosed флаг для блокировки получения запросов, если установлен,
 * то [`fetch`][RequestReceiver.fetchAndAnswer] возвращает `null`, а [`send`][RequestTransmitter] работает без изменений
 * @see LocalRequestReceiver
 * @see RequestsRedirector
 */
@Suppress("unused", "SpellCheckingInspection")
public class LocalRequestCarrier<R : BoundRequest<*, *>> : RequestReceiver<R>, RequestTransmitter<R> {
    /**
     * Синхранизационный примитив
     */
    private val mutex1 = Mutex()
    private val mutex2 = Mutex(true)

    public var isClosed: Boolean = false

    /**
     * Закрывает поток
     * @see LocalRequestCarrier.isClosed
     */
    @Suppress("unused", "NOTHING_TO_INLINE")
    public inline fun close() {
        this.isClosed = true
    }


    /*
     * Открывает поток
     * @see LocalRequestCarrier.isClosed
     */
    /*
    @Suppress("unused", "NOTHING_TO_INLINE")
    public inline fun reopen() {
        this.isClosed = false
    }
    */


    private sealed interface Exchange {
        @JvmInline
        value class Request(val req: BoundRequest<*, *>) : Exchange

        object Success : Exchange

        @JvmInline
        value class Failed(val exception: Throwable) : Exchange
    }

    private lateinit var exchange: Exchange
    private lateinit var requestOutput: RequestOutputDefaultEncodedInMemoryList

    override suspend fun send(request: R): RequestOutputAccessor {
        if (this.isClosed) throw RequestTransmittingIsClosedException("Carrier is closed")
        this.mutex1.lock()
        this.exchange = Exchange.Request(request)
        this.requestOutput = RequestOutputDefaultEncodedInMemoryList()
        this.mutex2.unlock()
        while (this.exchange is Exchange.Request) {
            yield()
        }
        val resp = this.exchange
        this.mutex1.unlock()
        when (resp) {
            is Exchange.Failed  -> throw resp.exception
            is Exchange.Success -> return this.requestOutput
            else                -> throw IllegalStateException()
        }
    }

    override suspend fun fetchAndAnswer(executor: suspend (R, RequestOutputBuilder) -> Unit) {
        if (this.isClosed) throw RequestTransmittingIsClosedException("Carrier is closed")
        this.mutex2.lock()
        @Suppress("UNCHECKED_CAST")
        val req = (this.exchange as? Exchange.Request ?: throw IllegalStateException()).req as R
        try {
            executor(req, this.requestOutput)
            this.exchange = Exchange.Success
        } catch (t: Throwable) {
            this.exchange = Exchange.Failed(t)
            throw t
        }
    }
}