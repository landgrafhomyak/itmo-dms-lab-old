package io.github.landgrafhomyak.itmo.dms_lab.requests

/**
 * Мета-объект описывающий типы [запросов][BoundRequest]
 * @see BoundRequest
 */
public interface RequestMeta {
    /**
     * Описание [запроса][BoundRequest]
     */
    public val description: String

    /**
     * Идентификатор, по которому [запрос][BoundRequest] может быть выполнен из консоли.
     *
     * Если равен `null` - не может быть выполнен из консоли.
     */
    @Suppress("unused")
    public val consoleName: String?
        get() = null
}