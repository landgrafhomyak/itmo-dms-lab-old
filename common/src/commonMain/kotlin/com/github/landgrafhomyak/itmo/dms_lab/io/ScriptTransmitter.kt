package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

/**
 * Передатчик скрипта в поток
 */
interface ScriptTransmitter {
    /**
     * Дублирует запрос в выходной поток, если он был передан в не связанном входном потоке
     */
    suspend fun request(request: BoundRequest)
}