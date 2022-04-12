package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundCommand

/**
 * Предоставляет унифицированный доступ к потокам с запросами к базе данных
 */
interface ScriptSource {
    /**
     * Получает из потока и возвращает следующую команду или `null`, если поток был закрыт
     */
    suspend fun fetch(): BoundCommand?
}