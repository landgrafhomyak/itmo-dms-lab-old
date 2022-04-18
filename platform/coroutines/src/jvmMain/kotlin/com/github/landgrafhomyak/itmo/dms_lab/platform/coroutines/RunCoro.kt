package com.github.landgrafhomyak.itmo.dms_lab.platform.coroutines

import kotlinx.coroutines.launch

/**
 * Запускает сопрограмму
 */
actual fun runCoro(coro: suspend () -> Unit) {
    kotlinx.coroutines.runBlocking { launch { coro() }.join() }
}