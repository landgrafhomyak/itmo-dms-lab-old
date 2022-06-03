package io.github.landgrafhomyak.itmo.dms_lab.io

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

/**
 * Интерфейс для унифицированного приёма [запросов][BoundRequest]
 * @param R общий тип [запросов][BoundRequest] с указанием типа [коллекции][AbstractRecordsCollection] и элементов в ней
 * @see RequestTransmitter
 * @see LocalRequestCarrier
 */
public interface RequestReceiver<R : BoundRequest<*, *>> {
    /**
     * Функция для получения следующего [запроса][BoundRequest]
     * @return новый [запрос][BoundRequest] или `null`, если [запросы][BoundRequest] закончились
     */
    public suspend fun fetch(): R?
}