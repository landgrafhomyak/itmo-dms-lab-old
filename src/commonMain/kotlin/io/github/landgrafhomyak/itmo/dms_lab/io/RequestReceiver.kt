package io.github.landgrafhomyak.itmo.dms_lab.io

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.interop.RequestOutput
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.serialization.encoding.Encoder

/**
 * Интерфейс для унифицированного приёма [запросов][BoundRequest]
 * @param R общий тип [запросов][BoundRequest] с указанием типа [коллекции][AbstractRecordsCollection] и элементов в ней
 * @see RequestTransmitter
 * @see LocalRequestCarrier
 */
public interface RequestReceiver<R : BoundRequest<*, *>> {
    /**
     * Функция для получения следующего [запроса][BoundRequest] и отправления ответа на него
     * @param executor функция, которая [выполняет][BoundRequest.execute] [запрос][BoundRequest] и возвращает его [вывод][RequestOutput]
     */
    public suspend fun <T> fetchAndAnswer(executor: (R?, Encoder) -> RequestOutput)
}