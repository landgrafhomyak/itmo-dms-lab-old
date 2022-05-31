package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import kotlinx.serialization.KSerializer

/**
 * Универсальный [запрос][BoundRequest] для вывода [истории][RequestsHistory]
 */
public abstract class AbstractHistory<C : AbstractRecordsCollection<E>, E : Any>(
    private val requestPolymorphicSerializer: KSerializer<BoundRequest<C, E>>
) : BoundRequest<C, E> {
    override suspend fun ExecutionContext<C, E>.execute() {
        this@execute.log.info("Последние ${this@execute.history.capacity} запросов:")
        for (req in this@execute.history)
            this@execute.log.info(req, this@AbstractHistory.requestPolymorphicSerializer)
    }
}