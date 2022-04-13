package com.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Приёмник сообщений
 */
interface LogReceiver {
    /**
     * Получает сообщение лога из потока и возвращает его или `null`, если поток был закрыт
     */
    suspend fun fetch(): Message?
}