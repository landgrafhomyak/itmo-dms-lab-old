@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(ExperimentalContracts::class)

package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundCommand
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Считывает одну команду и применяет к ней функцию
 */
@Suppress("unused")
suspend inline fun ScriptSource.fetch(proc: (BoundCommand) -> Unit) {
    contract {
        callsInPlace(proc, InvocationKind.AT_MOST_ONCE)
    }
    this.fetch()?.also(proc)
}

/**
 * Считывает команды в цикле до тех пор, пока поток не будет закрыт или функция не вернёт `null`
 */
@Suppress("unused")
suspend inline fun ScriptSource.fetching(proc: (BoundCommand) -> Unit?) {
    contract {
        callsInPlace(proc, InvocationKind.UNKNOWN)
    }
    while (true) {
        (this.fetch() ?: break).let(proc) ?: break
    }
}


/**
 * Считывает одно сообщение и применяет к нему функцию
 */
@Suppress("unused")
suspend inline fun LogReceiver.fetch(proc: (Message) -> Unit) {
    contract {
        callsInPlace(proc, InvocationKind.AT_MOST_ONCE)
    }
    this.fetch()?.also(proc)
}

/**
 * Считывает сообщения в цикле до тех пор, пока поток не будет закрыт или функция не вернёт `null`
 */
@Suppress("unused")
suspend inline fun LogReceiver.fetching(proc: (Message) -> Unit?) {
    contract {
        callsInPlace(proc, InvocationKind.UNKNOWN)
    }
    while (true) {
        (this.fetch() ?: break).let(proc) ?: break
    }
}

/**
 * Отправляет [упакованное][Message] сообщение
 * @see Logger.info
 * @see Logger.warning
 * @see Logger.error
 * @see Logger.request
 * @see Logger.sendObject
 */
@Suppress("unused")
suspend inline fun Logger.send(message: Message) = when (message) {
    is Message.Error   -> this.error(message.message)
    is Message.Info    -> this.info(message.message)
    is Message.Object  -> this.sendObject(message.work)
    is Message.Request -> this.request(message.level, message.command)
    is Message.Warning -> this.warning(message.message)
}