package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork

@Suppress("SpellCheckingInspection")
/**
 * Логгер
 */
interface Logger {
    /**
     * Передаёт информационное сообщение
     * @param message сообщение
     */
    suspend fun info(message: String)

    /**
     * Передаёт предупреждение
     * @param message информация о предупреждении
     */
    suspend fun warning(message: String)

    /**
     * Передаёт ошибку
     * @param message информация об ошибке
     */
    suspend fun error(message: String)


    /**
     * Дублирует запрос в выходной поток, если он был передан в не связанном входном потоке
     * @param level уровень вложенности запроса
     * @param request строка с запросом
     */
    suspend fun request(level: UInt, request: String)

    /**
     * Пересылает [объект коллекции][LabWork]
     */
    suspend fun sendObject(obj: LabWork)
}