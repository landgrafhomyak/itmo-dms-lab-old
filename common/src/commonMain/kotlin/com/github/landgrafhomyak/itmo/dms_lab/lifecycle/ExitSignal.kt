package com.github.landgrafhomyak.itmo.dms_lab.lifecycle

import com.github.landgrafhomyak.itmo.dms_lab.io.RequestReceiver
import com.github.landgrafhomyak.itmo.dms_lab.requests.AbstractExit

/**
 * Сигнал для прекращения [чтения][RequestReceiver.fetch] [источника][RequestReceiver] в [RequestsExecutor]
 * @see AbstractExit
 */
internal class ExitSignal: Throwable()