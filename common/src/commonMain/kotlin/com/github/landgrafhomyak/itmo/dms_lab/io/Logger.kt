package com.github.landgrafhomyak.itmo.dms_lab.io

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