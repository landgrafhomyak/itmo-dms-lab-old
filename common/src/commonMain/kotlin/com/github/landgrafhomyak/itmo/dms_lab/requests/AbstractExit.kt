package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import com.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import com.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExitSignal
import com.github.landgrafhomyak.itmo.dms_lab.lifecycle.RequestsExecutor

/**
 * Универсальный [запрос][BoundRequest] для прекращения [чтения][RequestReceiver.fetch] [источника][RequestReceiver] из [RequestsExecutor]
 */
public abstract class AbstractExit<C : AbstractRecordsCollection<E>, E : Any> : BoundRequest<C, E> {
    override suspend fun ExecutionContext<C, E>.execute() : Nothing {
        throw ExitSignal()
    }
}