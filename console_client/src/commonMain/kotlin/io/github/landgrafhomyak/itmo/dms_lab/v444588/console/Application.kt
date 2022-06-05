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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

public class Application(remoteHost: String? = null, public val debugMode: Boolean = false) {
    public constructor(argv: ArgvParsed) : this()

    public suspend fun instantiateAndRun() {
        val exchange = LocalRequestCarrier<BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId>>()
        val logger = ConsoleLogger(AnsiColoring)
        val executor = RequestsExecutor(exchange, InMemoryLabWorkCollection(), logger)
        val parser = ConsoleReader(exchange, logger, ::readln)
        try {
            coroutineScope {
                launch {
                    executor.run()
                    parser.shutdown()
                }
                launch {
                    parser.run()
                    executor.shutdown()
                }
            }
        } catch (e: Throwable) {
            logger.fatal(e.stackTraceToString())
        }
    }
}