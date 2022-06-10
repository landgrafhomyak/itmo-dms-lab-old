package io.github.landgrafhomyak.itmo.dms_lab.io

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.RequestOutputList
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

/**
 * Обёртка для получения [запросов][BoundRequest] из [итератора][Iterator] или любой коллекции.
 *
 * @param iterator [итератор][Iterator] из которого будет производится чтение
 * @param R общий тип [запросов][BoundRequest] с указанием типа [коллекции][AbstractRecordsCollection] и элементов в ней
 * @see LocalRequestCarrier
 */
@Suppress("unused", "KDocUnresolvedReference")
public class LocalRequestReceiver<R : BoundRequest<*, *>>(private val iterator: Iterator<R>) : RequestReceiver<R> {
    public constructor(vararg requests: R) : this(requests.iterator())
    public constructor(requests: Iterable<R>) : this(requests.iterator())
    public constructor(requests: Sequence<R>) : this(requests.iterator())

    override suspend fun fetchAndAnswer(executor: suspend (R) -> RequestOutputList) {
        if (!this.iterator.hasNext())
            throw RequestTransmittingIsClosedException("Iterator is ended")
        executor(this.iterator.next())
    }
}