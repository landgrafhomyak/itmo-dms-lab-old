package io.github.landgrafhomyak.itmo.dms_lab.v444588.console

import io.github.landgrafhomyak.itmo.dms_lab.interop.ConsoleInputDecoder
import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestTransmitter
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.RequestsExecutor
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.RequestsConsoleParser
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkWithId
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests.ShowRequest

public class ConsoleReader(
    transmitter: RequestTransmitter<BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId>>,
    logger: Logger,
    readLine: () -> String,
    private val triesCount: UInt = 3u
) : RequestsConsoleParser<BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId>>(transmitter, logger, readLine) {
    override fun InitializerContext.initRequestsFactories() {
        useExit()
        useHelp()
        ShowRequest { ShowRequest }
    }

}