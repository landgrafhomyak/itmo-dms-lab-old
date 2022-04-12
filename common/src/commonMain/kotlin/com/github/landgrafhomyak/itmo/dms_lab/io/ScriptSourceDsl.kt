@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(ExperimentalContracts::class)

package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundCommand
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("unused")
suspend inline fun ScriptSource.fetch(proc: (BoundCommand) -> Unit) {
    contract {
        callsInPlace(proc, InvocationKind.AT_MOST_ONCE)
    }
    this.fetch()?.also(proc)
}

@Suppress("unused")
suspend inline fun ScriptSource.fetching(proc: (BoundCommand) -> Unit?) {
    contract {
        callsInPlace(proc, InvocationKind.UNKNOWN)
    }
    while (true)
    {
        (this.fetch()?: break).let(proc) ?: break
    }
}