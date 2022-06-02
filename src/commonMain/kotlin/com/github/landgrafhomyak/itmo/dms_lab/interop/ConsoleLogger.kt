package com.github.landgrafhomyak.itmo.dms_lab.interop

import com.github.landgrafhomyak.itmo.dms_lab.io.Coloring
import com.github.landgrafhomyak.itmo.dms_lab.io.NoColoring

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