package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

/**
 * Предоставляет унифицированный доступ к потокам с запросами к базе данных
 */
interface ScriptSource {
    /**
     * Получает из потока и возвращает следующую запрос или `null`, если поток был закрыт
     */
    suspend fun fetch(): BoundRequest?
}