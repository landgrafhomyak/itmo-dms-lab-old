package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import io.github.landgrafhomyak.itmo.dms_lab.requests.AbstractExit

/**
 * Сигнал для прекращения [чтения][RequestReceiver.fetch] [источника][RequestReceiver] из [RequestsExecutor]
 * @see AbstractExit
 */
internal class ExitSignal: Throwable()