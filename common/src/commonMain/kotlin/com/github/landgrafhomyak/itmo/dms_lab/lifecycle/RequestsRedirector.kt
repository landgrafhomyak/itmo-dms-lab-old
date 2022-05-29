package com.github.landgrafhomyak.itmo.dms_lab.lifecycle

import com.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import com.github.landgrafhomyak.itmo.dms_lab.io.RequestTransmitter
import com.github.landgrafhomyak.itmo.dms_lab.io.LocalRequestCarrier
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

/**
 * Модуль для перенаправления [запросов][BoundRequest]
 * @see LocalRequestCarrier
 */
public class RequestsRedirector<R : BoundRequest<*, *>>(
    private val receiver: RequestReceiver<R>,
    private val transmitter: RequestTransmitter<R>,
) {
    /**
     * Запускает перенаправление [запросов][BoundRequest]
     */
    public suspend fun run() {
        while (true)
            this.transmitter.send(this.receiver.fetch() ?: break)
    }
}