package io.github.landgrafhomyak.itmo.dms_lab.requests

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext

/**
 * [Замыкание](https://ru.wikipedia.org/wiki/%D0%97%D0%B0%D0%BC%D1%8B%D0%BA%D0%B0%D0%BD%D0%B8%D0%B5_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 * содержащее все аргументы, необходимые для самостоятельного существования.
 * [Применяется][BoundRequest.execute] к [коллекции][AbstractRecordsCollection] или
 * [контексту][ExecutionContext] без передачи дополнительных аргументов
 * @param C тип коллекции на которой выполняется запрос
 * @param E тип элементов коллекции, передаётся для запросов которые в качестве аргументов получают
 * эти элементы
 * @see RequestMeta
 * @see AbstractRecordsCollection
 * @see ExecutionContext
 */
public interface BoundRequest<C : AbstractRecordsCollection<E>, E : Any> {
    /**
     * [Мета-объект][RequestMeta] описывающий тип этого [запроса][BoundRequest]
     */
    public val meta: RequestMeta

    /**
     * Метод для выполнения [запроса][BoundRequest]
     */
    public suspend fun ExecutionContext<C, E>.execute()
}