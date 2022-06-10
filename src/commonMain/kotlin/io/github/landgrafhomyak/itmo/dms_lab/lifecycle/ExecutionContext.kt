package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestOutputBuilder
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import io.github.landgrafhomyak.itmo.dms_lab.requests.AbstractHistory
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestsHistory

/**
 * Контекст выполнения [запроса][BoundRequest]
 * @see BoundRequest.execute
 */
public abstract class ExecutionContext<C : AbstractRecordsCollection<E>, E : Any> {
    /**
     * Запускает выполнение из переданного [источника][RequestReceiver].
     * Возвращается после окончания выполнения подпрограммы.
     */
    public abstract suspend fun subscript(receiver: RequestReceiver<BoundRequest<C, E>>)

    /**
     * [Коллекция][AbstractRecordsCollection] к которой применяется [запрос][BoundRequest]
     */
    public abstract val collection: C

    /**
     * [История][RequestsHistory] выполнения [запросов][BoundRequest]
     * @see AbstractHistory
     */
    internal abstract val history: RequestsHistory<BoundRequest<C, E>>

    /**
     * [Логгер][Logger]
     */
    @Suppress("SpellCheckingInspection")
    public abstract val log: Logger

    /**
     * [Вывод][RequestOutput] для клиента
     */
    @Suppress("SpellCheckingInspection")
    public abstract val out: RequestOutputBuilder
}