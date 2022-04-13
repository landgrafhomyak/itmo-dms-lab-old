package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundCommand

/**
 * Передатчик скрипта в поток
 */
interface ScriptTransmitter {
    /**
     * Дублирует запрос в выходной поток, если он был передан в не связанном входном потоке
     * @param level уровень вложенности запроса
     * @param request строка с запросом
     */
    suspend fun request(level: UInt, request: BoundCommand)

}