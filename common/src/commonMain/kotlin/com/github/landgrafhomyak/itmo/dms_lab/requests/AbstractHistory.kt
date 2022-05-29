package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext

public abstract class AbstractHistory<C : AbstractRecordsCollection<E>, E : Any> : BoundRequest<C, E> {
    override suspend fun ExecutionContext<C>.execute() {
        this@execute.history // TODO
    }
}