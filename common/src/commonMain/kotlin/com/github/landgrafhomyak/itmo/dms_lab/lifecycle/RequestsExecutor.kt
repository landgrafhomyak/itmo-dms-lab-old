package com.github.landgrafhomyak.itmo.dms_lab.lifecycle

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.RequestsHistory

/**
 * Модуль выполнения [запросов][BoundRequest] к [коллекции][AbstractRecordsCollection]
 *
 * @param C тип [коллекции][AbstractRecordsCollection]
 * @param receiver [источник][RequestReceiver] [запросов][BoundRequest]
 * @param collection [коллекция][RequestReceiver], к которой будут применятся запросы
 * @param historyCapacity максимальный размер [истории][RequestsHistory] выполнения [запросов][BoundRequest]
 */
@Suppress("GrazieInspection", "KDocUnresolvedReference")
public class RequestsExecutor<C : AbstractRecordsCollection<*>>(
    private val receiver: RequestReceiver<BoundRequest<C, *>>,
    private val collection: C,
    historyCapacity: UInt = 10u
) {
    private val context = this.Context()
    private val history = RequestsHistory<BoundRequest<C, *>>(historyCapacity)

    /**
     * [Контекст выполнения][ExecutionContext] [запроса][BoundRequest] с доступом к [модулю][RequestsExecutor]
     */
    private inner class Context : ExecutionContext<C>() {
        override suspend fun subscript(receiver: RequestReceiver<BoundRequest<C, *>>) {
            this@RequestsExecutor.runReceiver(receiver)
        }

        override val collection: C
            get() = this@RequestsExecutor.collection

        override val history: RequestsHistory<BoundRequest<C, *>>
            get() = this@RequestsExecutor.history
    }

    /**
     * Считывает [запросы][BoundRequest] из [источника][RequestReceiver] и выполняет их
     */
    private suspend fun runReceiver(receiver: RequestReceiver<BoundRequest<C, *>>) {
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