package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection

public interface BoundRequest<C : AbstractRecordsCollection<E>, E : Any> {
    /**
     * [Мета-объект][RequestMeta] описывающий тип этого запроса
     */
    public val meta: RequestMeta
}