package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestTransmitter
import io.github.landgrafhomyak.itmo.dms_lab.io.LocalRequestCarrier
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * Модуль для перенаправления [запросов][BoundRequest]
 * @property isRunning показывает состояние модуля, `true` если в данный момент идёт выполнение
 * @see LocalRequestCarrier
 */
public class RequestsRedirector<R : BoundRequest<*, *>>(
    private val receiver: RequestReceiver<R>,
    private val transmitter: RequestTransmitter<R>,
) {
    private val mutex = Mutex()

    @Suppress("MemberVisibilityCanBePrivate")
    public var isRunning: Boolean = false
        private set

    private lateinit var coro: Job


    /**
     * Запускает перенаправление [запросов][BoundRequest]
     */
    public suspend fun run() {
        if (this.isRunning) throw IllegalStateException("Redirector already running, shutdown it first")
        this.mutex.lock()
        this.isRunning = true
        try {
            coroutineScope {
                this@RequestsRedirector.coro = launch {
                    while (true) {
                        this@RequestsRedirector.receiver.fetchAndAnswer { r -> this@RequestsRedirector.transmitter.send(r) }
                    }
                }
            }
        } finally {
            this.isRunning = false
            this.mutex.unlock()
        }
    }

    /**
     * Останавливает перенаправление [запросов][BoundRequest]
     */
    public suspend fun shutdown() {
        this.coro.cancel()
        this.mutex.lock()
        this.mutex.unlock()
    }
}