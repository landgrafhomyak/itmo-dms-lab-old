package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestsHistory

/**
 * Модуль выполнения [запросов][BoundRequest] к [коллекции][AbstractRecordsCollection]
 *
 * @param C тип [коллекции][AbstractRecordsCollection]
 * @param E тип элемента в [коллекции][AbstractRecordsCollection]
 * @param receiver [источник][RequestReceiver] [запросов][BoundRequest]
 * @param collection [коллекция][RequestReceiver], к которой будут применятся запросы
 * @param logger [логгер][Logger] в который будет производится вывод
 * @param historyCapacity максимальный размер [истории][RequestsHistory] выполнения [запросов][BoundRequest]
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

    /**
     * [Контекст выполнения][ExecutionContext] [запроса][BoundRequest] с доступом к [модулю][RequestsExecutor]
     */
    private inner class Context : ExecutionContext<C, E>() {
        override suspend fun subscript(receiver: RequestReceiver<BoundRequest<C, E>>) {
            this@RequestsExecutor.runReceiver(receiver)
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
    private suspend fun runReceiver(receiver: RequestReceiver<BoundRequest<C, E>>) {
        while (true) {
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
        this.runReceiver(this.receiver)
    }
}