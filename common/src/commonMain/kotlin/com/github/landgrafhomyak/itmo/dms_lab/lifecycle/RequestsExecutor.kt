package com.github.landgrafhomyak.itmo.dms_lab.lifecycle

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.io.FlushableEncoder
import com.github.landgrafhomyak.itmo.dms_lab.io.LogLevel
import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.RequestsHistory
import kotlinx.serialization.KSerializer

/**
 * Модуль выполнения [запросов][BoundRequest] к [коллекции][AbstractRecordsCollection]
 *
 * @param C тип [коллекции][AbstractRecordsCollection]
 * @param E тип элемента в [коллекции][AbstractRecordsCollection]
 * @param receiver [источник][RequestReceiver] [запросов][BoundRequest]
 * @param collection [коллекция][RequestReceiver], к которой будут применятся запросы
 * @param encoder объект в который будет производится вывод
 * @param historyCapacity максимальный размер [истории][RequestsHistory] выполнения [запросов][BoundRequest]
 */
@Suppress("GrazieInspection", "KDocUnresolvedReference")
public class RequestsExecutor<C : AbstractRecordsCollection<E>, E : Any>(
    private val receiver: RequestReceiver<BoundRequest<C, E>>,
    private val collection: C,
    private val encoder: FlushableEncoder,
    historyCapacity: UInt = 10u
) {
    private val context = this.Context()
    private val history = RequestsHistory<BoundRequest<C, E>>(historyCapacity)
    private val logger = this.EncoderLogger()

    /**
     * [Контекст выполнения][ExecutionContext] [запроса][BoundRequest] с доступом к [модулю][RequestsExecutor]
     */
    private inner class Context : ExecutionContext<C, E>() {
        override suspend fun subscript(receiver: RequestReceiver<BoundRequest<C, E>>) {
            this@RequestsExecutor.runReceiver(receiver)
        }

        override val collection: C
            get() = this@RequestsExecutor.collection

        override val history: RequestsHistory<BoundRequest<C, E>>
            get() = this@RequestsExecutor.history

        override val log: Logger
            get() = this@RequestsExecutor.logger
    }

    /**
     * Считывает [запросы][BoundRequest] из [источника][RequestReceiver] и выполняет их
     */
    private suspend fun runReceiver(receiver: RequestReceiver<BoundRequest<C, E>>) {
        while (true) {
            val request = receiver.fetch() ?: return
            try {
                request.apply { this@RequestsExecutor.context.execute() }
                this.history.push(request)
            } catch (_: ExitSignal) {
                return
            } finally {
                this.encoder.flush()
            }
        }
    }

    /**
     * Запускает получение и выполнение [запросов][BoundRequest]
     */
    public suspend fun run() {
        this.runReceiver(this.receiver)
    }

    /**
     * [Логгер][Logger] перенаправляющий вывод в [энкодер][EncoderLogger]
     */
    @Suppress("SpellCheckingInspection")
    private inner class EncoderLogger : Logger {
        override suspend fun debug(message: String) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.DEBUG)
            this@RequestsExecutor.encoder.encodeString(message)
        }

        override suspend fun <T> debug(obj: T, serializer: KSerializer<T>) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.DEBUG)
            this@RequestsExecutor.encoder.encodeSerializableValue(serializer, obj)
        }

        override suspend fun info(message: String) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.INFO)
            this@RequestsExecutor.encoder.encodeString(message)
        }

        override suspend fun <T> info(obj: T, serializer: KSerializer<T>) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.INFO)
            this@RequestsExecutor.encoder.encodeSerializableValue(serializer, obj)
        }

        override suspend fun warning(message: String) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.WARNING)
            this@RequestsExecutor.encoder.encodeString(message)
        }

        override suspend fun <T> warning(obj: T, serializer: KSerializer<T>) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.WARNING)
            this@RequestsExecutor.encoder.encodeSerializableValue(serializer, obj)
        }

        override suspend fun error(message: String) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.ERROR)
            this@RequestsExecutor.encoder.encodeString(message)
        }

        override suspend fun <T> error(obj: T, serializer: KSerializer<T>) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.ERROR)
            this@RequestsExecutor.encoder.encodeSerializableValue(serializer, obj)
        }

        override suspend fun fatal(message: String) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.FATAL)
            this@RequestsExecutor.encoder.encodeString(message)
        }

        override suspend fun <T> fatal(obj: T, serializer: KSerializer<T>) {
            this@RequestsExecutor.encoder.encodeSerializableValue(LogLevel.serializer(), LogLevel.FATAL)
            this@RequestsExecutor.encoder.encodeSerializableValue(serializer, obj)
        }
    }
}