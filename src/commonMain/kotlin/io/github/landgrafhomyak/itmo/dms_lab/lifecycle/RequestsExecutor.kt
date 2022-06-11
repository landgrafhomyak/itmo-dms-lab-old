package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestOutputBuilder
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestsHistory
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.cancellation.CancellationException

/**
 * Модуль выполнения [запросов][BoundRequest] к [коллекции][AbstractRecordsCollection]
 *
 * @param C тип [коллекции][AbstractRecordsCollection]
 * @param E тип элемента в [коллекции][AbstractRecordsCollection]
 * @param receiver [источник][RequestReceiver] [запросов][BoundRequest]
 * @param collection [коллекция][RequestReceiver], к которой будут применятся запросы
 * @param logger [логгер][Logger] в который будет производится вывод
 * @param historyCapacity максимальный размер [истории][RequestsHistory] выполнения [запросов][BoundRequest]
 * @property isRunning показывает состояние модуля, `true` если в данный момент идёт выполнение
 */
@Suppress("GrazieInspection", "KDocUnresolvedReference", "SpellCheckingInspection")
public class RequestsExecutor<C : AbstractRecordsCollection<E>, E : Any>(
    private val receiver: RequestReceiver<BoundRequest<C, E>>,
    private val collection: C,
    private val logger: Logger,
    historyCapacity: UInt = 10u
) {
    private val history = RequestsHistory<BoundRequest<C, E>>(historyCapacity)

    // @Suppress("MemberVisibilityCanBePrivate")
    // public var isRunning: Boolean = false
    //    private set

    private var mutex = Mutex()

    /**
     * [Контекст выполнения][ExecutionContext] [запроса][BoundRequest] с доступом к [модулю][RequestsExecutor]
     */
    private inner class Context(override val out: RequestOutputBuilder) : ExecutionContext<C, E>() {
        override suspend fun subscript(receiver: RequestReceiver<BoundRequest<C, E>>) {
            this@RequestsExecutor.runOnReceiver(receiver)
        }

        override val collection: C
            get() = this@RequestsExecutor.collection

        override val history: RequestsHistory<BoundRequest<C, E>>
            get() = this@RequestsExecutor.history

        override val log: Logger
            get() = this@RequestsExecutor.logger
    }

    /**
     * Считывает [запросы][BoundRequest] из [источника][RequestReceiver] и выполняет их
     */
    private suspend fun runOnReceiver(receiver: RequestReceiver<BoundRequest<C, E>>) {
        this.logger.info("Исполнитель `${this::class.simpleName}` успешно запущен")
        while (true) {
            try {
                this.logger.debug("Ожидание запроса...")
                receiver.fetchAndAnswer { request, output ->
                    this.logger.debug("Получен запрос `${request::class.simpleName}`, выполняется...")
                    request.apply {
                        this@RequestsExecutor.Context(output).execute()
                    }
                    this.logger.debug("Запрос `${request::class.simpleName}` выполнен, сохранение в историю запросов...")
                    this@RequestsExecutor.history.push(request)
                    this.logger.debug("Запрос `${request::class.simpleName}` выполнен и сохранён в историю")
                }
            } catch (_: ExitSignal) {
                this.logger.debug("Получен сигнал остановки в исполнителе `${this::class.simpleName}`")
                return
            }
        }
    }

    /**
     * Запускает получение и выполнение [запросов][BoundRequest]
     */
    public suspend fun run() {
        this.logger.info("Запуск исполнителя `${this::class.simpleName}`...")
        if (!this.mutex.tryLock()) {
            this.logger.fatal("Исполнитель `${this::class.simpleName}` уже был запущен ранее")
            throw IllegalStateException("Executor already running, shutdown it first")
        }
        // this.isRunning = true
        try {
            this.runOnReceiver(this.receiver)
        } catch (e: CancellationException) {
            @Suppress("SpellCheckingInspection")
            this.logger.debug("Исполнитель `${this::class.simpleName}` остановлен отменой корутины")
            throw e
        } catch (e: Throwable) {
            this.logger.fatal("Исполнитель `${this::class.simpleName}` прерван ошибкой: \t\n${e.stackTraceToString()}")
            throw e
        } finally {
            // this.isRunning = false
            this.mutex.unlock()
            this.logger.info("Исполнитель `${this::class.simpleName}` остановлен")
        }
    }
}