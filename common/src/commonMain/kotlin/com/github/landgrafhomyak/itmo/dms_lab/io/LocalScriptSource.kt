package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.collections.LinkedQueue
import com.github.landgrafhomyak.itmo.dms_lab.collections.Queue
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.coroutines.yield

/**
 * Логгер для передачи локальных запросов между корутинами
 *
 * **ПотокоНЕбезопасный**
 */
@Suppress("unused", "SpellCheckingInspection")
class LocalScriptSource : ScriptTransmitter, ScriptSource {
    @Suppress("SpellCheckingInspection")
    /**
     * Очередь необработанных запросов
     */
    private val queue: Queue<BoundRequest> = LinkedQueue()

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

    override suspend fun fetch(): BoundRequest? {
        if (this.isClosed)
            return null

        while (true) {
            val msg = queue.popOrNull()
            if (msg != null) return msg
            yield()
        }
    }

    override suspend fun request(request: BoundRequest) {
        if (this.isClosed)
            return

        this.queue.push(request)
    }
}