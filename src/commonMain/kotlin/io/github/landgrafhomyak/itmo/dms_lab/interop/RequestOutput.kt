package io.github.landgrafhomyak.itmo.dms_lab.interop

import io.github.landgrafhomyak.itmo.dms_lab.RequestOutputList
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Encoder

/**
 * Вывод [запроса][BoundRequest]
 */
public interface RequestOutput {
    public suspend fun info(message: String)
    public suspend fun <T> info(obj: T, serializer: KSerializer<T>)
    public suspend fun warning(message: String)
    public suspend fun <T> warning(obj: T, serializer: KSerializer<T>)
    public suspend fun error(message: String)
    public suspend fun <T> error(obj: T, serializer: KSerializer<T>)

    @Serializable
    public enum class MessageType {
        @SerialName("i")
        INFO,

        @SerialName("w")
        WARNING,

        @SerialName("e")
        ERROR
    }

    @Serializable
    public data class Message<T>(val type: MessageType, val message: T)
}

@Suppress("NOTHING_TO_INLINE")
public inline fun RequestOutput(rol: RequestOutputList): RequestOutput = RequestOutputImpl(encoder)

@PublishedApi
internal class RequestOutputImpl(private val encoder: Encoder) : RequestOutput {

    private inline fun <T> write(type: RequestOutput.MessageType, serializer: KSerializer<T>, message: T) {
        RequestOutput.Message.serializer(serializer).serialize(this.encoder, RequestOutput.Message(type, message))
    }

    override suspend fun info(message: String): Unit =
        this.write(RequestOutput.MessageType.INFO, String.serializer(), message)

    override suspend fun <T> info(obj: T, serializer: KSerializer<T>): Unit =
        this.write(RequestOutput.MessageType.INFO, serializer, obj)

    override suspend fun warning(message: String): Unit =
        this.write(RequestOutput.MessageType.WARNING, String.serializer(), message)

    override suspend fun <T> warning(obj: T, serializer: KSerializer<T>): Unit =
        this.write(RequestOutput.MessageType.WARNING, serializer, obj)

    override suspend fun error(message: String): Unit =
        this.write(RequestOutput.MessageType.ERROR, String.serializer(), message)

    override suspend fun <T> error(obj: T, serializer: KSerializer<T>): Unit =
        this.write(RequestOutput.MessageType.ERROR, serializer, obj)
}