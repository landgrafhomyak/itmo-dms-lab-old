package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import com.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExitSignal

public abstract class AbstractExit<C : AbstractRecordsCollection<E>, E : Any> : BoundRequest<C, E> {
    override suspend fun ExecutionContext<C>.execute() : Nothing {
        throw ExitSignal()
    }
}