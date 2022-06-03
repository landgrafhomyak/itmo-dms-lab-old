package io.github.landgrafhomyak.itmo.dms_lab.interop

import io.github.landgrafhomyak.itmo.dms_lab.io.Coloring
import io.github.landgrafhomyak.itmo.dms_lab.io.NoColoring

/**
 * [Логгер][Logger] выводящий логи в консоль
 * @param coloring [цветовой режим][Coloring]
 * @see AbstractTextLogger
 * @see FileLogger
 */
@Suppress("SpellCheckingInspection")
public class ConsoleLogger(override val coloring: Coloring = NoColoring) : AbstractTextLogger() {
    override suspend fun write(s: String) {
        print(s)
    }
}