package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.collections.LinkedQueue
import com.github.landgrafhomyak.itmo.dms_lab.collections.Queue
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import kotlinx.coroutines.yield

/**
 * Логгер для передачи локальных сообщений между корутинами
 *
 * **ПотокоНЕбезопасный**
 */
@Suppress("unused", "SpellCheckingInspection")
class LocalLogger : Logger, LogReceiver {
    @Suppress("SpellCheckingInspection")
    /**
     * Очередь непрочтённых сообщений
     */
    private val queue: Queue<Message> = LinkedQueue()

    /**
     * Проверяет, что поток закрыт
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var isClosed = false
        private set

    /**
     * Закрывает поток для чтения и записи
     */
    @Suppress("unused")
    fun close() {
        this.isClosed = true
    }

    override suspend fun fetch(): Message? {
        if (this.isClosed)
            return null

        while (true) {
            val msg = queue.popOrNull()
            if (msg != null) return msg
            yield()
        }
    }

    override suspend fun info(message: String) = this.send(Message.Info(message))

    override suspend fun warning(message: String) = this.send(Message.Warning(message))

    override suspend fun error(message: String) = this.send(Message.Error(message))

    override suspend fun sendObject(obj: LabWork) = this.send(Message.Object(obj))

    override suspend fun request(level: UInt, request: BoundRequest) = this.send(Message.Request(level, request))

    override suspend fun send(message: Message) {
        if (this.isClosed)
            return
        this.queue.push(message)
    }
}