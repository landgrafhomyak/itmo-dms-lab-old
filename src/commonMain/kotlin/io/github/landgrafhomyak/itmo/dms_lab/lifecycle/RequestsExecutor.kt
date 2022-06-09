package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.interop.RequestOutput
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestsHistory
import kotlinx.coroutines.sync.Mutex

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
    private val context = this.Context()
    private val history = RequestsHistory<BoundRequest<C, E>>(historyCapacity)

    @Suppress("MemberVisibilityCanBePrivate")
    public var isRunning: Boolean = false
        private set
    private var mutex = Mutex()

    /**
     * [Контекст выполнения][ExecutionContext] [запроса][BoundRequest] с доступом к [модулю][RequestsExecutor]
     */
    private inner class Context(override val out: RequestOutput) : ExecutionContext<C, E>() {
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
        while (this.isRunning) {
            val request = receiver.fetch() ?: return
            try {
                request.apply { this@RequestsExecutor.context.execute() }
                this.history.push(request)
            } catch (_: ExitSignal) {
                return
            }
        }
    }

    /**
     * Запускает получение и выполнение [запросов][BoundRequest]
     */
    public suspend fun run() {
        this.mutex.lock()
        if (this.isRunning) throw IllegalStateException("Executor already running, shutdown it first")
        this.isRunning = true
        try {
            this.runOnReceiver(this.receiver)
        } finally {
            this.isRunning = false
            this.mutex.unlock()
        }
    }

    /**
     * Останавливает выполнение [запросов][BoundRequest]
     */
    public suspend fun shutdown() {
        this.isRunning = false
        this.mutex.lock()
        this.mutex.unlock()
    }
}