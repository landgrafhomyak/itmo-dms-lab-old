package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestTransmitter
import io.github.landgrafhomyak.itmo.dms_lab.io.LocalRequestCarrier
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.cancellation.CancellationException

/**
 * Модуль для перенаправления [запросов][BoundRequest]
 * @property isRunning показывает состояние модуля, `true` если в данный момент идёт выполнение
 * @see LocalRequestCarrier
 */
public class RequestsRedirector<R : BoundRequest<*, *>>(
    private val receiver: RequestReceiver<R>,
    private val transmitter: RequestTransmitter<R>,
    private val logger: Logger
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
        this.logger.info("Запуск перенаправления `${this::class.simpleName}`...")
        // if (this.isRunning) throw IllegalStateException("Redirector already running, shutdown it first")
        if (!this.mutex.tryLock()) {
            this.logger.fatal("Перенаправление `${this::class.simpleName}` уже было запущено ранее")
            throw IllegalStateException("Redirector already running, shutdown it first")
        }
        // this.isRunning = true
        try {
            coroutineScope {
                this@RequestsRedirector.coro = launch {
                    this@RequestsRedirector.logger.info("Перенаправление `${this::class.simpleName}` успешно запущено")
                    while (true) {
                        this@RequestsRedirector.logger.debug("Ожидание запроса...")
                        this@RequestsRedirector.receiver.fetchAndAnswer { r, o ->
                            this@RequestsRedirector.logger.debug("Запрос получен, перенаправление...")
                            o.addFrom(this@RequestsRedirector.transmitter.send(r))
                            this@RequestsRedirector.logger.debug("Запрос перенаправлен, ответ получен")
                        }
                    }
                }
            }
        }catch (e: CancellationException) {
            @Suppress("SpellCheckingInspection")
            this.logger.debug("Перенаправление `${this::class.simpleName}` остановлено отменой корутины")
            throw e
        } catch (e: Throwable) {
            this.logger.fatal("Перенаправление `${this::class.simpleName}` прервано ошибкой:\n\t${e.stackTraceToString()}")
            throw e
        } finally {
            // this.isRunning = false
            this.mutex.unlock()
        }
    }
}