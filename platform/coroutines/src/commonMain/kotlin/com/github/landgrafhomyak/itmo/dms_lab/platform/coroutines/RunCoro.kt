package com.github.landgrafhomyak.itmo.dms_lab.platform.coroutines


/**
 * Запускает сопрограмму
 */
expect fun runCoro(coro: suspend () -> Unit)
