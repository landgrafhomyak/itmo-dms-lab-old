@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(ExperimentalContracts::class)

package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Считывает один запрос и применяет к ней функцию
 */
@Suppress("unused")
suspend inline fun ScriptSource.fetch(proc: (BoundRequest) -> Unit) {
    contract {
        callsInPlace(proc, InvocationKind.AT_MOST_ONCE)
    }
    this.fetch()?.also(proc)
}

/**
 * Считывает запросы в цикле до тех пор, пока поток не будет закрыт или функция не вернёт `null`
 */
@Suppress("unused")
suspend inline fun ScriptSource.fetching(proc: (BoundRequest) -> Unit?) {
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