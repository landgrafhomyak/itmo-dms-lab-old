package io.github.landgrafhomyak.itmo.dms_lab.v444588.console

import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.interop.RequestOutputConsolePrinter
import io.github.landgrafhomyak.itmo.dms_lab.interop.RequestOutputPrinter
import io.github.landgrafhomyak.itmo.dms_lab.io.AnsiColoring
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestTransmitter
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.RequestsConsoleParser
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkWithId
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests.AddRequest
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests.RemoveByIdRequest
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests.ShowRequest
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests.UpdateRequest

public class ConsoleReader(
    transmitter: RequestTransmitter<BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId>>,
    logger: Logger,
    readLine: () -> String,
    triesCount: UInt = 3u
) : RequestsConsoleParser<BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId>>(transmitter, logger, readLine, triesCount, RequestOutputConsolePrinter(AnsiColoring)) {
    override fun InitializerContext.initRequestsFactories() {
        useLocalExit()
        useLocalHelp()
        ShowRequest { ShowRequest }
        AddRequest { decoder -> AddRequest.serializer().deserialize(decoder) }
        UpdateRequest { decoder -> UpdateRequest.serializer().deserialize(decoder) }
        RemoveByIdRequest { decoder -> RemoveByIdRequest.serializer().deserialize(decoder) }
    }
}