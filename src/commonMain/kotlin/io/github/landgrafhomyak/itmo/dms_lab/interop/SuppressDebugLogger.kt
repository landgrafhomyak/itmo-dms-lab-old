package io.github.landgrafhomyak.itmo.dms_lab.interop

import kotlinx.serialization.KSerializer
import kotlin.jvm.JvmInline

@JvmInline
public value class SuppressDebugLogger(private val original: Logger) : Logger {
    override suspend fun debug(message: String) {}

    override suspend fun <T> debug(obj: T, serializer: KSerializer<T>) {}

    override suspend fun info(message: String): Unit = this.original.info(message)

    override suspend fun <T> info(obj: T, serializer: KSerializer<T>): Unit = this.original.info(obj, serializer)

    override suspend fun warning(message: String): Unit = this.original.warning(message)

    override suspend fun <T> warning(obj: T, serializer: KSerializer<T>): Unit = this.original.warning(obj, serializer)

    override suspend fun error(message: String): Unit = this.original.error(message)

    override suspend fun <T> error(obj: T, serializer: KSerializer<T>): Unit = this.original.error(obj, serializer)

    override suspend fun fatal(message: String): Unit = this.original.fatal(message)

    override suspend fun <T> fatal(obj: T, serializer: KSerializer<T>): Unit = this.original.fatal(obj, serializer)
}