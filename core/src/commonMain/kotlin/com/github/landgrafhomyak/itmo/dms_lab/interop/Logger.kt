package com.github.landgrafhomyak.itmo.dms_lab.interop

import kotlinx.serialization.KSerializer

public interface Logger {
    public suspend fun debug(message: String)
    public suspend fun <T> debug(obj: T, serializer: KSerializer<T>)
    public suspend fun info(message: String)
    public suspend fun <T> info(obj: T, serializer: KSerializer<T>)
    public suspend fun warning(message: String)
    public suspend fun <T> warning(obj: T, serializer: KSerializer<T>)
    public suspend fun error(message: String)
    public suspend fun <T> error(obj: T, serializer: KSerializer<T>)
    public suspend fun fatal(message: String)
    public suspend fun <T> fatal(obj: T, serializer: KSerializer<T>)
}

/**
 * Отлавливает [исключение][Throwable] в блоке и печатает стек вызова в [логгер][Logger], но не подавляет его
 */
@Suppress("SpellCheckingInspection")
public suspend inline fun <T> Logger.catching(scope: () -> T): T {
    try {
        return scope()
    } catch (err: Throwable) {
        this@catching.fatal(err.stackTraceToString())
        throw err
    }
}