package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork

@Suppress("SpellCheckingInspection")
/**
 * Логгер
 */
interface Logger : ScriptTransmitter {
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
     * Пересылает [объект коллекции][LabWork]
     */
    suspend fun sendObject(obj: LabWork)
}