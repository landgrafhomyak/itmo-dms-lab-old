package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundRequest
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


    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Для передачи сообщения с запросом нужно указать уровень вложенности", level = DeprecationLevel.ERROR)
    override suspend fun request(request: BoundRequest) = this.request(0u, request)

    /**
     * Дублирует запрос в выходной поток, если он был передан в не связанном входном потоке
     * @param level уровень вложенности запроса
     * @param request конечный объект запроса
     */
    suspend fun request(level: UInt, request: BoundRequest)


    /**
     * Пересылает [объект коллекции][LabWork]
     */
    suspend fun sendObject(obj: LabWork)

    /**
     * Отправляет [упакованное][Message] сообщение
     * @see Logger.info
     * @see Logger.warning
     * @see Logger.error
     * @see Logger.request
     * @see Logger.sendObject
     */
    suspend fun send(message: Message) = when (message) {
        is Message.Error   -> this.error(message.message)
        is Message.Info    -> this.info(message.message)
        is Message.Object  -> this.sendObject(message.work)
        is Message.Request -> this.request(message.level, message.command)
        is Message.Warning -> this.warning(message.message)
    }
}