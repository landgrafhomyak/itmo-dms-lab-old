package io.github.landgrafhomyak.itmo.dms_lab.v444588.console

import io.github.landgrafhomyak.itmo.dms_lab.interop.ArgvParsed
import io.github.landgrafhomyak.itmo.dms_lab.interop.ConsoleLogger
import io.github.landgrafhomyak.itmo.dms_lab.io.AnsiColoring
import io.github.landgrafhomyak.itmo.dms_lab.io.LocalRequestCarrier
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.RequestsExecutor
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkWithId
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.InMemoryLabWorkCollection
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

public class Application(remoteHost: String? = null, public val debugMode: Boolean = false) {
    public constructor(argv: ArgvParsed) : this()

    public suspend fun instantiateAndRun() {
        val logger = ConsoleLogger(AnsiColoring)
        logger.info("Инициализация приложения...")
        val exchange = LocalRequestCarrier<BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId>>()
        val executor = RequestsExecutor(exchange, InMemoryLabWorkCollection(), logger)
        val parser = ConsoleReader(exchange, logger, ::readln)
        try {
            coroutineScope scope@{
                launch {
                    executor.run()
                    this@scope.cancel()
                }
                launch {
                    parser.run()
                    this@scope.cancel()
                }
            }
        } catch (e: CancellationException) {
            @Suppress("SpellCheckingInspection")
            logger.debug("Приложение остановлено отменой корутины")
        } catch (e: Throwable) {
            logger.fatal("Приложение прервано ошибкой \n\t${e.stackTraceToString()}")
        } finally {
            logger.info("Приложение остановлено")
        }
    }
}