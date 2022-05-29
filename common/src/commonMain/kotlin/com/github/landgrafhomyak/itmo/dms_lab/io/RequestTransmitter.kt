package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

/**
 * Интерфейс для унифицированной передачи [запросов][BoundRequest]
 * @param R общий тип [запросов][BoundRequest] с указанием типа [коллекции][AbstractRecordsCollection] и элементов в ней
 * @see RequestReceiver
 * @see LocalRequestCarrier
 */
public interface RequestTransmitter<R: BoundRequest<*, *>> {
    /**
     * Функция для передачи [запроса][BoundRequest]
     */
    public suspend fun send(data: R)
}